# 📦 项目打包指南

## 当前状态

项目代码已完整创建（40+ 个文件），但需要 Android 开发环境才能打包成 APK。

## 🚀 打包方法

### 方法一：使用 Android Studio（推荐）

#### 1. 安装 Android Studio
下载地址：https://developer.android.com/studio

#### 2. 打开项目
```
File → Open → 选择 AndroidBookRankApp 文件夹
```

#### 3. 等待 Gradle 同步
- Android Studio 会自动下载 Gradle Wrapper
- 首次同步可能需要较长时间（取决于网络）

#### 4. 打包 APK

**Debug 版本（用于测试）：**
```
Build → Build Bundle(s) / APK(s) → Build APK(s)
```

**Release 版本（用于发布）：**
```
Build → Generate Signed Bundle / APK
→ 选择 APK → 输入密钥信息 → 完成
```

#### 5. 找到生成的 APK
```
Debug: app/build/outputs/apk/debug/app-debug.apk
Release: app/build/outputs/apk/release/app-release.apk
```

---

### 方法二：使用命令行（需要配置环境）

#### 1. 安装 Java JDK 17+
下载地址：https://www.oracle.com/java/technologies/downloads/

设置环境变量：
```
JAVA_HOME = C:\Program Files\Java\jdk-17
```

#### 2. 安装 Android SDK
通过 Android Studio 安装，或手动下载：
https://developer.android.com/studio#command-tools

#### 3. 设置环境变量
```
ANDROID_HOME = C:\Users\你的用户名\AppData\Local\Android\Sdk
PATH = %PATH%;%ANDROID_HOME%\platform-tools;%ANDROID_HOME%\tools
```

#### 4. 创建 Gradle Wrapper
在项目根目录执行：
```powershell
# 下载 gradle-wrapper.jar
Invoke-WebRequest -Uri "https://services.gradle.org/distributions/gradle-8.2-bin.zip" -OutFile "gradle.zip"
# 解压后复制 gradle-wrapper.jar 到 gradle/wrapper/ 目录
```

#### 5. 执行打包命令
```powershell
# Debug 版本
.\gradlew.bat assembleDebug

# Release 版本（需要先配置签名）
.\gradlew.bat assembleRelease
```

---

### 方法三：在线构建服务（无需本地环境）

#### 使用 GitHub Actions
1. 将项目上传到 GitHub
2. 创建 `.github/workflows/android.yml`
3. 自动构建并生成 APK

示例配置文件：
```yaml
name: Android CI

on: [push]

jobs:
  build:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v2
    
    - name: set up JDK 17
      uses: actions/setup-java@v2
      with:
        java-version: '17'
        distribution: 'adopt'
    
    - name: Grant execute permission for gradlew
      run: chmod +x gradlew
    
    - name: Build with Gradle
      run: ./gradlew assembleDebug
    
    - name: Upload APK
      uses: actions/upload-artifact@v2
      with:
        name: app-debug
        path: app/build/outputs/apk/debug/app-debug.apk
```

---

## ⚠️ 常见问题

### 1. Gradle 下载慢
**解决方案**：使用国内镜像
修改 `settings.gradle`：
```gradle
buildscript {
    repositories {
        google()
        maven { url 'https://maven.aliyun.com/repository/google' }
        maven { url 'https://maven.aliyun.com/repository/central' }
        mavenCentral()
    }
}
dependencyResolutionManagement {
    repositories {
        google()
        maven { url 'https://maven.aliyun.com/repository/google' }
        maven { url 'https://maven.aliyun.com/repository/central' }
        mavenCentral()
    }
}
```

### 2. 依赖下载失败
**解决方案**：检查网络连接，或配置代理

### 3. SDK 版本不匹配
**解决方案**：
- 安装对应的 Android SDK 版本（SDK 34）
- 或在 `build.gradle` 中降低目标版本

### 4. 内存不足
**解决方案**：修改 `gradle.properties`
```properties
org.gradle.jvmargs=-Xmx2048m -XX:MaxPermSize=512m
```

---

## 📋 打包前检查清单

- [ ] 已安装 Android Studio 或 Android SDK
- [ ] 已安装 JDK 17+
- [ ] 环境变量配置正确
- [ ] Gradle 同步成功
- [ ] 编译无错误
- [ ] 已在真机或模拟器测试通过

---

## 🔐 Release 版本签名配置

### 1. 生成密钥库
```bash
keytool -genkey -v -keystore my-release-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias my-key-alias
```

### 2. 配置签名
在 `app/build.gradle` 添加：
```gradle
android {
    ...
    signingConfigs {
        release {
            storeFile file('my-release-key.jks')
            storePassword 'your-store-password'
            keyAlias 'my-key-alias'
            keyPassword 'your-key-password'
        }
    }
    
    buildTypes {
        release {
            signingConfig signingConfigs.release
            minifyEnabled true
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
}
```

---

## 📱 APK 安装

### 在真机安装
1. 开启开发者选项和 USB 调试
2. 连接电脑
3. 拖动 APK 到手机或执行：
```bash
adb install app-debug.apk
```

### 在模拟器安装
```bash
adb install app-debug.apk
```

---

## 🆘 需要帮助？

如果遇到打包问题，请提供：
1. 错误日志
2. 操作系统版本
3. Android Studio 版本（如果有）
4. 已安装的 SDK 版本

---

**当前项目状态**：✅ 代码已完成，等待构建环境  
**下一步**：安装 Android Studio 并打开项目进行构建
