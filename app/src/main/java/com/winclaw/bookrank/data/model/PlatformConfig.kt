package com.winclaw.bookrank.data.model

/**
 * 平台配置信息
 */
data class PlatformConfig(
    val platform: PlatformType,
    val baseUrl: String,
    val enabled: Boolean = true,
    val cookies: Map<String, String> = emptyMap(),
    val headers: Map<String, String> = defaultHeaders(),
    val crawlInterval: Long = 12 * 60 * 60 * 1000L, // 默认 12 小时
    val maxPages: Int = 5
) {
    companion object {
        private fun defaultHeaders(): Map<String, String> {
            return mapOf(
                "User-Agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
                "Accept" to "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8",
                "Accept-Language" to "zh-CN,zh;q=0.9,en;q=0.8",
                "Connection" to "keep-alive",
                "Upgrade-Insecure-Requests" to "1"
            )
        }
        
        fun getDefaultConfigs(): List<PlatformConfig> {
            return listOf(
                PlatformConfig(
                    platform = PlatformType.QIANDIAN,
                    baseUrl = "https://book.qidian.com"
                ),
                PlatformConfig(
                    platform = PlatformType.JINJIANG,
                    baseUrl = "https://www.jjwxc.net",
                    maxPages = 10 // 晋江数据量大，多爬几页
                ),
                PlatformConfig(
                    platform = PlatformType.DOUBAN,
                    baseUrl = "https://book.douban.com"
                ),
                PlatformConfig(
                    platform = PlatformType.FANQIE,
                    baseUrl = "https://www.tamox.cn"
                ),
                PlatformConfig(
                    platform = PlatformType.QIMAO,
                    baseUrl = "https://www.qimao.com"
                )
            )
        }
    }
}

/**
 * 爬取结果
 */
data class CrawlResult(
    val platform: PlatformType,
    val books: List<BookRank>,
    val success: Boolean,
    val errorMessage: String? = null,
    val crawledAt: Long = System.currentTimeMillis(),
    val pageCount: Int = 0
)

/**
 * 统计信息
 */
data class PlatformStats(
    val platform: PlatformType,
    val totalBooks: Int,
    val lastUpdateTime: Long,
    val crawlSuccess: Boolean,
    val averageHeat: Double? = null
)
