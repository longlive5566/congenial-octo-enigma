package com.winclaw.bookrank.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * 书籍排行榜数据访问对象
 */
@Dao
interface BookRankDao {
    
    @Query("SELECT * FROM book_ranks WHERE platformCode = :platformCode ORDER BY rank ASC")
    fun getRanksByPlatform(platformCode: String): Flow<List<BookRankEntity>>
    
    @Query("SELECT * FROM book_ranks ORDER BY updateTime DESC LIMIT :limit")
    fun getHotRanks(limit: Int = 20): Flow<List<BookRankEntity>>
    
    @Query("SELECT * FROM book_ranks WHERE title LIKE :query OR author LIKE :query")
    fun searchBooks(query: String): Flow<List<BookRankEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(books: List<BookRankEntity>)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(book: BookRankEntity)
    
    @Update
    suspend fun update(book: BookRankEntity)
    
    @Delete
    suspend fun delete(book: BookRankEntity)
    
    @Query("DELETE FROM book_ranks WHERE platformCode = :platformCode")
    suspend fun deleteByPlatform(platformCode: String)
    
    @Query("DELETE FROM book_ranks WHERE updateTime < :olderThan")
    suspend fun deleteOldData(olderThan: Long)
    
    @Query("SELECT DISTINCT platformCode FROM book_ranks")
    fun getAllPlatforms(): Flow<List<String>>
    
    @Query("SELECT COUNT(*) FROM book_ranks")
    fun getTotalCount(): Flow<Int>
}
