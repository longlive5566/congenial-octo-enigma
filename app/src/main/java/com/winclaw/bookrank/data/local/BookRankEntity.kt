package com.winclaw.bookrank.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 书籍排行榜数据库实体
 */
@Entity(tableName = "book_ranks")
data class BookRankEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val author: String,
    val platformCode: String,
    val rank: Int,
    val coverUrl: String? = null,
    val description: String? = null,
    val category: String? = null,
    val wordCount: String? = null,
    val heatValue: String? = null,
    val score: Double? = null,
    val detailUrl: String? = null,
    val updateTime: Long = System.currentTimeMillis()
)
