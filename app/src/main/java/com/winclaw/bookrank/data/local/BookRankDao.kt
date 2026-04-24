package com.winclaw.bookrank.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * 书籍排行榜数据访问对象
 */
@Dao
interface BookRankDao {
    
    /**
     * 插入或更新书籍记录
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(books: List<BookRankEntity>)
    
    /**
     * 插入单条记录
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(book: BookRankEntity)
    
    /**
     * 根据平台获取排行榜（按排名排序）
     */
    @Query("SELECT * FROM book_rank WHERE platformCode = :platformCode ORDER BY rank ASC")
    fun getRanksByPlatform(platformCode: String): Flow<List<BookRankEntity>>
    
    /**
     * 根据平台获取排行榜（一次性查询）
     */
    @Query("SELECT * FROM book_rank WHERE platformCode = :platformCode ORDER BY rank ASC")
    suspend fun getRanksByPlatformSync(platformCode: String): List<BookRankEntity>
    
    /**
     * 获取所有平台的最新排行榜
     */
    @Query("SELECT * FROM book_rank ORDER BY updateTime DESC, rank ASC LIMIT :limit")
    fun getRecentRanks(limit: Int = 100): Flow<List<BookRankEntity>>
    
    /**
     * 跨平台热门书籍（按热度排序）
     */
    @Query("""
        SELECT * FROM book_rank 
        WHERE heatValue IS NOT NULL 
        ORDER BY heatValue DESC 
        LIMIT :limit
    """)
    fun getHotRanks(limit: Int = 10): Flow<List<BookRankEntity>>
    
    /**
     * 搜索书籍
     */
    @Query("""
        SELECT * FROM book_rank 
        WHERE title LIKE :keyword OR author LIKE :keyword 
        ORDER BY updateTime DESC
    """)
    fun searchBooks(keyword: String): Flow<List<BookRankEntity>>
    
    /**
     * 根据 ID 获取书籍
     */
    @Query("SELECT * FROM book_rank WHERE id = :id")
    suspend fun getBookById(id: Long): BookRankEntity?
    
    /**
     * 删除旧数据（超过指定时间的）
     */
    @Query("DELETE FROM book_rank WHERE updateTime < :timestamp")
    suspend fun deleteOldData(timestamp: Long)
    
    /**
     * 删除某个平台的所有数据
     */
    @Query("DELETE FROM book_rank WHERE platformCode = :platformCode")
    suspend fun deleteByPlatform(platformCode: String)
    
    /**
     * 清空所有数据
     */
    @Query("DELETE FROM book_rank")
    suspend fun deleteAll()
    
    /**
     * 获取书籍总数
     */
    @Query("SELECT COUNT(*) FROM book_rank")
    fun getTotalCount(): Flow<Int>
    
    /**
     * 获取各平台书籍数量
     */
    @Query("SELECT platformCode, COUNT(*) as count FROM book_rank GROUP BY platformCode")
    fun getPlatformCounts(): Flow<List<PlatformCount>>
    
    /**
     * 获取最后更新时间
     */
    @Query("SELECT MAX(updateTime) FROM book_rank WHERE platformCode = :platformCode")
    suspend fun getLastUpdateTime(platformCode: String): Long?
}

/**
 * 平台计数
 */
@Entity(tableName = "platform_count", primaryKeys = ["platformCode"])
data class PlatformCount(
    val platformCode: String,
    val count: Int
)
