package com.winclaw.bookrank.util.crawler

import com.winclaw.bookrank.data.model.BookRank
import com.winclaw.bookrank.data.model.PlatformType
import com.winclaw.bookrank.util.network.NetworkClient
import kotlinx.coroutines.delay
import okhttp3.Request
import java.io.IOException

/**
 * 爬虫基类
 */
abstract class BaseCrawler(
    protected val platform: PlatformType,
    protected val cookies: Map<String, String> = emptyMap()
) {
    
    protected val client = if (cookies.isEmpty()) {
        NetworkClient.httpClient
    } else {
        NetworkClient.createClientWithCookies(cookies)
    }
    
    /**
     * 获取 HTML 内容
     */
    protected suspend fun fetchHtml(url: String, headers: Map<String, String> = emptyMap()): String {
        return withContext(Dispatchers.IO) {
            val request = Request.Builder()
                .url(url)
                .apply {
                    headers.forEach { (key, value) -> addHeader(key, value) }
                }
                .build()
            
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                throw IOException("请求失败：${response.code} ${response.message}")
            }
            response.body?.string() ?: throw IOException("响应内容为空")
        }
    }
    
    /**
     * 解析 HTML 并提取书籍列表
     */
    abstract fun parseBooks(html: String): List<BookRank>
    
    /**
     * 获取榜单 URL
     */
    abstract fun getRankUrl(page: Int): String
    
    /**
     * 爬取多页数据
     */
    suspend fun fetchAllPages(maxPages: Int = 5): List<BookRank> {
        val allBooks = mutableListOf<BookRank>()
        
        for (page in 1..maxPages) {
            try {
                println("[$platform] 正在爬取第 $page 页...")
                
                val html = fetchHtml(getRankUrl(page))
                val books = parseBooks(html)
                allBooks.addAll(books)
                
                println("[$platform] 第 $page 页爬取成功，获取 ${books.size} 本书")
                
                // 防止被封锁，添加随机延迟
                val delayTime = 1000L + (Random.nextLong() % 2000)
                delay(delayTime)
                
                if (books.isEmpty()) {
                    println("[$platform] 第 $page 页无数据，停止爬取")
                    break
                }
            } catch (e: Exception) {
                println("[$platform] 爬取第 $page 页失败：${e.message}")
                break
            }
        }
        
        return allBooks
    }
    
    /**
     * 获取单个页面
     */
    suspend fun fetchSinglePage(page: Int = 1): List<BookRank> {
        return try {
            val html = fetchHtml(getRankUrl(page))
            parseBooks(html)
        } catch (e: Exception) {
            println("[$platform] 爬取失败：${e.message}")
            emptyList()
        }
    }
    
    companion object {
        protected fun withContext(context: CoroutineContext, block: suspend CoroutineScope.() -> String): String {
            return kotlinx.coroutines.runBlocking {
                kotlinx.coroutines.withContext(context) { block() }
            }
        }
    }
}
