package com.king.keyboard

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.media.AudioManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.text.InputType
import android.util.Log
import android.util.SparseArray
import android.view.*
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.EditText
import androidx.annotation.IdRes


/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
open class KingKeyboard {

    private lateinit var context: Context


    private var isCap = false

    private var isAllCaps = false

    private var keyboardType = KeyboardType.NORMAL

    private val keyboardNormal by lazy { Keyboard(context, R.xml.king_keyboard_normal) }
    private val keyboardNormalModeChange by lazy { Keyboard(context, R.xml.king_keyboard_normal_mode_change) }
    private val keyboardNormalMore by lazy { Keyboard(context, R.xml.king_keyboard_normal_more_symbol) }

    private val keyboardLetter by lazy { Keyboard(context, R.xml.king_keyboard_letter) }

    private val keyboardLowercaseLetter by lazy { Keyboard(context, R.xml.king_keyboard_lowercase_letter_only) }

    private val keyboardUppercaseLetter by lazy { Keyboard(context, R.xml.king_keyboard_uppercase_letter_only) }

    private val keyboardLetterNumber by lazy { Keyboard(context, R.xml.king_keyboard_letter_number) }

    private val keyboardNumber by lazy { Keyboard(context, R.xml.king_keyboard_number) }

    private val keyboardNumberDecimal by lazy { Keyboard(context, R.xml.king_keyboard_number_decimal) }

    private val keyboardPhone by lazy { Keyboard(context, R.xml.king_keyboard_phone) }

    private val keyboardIDCard by lazy { Keyboard(context, R.xml.king_keyboard_id_card) }

    private val keyboardLicensePlate by lazy { Keyboard(context, R.xml.king_keyboard_license_plate) }

    /**
     * LICENSE_PLATE_MODE_CHANGE与 LICENSE_PLATE_MODE_NUMBER
     */
    private val keyboardLicensePlateNumber by lazy { Keyboard(context, R.xml.king_keyboard_license_plate_number) }

    private val keyboardLicensePlateMore by lazy { Keyboard(context, R.xml.king_keyboard_license_plate_more) }

    private val keyboardLicensePlateProvince by lazy { Keyboard(context, R.xml.king_keyboard_license_plate_province) }


    private var keyboardCustom: Keyboard? = null
    private var keyboardCustomModeChange: Keyboard? = null
    private var keyboardCustomMore: Keyboard? = null

    private var currentKeyboard: Keyboard

    private lateinit var keyboardViewGroup: View
    private var keyboardView: KingKeyboardView? = null

    private var currentEditText: EditText? = null

    /**
     * SparseArray存储EditText,SparseArray的key为EditText的ID，value为EditText
     */
    private val editTextArray by lazy {
        SparseArray<EditText?>()
    }

    /**
     * 键盘类型，SparseArray的key为EditText的ID，value为键盘类型
     */
    private val keyboardTypeArray by lazy {
        SparseArray<Int>()
    }
    /**
     * 键盘显示动画
     */
    private lateinit var showAnimation: Animation
    /**
     * 键盘隐藏动画
     */
    private lateinit var hideAnimation: Animation

    private lateinit var onTouchListener: View.OnTouchListener
    private lateinit var globalFocusChangeListener: ViewTreeObserver.OnGlobalFocusChangeListener

    private var onKeyboardActionListener: KeyboardView.OnKeyboardActionListener? = null
    private var onKeyDoneListener: OnKeyListener? = null
    private var onKeyCancelListener: OnKeyListener? = null
    private var onKeyExtraListener: OnKeyListener? = null

    private var vibrator: Vibrator? = null

    private var audioManager: AudioManager? = null

    /**
     * 是否震动
     */
    private var isVibrationEffect = false
    /**
     * 是否播放音效
     */
    private var isPlaySoundEffect = false

    companion object{

        private const val TAG = "KingKeyboard"

        private const val ANIM_DURATION_TIME = 200L

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

    }

    /**
     * 构造
     * @param activity 上下文
     * @param keyboardParentView 键盘的父布局容器 -> 一般在界面底部，用来容纳键盘布局
     *
     */
    constructor(activity: Activity, keyboardParentView: ViewGroup):
            this(activity,
                activity.window.decorView.findViewById<ViewGroup>(android.R.id.content).getChildAt(0) as ViewGroup,
                keyboardParentView
            )

    /**
     * 构造
     * @param context 上下文
     * @param rootView 界面的根布局 -> 也可以是当前界面包含所有的EditText的公共父布局
     * @param keyboardParentView 键盘的父布局容器 -> 一般在界面底部，用来容纳键盘布局
     */
    constructor(context: Context, rootView: ViewGroup,keyboardParentView: ViewGroup):
            this(context,
                rootView,
                keyboardParentView,
                LayoutInflater.from(context).inflate(R.layout.king_keyboard_container, null),
                R.id.keyboardView
            )
    /**
     * 构造
     * @param context 上下文
     * @param rootView 界面的根布局 -> 也可以是当前界面包含所有的EditText的公共父布局
     * @param keyboardParentView 键盘的父布局容器 -> 一般在界面底部，用来容纳键盘布局
     * @param keyboardContainer 键盘的容器
     * @param keyboardViewId KingKeyboard视图控件的ID
     */
    constructor(context: Context, rootView: ViewGroup,keyboardParentView: ViewGroup,keyboardContainer: View,@IdRes keyboardViewId: Int) {
        this.context = context
        currentKeyboard = keyboardNormal
        intKeyboard(context)
        intKeyboardView(rootView,keyboardParentView,keyboardContainer,keyboardViewId)

    }

    open fun intKeyboard(context: Context){

    }

    /**
     * 初始化KeyboardView
     */
    private fun intKeyboardView(rootView: ViewGroup,keyboardParentView: ViewGroup,keyboardContainer: View,@IdRes keyboardViewId: Int){
        //初始化键盘相关
        keyboardViewGroup = keyboardContainer
        keyboardView = keyboardViewGroup.findViewById(keyboardViewId)

        keyboardView?.let {
            it.keyboard = currentKeyboard
            it.isEnabled = true
            it.isPreviewEnabled = false
            it.onKeyboardActionListener = object: KeyboardView.OnKeyboardActionListener{

                override fun swipeRight() {
                    onKeyboardActionListener?.swipeRight()
                }

                override fun onPress(primaryCode: Int) {
                    onKeyboardActionListener?.onPress(primaryCode)
                }

                override fun onRelease(primaryCode: Int) {
                    onKeyboardActionListener?.onRelease(primaryCode)
                }

                override fun swipeLeft() {
                    onKeyboardActionListener?.swipeLeft()
                }

                override fun swipeUp() {
                    onKeyboardActionListener?.swipeUp()
                }

                override fun swipeDown() {
                    onKeyboardActionListener?.swipeDown()
                }

                override fun onKey(primaryCode: Int, keyCodes: IntArray?) {
                    playSoundEffect()
                    sendVibrationEffect()
                    //根据不同的按键值去处理
                    when(primaryCode){
                        KEYCODE_SHIFT -> keyShift()
                        KEYCODE_MODE_CHANGE -> keyModeChange()
                        KEYCODE_CANCEL -> keyCancel(primaryCode)
                        KEYCODE_DONE -> keyDone(primaryCode)
                        KEYCODE_DELETE -> keyDelete()
                        KEYCODE_ALT -> keyAlt()
                        KEYCODE_MODE_BACK -> keyBack(false)
                        KEYCODE_BACK -> keyBack(true)
                        KEYCODE_MORE -> keyMore()
                        //预留值，具有相同的作用
                        KEYCODE_KING_SHIFT -> keyShift()
                        KEYCODE_KING_MODE_CHANGE -> keyModeChange()
                        KEYCODE_KING_CANCEL -> keyCancel(primaryCode)
                        KEYCODE_KING_DONE -> keyDone(primaryCode)
                        KEYCODE_KING_DELETE -> keyDelete()
                        KEYCODE_KING_ALT -> keyAlt()
                        KEYCODE_KING_MODE_BACK -> keyBack(false)
                        KEYCODE_KING_BACK -> keyBack(true)
                        KEYCODE_KING_MORE -> keyMore()
                        //预留的自定义可扩展按键
                        in -999..-300 -> keyExtra(primaryCode)
                        //直接输入按键值
                        in 32..Int.MAX_VALUE -> keyInput(primaryCode)
                        //无效的按键值，打印相关日志
                        else -> Log.d(TAG,"primaryCode:$primaryCode")
                    }
                    onKeyboardActionListener?.onKey(primaryCode,keyCodes)

                }

                override fun onText(text: CharSequence?) {
                    onKeyboardActionListener?.onText(text)
                }
            }

            isCap = it.isCap()
            isAllCaps = it.isAllCaps()

            keyboardViewGroup.isVisible = false
        }

        //初始化动画
        showAnimation = TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
            0.0f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f)
        showAnimation.duration = ANIM_DURATION_TIME

        hideAnimation = TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
            0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f)
        hideAnimation.duration = ANIM_DURATION_TIME

        hideAnimation.setAnimationListener(object : Animation.AnimationListener{
            override fun onAnimationRepeat(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {
                if(keyboardViewGroup.isVisible){
                    keyboardViewGroup.isVisible = false
                }
            }

            override fun onAnimationStart(animation: Animation?) {

            }

        })

        onTouchListener = View.OnTouchListener { v, event ->
            if(event.action == MotionEvent.ACTION_UP){
                viewFocus(v)
            }

            false
        }

        globalFocusChangeListener = ViewTreeObserver.OnGlobalFocusChangeListener {oldFocus, newFocus ->
            if(newFocus is EditText){
                if(editTextArray.containsKey(newFocus.id)){//newFocus使用的是KingKeyboard
                    viewFocus(newFocus)
                }else{//没有使用KingKeyboard，可能使用的是系统输入法，则隐藏KingKeyboard
                    hide()
                }
            }
        }

        //将键盘布局添加到父布局
        keyboardParentView.addView(keyboardViewGroup)

        rootView.viewTreeObserver.addOnGlobalFocusChangeListener(globalFocusChangeListener)

    }

    /**
     * 自定义键盘Custom，键盘类型为{@link KeyboardType#CUSTOM}
     *
     * 当默认已有的键盘类型满足不了您的需求时，可通过此方法来自定义键盘。
     *
     * 与之相关的方法有{@code setKeyboardCustomModeChange(Keyboard)}和{@code setKeyboardCustomMore(Keyboard)}
     *
     * @param keyboard 键盘
     */
    fun setKeyboardCustom(keyboard: Keyboard){
        this.keyboardCustom = keyboard
    }

    /**
     * 自定义键盘CustomModeChange，键盘类型为{@link KeyboardType#CUSTOM_MODE_CHANGE}
     *
     * 当需要自定义键盘的按键太多，一个自定义键盘布局满足不了你的需求时，即Custom不够用时，你可以通过
     * 自定义CustomModeChange来扩展，通过键盘切换，来满足您的需求。
     *
     * 与之相关的方法有{@code setKeyboardCustom(Keyboard)}和{@code setKeyboardCustomMore(Keyboard)}
     *
     * @param keyboard 键盘
     */
    fun setKeyboardCustomModeChange(keyboard: Keyboard){
        this.keyboardCustomModeChange = keyboard
    }

    /**
     * 自定义键盘CustomMore，键盘类型为{@link KeyboardType#CUSTOM_MORE}
     *
     * 当需要自定义键盘的按键太多，两个自定义键盘布局满足不了你的需求时，即Custom加上CustomModeChange还不够用时，
     * 你可以通过自定义CustomModeChange来扩展，通过键盘切换，来满足您的需求。
     *
     * 与之相关的方法有{@code setKeyboardCustom(Keyboard)}和{@code setKeyboardCustomModeChange(Keyboard)}
     *
     * @param keyboard 键盘
     */
    fun setKeyboardCustomMore(keyboard: Keyboard){
        this.keyboardCustomMore = keyboard
    }

    /**
     * 自定义键盘Custom，键盘类型为{@link KeyboardType#CUSTOM}
     *
     * 当默认已有的键盘类型满足不了您的需求时，可通过此方法来自定义键盘。
     *
     * 与之相关的方法有{@code setKeyboardCustomModeChange(Int)}和{@code setKeyboardCustomMore(Int)}
     *
     * @param xmlLayoutResId 键盘布局的资源文件，其中包含键盘布局和键值码等相关信息
     */
    fun setKeyboardCustom(xmlLayoutResId: Int){
        this.keyboardCustom = Keyboard(context,xmlLayoutResId)
    }

    /**
     * 自定义键盘CustomModeChange，键盘类型为{@link KeyboardType#CUSTOM_MODE_CHANGE}
     *
     * 当需要自定义键盘的按键太多，一个自定义键盘布局满足不了你的需求时，即Custom不够用时，你可以通过
     * 自定义CustomModeChange来扩展，通过键盘切换，来满足您的需求。
     *
     * 与之相关的方法有{@code setKeyboardCustom(Int)}和{@code setKeyboardCustomMore(Int)}
     *
     * @param xmlLayoutResId 键盘布局的资源文件，其中包含键盘布局和键值码等相关信息
     */
    fun setKeyboardCustomModeChange(xmlLayoutResId: Int){
        this.keyboardCustomModeChange = Keyboard(context,xmlLayoutResId)
    }

    /**
     * 自定义键盘CustomMore，键盘类型为{@link KeyboardType#CUSTOM_MORE}
     *
     * 当需要自定义键盘的按键太多，两个自定义键盘布局满足不了你的需求时，即Custom加上CustomModeChange还不够用时，
     * 你可以通过自定义CustomModeChange来扩展，通过键盘切换，来满足您的需求。
     *
     * 与之相关的方法有{@code setKeyboardCustom(Int)}和{@code setKeyboardCustomModeChange(Int)}
     *
     * @param xmlLayoutResId 键盘布局的资源文件，其中包含键盘布局和键值码等相关信息
     */
    fun setKeyboardCustomMore(xmlLayoutResId: Int){
        this.keyboardCustomMore = Keyboard(context,xmlLayoutResId)
    }

    /**
     * 获取当前键盘输入法类型
     * return 返回当前键盘输入法类型
     */
    fun getKeyboardType(): Int{
        return keyboardType
    }

    private fun disableShowSoftInput(editText: EditText){
        try {
            val method = EditText::class.java.getMethod("setShowSoftInputOnFocus", Boolean::class.java)
            method.isAccessible = true
            method.invoke(editText, false)
        } catch (e: Exception) {
            editText.inputType = InputType.TYPE_NULL
        }
    }

    /**
     * 执行当获View获取焦点时的一些逻辑，如：显示键盘
     */
    private fun viewFocus(v: View){
        if(v is EditText){
            v.hideSystemInputMethod()
            disableShowSoftInput(v)
            if(v.hasFocus()){
                currentEditText = v
                keyboardType = keyboardTypeArray[v.id]!!
                switchKeyboard()
                show()
            }
        }
    }


    /**
     * 注册
     * @param editText 要注册的EditText
     * @param keyboardType 键盘输入法类型
     */
    fun register(editText: EditText,keyboardType: Int) {
        editTextArray[editText.id] = editText
        keyboardTypeArray[editText.id] = keyboardType
//        editText.setOnTouchListener(onTouchListener)
    }

    fun onResume(){
        currentEditText?.let {
            if(it.hasFocus()){
                it.postDelayed({ it.hideSystemInputMethod() },100)
            }
        }
    }

    fun onDestroy(){
        currentEditText?.let {
            it.clearAnimation()
            currentEditText = null
        }
        editTextArray.clear()
        keyboardTypeArray.clear()
    }

    /**
     * 键盘输入法是否显示
     */
    fun isShow(): Boolean{
        return keyboardViewGroup.isVisible
    }

    /**
     * 显示键盘输入法
     */
    private fun show(){
        if(!keyboardViewGroup.isVisible){
            keyboardViewGroup.apply {
                isVisible = true
                clearAnimation()
                startAnimation(showAnimation)
            }
        }
    }

    /**
     * 隐藏键盘输入法
     */
     open fun hide(){
        if(keyboardViewGroup.isVisible){
            keyboardViewGroup.apply {
                clearAnimation()
                startAnimation(hideAnimation)
            }
        }
    }

    /**
     * 设置背景
     */
    fun setBackground(drawable: Drawable?){
        drawable?.let {
            keyboardViewGroup.background = drawable
        }
    }

    /**
     * 设置背景
     */
    fun setBackgroundResource(drawableId: Int){
        keyboardViewGroup.setBackgroundResource(drawableId)
    }

    /**
     * 对外提供获取KingKeyboardView
     */
    fun getKeyboardView(): KingKeyboardView? {
        return keyboardView
    }

    /**
     * 对外提供获取KingKeyboardView的配置
     */
    fun getKeyboardViewConfig(): KingKeyboardView.Config?{
        return keyboardView?.getConfig()
    }

    /**
     * 对外提供设置KingKeyboardView的配置
     */
    fun setKeyboardViewConfig(config: KingKeyboardView.Config){
        keyboardView?.setConfig(config)
    }

    //----------------------------------

    /**
     * 是否开启音效 -> 暂不对外提供
     */
    private fun isSoundEffectsEnabled(): Boolean{
        return isPlaySoundEffect
    }

    /**
     * 设置是否开启音效 -> 暂不对外提供
     */
    private fun setSoundEffectEnabled(soundEffectEnabled: Boolean){
        this.isPlaySoundEffect = soundEffectEnabled
    }

    /**
     * 是否开启震动
     */
    fun isVibrationEffectEnabled(): Boolean{
        return isVibrationEffect
    }

    /**
     * 设置是否开启震动
     */
    fun setVibrationEffectEnabled(vibrationEffectEnabled: Boolean){
        this.isVibrationEffect = vibrationEffectEnabled
    }

    /**
     * 对外提供监听键盘相关动作
     */
    fun setOnKeyboardActionListener(listener: KeyboardView.OnKeyboardActionListener?){
        this.onKeyboardActionListener = listener
    }

    /**
     * 对外提供监听“完成”按键
     */
    fun setOnKeyDoneListener(listener: OnKeyListener?){
        this.onKeyDoneListener = listener
    }

    /**
     * 对外提供监听“关闭键盘”按键
     */
    fun setOnKeyCancelListener(listener: OnKeyListener?){
        this.onKeyCancelListener = listener
    }

    /**
     * 对外提供监听扩展自定义的按键
     */
    fun setOnKeyExtraListener(listener: OnKeyListener?){
        this.onKeyExtraListener = listener
    }

    /**
     * 监听“完成”按键接口
     */
    interface OnKeyListener{
        /**
         * 点击触发按键时，触发此回调方法
         * @param primaryCode 为原始code值，即Key的code
         */
        fun onKey(editText: View?,primaryCode: Int)
    }

    //----------------------------------

    /**
     * 切换键盘输入法
     */
    private fun switchKeyboard(){
        when(keyboardType){
            KeyboardType.NORMAL -> {
                currentKeyboard = keyboardNormal
            }
            KeyboardType.NORMAL_MODE_CHANGE -> {
                currentKeyboard = keyboardNormalModeChange
            }
            KeyboardType.NORMAL_MORE -> {
                currentKeyboard = keyboardNormalMore
            }
            KeyboardType.LETTER -> {
                currentKeyboard = keyboardLetter
            }
            KeyboardType.LOWERCASE_LETTER_ONLY -> {
                currentKeyboard = keyboardLowercaseLetter
            }
            KeyboardType.UPPERCASE_LETTER_ONLY -> {
                currentKeyboard = keyboardUppercaseLetter
            }
            KeyboardType.LETTER_NUMBER -> {
                currentKeyboard = keyboardLetterNumber
            }
            KeyboardType.NUMBER -> {
                currentKeyboard = keyboardNumber
            }
            KeyboardType.NUMBER_DECIMAL -> {
                currentKeyboard = keyboardNumberDecimal
            }
            KeyboardType.PHONE -> {
                currentKeyboard = keyboardPhone
            }
            KeyboardType.ID_CARD -> {
                currentKeyboard = keyboardIDCard
            }
            KeyboardType.LICENSE_PLATE -> {
                currentKeyboard = keyboardLicensePlate
            }
            KeyboardType.LICENSE_PLATE_MODE_CHANGE -> {
                currentKeyboard = keyboardLicensePlateNumber
            }
            KeyboardType.LICENSE_PLATE_MORE -> {
                currentKeyboard = keyboardLicensePlateMore
            }
            KeyboardType.LICENSE_PLATE_PROVINCE -> {
                currentKeyboard = keyboardLicensePlateProvince
            }
            KeyboardType.LICENSE_PLATE_NUMBER -> {
                currentKeyboard = keyboardLicensePlateNumber
            }
            KeyboardType.CUSTOM -> {//当自定义了键盘，但没有自定义相关布局时，使用默认键盘keyboardNormal
                currentKeyboard = keyboardCustom ?: keyboardNormal
            }
            KeyboardType.CUSTOM_MODE_CHANGE -> {//当自定义了键盘，但没有自定义相关布局时，使用默认键盘keyboardNormalModeChange
                currentKeyboard = keyboardCustomModeChange ?: keyboardNormalModeChange
            }
            KeyboardType.CUSTOM_MORE -> {//当自定义了键盘，但没有自定义相关布局时，使用默认键盘keyboardNormalMore
                currentKeyboard = keyboardCustomMore ?: keyboardNormalMore
            }

        }

        keyboardView?.run {
            keyboard = currentKeyboard
        }

    }

    /**
     * 模式改变，切换键盘
     */
    private fun keyModeChange(){
        when(keyboardType){
            KeyboardType.NORMAL -> {
                keyboardType = KeyboardType.NORMAL_MODE_CHANGE
            }
            KeyboardType.LICENSE_PLATE -> {
                keyboardType = KeyboardType.LICENSE_PLATE_MODE_CHANGE
            }
            KeyboardType.LICENSE_PLATE_MORE -> {
                keyboardType = KeyboardType.LICENSE_PLATE_MODE_CHANGE
            }
            KeyboardType.LICENSE_PLATE_PROVINCE -> {
                keyboardType = KeyboardType.LICENSE_PLATE_NUMBER
            }
            KeyboardType.CUSTOM -> {
                keyboardType = KeyboardType.CUSTOM_MODE_CHANGE
            }
            KeyboardType.CUSTOM_MORE -> {
                keyboardType = KeyboardType.CUSTOM_MODE_CHANGE
            }
        }

        switchKeyboard()
    }

    /**
     * 取消，关闭键盘
     */
    private fun keyCancel(primaryCode: Int){
        hide()
        onKeyCancelListener?.onKey(currentEditText,primaryCode)
    }

    /**
     * 完成
     */
    private fun keyDone(primaryCode: Int){
        hide()
        onKeyDoneListener?.onKey(currentEditText,primaryCode)
    }

    /**
     * Alt键，暂时未用到
     */
    private fun keyAlt(){

    }

    /**
     * 返回
     */
    private fun keyBack(isBack: Boolean){
        when(keyboardType){
            KeyboardType.NORMAL_MODE_CHANGE -> {
                keyboardType = KeyboardType.NORMAL
            }
            KeyboardType.NORMAL_MORE -> {
                keyboardType = if(isBack) KeyboardType.NORMAL else KeyboardType.NORMAL_MODE_CHANGE
            }
            KeyboardType.CUSTOM_MODE_CHANGE -> {
                keyboardType = KeyboardType.CUSTOM
            }
            KeyboardType.CUSTOM_MORE -> {
                keyboardType = if(isBack) KeyboardType.CUSTOM else KeyboardType.CUSTOM_MODE_CHANGE
            }
            KeyboardType.LICENSE_PLATE -> {
                keyboardType = KeyboardType.LICENSE_PLATE_NUMBER
            }
            KeyboardType.LICENSE_PLATE_MODE_CHANGE -> {
                keyboardType = KeyboardType.LICENSE_PLATE
            }
            KeyboardType.LICENSE_PLATE_MORE -> {
                keyboardType = if(isBack) KeyboardType.LICENSE_PLATE else KeyboardType.LICENSE_PLATE_MODE_CHANGE
            }
            KeyboardType.LICENSE_PLATE_PROVINCE -> {
                keyboardType = KeyboardType.LICENSE_PLATE_NUMBER
            }
            KeyboardType.LICENSE_PLATE_NUMBER -> {
                keyboardType = KeyboardType.LICENSE_PLATE_PROVINCE
            }
        }

        switchKeyboard()
    }

    /**
     * 更多
     */
    private fun keyMore(){

        when(keyboardType){
            KeyboardType.NORMAL -> {
                keyboardType = KeyboardType.NORMAL_MORE
            }
            KeyboardType.NORMAL_MODE_CHANGE -> {
                keyboardType = KeyboardType.NORMAL_MORE
            }
            KeyboardType.LICENSE_PLATE -> {
                keyboardType = KeyboardType.LICENSE_PLATE_MORE
            }
            KeyboardType.LICENSE_PLATE_MODE_CHANGE -> {
                keyboardType = KeyboardType.LICENSE_PLATE_MORE
            }
            KeyboardType.CUSTOM -> {
                keyboardType = KeyboardType.CUSTOM_MORE
            }
            KeyboardType.CUSTOM_MODE_CHANGE -> {
                keyboardType = KeyboardType.CUSTOM_MORE
            }

        }

        switchKeyboard()
    }

    /**
     * 输入
     */
    private fun keyInput(primaryCode: Int){
        currentEditText?.let {
            val start = it.selectionStart
            val end = it.selectionEnd

            it.text?.replace(start,end, primaryCode.toChar().toString())
            if(isCap && !isAllCaps){//如果当前是大写键盘，并且并且没有锁定，则自动变换成小写键盘
                isCap = false
                isAllCaps = false
                toLowerCaseKey(currentKeyboard)

                keyboardView?.run {
                    setCap(isCap)
                    setAllCaps(isAllCaps)
                    keyboard = currentKeyboard
                }

            }

        }
    }

    /**
     * 播放音效
     */
    private fun playSoundEffect(effectType: Int = AudioManager.FX_KEYPRESS_STANDARD){
        if(isPlaySoundEffect){
            if(audioManager == null){
                try{
                    audioManager = (context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager)
                }catch (e: Exception){
                    Log.w(TAG,e)
                }

            }
            audioManager?.playSoundEffect(effectType,.5f)

        }
    }

    /**
     * 震动
     */
    private fun sendVibrationEffect(){
        if(isVibrationEffect){
            if(vibrator == null){
                vibrator = (context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator)
            }

            try {
                //震动
                vibrator?.let {
                    if (Build.VERSION.SDK_INT >= 26) {
                        it.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
                    }else{
                        it.vibrate(16L)
                    }
                }
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
    }


    /**
     * 按下
     */
    private fun keyDown(keycode: Int,action: Int = KeyEvent.ACTION_DOWN){
        currentEditText?.let {
            it.onKeyDown(keycode, KeyEvent(action,keycode))
        }
    }

    /**
     * 触发删除
     */
    private fun keyDelete(){
        keyDown(KeyEvent.KEYCODE_DEL)
    }

    /**
     * 触发自定义可扩展按键
     */
    private fun keyExtra(primaryCode: Int){
        Log.d(TAG,"primaryCode:$primaryCode")
        onKeyExtraListener?.onKey(currentEditText,primaryCode)
    }

    /**
     * 触发Shift，切换大小字母键盘
     */
    private fun keyShift(){

        //将键盘进行大小写键盘切换

        if(isAllCaps){//上次状态为大写锁定时，转换为小写
            toLowerCaseKey(currentKeyboard)
        }else{//反之上次状态即为小写时，转换为大写
            toUpperCaseKey(currentKeyboard)
        }

        when {
            isAllCaps -> {//上次状态为锁定时，此次状态将改变为小写，将变量状态改变
                isAllCaps = false
                isCap = false
            }
            isCap -> {//上次状态为非锁定，此次状态改变为锁定
                isAllCaps = true
            }
            else -> {//上次状态为小写（默认）,此次状态改变为大写
                isCap = true
                isAllCaps = false
            }
        }

        keyboardView?.let {
            it.setCap(isCap)
            it.setAllCaps(isAllCaps)
            it.keyboard = currentKeyboard
        }



    }

    /**
     * 转换为大写
     */
    private fun toUpperCaseKey(keyboard: Keyboard){
        keyboard.run {
            for(key in keys){
                if(key.label?.length == 1){// 一个字符
                    var c = key.label.toString()[0]
                    if(c.isLowerCase()){ //是小写字母
                        //转换为大写
                        val letter = c.toUpperCase()
                        key.label = letter.toString()
                        key.codes[0] = letter.toInt()
                    }

                }
            }
        }

    }

    /**
     * 转换为小写
     */
    private fun toLowerCaseKey(keyboard: Keyboard){
        keyboard.run {
            for(key in keys){
                if(key.label?.length == 1){// 一个字符
                    var c = key.label.toString()[0]
                    if(c.isUpperCase()){ //是大写字母
                        //转换为小写
                        val letter = c.toLowerCase()
                        key.label = letter.toString()
                        key.codes[0] = letter.toInt()
                    }

                }
            }
        }

    }


    /**
     * 键盘类型
     */
    object KeyboardType{
        /**
         * 默认键盘 - 字母带符号
         */
        const val NORMAL = 0x00000001
        /**
         * 默认键盘 - 切换键盘
         */
        internal const val NORMAL_MODE_CHANGE = 0x00000002
        /**
         * 默认键盘 - 更多
         */
        internal const val NORMAL_MORE = 0x00000003

        /**
         * 字母键盘
         */
        const val LETTER = 0x00000011

        /**
         * 仅小写字母键盘
         */
        const val LOWERCASE_LETTER_ONLY = 0x00000101
        /**
         * 仅大写字母键盘
         */
        const val UPPERCASE_LETTER_ONLY = 0x00000102

        /**
         * 字母+数字键盘
         */
        const val LETTER_NUMBER = 0x00000201

        /**
         * 数字键盘
         */
        const val NUMBER = 0x00000301
        /**
         * 浮点数键盘（数字加“.”符号）
         */
        const val NUMBER_DECIMAL = 0x00000302

        /**
         * 电话拨号键盘（数字加“-”符号）
         */
        const val PHONE = 0x00000303

        /**
         * 身份证键盘
         */
        const val ID_CARD = 0x00000304
        /**
         * 车牌键盘 - 车牌 -> 归属地 + 切换车牌号
         */
        const val LICENSE_PLATE = 0x00000401

        /**
         * 车牌键盘- 切换 -> 车牌号
         */
        internal const val LICENSE_PLATE_MODE_CHANGE = 0x00000402

        /**
         * 车牌键盘 - 更多
         */
        internal const val LICENSE_PLATE_MORE = 0x00000403

        /**
         * 车牌键盘 - 车牌 相对于 LICENSE_PLATE 少了更多相关的
         */
        const val LICENSE_PLATE_PROVINCE = 0x00000404

        /**
         * 车牌键盘 - 车牌号
         */
        internal const val LICENSE_PLATE_NUMBER = 0x00000405

        /**
         * 预留自定义键盘类型
         */
        const val CUSTOM = 0x00001001
        /**
         * 预留自定义键盘类型 - 键盘模式切换
         */
        const val CUSTOM_MODE_CHANGE = 0x00001002
        /**
         * 预留自定义键盘类型 - 更多
         */
        const val CUSTOM_MORE = 0x00001003

    }
}