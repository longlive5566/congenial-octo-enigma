package com.winclaw.bookrank

import android.app.Application

/**
 * 应用入口
 */
class BookRankApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        // 初始化数据库
        val database = data.local.AppDatabase.getDatabase(this)
    }
}
