package com.winclaw.bookrank.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.winclaw.bookrank.data.local.AppDatabase
import com.winclaw.bookrank.data.local.BookRankEntity
import com.winclaw.bookrank.data.model.BookRank
import com.winclaw.bookrank.data.model.PlatformType
import com.winclaw.bookrank.util.crawler.CrawlManager
import com.winclaw.bookrank.util.crawler.CrawlResult
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * 主页面 ViewModel
 */
class MainViewModel : ViewModel() {
    
    private val database = AppDatabase.getInstance(com.winclaw.bookrank.BookRankApplication.instance)
    private val repository = com.winclaw.bookrank.data.local.BookRankRepository(database)
    private val crawlManager = CrawlManager()
    
    private val _uiState = MutableLiveData<UiState>(UiState.Idle)
    val uiState: LiveData<UiState> = _uiState
    
    private val _currentPlatform = MutableStateFlow(PlatformType.QIANDIAN)
    val currentPlatform: StateFlow<PlatformType> = _currentPlatform.asStateFlow()
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    // 书籍列表
    val bookList: LiveData<List<BookRank>> = _currentPlatform
        .flatMapLatest { platform ->
            repository.getRanksByPlatform(platform.platformCode)
                .map { entities -> entities.map { entity -> entity.toBookRank() } }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
        .asLiveData()
    
    // 热门榜单（跨平台）
    val hotBooks: LiveData<List<BookRank>> = repository.getHotRanks(10)
        .map { entities -> entities.map { entity -> entity.toBookRank() } }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
        .asLiveData()
    
    // 搜索结果
    val searchResults: LiveData<List<BookRank>> = _searchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) {
                flow { emit(emptyList<BookRankEntity>()) }
            } else {
                repository.searchBooks(query)
            }
        }
        .map { entities -> entities.map { entity -> entity.toBookRank() } }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
        .asLiveData()
    
    /**
     * 切换平台
     */
    fun switchPlatform(platform: PlatformType) {
        _currentPlatform.value = platform
    }
    
    /**
     * 开始爬取
     */
    fun startCrawl() {
        _uiState.value = UiState.Loading
        
        viewModelScope.launch {
            try {
                val platform = _currentPlatform.value
                val result = crawlManager.crawlPlatform(platform)
                
                if (result.success) {
                    repository.insertAll(result.books)
                    _uiState.value = UiState.Success("爬取成功，获取 ${result.books.size} 本书")
                } else {
                    _uiState.value = UiState.Error(result.errorMessage ?: "爬取失败")
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "未知错误")
            }
        }
    }
    
    /**
     * 爬取所有平台
     */
    fun crawlAllPlatforms() {
        _uiState.value = UiState.Loading
        
        viewModelScope.launch {
            try {
                val results = crawlManager.crawlAllPlatforms()
                val totalBooks = results.sumOf { if (it.success) it.books.size else 0 }
                val successCount = results.count { it.success }
                
                repository.insertAll(results.flatMap { it.books })
                
                _uiState.value = UiState.Success("爬取完成：${successCount}/${results.size} 平台成功，共 ${totalBooks} 本书")
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "未知错误")
            }
        }
    }
    
    /**
     * 搜索书籍
     */
    fun search(query: String) {
        _searchQuery.value = query
    }
    
    /**
     * 清除搜索
     */
    fun clearSearch() {
        _searchQuery.value = ""
    }
    
    /**
     * 清理旧数据
     */
    fun cleanOldData() {
        viewModelScope.launch {
            repository.cleanOldData()
        }
    }
    
    /**
     * UI 状态
     */
    sealed class UiState {
        object Idle : UiState()
        object Loading : UiState()
        data class Success(val message: String) : UiState()
        data class Error(val message: String) : UiState()
    }
}
