package com.winclaw.bookrank.data.local

import com.winclaw.bookrank.data.model.BookRank
import com.winclaw.bookrank.data.model.PlatformType
import kotlinx.coroutines.flow.Flow

/**
 * 书籍排行榜仓库
 */
class BookRankRepository(
    private val database: AppDatabase
) {
    
    private val dao = database.bookRankDao()
    
    /**
     * 获取指定平台的排行榜
     */
    fun getRanksByPlatform(platformCode: String): Flow<List<BookRankEntity>> {
        return dao.getRanksByPlatform(platformCode)
    }
    
    /**
     * 获取热门榜单（跨平台）
     */
    fun getHotRanks(limit: Int): Flow<List<BookRankEntity>> {
        return dao.getHotRanks(limit)
    }
    
    /**
     * 搜索书籍
     */
    fun searchBooks(query: String): Flow<List<BookRankEntity>> {
        return dao.searchBooks("%$query%")
    }
    
    /**
     * 插入书籍列表
     */
    suspend fun insertAll(books: List<BookRank>) {
        val entities = books.map { it.toEntity() }
        dao.insertAll(entities)
    }
    
    /**
     * 插入单本书
     */
    suspend fun insert(book: BookRank) {
        dao.insert(book.toEntity())
    }
    
    /**
     * 更新书籍
     */
    suspend fun update(book: BookRank) {
        dao.update(book.toEntity())
    }
    
    /**
     * 删除书籍
     */
    suspend fun delete(book: BookRank) {
        dao.delete(book.toEntity())
    }
    
    /**
     * 删除指定平台的所有数据
     */
    suspend fun deleteByPlatform(platformCode: String) {
        dao.deleteByPlatform(platformCode)
    }
    
    /**
     * 删除旧数据
     */
    suspend fun cleanOldData(olderThan: Long = System.currentTimeMillis() - 7L * 24 * 60 * 60 * 1000) {
        dao.deleteOldData(olderThan)
    }
    
    /**
     * 获取所有平台
     */
    fun getAllPlatforms(): Flow<List<String>> {
        return dao.getAllPlatforms()
    }
    
    /**
     * 获取书籍总数
     */
    fun getTotalCount(): Flow<Int> {
        return dao.getTotalCount()
    }
}

/**
 * BookRank 与 BookRankEntity 的转换扩展函数
 */
fun BookRank.toEntity(): BookRankEntity {
    return BookRankEntity(
        id = this.id,
        title = this.title,
        author = this.author,
        platformCode = this.platform.platformCode,
        rank = this.rank,
        coverUrl = this.coverUrl,
        description = this.description,
        category = this.category,
        wordCount = this.wordCount,
        heatValue = this.heatValue,
        score = this.score,
        detailUrl = this.detailUrl,
        tags = this.tags?.let { android.os.Parcelable.SERIALIZABLE::class.java.name },
        updateTime = System.currentTimeMillis()
    )
}

fun BookRankEntity.toBookRank(): BookRank {
    return BookRank(
        id = this.id,
        title = this.title,
        author = this.author,
        platform = PlatformType.fromCode(this.platformCode) ?: PlatformType.QIANDIAN,
        rank = this.rank,
        coverUrl = this.coverUrl,
        description = this.description,
        category = this.category,
        wordCount = this.wordCount,
        heatValue = this.heatValue,
        score = this.score,
        detailUrl = this.detailUrl,
        tags = null,
        updateTime = this.updateTime
    )
}
