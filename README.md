# KingKeyboard

![Image](app/src/main/ic_launcher-web.png)

[![Download](https://img.shields.io/badge/download-App-blue.svg)](https://raw.githubusercontent.com/jenly1314/KingKeyboard/master/app/release/app-release.apk)
[![MavenCentral](https://img.shields.io/maven-central/v/com.github.jenly1314/kingkeyboard)](https://repo1.maven.org/maven2/com/github/jenly1314/kingkeyboard)
[![JitPack](https://jitpack.io/v/jenly1314/KingKeyboard.svg)](https://jitpack.io/#jenly1314/KingKeyboard)
[![CircleCI](https://circleci.com/gh/jenly1314/KingKeyboard.svg?style=svg)](https://circleci.com/gh/jenly1314/KingKeyboard)
[![API](https://img.shields.io/badge/API-21%2B-blue.svg?style=flat)](https://android-arsenal.com/api?level=21)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](https://opensource.org/licenses/mit-license.php)

**KingKeyboard** 是一款灵活好用的自定义键盘，内置多种智能预设模式（包括字母、数字、电话、身份证、车牌号等专用键盘），同时支持完全自定义，轻松满足各种输入场景需求。

## 功能特色
- 🎯 **开箱即用：** 内置多种预设键盘，轻松应对各类输入场景
- 🛠️ **深度定制：** 支持完全自定义，满足个性化需求
- 🚀 **极简集成：** 提供简洁的API，可快速接入
- 🎨 **舒适视觉：** 专业设计师精心调校的配色方案，兼顾美学表现与实用体验

## Gif 展示
![Image](GIF.gif)

> 你也可以直接下载 [演示App](https://raw.githubusercontent.com/jenly1314/KingKeyboard/master/app/release/app-release.apk) 体验效果

## 引入

### Gradle:

1. 在Project的 **build.gradle** 或 **setting.gradle** 中添加远程仓库

    ```gradle
    repositories {
        //...
        mavenCentral()
    }
    ```

2. 在Module的 **build.gradle** 中添加依赖项

    ```gradle
    implementation 'com.github.jenly1314:kingkeyboard:1.1.0'
    ```

## 使用

### 键盘类型说明

`KingKeyboard.KeyboardType`中定义了一些键盘类型，以供使用

```Kotlin
/**
 * 默认键盘 - 数字 + 字母 + 符号
 */
KeyboardType.NORMAL
/**
 * 字母键盘
 */
KeyboardType.LETTER
/**
 * 仅小写字母键盘
 */
KeyboardType.LOWERCASE_LETTER_ONLY
/**
 * 仅大写字母键盘
 */
KeyboardType.UPPERCASE_LETTER_ONLY
/**
 * 字母 + 数字键盘
 */
KeyboardType.LETTER_NUMBER
/**
 * 数字键盘
 */
KeyboardType.NUMBER
/**
 * 浮点数键盘（数字加“.”符号）
 */
KeyboardType.NUMBER_DECIMAL
/**
 * 电话拨号键盘（数字加“-”符号）
 */
KeyboardType.PHONE
/**
 * 身份证键盘
 */
KeyboardType.ID_CARD
/**
 * 车牌键盘 - 车牌 -> 归属地 + 切换车牌号（包含不常见的一些特殊车牌）
 */
KeyboardType.LICENSE_PLATE
/**
 * 车牌键盘 - 车牌（不包含一些少见的特殊车牌），如需要更全的可以使用[LICENSE_PLATE]
 */
KeyboardType.LICENSE_PLATE_PROVINCE
/**
 * 预留自定义键盘类型
 */
KeyboardType.CUSTOM
/**
 * 预留自定义键盘类型 - 键盘模式切换
 */
KeyboardType.CUSTOM_MODE_CHANGE
/**
 * 预留自定义键盘类型 - 更多
 */
KeyboardType.CUSTOM_MORE

```
> 最后面几个定义的`CUSTOM`开头的键盘类型是预留给自定义键盘使用的，当上面内置的键盘类型不满足你的需求时，你可以通过预留的自定义键盘类型，来便捷定制你的自定义键盘。

### 基本使用

#### 使用示例
```Kotlin
    // 初始化KingKeyboard
    kingKeyboard = KingKeyboard(this, keyboardParent)
    // 然后将EditText注册到KingKeyboard即可
    kingKeyboard.register(editText, KingKeyboard.KeyboardType.NUMBER)
```
* 注意：示例中初始化时的`keyboardParent`参数可为空或省略，但若遇到输入时`EditText`被键盘遮挡的问题，通常是由于布局层级不当导致。
> 推荐方案：在布局中添加一层可滚动视图（如：`ScrollView`）包裹内容区域，同时将`keyboardParent`与`ScrollView`置于同一层级并固定在底部。这样可确保键盘弹出时内容自动滚动，避免遮挡输入框。（完整实现可参考 [app](app) 中的示例代码）

在`Activity`或`Fragment`相应的生命周期中调用，如下所示

```Kotlin
    override fun onResume() {
        super.onResume()
        kingKeyboard.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        kingKeyboard.onDestroy()
    }

```
> 从 **v1.1.0** 版本开始，`KingKeyboard`实现了`LifecycleObserver`；如果是通过`Activity`实例化的`KingKeyboard`，
> `KingKeyboard`会在初始化时内部调用`activity.lifecycle.addObserver(this)`；可以不用主动在生命周期中调用`kingKeyboard.onResume()`和`kingKeyboard.onDestroy()`了。

好了，到这里`KingKeyboard`键盘的基本使用你已经学会了，是不是很简单？
> 如果以上已经能满足你的需求，可以不用往下看了。

---

从分割线开始，下面是关于 **KingKeyboard** 键盘更细节的一些说明

---

### 键盘配置

```Kotlin
    // 获取键盘相关的配置信息
    val config = kingKeyboard.getKeyboardViewConfig()

    //... 修改一些键盘的配置信息
    // config.keyTextColor = ContextCompat.getColor(context, R.color.king_keyboard_key_text_color)

    // 重新设置键盘配置信息
    kingKeyboard.setKeyboardViewConfig(config)

    // 按键是否启用震动
    kingKeyboard.setVibrationEffectEnabled(isVibrationEffectEnabled)

    //... 还有各种监听方法。更多详情，请直接使用。

```
> 也可通过资源覆盖的方式，统一修改键盘配置。（`KingKeyboard`中的所有资源均以`king_keyboard`开头）

### 键盘状态

通过`kingKeyboard.isShow()`可以判断键盘是否显示，通过调用`kingKeyboard.hide()` 主动隐藏键盘。

```Kotlin
    /**
     * 键盘是否显示
     */
    kingKeyboard.isShow()

    /**
     * 隐藏键盘
     */
    kingKeyboard.hide()

```
> 不对外提供`kingKeyboard.show()`函数的原因是：`KingKeyboard`内部已经根据`EditText`的焦点状态处理了键盘的显示时机。

### 键盘控制：代码发送键值（v1.0.2新增）

有时，我们会有动态控制键盘的需求；这时你就可以使用 `kingKeyboard.sendKey(primaryCode)` 来发送键值操控键盘了。

```kotlin
    var beforeCount = 0

    kingKeyboard.register(et,KingKeyboard.KeyboardType.LICENSE_PLATE_PROVINCE)

    //通过监听输入框内容改变，来通过发送功能键来切换键盘（这里只是举例展示kingKeyboard.sendKey的用法，具体怎么用还需根据需求场景去决定）
    et.addTextChangedListener(object : TextWatcher{
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            beforeCount = s?.length ?: 0
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            when(s?.length){
                0 -> {//车牌键盘：如果输入的内容长度改变为0，并且当前的键盘不是省份键盘模式时，通过发送“返回”功能按键值，让键盘自动切换到省份键盘模式
                    if(kingKeyboard.getKeyboardType() != KingKeyboard.KeyboardType.LICENSE_PLATE_PROVINCE){
                        kingKeyboard.sendKey(KingKeyboard.KEYCODE_BACK)
                    }
                }
                1 -> {//车牌键盘：如果输入的内容长度从0改变为1，并且当前的键盘为省份键盘模式时，通过发送“模式改变”功能按键值，让键盘自动切换到字母键盘模式
                    if(beforeCount == 0 && kingKeyboard.getKeyboardType() == KingKeyboard.KeyboardType.LICENSE_PLATE_PROVINCE){
                        kingKeyboard.sendKey(KingKeyboard.KEYCODE_MODE_CHANGE)
                    }
                }
            }
        }

        override fun afterTextChanged(s: Editable?) {

        }

    })
```
> 根据 `primaryCode` 发送按键事件，可通过调用触发去执行按键值对应的功能（仅限功能键） 特别说明：之所以仅限功能键，是因为如果对外支持输入相关的按键值，会破坏输入内容的限制。

### 自定义键盘

如果目前所支持的键盘满足不了你的需求，你也可以自定义键盘，`KingKeyboard`对外提供自定义键盘类型。

**`KeyboardType` 中预留的自定义键盘类型**

| 键盘类型                           | 设置布局方法                                       | 说明 |
|----------------------------------|----------------------------------------------|------|
| `CUSTOM`            | `kingKeyboard.setKeyboardCustom()`           | 自定义键盘 |
| `CUSTOM_MODE_CHANGE` | `kingKeyboard.setKeyboardCustomModeChange()` | 自定义键盘 - 模式切换 |
| `CUSTOM_MORE`       | `kingKeyboard.setKeyboardCustomMore()`       | 自定义键盘 - 更多 |

> 自定义键盘步骤也非常简单，你只需自定义键盘的xml布局，然后将`EditText`注册到对应的自定义键盘类型即可。

#### 自定义键盘代码示例

```Kotlin
    // 初始化KingKeyboard
    kingKeyboard = KingKeyboard(this, keyboardParent)
    // 设置自定义键盘布局
    kingKeyboard.setKeyboardCustom(R.xml.keyboard_custom)
    // 然后将EditText注册到KingKeyboard即可
    kingKeyboard.register(editText, KingKeyboard.KeyboardType.CUSTOM)
```
> 示例中的[`R.xml.keyboard_custom`](app/src/main/res/xml/keyboard_custom.xml) 为自定义键盘布局的xml资源文件，其中包含键盘布局和键值码等相关信息。

#### 自定义按键值说明

在`KingKeyboard`的伴生对象中定义了一些核心的按键值，当你需要自定义键盘时，可能需要用到

```Kotlin

    //------------------------------ 下面是定义的一些公用功能按键值
    /**
     * Shift键 -> 一般用来切换键盘大小写字母
     */
    const val KEYCODE_SHIFT = -1
    /**
     * 模式改变 -> 切换键盘输入法
     */
    const val KEYCODE_MODE_CHANGE = -2
    /**
     * 取消键 -> 关闭输入法
     */
    const val KEYCODE_CANCEL = -3
    /**
     * 完成键 -> 长出现在右下角蓝色的完成按钮
     */
    const val KEYCODE_DONE = -4
    /**
     * 删除键 -> 删除输入框内容
     */
    const val KEYCODE_DELETE = -5
    /**
     * Alt键 -> 预留，暂时未使用
     */
    const val KEYCODE_ALT = -6
    /**
     * 空格键
     */
    const val KEYCODE_SPACE = 32

    /**
     * 无作用键 -> 一般用来占位或者禁用按键
     */
    const val KEYCODE_NONE = 0

    //------------------------------

    /**
     * 键盘按键 -> 返回（返回，适用于切换键盘后界面使用，如：NORMAL_MODE_CHANGE或CUSTOM_MODE_CHANGE键盘）
     */
    const val KEYCODE_MODE_BACK = -101

    /**
     * 键盘按键 ->返回（直接返回到最初,直接返回到NORMAL或CUSTOM键盘）
     */
    const val KEYCODE_BACK = -102

    /**
     * 键盘按键 ->更多
     */
    const val KEYCODE_MORE = -103

    //------------------------------ 下面是自定义的一些预留按键值，与共用按键功能一致,但会使用默认的背景按键

    const val KEYCODE_KING_SHIFT = -201
    const val KEYCODE_KING_MODE_CHANGE = -202
    const val KEYCODE_KING_CANCEL = -203
    const val KEYCODE_KING_DONE = -204
    const val KEYCODE_KING_DELETE = -205
    const val KEYCODE_KING_ALT = -206

    //------------------------------ 下面是自定义的一些功能按键值，与共用按键功能一致,但会使用默认背景颜色

    /**
     * 键盘按键 -> 返回（返回，适用于切换键盘后界面使用，如：NORMAL_MODE_CHANGE或CUSTOM_MODE_CHANGE键盘）
     */
    const val KEYCODE_KING_MODE_BACK = -251

    /**
     * 键盘按键 ->返回（直接返回到最初,直接返回到NORMAL或CUSTOM键盘）
     */
    const val KEYCODE_KING_BACK = -252

    /**
     * 键盘按键 ->更多
     */
    const val KEYCODE_KING_MORE = -253

    /*
        用户也可自定义按键值，primaryCode范围区间为-999 ~ -300时，表示预留可扩展按键值。
        其中-399~-300区间为功能型按键，使用Special背景色，-999~-400自定义按键为默认背景色
    */

```

更多使用详情，请查看[app](app)中的源码使用示例或直接查看[API帮助文档](https://jenly1314.github.io/KingKeyboard/api/)

## 相关推荐

* [SplitEditText](https://github.com/jenly1314/SplitEditText) 一个灵活的分割可编辑框；常常应用于 **验证码输入** 、**密码输入** 等场景。
* [CodeTextField](https://github.com/jenly1314/CodeTextField) 一个使用 Compose 实现的验证码输入框。

<!-- end -->

## 版本日志

#### v1.1.0：2025-4-6
* 更新gradle至v8.0
* 更新compileSdk至32
* 更新kotlin至v1.8.10
* `KingKeyboard`实现`LifecycleObserver`（构造中的`Activity`改为`ComponentActivity`）
* 优化Java调用Kotlin默认参数函数时的兼容性（构造添加注解：`@JvmOverloads`）
* 调整车牌键盘按键的排列顺序
* 优化点击音效和振动触感反馈实现方式（移除振动权限）
* 优化键盘按键默认配置的背景（颜色搭配微调，使整体看起来更美观）
* 优化一些细节

#### [查看更多版本日志](CHANGELOG.md)

## 赞赏
如果你喜欢KingKeyboard，或感觉KingKeyboard帮助到了你，可以点右上角“Star”支持一下，你的支持就是我的动力，谢谢 :smiley:
<p>你也可以扫描下面的二维码，请作者喝杯咖啡 :coffee:

<div>
   <img src="https://jenly1314.github.io/image/page/rewardcode.png">
</div>

## 关于我

| 我的博客                                                                                | GitHub                                                                                  | Gitee                                                                                  | CSDN                                                                                 | 博客园                                                                            |
|:------------------------------------------------------------------------------------|:----------------------------------------------------------------------------------------|:---------------------------------------------------------------------------------------|:-------------------------------------------------------------------------------------|:-------------------------------------------------------------------------------|
| <a title="我的博客" href="https://jenly1314.github.io" target="_blank">Jenly's Blog</a> | <a title="GitHub开源项目" href="https://github.com/jenly1314" target="_blank">jenly1314</a> | <a title="Gitee开源项目" href="https://gitee.com/jenly1314" target="_blank">jenly1314</a>  | <a title="CSDN博客" href="http://blog.csdn.net/jenly121" target="_blank">jenly121</a>  | <a title="博客园" href="https://www.cnblogs.com/jenly" target="_blank">jenly</a>  |

## 联系我

| 微信公众号        | Gmail邮箱                                                                          | QQ邮箱                                                                              | QQ群                                                                                                                       | QQ群                                                                                                                       |
|:-------------|:---------------------------------------------------------------------------------|:----------------------------------------------------------------------------------|:--------------------------------------------------------------------------------------------------------------------------|:--------------------------------------------------------------------------------------------------------------------------|
| [Jenly666](http://weixin.qq.com/r/wzpWTuPEQL4-ract92-R) | <a title="给我发邮件" href="mailto:jenly1314@gmail.com" target="_blank">jenly1314</a> | <a title="给我发邮件" href="mailto:jenly1314@vip.qq.com" target="_blank">jenly1314</a> | <a title="点击加入QQ群" href="https://qm.qq.com/cgi-bin/qm/qr?k=6_RukjAhwjAdDHEk2G7nph-o8fBFFzZz" target="_blank">20867961</a> | <a title="点击加入QQ群" href="https://qm.qq.com/cgi-bin/qm/qr?k=Z9pobM8bzAW7tM_8xC31W8IcbIl0A-zT" target="_blank">64020761</a> |

<div>
   <img src="https://jenly1314.github.io/image/page/footer.png">
</div>
