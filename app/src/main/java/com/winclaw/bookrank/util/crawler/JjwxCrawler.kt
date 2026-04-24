package com.winclaw.bookrank.util.crawler

import com.winclaw.bookrank.data.model.BookRank
import com.winclaw.bookrank.data.model.PlatformType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.util.Random

/**
 * 晋江文学城爬虫
 */
class JjwxCrawler(
    cookies: Map<String, String> = emptyMap()
) : BaseCrawler(PlatformType.JINJIANG, cookies) {
    
    override fun getRankUrl(page: Int): String {
        // 收藏榜 URL，可以修改参数获取不同榜单
        return "https://www.jjwxc.net/bookbase.php?fw0=0&fbsj0=0&ycx0=0&xx0=0&mainview0=0&sd0=0&lx0=0&fg0=0&bq=-1&sortType=4&isfinish=0&collectiontypes=ors&page=$page"
    }
    
    override fun parseBooks(html: String): List<BookRank> {
        val document = Jsoup.parse(html)
        val books = mutableListOf<BookRank>()
        
        // 跳过表头，从第一行数据开始
        document.select("table tr").drop(1).forEachIndexed { index, row ->
            try {
                val cells = row.select("td")
                if (cells.size >= 10) {
                    // 作者
                    val author = cells[0].select("a").text()
                    
                    // 书名和链接
                    val titleElement = cells[1].select("a")
                    val title = titleElement.text()
                    val detailUrl = titleElement.attr("href")
                    
                    // 类型和风格
                    val type = cells[2].text().trim()
                    val style = cells[3].text().trim()
                    
                    // 进度
                    val progress = cells[4].text().trim()
                    
                    // 字数（需要处理格式）
                    val wordCount = cells[7].text().trim().toLongOrNull()
                    
                    // 积分/热度
                    val heatValue = cells[8].text().trim().toLongOrNull()
                    
                    // 发表时间
                    val publishTime = cells[9].text().trim()
                    
                    if (title.isNotEmpty() && author.isNotEmpty()) {
                        books.add(
                            BookRank(
                                title = title,
                                author = author,
                                platform = PlatformType.JINJIANG,
                                rank = index + 1,
                                description = "$type - $style",
                                category = type.ifEmpty { null },
                                wordCount = wordCount,
                                heatValue = heatValue,
                                detailUrl = if (detailUrl.startsWith("http")) detailUrl else "https://www.jjwxc.net/$detailUrl"
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                println("[晋江] 解析书籍失败：${e.message}")
            }
        }
        
        return books
    }
}
