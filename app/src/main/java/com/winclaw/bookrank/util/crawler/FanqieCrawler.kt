package com.winclaw.bookrank.util.crawler

import com.winclaw.bookrank.data.model.BookRank
import com.winclaw.bookrank.data.model.PlatformType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.util.Random

/**
 * 番茄小说爬虫
 */
class FanqieCrawler(
    cookies: Map<String, String> = emptyMap()
) : BaseCrawler(PlatformType.FANQIE, cookies) {
    
    override fun getRankUrl(page: Int): String {
        // 番茄热度榜
        return "https://www.tamox.cn/rank/hot?page=$page"
    }
    
    override fun parseBooks(html: String): List<BookRank> {
        val document = Jsoup.parse(html)
        val books = mutableListOf<BookRank>()
        
        // 番茄书籍列表（选择器可能需要根据实际页面调整）
        document.select(".book-item, .book-list li, .book").forEachIndexed { index, element ->
            try {
                // 书名
                val title = element.select("h3 a, .book-title a, .title a").text()
                
                // 作者
                val author = element.select(".author, .book-author, .author-name").text()
                
                // 热度值
                val heatText = element.select(".heat, .hot-value, .read-count").text()
                val heatValue = extractNumber(heatText)
                
                // 字数
                val wordText = element.select(".word-count, .count").text()
                val wordCount = extractWordCount(wordText)
                
                // 封面
                val coverUrl = element.select("img").attr("src")
                
                // 链接
                val detailUrl = element.select("h3 a, .book-title a, .title a").attr("href")
                
                if (title.isNotEmpty() && author.isNotEmpty()) {
                    books.add(
                        BookRank(
                            title = title,
                            author = author,
                            platform = PlatformType.FANQIE,
                            rank = index + 1,
                            coverUrl = if (coverUrl.isNotEmpty()) coverUrl else null,
                            wordCount = wordCount,
                            heatValue = heatValue,
                            detailUrl = detailUrl.ifEmpty { null }
                        )
                    )
                }
            } catch (e: Exception) {
                println("[番茄] 解析书籍失败：${e.message}")
            }
        }
        
        // 如果标准选择器没找到，尝试备用方案
        if (books.isEmpty()) {
            books.addAll(parseFallback(html))
        }
        
        return books
    }
    
    /**
     * 备用解析方案
     */
    private fun parseFallback(html: String): List<BookRank> {
        val books = mutableListOf<BookRank>()
        val document = Jsoup.parse(html)
        
        // 尝试查找包含书籍信息的 div
        document.select("div[class*='book'], li[class*='book']").forEachIndexed { index, element ->
            try {
                val title = element.select("a").first()?.text() ?: return@forEach
                val author = element.text().substringAfter("作者：").substringBefore("\n").takeIf { it.length < 50 } ?: "未知作者"
                
                books.add(
                    BookRank(
                        title = title,
                        author = author,
                        platform = PlatformType.FANQIE,
                        rank = index + 1
                    )
                )
            } catch (e: Exception) {
                // 忽略
            }
        }
        
        return books
    }
    
    /**
     * 提取数字
     */
    private fun extractNumber(text: String): Long? {
        return text.replace(Regex("[^0-9]"), "").toLongOrNull()
    }
    
    /**
     * 提取字数
     */
    private fun extractWordCount(text: String): Long? {
        val match = Regex("(\\d+(?:\\.\\d+)?)\\s*万").find(text)
        return match?.groupValues?.get(1)?.toDoubleOrNull()?.toLong()?.times(10000)
    }
}
