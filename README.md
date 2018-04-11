# EXOPlayer视频播放器
## 1.gradle
### Step 1. Add the JitPack repository to your build file
Add it in your root build.gradle at the end of repositories:
```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
        maven { url "https://maven.google.com" }
    }
}
```
### Step 2. Add the dependency
```
dependencies {
    compile 'com.github.xieguangwei:MyCustomExoPlayer:1.0'
}
```
### 感兴趣的可以下载demo看下