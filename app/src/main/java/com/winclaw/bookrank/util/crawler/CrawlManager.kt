package com.winclaw.bookrank.util.crawler

import com.winclaw.bookrank.data.model.BookRank
import com.winclaw.bookrank.data.model.CrawlResult
import com.winclaw.bookrank.data.model.PlatformConfig
import com.winclaw.bookrank.data.model.PlatformType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext

/**
 * 爬虫管理器
 */
class CrawlManager(
    private val platformConfigs: List<PlatformConfig> = PlatformConfig.getDefaultConfigs()
) {
    
    private val crawlers = mapOf(
        PlatformType.QIANDIAN to QidianCrawler(),
        PlatformType.JINJIANG to JjwxCrawler(),
        PlatformType.DOUBAN to DoubanCrawler(),
        PlatformType.FANQIE to FanqieCrawler()
    )
    
    /**
     * 爬取单个平台
     */
    suspend fun crawlPlatform(platform: PlatformType, maxPages: Int = 5): CrawlResult {
        return withContext(Dispatchers.IO) {
            try {
                val config = platformConfigs.find { it.platform == platform } ?: PlatformConfig(platform, "")
                val crawler = crawlers[platform]
                
                if (crawler == null) {
                    CrawlResult(
                        platform = platform,
                        books = emptyList(),
                        success = false,
                        errorMessage = "爬虫未找到"
                    )
                } else {
                    val books = crawler.fetchAllPages(maxPages.coerceAtMost(config.maxPages))
                    
                    CrawlResult(
                        platform = platform,
                        books = books,
                        success = true,
                        pageCount = books.size / config.maxPages.coerceAtLeast(1)
                    )
                }
            } catch (e: Exception) {
                CrawlResult(
                    platform = platform,
                    books = emptyList(),
                    success = false,
                    errorMessage = e.message
                )
            }
        }
    }
    
    /**
     * 同时爬取多个平台
     */
    suspend fun crawlMultiplePlatforms(platforms: List<PlatformType> = PlatformType.values().toList()): List<CrawlResult> {
        return coroutineScope {
            platforms
                .filter { platformConfigs.find { it.platform == it }?.enabled == true }
                .map { platform ->
                    async {
                        crawlPlatform(platform)
                    }
                }
                .awaitAll()
        }
    }
    
    /**
     * 爬取所有启用的平台
     */
    suspend fun crawlAllPlatforms(): List<CrawlResult> {
        val enabledPlatforms = platformConfigs.filter { it.enabled }.map { it.platform }
        return crawlMultiplePlatforms(enabledPlatforms)
    }
    
    /**
     * 更新爬虫配置
     */
    fun updatePlatformConfig(platform: PlatformType, cookies: Map<String, String>) {
        val newCrawler = when (platform) {
            PlatformType.QIANDIAN -> QidianCrawler(cookies)
            PlatformType.JINJIANG -> JjwxCrawler(cookies)
            PlatformType.DOUBAN -> DoubanCrawler(cookies)
            PlatformType.FANQIE -> FanqieCrawler(cookies)
        }
        crawlers.toMutableMap()[platform] = newCrawler
    }
}
