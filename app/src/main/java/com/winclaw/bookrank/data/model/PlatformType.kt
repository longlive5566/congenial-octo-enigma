package com.winclaw.bookrank.data.model

/**
 * 平台类型枚举
 */
enum class PlatformType(
    val platformCode: String,
    val displayName: String
) {
    QIANDIAN("qidian", "起点中文网"),
    JINJIANG("jjwxc", "晋江文学城"),
    DOUBAN("douban", "豆瓣读书"),
    FANQIE("fanqie", "番茄小说"),
    QIMAO("qimao", "七猫小说");
    
    companion object {
        fun fromCode(code: String): PlatformType? {
            return values().find { it.platformCode == code }
        }
    }
}
