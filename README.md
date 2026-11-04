# 去上班（goToWork）

一个用于查看工作日收入进度的 Android 小应用。用户配置月薪、每月工作天数、上下班时间、午休时间和发薪日后，主界面会根据当前时间持续更新当日收入估算、工作进度和下班倒计时。

## 功能

- 显示距离下班的倒计时；下班后显示“已下班”。
- 按已工作的秒数估算今日收入，并显示每秒赚钱速度。
- 展示当日工作进度、月累计收入估算、时薪、满勤日薪及距离发薪日的天数。
- 提供按收入进度变化的鼓励语。
- 首次启动时引导填写配置；之后可从主界面进入设置页修改。

## 薪资估算口径

- 每日有效工时 = 下班时间 - 上班时间 - 午休时长。
- 时薪 = 月薪 /（每月工作天数 × 每日有效工时）。
- 今日已赚 = 今日已工作秒数 × 时薪 / 3600。午休时间不计入工作时长。
- 月累计金额按“本月已过天数 × 满勤日薪”估算，并不根据真实工作日或节假日计算。
- 工作时间使用当天的本地时钟计算；应用不依赖网络服务。

以上均为简化估算，不等同于工资单或实际发薪金额。当前设置校验主要检查上下班及午休时间的先后顺序。

## 技术栈

- Android 原生应用，Java 17
- Android Gradle Plugin（版本见根目录 `build.gradle.kts`）
- compileSdk / targetSdk：34；minSdk：26
- ViewBinding
- AndroidX AppCompat、Material Components、ConstraintLayout
- 使用 SharedPreferences 保存用户配置

## 项目结构

```text
app/src/main/
├── java/com/czc/gotowork/
│   ├── MainActivity.java          # 主界面与定时刷新
│   ├── ui/SettingsActivity.java   # 工作与薪资设置
│   └── util/
│       ├── Prefs.java             # 本地配置读写
│       └── SalaryCalc.java        # 工时、收入与发薪日计算
├── res/layout/                   # 主界面和设置页布局
└── AndroidManifest.xml
```

## 构建与运行

使用 Android Studio 打开项目根目录，等待 Gradle 同步后运行 `app` 配置。也可以在 Windows 命令提示符中执行：

```bat
gradlew.bat assembleDebug
```

调试 APK 输出位置：`app/build/outputs/apk/debug/app-debug.apk`。

