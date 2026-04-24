# 📚 书籍排行榜聚合 App

[![Android CI](https://github.com/your-username/AndroidBookRankApp/actions/workflows/android.yml/badge.svg)](https://github.com/your-username/AndroidBookRankApp/actions/workflows/android.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

一个能够自动搜索和聚合各大阅读平台书籍排行榜的安卓应用。

## 🚀 快速开始

### 下载 APK
- [下载 Debug 版本](https://github.com/your-username/AndroidBookRankApp/releases/latest)
- [查看构建历史](https://github.com/your-username/AndroidBookRankApp/actions)

### 一键构建
本项目支持 GitHub Actions 自动构建，推送到仓库即可自动生成 APK！
👉 [查看构建指南](GITHUB_ACTIONS_GUIDE.md)

## 🎯 功能特性

- ✅ **多平台支持**：起点中文网、晋江文学城、豆瓣读书、番茄小说
- ✅ **自动爬取**：定时自动更新排行榜数据
- ✅ **跨平台对比**：查看不同平台的热门书籍
- ✅ **搜索功能**：按书名/作者搜索
- ✅ **数据展示**：排名、热度、评分、字数等信息
- ✅ **后台更新**：使用 WorkManager 定时任务

## 🏗️ 技术架构

- **开发语言**: Kotlin
- **架构模式**: MVVM + Repository
- **网络请求**: Retrofit + OkHttp
- **HTML 解析**: Jsoup
- **本地存储**: Room Database
- **后台任务**: WorkManager
- **异步处理**: Coroutine + Flow
- **UI 组件**: Material Design

## 📦 项目结构

```
app/
├── data/
│   ├── local/           # Room 数据库层
│   │   ├── AppDatabase.kt
│   │   ├── BookRankDao.kt
│   │   └── BookRankEntity.kt
│   ├── model/           # 数据模型
│   │   ├── BookRank.kt
│   │   └── PlatformConfig.kt
│   └── worker/          # 后台任务
│       └── RankUpdateWorker.kt
├── util/
│   ├── crawler/         # 爬虫实现
│   │   ├── BaseCrawler.kt
│   │   ├── QidianCrawler.kt
│   │   ├── JjwxCrawler.kt
│   │   ├── DoubanCrawler.kt
│   │   ├── FanqieCrawler.kt
│   │   └── CrawlManager.kt
│   └── network/         # 网络工具
│       └── NetworkClient.kt
├── ui/
│   ├── adapter/         # RecyclerView 适配器
│   │   └── BookRankAdapter.kt
│   ├── viewmodel/       # ViewModel
│   │   └── MainViewModel.kt
│   └── MainActivity.kt
└── BookRankApplication.kt
```

## 🚀 快速开始

### 前置要求

- Android Studio Arctic Fox 或更高版本
- JDK 11 或更高版本
- Android SDK 24+

### 构建步骤

1. **克隆项目**
   ```bash
   git clone <repository-url>
   cd AndroidBookRankApp
   ```

2. **打开项目**
   - 在 Android Studio 中打开项目文件夹

3. **同步 Gradle**
   - File → Sync Project with Gradle Files

4. **运行应用**
   - 连接 Android 设备或启动模拟器
   - 点击 Run 按钮

## ⚙️ 配置说明

### Cookie 配置

某些平台（如晋江）需要登录后才能获取完整数据：

1. 在浏览器中登录目标平台
2. 打开开发者工具（F12）
3. 找到请求头中的 Cookie
4. 在 App 设置中粘贴 Cookie

### 定时任务

默认每 12 小时自动更新一次排行榜，可在设置中调整。

## 📱 截图

（此处可添加应用截图）

## 🛠️ 开发指南

### 添加新平台

1. 在 `PlatformType.kt` 中添加新的平台枚举
2. 创建对应的 Crawler 类继承 `BaseCrawler`
3. 在 `CrawlManager.kt` 中注册爬虫
4. 实现 `getRankUrl()` 和 `parseBooks()` 方法

### 修改选择器

各平台的 HTML 结构可能会变化，需要更新 Jsoup 选择器：

```kotlin
// 示例：更新起点爬虫选择器
document.select(".new-selector").forEach { element ->
    // 解析逻辑
}
```

## ⚠️ 注意事项

1. **反爬虫策略**：
   - 设置合理的请求间隔（1-3 秒）
   - 使用真实的 User-Agent
   - 必要时使用 Cookie

2. **法律合规**：
   - 仅用于个人学习研究
   - 不存储和传播 copyrighted 内容
   - 仅展示书籍元数据

3. **性能优化**：
   - 使用缓存减少重复请求
   - 分页加载数据
   - 图片懒加载

## 📄 许可证

本项目仅供学习研究使用。

## 🤝 贡献

欢迎提交 Issue 和 Pull Request！

## 📧 联系方式

如有问题，请提交 Issue 或联系开发者。

---

**开发时间**: 2026 年 4 月  
**版本**: 1.0.0
