package com.king.keyboard.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.SoundEffectConstants
import com.king.keyboard.KingKeyboard
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var kingKeyboard : KingKeyboard

    private var isVibrationEffectEnabled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //初始化KingKeyboard
        kingKeyboard = KingKeyboard(this,keyboardParent)
        //然后将EditText注册到KingKeyboard即可
        kingKeyboard.register(et1,KingKeyboard.KeyboardType.NORMAL)
        kingKeyboard.register(et2,KingKeyboard.KeyboardType.LETTER)
        kingKeyboard.register(et3,KingKeyboard.KeyboardType.LOWERCASE_LETTER_ONLY)
        kingKeyboard.register(et4,KingKeyboard.KeyboardType.UPPERCASE_LETTER_ONLY)
        kingKeyboard.register(et5,KingKeyboard.KeyboardType.LETTER_NUMBER)
        kingKeyboard.register(et6,KingKeyboard.KeyboardType.NUMBER)
        kingKeyboard.register(et7,KingKeyboard.KeyboardType.NUMBER_DECIMAL)
        kingKeyboard.register(et8,KingKeyboard.KeyboardType.PHONE)
        kingKeyboard.register(et9,KingKeyboard.KeyboardType.ID_CARD)
        kingKeyboard.register(et10,KingKeyboard.KeyboardType.LICENSE_PLATE)
        kingKeyboard.register(et11,KingKeyboard.KeyboardType.LICENSE_PLATE_PROVINCE)

        /*
         * 如果目前所支持的键盘满足不了您的需求，您也可以自定义键盘，KingKeyboard对外提供自定义键盘类型。
         * 自定义步骤也非常简单，只需自定义键盘的xml布局，然后将EditText注册到对应的自定义键盘类型即可
         *
         * 1. 自定义键盘Custom，自定义方法setKeyboardCustom，键盘类型为{@link KeyboardType#CUSTOM}
         * 2. 自定义键盘CustomModeChange，自定义方法setKeyboardCustomModeChange，键盘类型为{@link KeyboardType#CUSTOM_MODE_CHANGE}
         * 3. 自定义键盘CustomMore，自定义方法setKeyboardCustomMore，键盘类型为{@link KeyboardType#CUSTOM_MORE}
         *
         * xmlLayoutResId 键盘布局的资源文件，其中包含键盘布局和键值码等相关信息
         */
        kingKeyboard.setKeyboardCustom(R.xml.keyboard_custom)
//        kingKeyboard.setKeyboardCustomModeChange(xmlLayoutResId)
//        kingKeyboard.setKeyboardCustomMore(xmlLayoutResId)
        kingKeyboard.register(et12,KingKeyboard.KeyboardType.CUSTOM)

        isVibrationEffectEnabled = kingKeyboard.isVibrationEffectEnabled()

        btn.setOnClickListener{
            btn.text = if(isVibrationEffectEnabled) "Enabled" else "Disabled"
            isVibrationEffectEnabled = !isVibrationEffectEnabled
            kingKeyboard.setVibrationEffectEnabled(isVibrationEffectEnabled)
        }

    }

    /**
     * 在Activity或Fragment的生命周期中调用对应的方法
     */
    override fun onResume() {
        super.onResume()
        kingKeyboard.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        kingKeyboard.onDestroy()
    }
}
