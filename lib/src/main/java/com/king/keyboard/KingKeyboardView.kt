package com.king.keyboard

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat

/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
open class KingKeyboardView : KeyboardView {


    private var isCap = false

    private var isAllCaps = false

    private lateinit var config: Config

    private val paint by lazy { Paint() }

    companion object {
        const val iconRatio = 0.5f

    }


    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes){
        init(context, attrs)
    }


    private fun init(context: Context, attrs: AttributeSet?) {
        config = Config(context)

        var a = context.obtainStyledAttributes(attrs, R.styleable.KingKeyboardView)

        a.indexCount.let {
            config.run {
                for (i in 0 until it) {
                    when (val attr = a.getIndex(i)) {
                        R.styleable.KingKeyboardView_kkbDeleteDrawable -> deleteDrawable = a.getDrawable(attr)
                        R.styleable.KingKeyboardView_kkbCapitalDrawable -> capitalDrawable = a.getDrawable(attr)
                        R.styleable.KingKeyboardView_kkbCapitalLockDrawable -> capitalLockDrawable = a.getDrawable(attr)
                        R.styleable.KingKeyboardView_kkbCancelDrawable -> cancelDrawable = a.getDrawable(attr)
                        R.styleable.KingKeyboardView_kkbCancelDrawable -> spaceDrawable = a.getDrawable(attr)
                        R.styleable.KingKeyboardView_android_labelTextSize -> labelTextSize = a.getDimensionPixelSize(attr, labelTextSize)
                        R.styleable.KingKeyboardView_android_keyTextSize -> keyTextSize = a.getDimensionPixelSize(attr, keyTextSize)
                        R.styleable.KingKeyboardView_android_keyTextColor -> keyTextColor = a.getColor(attr, keyTextColor)
                        R.styleable.KingKeyboardView_kkbKeyIconColor -> keyIconColor = a.getColor(attr, ContextCompat.getColor(context, R.color.king_keyboard_key_icon_color))
                        R.styleable.KingKeyboardView_kkbKeySpecialTextColor -> keySpecialTextColor = a.getColor(attr, keySpecialTextColor)
                        R.styleable.KingKeyboardView_kkbKeyDoneTextColor -> keyDoneTextColor = a.getColor(attr, keyDoneTextColor)
                        R.styleable.KingKeyboardView_kkbKeyNoneTextColor -> keyNoneTextColor = a.getColor(attr, keyNoneTextColor)
                        R.styleable.KingKeyboardView_android_keyBackground -> keyBackground = a.getDrawable(attr)
                        R.styleable.KingKeyboardView_kkbSpecialKeyBackground -> specialKeyBackground = a.getDrawable(attr)
                        R.styleable.KingKeyboardView_kkbDoneKeyBackground -> doneKeyBackground = a.getDrawable(attr)
                        R.styleable.KingKeyboardView_kkbNoneKeyBackground -> noneKeyBackground = a.getDrawable(attr)
                        R.styleable.KingKeyboardView_kkbKeyDoneTextSize -> keyDoneTextSize = a.getDimensionPixelSize(attr, keyDoneTextSize)
                        R.styleable.KingKeyboardView_kkbKeyDoneText -> keyDoneText = a.getString(attr)
                    }
                }
            }
            a.recycle()
        }

        paint.textAlign = Paint.Align.CENTER
        paint.isAntiAlias = true

    }

    fun getConfig(): Config {
        return config
    }

    fun setConfig(config: Config) {
        this.config = config
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawKeyboard(canvas, keyboard?.keys)
    }

    /**
     * 绘制键盘
     */
    private fun drawKeyboard(canvas: Canvas,keys: List<Keyboard.Key>?){
        keys?.let {
            for (key in it) {
                drawKey(canvas, key)
            }
        }
    }

    /**
     * 绘制键盘按键
     */
    private fun drawKey(canvas: Canvas, key: Keyboard.Key) {
        when (key.codes[0]) {
            KingKeyboard.KEYCODE_SHIFT -> drawShiftKey(canvas, key)
            KingKeyboard.KEYCODE_MODE_CHANGE -> drawKey(canvas, key, config.specialKeyBackground, config.keySpecialTextColor)
            KingKeyboard.KEYCODE_CANCEL -> drawCancelKey(canvas, key)
            KingKeyboard.KEYCODE_DONE -> drawDoneKey(canvas, key)
            KingKeyboard.KEYCODE_DELETE -> drawDeleteKey(canvas, key)
            KingKeyboard.KEYCODE_ALT -> drawAltKey(canvas, key)
            KingKeyboard.KEYCODE_SPACE -> drawKey(canvas, key, config.keyBackground, config.keyTextColor, config.spaceDrawable)
            KingKeyboard.KEYCODE_NONE -> drawNoneKey(canvas, key)
            KingKeyboard.KEYCODE_MODE_BACK -> drawKey(canvas, key, config.specialKeyBackground, config.keySpecialTextColor)
            KingKeyboard.KEYCODE_BACK -> drawKey(canvas, key, config.specialKeyBackground, config.keySpecialTextColor)
            KingKeyboard.KEYCODE_MORE -> drawKey(canvas, key, config.specialKeyBackground, config.keySpecialTextColor)
            in -399..-300 -> drawKey(canvas, key, config.specialKeyBackground, config.keySpecialTextColor)
//            else -> drawKey(canvas,key,keyBackground)
        }
    }

    /**
     * 绘制Cancel键，常见于关闭键盘键
     */
    private fun drawCancelKey(canvas: Canvas, key: Keyboard.Key) {
        drawKey(canvas, key, config.specialKeyBackground, config.keySpecialTextColor, config.cancelDrawable)
    }

    /**
     * 绘制Done键，常见于右下角蓝色的“确定”按键
     */
    private fun drawDoneKey(canvas: Canvas, key: Keyboard.Key) {
        drawKey(canvas, key, config.doneKeyBackground, config.keyDoneTextColor, null, true)
    }

    /**
     * 绘制Delete键
     */
    private fun drawNoneKey(canvas: Canvas, key: Keyboard.Key) {
        drawKey(canvas, key, config.noneKeyBackground, config.keyNoneTextColor)
    }

    /**
     * 绘制Alt键
     */
    private fun drawAltKey(canvas: Canvas, key: Keyboard.Key) {
        drawKey(canvas, key, config.specialKeyBackground, config.keySpecialTextColor)
    }

    /**
     * 绘制Delete键
     */
    private fun drawDeleteKey(canvas: Canvas, key: Keyboard.Key) {
        drawKey(canvas, key, config.specialKeyBackground, config.keySpecialTextColor, config.deleteDrawable)
    }

    /**
     * 绘制Shift键
     */
    private fun drawShiftKey(canvas: Canvas, key: Keyboard.Key) {
        when {
            isAllCaps ->  drawKey(canvas, key, config.specialKeyBackground, config.keySpecialTextColor, config.capitalLockDrawable)
            isCap -> drawKey(canvas, key, config.specialKeyBackground, config.keySpecialTextColor, config.capitalDrawable)
            else -> drawKey(canvas, key, config.specialKeyBackground, config.keySpecialTextColor, config.lowerDrawable)
        }
    }

    /**
     * 绘制键盘按键
     */
    private fun drawKey(canvas: Canvas, key: Keyboard.Key, keyBackground: Drawable?, textColor: Int, iconDrawable: Drawable? = key.icon, isDone: Boolean = false) {
        //绘制按键背景
        keyBackground?.run {
            if (key.codes[0] != 0) {
                state = key.currentDrawableState
            }

            setBounds(
                key.x.plus(paddingLeft),
                key.y.plus(paddingTop),
                key.x.plus(paddingLeft).plus(key.width),
                key.y.plus(paddingTop).plus(key.height)
            )
            draw(canvas)
        }

        if (isDone) {
            config.keyDoneText?.let {
                key.label = it
            }

        }

        //绘制键盘图标
        iconDrawable?.run {

            val drawable = DrawableCompat.wrap(this)
            config.keyIconColor?.takeIf { it != 0 }?.let {
                drawable.setTint(it)
            }

            key.icon = drawable

            var iconWidth = key.icon.intrinsicWidth.toFloat()
            var iconHeight = key.icon.intrinsicHeight.toFloat()

            val widthRatio = iconWidth.div(key.width.toFloat())
            val heightRatio = iconHeight.div(key.height.toFloat())

            if (widthRatio <= heightRatio) {//当图标的宽占比小于等于高占比时，以高度比例为基准并控制在iconRatio比例范围内，进行同比例缩放

                val ratio = heightRatio.coerceAtMost(iconRatio)
                iconWidth = iconWidth.div(heightRatio).times(ratio)
                iconHeight = iconHeight.div(heightRatio).times(ratio)

            } else {//反之，则以宽度比例为基准并控制在iconRatio比例范围内，进行同比例缩放

                val ratio = widthRatio.coerceAtMost(iconRatio)
                iconWidth = iconWidth.div(widthRatio).times(ratio)
                iconHeight = iconHeight.div(widthRatio).times(ratio)

            }

            val left = key.x.plus(paddingLeft).plus(key.width.minus(iconWidth).div(2f)).toInt()
            val top = key.y.plus(paddingTop).plus(key.height.minus(iconHeight).div(2f)).toInt()
            val right = left.plus(iconWidth).toInt()
            val bottom = top.plus(iconHeight).toInt()
            key.icon.setBounds(left, top, right, bottom)
            key.icon.draw(canvas)
            this
        } ?: key.label?.let {
            //绘制键盘文字
            if (isDone) {
                paint.textSize = config.keyDoneTextSize.toFloat()
            } else if (it.length > 1 && key.codes.size < 2) {// 键盘key内容多个字符
                paint.textSize = config.labelTextSize.toFloat()
            } else {
                paint.textSize = config.keyTextSize.toFloat()
            }
            paint.color = textColor
            paint.typeface = Typeface.DEFAULT

            canvas.drawText(
                it.toString(),
                key.x.plus(paddingLeft).plus(key.width.div(2f)),
                key.y.plus(paddingTop).plus(key.height.div(2.0f)).plus(
                    paint.textSize.minus(paint.descent()).div(2.0f)
                ),
                paint
            )

        }

    }


    fun setCap(isCap: Boolean) {
        this.isCap = isCap
    }

    fun isCap(): Boolean {
        return isCap
    }

    fun setAllCaps(isAllCaps: Boolean) {
        this.isAllCaps = isAllCaps
    }

    fun isAllCaps(): Boolean {
        return isAllCaps
    }

    /**
     * Config为KingKeyboard的配置类，方便统一管理配置信息
     */
    open class Config(context: Context) {

        var deleteDrawable = context.getDrawable(R.drawable.king_keyboard_key_delete)
        var lowerDrawable = context.getDrawable(R.drawable.king_keyboard_key_lower)
        var capitalDrawable = context.getDrawable(R.drawable.king_keyboard_key_cap)
        var capitalLockDrawable = context.getDrawable(R.drawable.king_keyboard_key_all_caps)
        var cancelDrawable = context.getDrawable(R.drawable.king_keyboard_key_cancel)
        var spaceDrawable = context.getDrawable(R.drawable.king_keyboard_key_space)

        var labelTextSize = context.resources.getDimensionPixelSize(R.dimen.king_keyboard_label_text_size)

        var keyTextSize = context.resources.getDimensionPixelSize(R.dimen.king_keyboard_text_size)

        var keyTextColor = ContextCompat.getColor(context, R.color.king_keyboard_key_text_color)

        var keyIconColor: Int? = null

        var keySpecialTextColor = ContextCompat.getColor(context, R.color.king_keyboard_key_special_text_color)

        var keyDoneTextColor = ContextCompat.getColor(context, R.color.king_keyboard_key_done_text_color)

        var keyNoneTextColor = ContextCompat.getColor(context, R.color.king_keyboard_key_none_text_color)

        var keyBackground = context.getDrawable(R.drawable.king_keyboard_key_bg)

        var specialKeyBackground = context.getDrawable(R.drawable.king_keyboard_special_key_bg)

        var doneKeyBackground = context.getDrawable(R.drawable.king_keyboard_done_key_bg)
        var noneKeyBackground = context.getDrawable(R.drawable.king_keyboard_none_key_bg)

        var keyDoneTextSize = context.resources.getDimensionPixelSize(R.dimen.king_keyboard_done_text_size)

        var keyDoneText: CharSequence? = context.getString(R.string.king_keyboard_key_done_text)

    }


}