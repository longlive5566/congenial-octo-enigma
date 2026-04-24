package com.winclaw.bookrank.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.winclaw.bookrank.data.model.PlatformType

/**
 * 书籍排行榜数据库实体
 */
@Entity(tableName = "book_rank")
data class BookRankEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val title: String,
    val author: String,
    val platformCode: String,
    val rank: Int,
    val coverUrl: String?,
    val description: String?,
    val wordCount: Long?,
    val score: Double?,
    val heatValue: Long?,
    val category: String?,
    val tags: String?, // JSON 格式存储
    val updateTime: Long,
    val detailUrl: String?
)

/**
 * 转换为数据模型
 */
fun BookRankEntity.toBookRank(): com.winclaw.bookrank.data.model.BookRank {
    return com.winclaw.bookrank.data.model.BookRank(
        id = id,
        title = title,
        author = author,
        platform = PlatformType.fromCode(platformCode) ?: PlatformType.QIANDIAN,
        rank = rank,
        coverUrl = coverUrl,
        description = description,
        wordCount = wordCount,
        score = score,
        heatValue = heatValue,
        category = category,
        tags = tags?.let { json ->
            try {
                org.json.JSONArray(json).listToStringList()
            } catch (e: Exception) {
                emptyList()
            }
        },
        updateTime = updateTime,
        detailUrl = detailUrl
    )
}

/**
 * 从数据模型转换
 */
fun com.winclaw.bookrank.data.model.BookRank.toEntity(): BookRankEntity {
    return BookRankEntity(
        id = id,
        title = title,
        author = author,
        platformCode = platform.platformCode,
        rank = rank,
        coverUrl = coverUrl,
        description = description,
        wordCount = wordCount,
        score = score,
        heatValue = heatValue,
        category = category,
        tags = tags?.let { org.json.JSONArray(it).toString() },
        updateTime = updateTime,
        detailUrl = detailUrl
    )
}

/**
 * JSONArray 扩展函数
 */
fun org.json.JSONArray.listToStringList(): List<String> {
    val list = mutableListOf<String>()
    for (i in 0 until length()) {
        list.add(optString(i))
    }
    return list
}
