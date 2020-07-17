## 开发助手 [![](https://jitpack.io/v/forJrking/ApmFloat.svg)](https://jitpack.io/#forJrking/ApmFloat)

辅助开发检查性能问题，快速找到视觉页面

Android 8.0 以上手机需要手动在应用的设置页面，开启悬浮窗口权限

<img src="Screenshot_20200708-180228.png" alt="png"/>

悬浮窗依次展示数据为：

>当前Activity名称、
FPS、文件句柄数量、
CPU占用率、当前线程数量、
内存占用值、JVM内存使用情况

测试环境集成方式

**Step 1.** Add it in your root build.gradle at the end of repositories:

```css
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

**Step 2.** Add the dependency

```css
	dependencies {
	       debugImplementation 'com.github.forJrking:ApmFloat:1.0.3'
	}
```

~~**Step 3.** initialize~~

```java
  1.0.3以上版本不需要（建议只在测试环境使用）  ApmOverlayController.initialize(context,isDebug);
```
