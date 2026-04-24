package com.winclaw.bookrank.data.model

/**
 * 爬取结果
 */
data class CrawlResult(
    val platform: PlatformType,
    val books: List<BookRank>,
    val success: Boolean,
    val errorMessage: String? = null,
    val pageCount: Int = 0
)

/**
 * 平台配置
 */
data class PlatformConfig(
    val platform: PlatformType,
    val cookies: String = "",
    val enabled: Boolean = true,
    val maxPages: Int = 5,
    val crawlInterval: Long = 12 // 小时
) {
    companion object {
        fun getDefaultConfigs(): List<PlatformConfig> {
            return PlatformType.values().map { PlatformConfig(it) }
        }
    }
}
