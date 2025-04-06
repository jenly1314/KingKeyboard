## 版本日志

#### v1.1.0：2025-4-6
* 更新gradle至v8.0
* 更新compileSdk至32
* 更新kotlin至v1.8.10
* `KingKeyboard`实现`LifecycleObserver`（构造中的Activity改为ComponentActivity）
* 优化Java调用Kotlin默认参数函数时的兼容性（构造添加注解：`@JvmOverloads`）
* 调整车牌键盘按键的排列顺序
* 优化点击音效和振动触感反馈实现方式
* 优化键盘按键默认配置的背景（颜色搭配微调，使整体看起来更美观）
* 优化一些细节

#### v1.0.4：2021-11-18
* 修复动态修改默认键盘字体大小不生效问题

#### v1.0.3：2021-10-22
* 优化细节

#### v1.0.2：2021-9-29
* 新增 **KingKeyboard.sendKey(primaryCode: Int)** 方法；（支持通过发送按键的值来控制键盘）

#### v1.0.1：2021-9-3 (从v1.0.1开始发布至 MavenCentral)
* 优化按键提示音策略（跟随系统的提示音设置）
* 发布至 MavenCentral

#### v1.0.0：2020-1-16
* KingKeyboard初始版本
