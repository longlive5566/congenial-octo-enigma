package com.winclaw.bookrank.data.model

/**
 * 书籍排行榜数据模型
 */
data class BookRank(
    val id: Long = 0,
    val title: String,                    // 书名
    val author: String,                   // 作者
    val platform: PlatformType,           // 平台类型
    val rank: Int,                        // 排名
    val coverUrl: String? = null,         // 封面 URL
    val description: String? = null,      // 简介
    val wordCount: Long? = null,          // 字数
    val score: Double? = null,            // 评分
    val heatValue: Long? = null,          // 热度值 (月票/收藏/热度等)
    val category: String? = null,         // 分类
    val tags: List<String>? = null,       // 标签
    val updateTime: Long = System.currentTimeMillis(),
    val detailUrl: String? = null         // 详情页 URL
)

/**
 * 平台类型枚举
 */
enum class PlatformType(val displayName: String, val platformCode: String) {
    QIANDIAN("起点中文网", "qidian"),
    JINJIANG("晋江文学城", "jjwxc"),
    DOUBAN("豆瓣读书", "douban"),
    FANQIE("番茄小说", "fanqie"),
    QIMAO("七猫小说", "qimao");
    
    companion object {
        fun fromCode(code: String): PlatformType? {
            return values().find { it.platformCode == code }
        }
    }
}

/**
 * 榜单类型
 */
enum class RankType(val displayName: String) {
    MONTHLY("月票榜"),
    RECOMMEND("推荐榜"),
    NEW_BOOK("新书榜"),
    COMPLETED("完结榜"),
    COLLECTION("收藏榜"),
    HOT("热度榜"),
    SCORE("评分榜"),
    ALL("全部榜单")
}

/**
 * 爬取任务状态
 */
enum class CrawlStatus {
    IDLE,           // 空闲
    RUNNING,        // 运行中
    COMPLETED,      // 完成
    FAILED,         // 失败
    PARTIAL         // 部分成功
}
