package com.winclaw.bookrank.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

/**
 * 类型转换器
 */
@TypeConverters
object Converters {
    
    @androidx.room.TypeConverter
    fun fromLongList(value: List<Long>?): String? {
        return value?.joinToString(",")
    }
    
    @androidx.room.TypeConverter
    fun toLongList(value: String?): List<Long>? {
        return value?.split(",")?.mapNotNull { it.toLongOrNull() }
    }
    
    @androidx.room.TypeConverter
    fun fromStringList(value: List<String>?): String? {
        return value?.joinToString("|")
    }
    
    @androidx.room.TypeConverter
    fun toStringList(value: String?): List<String>? {
        return value?.split("|")?.filter { it.isNotEmpty() }
    }
}

/**
 * 应用数据库
 */
@Database(
    entities = [BookRankEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun bookRankDao(): BookRankDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }
        
        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "book_rank_database"
            )
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}

/**
 * 数据库仓库
 */
class BookRankRepository(private val database: AppDatabase) {
    
    private val dao = database.bookRankDao()
    
    suspend fun insertAll(books: List<com.winclaw.bookrank.data.model.BookRank>) {
        val entities = books.map { it.toEntity() }
        dao.insertAll(entities)
    }
    
    fun getRanksByPlatform(platformCode: String) = dao.getRanksByPlatform(platformCode)
    
    suspend fun getRanksByPlatformSync(platformCode: String) = 
        dao.getRanksByPlatformSync(platformCode).map { it.toBookRank() }
    
    fun getRecentRanks(limit: Int = 100) = dao.getRecentRanks(limit).map { list ->
        list.map { it.toBookRank() }
    }
    
    fun getHotRanks(limit: Int = 10) = dao.getHotRanks(limit).map { list ->
        list.map { it.toBookRank() }
    }
    
    fun searchBooks(keyword: String) = dao.searchBooks("%$keyword%").map { list ->
        list.map { it.toBookRank() }
    }
    
    suspend fun getBookById(id: Long) = dao.getBookById(id)?.toBookRank()
    
    suspend fun deleteOldData(timestamp: Long) {
        dao.deleteOldData(timestamp)
    }
    
    suspend fun deleteByPlatform(platformCode: String) {
        dao.deleteByPlatform(platformCode)
    }
    
    suspend fun deleteAll() {
        dao.deleteAll()
    }
    
    fun getTotalCount() = dao.getTotalCount()
    
    suspend fun getLastUpdateTime(platformCode: String) = dao.getLastUpdateTime(platformCode)
    
    /**
     * 清除 7 天前的旧数据
     */
    suspend fun cleanOldData() {
        val sevenDaysAgo = System.currentTimeMillis() - 7L * 24 * 60 * 60 * 1000
        deleteOldData(sevenDaysAgo)
    }
}
