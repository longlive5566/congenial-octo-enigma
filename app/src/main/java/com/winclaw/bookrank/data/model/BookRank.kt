package com.winclaw.bookrank.data.model

/**
 * 书籍排行榜数据模型
 */
data class BookRank(
    val id: String,
    val title: String,
    val author: String,
    val platform: PlatformType,
    val rank: Int,
    val coverUrl: String? = null,
    val description: String? = null,
    val category: String? = null,
    val wordCount: String? = null,
    val heatValue: String? = null,
    val score: Double? = null,
    val detailUrl: String? = null,
    val tags: List<String>? = null,
    val updateTime: Long = System.currentTimeMillis()
)

/**
 * 平台类型枚举
 */
enum class PlatformType(val code: String, val name: String) {
    QIANDIAN("qidian", "起点中文网"),
    JJWXC("jjwxc", "晋江文学城"),
    DOUBAN("douban", "豆瓣读书"),
    FANQIE("fanqie", "番茄小说"),
    QIMAO("qimao", "七猫小说");
    
    companion object {
        fun fromCode(code: String): PlatformType? {
            return values().find { it.code == code }
        }
    }
}
