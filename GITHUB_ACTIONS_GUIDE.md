# 🚀 GitHub Actions 快速开始指南

## ✅ 已完成

我已经为你创建了完整的 GitHub Actions 配置文件：

- `.github/workflows/android.yml` - 自动构建工作流
- `gradlew` - Linux 版 Gradle Wrapper 脚本
- `gradle/wrapper/gradle-wrapper.properties` - Gradle 配置

## 📋 使用步骤

### 1. 创建 GitHub 仓库

#### 方式 A：使用网页界面
1. 访问 https://github.com/new
2. 仓库名称：例如 `AndroidBookRankApp`
3. 设为公开或私有（均可）
4. 点击 "Create repository"

#### 方式 B：使用 Git 命令
```bash
cd C:\Users\Administrator\AppData\Roaming\winclaw\.openclaw\workspace\AndroidBookRankApp

# 初始化 Git
git init

# 添加远程仓库（替换为你的仓库 URL）
git remote add origin https://github.com/你的用户名/AndroidBookRankApp.git

# 提交所有文件
git add .
git commit -m "Initial commit: Android Book Rank App"

# 推送到 GitHub
git push -u origin main
```

---

### 2. 推送项目到 GitHub

```bash
# 进入项目目录
cd C:\Users\Administrator\AppData\Roaming\winclaw\.openclaw\workspace\AndroidBookRankApp

# 初始化 Git（如果还没初始化）
git init

# 添加所有文件
git add .

# 提交
git commit -m "Initial commit: Android Book Rank App"

# 添加远程仓库
git remote add origin https://github.com/你的用户名/你的仓库名.git

# 推送
git push -u origin main
```

---

### 3. 等待自动构建

推送成功后，GitHub Actions 会自动触发构建：

1. 访问你的仓库页面
2. 点击 "Actions" 标签
3. 查看构建进度
4. 等待构建完成（约 5-10 分钟）

---

### 4. 下载 APK

构建完成后：

1. 在 Actions 页面点击最近的运行记录
2. 找到 "Artifacts" 部分
3. 下载 `app-debug` 或 `app-release`
4. 解压得到 APK 文件

---

## 🎯 构建触发方式

### 自动触发
- ✅ 推送代码到 main/master 分支
- ✅ 创建 Pull Request

### 手动触发
1. 进入 Actions 标签
2. 选择 "Android APK Build"
3. 点击 "Run workflow"
4. 选择分支并点击 "Run workflow"

---

## 📊 构建流程说明

```yaml
1. Checkout code        # 拉取代码
2. Set up JDK 17       # 配置 Java 环境
3. Cache Gradle        # 缓存依赖（加速构建）
4. Setup Android SDK   # 配置 Android SDK
5. Build Debug APK     # 构建 Debug 版本
6. Build Release APK   # 构建 Release 版本（可选）
7. Upload Artifacts    # 上传 APK 文件
```

---

## 🔧 自定义配置

### 修改构建分支
```yaml
on:
  push:
    branches: [ main, develop ]  # 添加更多分支
```

### 添加构建通知
```yaml
- name: Notify on Slack
  uses: 8398a7/action-slack@v3
  with:
    status: ${{ job.status }}
  if: always()
```

### 发布到 Release
```yaml
- name: Create Release
  uses: ncipollo/release-action@v1
  with:
    artifacts: "app/build/outputs/apk/**/*.apk"
    tag: v1.0.0
    token: ${{ secrets.GITHUB_TOKEN }}
```

---

## ⚠️ 注意事项

### 1. 首次构建较慢
- Gradle 依赖下载需要时间（5-15 分钟）
- 后续构建会更快（使用缓存）

### 2. Release 版本需要签名
- Debug 版本可直接使用
- Release 版本需要配置签名密钥
- 暂时未配置会跳过 Release 构建

### 3. 免费额度
- 公开仓库：每月 2000 分钟免费构建
- 私有仓库：每月 500 分钟免费构建

---

## 🐛 常见问题

### Q: 构建失败怎么办？
**A**: 点击构建记录查看日志，常见错误：
- 依赖下载失败 → 检查网络或配置镜像
- 编译错误 → 检查代码语法
- 内存不足 → 调整 Gradle 配置

### Q: 如何查看构建日志？
**A**: Actions → 点击构建记录 → 查看各步骤日志

### Q: APK 在哪里下载？
**A**: 构建完成后，在 "Artifacts" 部分下载

### Q: 如何重新构建？
**A**: 
- 推送新代码自动触发
- 或手动点击 "Run workflow"

---

## 📱 APK 安装

### Android 手机安装
1. 下载 APK 文件
2. 传输到手机
3. 允许"安装未知应用"权限
4. 点击 APK 安装

### 使用 ADB 安装
```bash
adb install app-debug.apk
```

---

## 🎉 完成！

现在你可以：
1. 将项目推送到 GitHub
2. 等待自动构建
3. 下载 APK 并安装

需要我帮你配置其他功能吗？例如：
- 自动发布 Release
- Slack/Discord 通知
- 代码质量检查
- 单元测试运行

告诉我你的需求！ 😊
