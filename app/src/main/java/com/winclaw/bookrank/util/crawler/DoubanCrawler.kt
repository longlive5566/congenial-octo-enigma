package com.winclaw.bookrank.util.crawler

import com.winclaw.bookrank.data.model.BookRank
import com.winclaw.bookrank.data.model.PlatformType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.util.Random

/**
 * 豆瓣读书爬虫
 */
class DoubanCrawler(
    cookies: Map<String, String> = emptyMap()
) : BaseCrawler(PlatformType.DOUBAN, cookies) {
    
    override fun getRankUrl(page: Int): String {
        // 豆瓣新书榜
        return "https://book.douban.com/tag/%E6%96%B0%E4%B9%A6?start=${(page - 1) * 20}&type=T"
    }
    
    override fun parseBooks(html: String): List<BookRank> {
        val document = Jsoup.parse(html)
        val books = mutableListOf<BookRank>()
        
        // 豆瓣书籍列表
        document.select(".grid_view .item").forEachIndexed { index, element ->
            try {
                // 书名
                val title = element.select("h2 a").text()
                
                // 作者和出版社
                val infoText = element.select(".info .pub").text()
                val author = extractAuthor(infoText)
                
                // 评分
                val scoreText = element.select(".star .rating_num").text()
                val score = scoreText.toDoubleOrNull()
                
                // 封面
                val coverUrl = element.select("img").attr("src")
                
                // 链接
                val detailUrl = element.select("h2 a").attr("href")
                
                // 评论数
                val commentCount = element.select(".info .pl").text().toIntOrNull()
                
                if (title.isNotEmpty()) {
                    books.add(
                        BookRank(
                            title = title,
                            author = author.ifEmpty { "未知作者" },
                            platform = PlatformType.DOUBAN,
                            rank = index + 1,
                            coverUrl = if (coverUrl.isNotEmpty()) coverUrl else null,
                            score = score,
                            heatValue = commentCount?.toLong(),
                            detailUrl = detailUrl.ifEmpty { null }
                        )
                    )
                }
            } catch (e: Exception) {
                println("[豆瓣] 解析书籍失败：${e.message}")
            }
        }
        
        return books
    }
    
    /**
     * 提取作者（从"作者/出版社"格式中）
     */
    private fun extractAuthor(infoText: String): String {
        return if (infoText.contains("/")) {
            infoText.split("/")[0].trim()
        } else {
            infoText.trim()
        }
    }
}
