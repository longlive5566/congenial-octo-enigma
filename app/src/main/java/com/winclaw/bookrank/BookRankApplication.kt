package com.winclaw.bookrank

import android.app.Application
import com.winclaw.bookrank.data.worker.RankScheduler

/**
 * 应用入口
 */
class BookRankApplication : Application() {
    
    companion object {
        lateinit var instance: BookRankApplication
            private set
    }
    
    override fun onCreate() {
        super.onCreate()
        instance = this
        
        // 初始化定时任务（可选）
        // RankScheduler(this).schedulePeriodicUpdate()
    }
}
