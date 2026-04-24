package com.winclaw.bookrank.util.crawler

import com.winclaw.bookrank.data.model.BookRank
import com.winclaw.bookrank.data.model.PlatformType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.util.Random

/**
 * 起点中文网爬虫
 */
class QidianCrawler(
    cookies: Map<String, String> = emptyMap()
) : BaseCrawler(PlatformType.QIANDIAN, cookies) {
    
    override fun getRankUrl(page: Int): String {
        return "https://book.qidian.com/rank/all?page=$page"
    }
    
    override fun parseBooks(html: String): List<BookRank> {
        val document = Jsoup.parse(html)
        val books = mutableListOf<BookRank>()
        
        // 起点书籍列表选择器
        document.select(".book-mid-info").forEachIndexed { index, element ->
            try {
                val title = element.select("h2 a").text()
                val author = element.select(".author").text()
                val coverUrl = element.select("img").attr("src")
                val detailUrl = element.select("h2 a").attr("href")
                
                // 提取热度值（月票数）
                val heatText = element.select(".heat").text()
                val heatValue = extractNumber(heatText)
                
                // 提取字数
                val wordText = element.select(".info .gray").text()
                val wordCount = extractWordCount(wordText)
                
                // 提取分类
                val category = element.select(".tag").text()
                
                if (title.isNotEmpty() && author.isNotEmpty()) {
                    books.add(
                        BookRank(
                            title = title,
                            author = author,
                            platform = PlatformType.QIANDIAN,
                            rank = index + 1,
                            coverUrl = if (coverUrl.isNotEmpty()) coverUrl else null,
                            wordCount = wordCount,
                            heatValue = heatValue,
                            category = category.ifEmpty { null },
                            detailUrl = detailUrl.ifEmpty { null }
                        )
                    )
                }
            } catch (e: Exception) {
                println("[起点] 解析书籍失败：${e.message}")
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
