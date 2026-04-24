package com.winclaw.bookrank.data.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.winclaw.bookrank.data.local.AppDatabase
import com.winclaw.bookrank.data.local.BookRank
import com.winclaw.bookrank.data.model.CrawlResult
import com.winclaw.bookrank.data.model.PlatformType
import com.winclaw.bookrank.util.crawler.CrawlManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

/**
 * 排行榜更新 Worker
 */
class RankUpdateWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    
    private val crawlManager = CrawlManager()
    private val database = AppDatabase.getInstance(applicationContext)
    private val dao = database.bookRankDao()
    
    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                println("开始执行排行榜更新任务...")
                
                // 爬取所有平台
                val results = crawlManager.crawlAllPlatforms()
                
                // 统计结果
                var totalBooks = 0
                var successCount = 0
                
                results.forEach { result ->
                    if (result.success) {
                        totalBooks += result.books.size
                        successCount++
                        
                        // 保存数据
                        val entities = result.books.map { it.toEntity() }
                        dao.insertAll(entities)
                        
                        println("平台 ${result.platform.displayName}: 成功爬取 ${result.books.size} 本书")
                    } else {
                        println("平台 ${result.platform.displayName}: 爬取失败 - ${result.errorMessage}")
                    }
                }
                
                // 清理旧数据（7 天前）
                val sevenDaysAgo = System.currentTimeMillis() - 7L * 24 * 60 * 60 * 1000
                dao.deleteOldData(sevenDaysAgo)
                
                println("排行榜更新完成：共 ${totalBooks} 本书，${successCount}/${results.size} 平台成功")
                
                Result.success()
            } catch (e: Exception) {
                println("排行榜更新任务失败：${e.message}")
                e.printStackTrace()
                Result.retry()
            }
        }
    }
}

/**
 * 排行榜调度器
 */
class RankScheduler(private val context: Context) {
    
    private val workManager = WorkManager.getInstance(context)
    
    companion object {
        private const val WORK_TAG = "book_rank_update"
        private const val WORK_NAME = "BookRankUpdateWorker"
    }
    
    /**
     * 设置定时更新任务（每 12 小时）
     */
    fun schedulePeriodicUpdate() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(false)
            .build()
        
        val request = PeriodicWorkRequestBuilder<RankUpdateWorker>(
            12, TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .setInitialDelay(1, TimeUnit.HOURS) // 初始延迟 1 小时
            .addTag(WORK_TAG)
            .setName(WORK_NAME)
            .build()
        
        workManager.enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
        
        println("定时任务已设置：每 12 小时更新一次排行榜")
    }
    
    /**
     * 立即执行一次更新
     */
    fun triggerImmediateUpdate() {
        val request = androidx.work.OneTimeWorkRequestBuilder<RankUpdateWorker>()
            .addTag(WORK_TAG)
            .build()
        
        workManager.enqueue(request)
        println("立即更新任务已触发")
    }
    
    /**
     * 取消所有定时任务
     */
    fun cancelAllUpdates() {
        workManager.cancelAllWorkByTag(WORK_TAG)
        println("定时任务已取消")
    }
    
    /**
     * 检查任务状态
     */
    fun getWorkStatus() = workManager.getWorkInfosForUniqueWorkFlow(WORK_NAME)
}
