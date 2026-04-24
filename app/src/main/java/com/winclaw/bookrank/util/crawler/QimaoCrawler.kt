package com.winclaw.bookrank.util.crawler

import com.winclaw.bookrank.data.model.BookRank
import com.winclaw.bookrank.data.model.PlatformType
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

/**
 * 七猫中文网爬虫
 */
class QimaoCrawler(
    cookies: Map<String, String> = emptyMap()
) : BaseCrawler(PlatformType.QIMAO, cookies) {
    
    override fun getRankUrl(page: Int): String {
        return "https://www.qimao.com/shuku/rank/"
    }
    
    override fun parseBooks(html: String): List<BookRank> {
        val doc: Document = Jsoup.parse(html)
        val books = mutableListOf<BookRank>()
        
        // 七猫的书籍列表选择器需要根据实际页面调整
        doc.select(".book-item").eachAttr("data-book-id").forEachIndexed { index, bookId ->
            try {
                val title = doc.select(".book-title").eq(index).text()
                val author = doc.select(".book-author").eq(index).text()
                val cover = doc.select(".book-cover img").eq(index).attr("src")
                val rank = index + 1
                
                if (title.isNotEmpty()) {
                    books.add(
                        BookRank(
                            id = "$platform-$bookId",
                            title = title,
                            author = author,
                            coverUrl = cover,
                            rank = rank,
                            platform = platform.platformCode,
                            tags = emptyList(),
                            description = "",
                            score = 0.0,
                            updateTime = System.currentTimeMillis()
                        )
                    )
                }
            } catch (e: Exception) {
                // 跳过解析失败的书籍
            }
        }
        
        return books
    }
}
