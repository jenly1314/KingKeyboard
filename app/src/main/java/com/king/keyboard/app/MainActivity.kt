package com.king.keyboard.app

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.king.keyboard.KingKeyboard
import com.king.keyboard.KingKeyboard.KeyboardType
import com.king.keyboard.app.databinding.ActivityMainBinding

/**
 * 示例
 *
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 * <p>
 * <a href="https://dgithub.xyz/jenly1314">Follow me</a>
 */
class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private lateinit var kingKeyboard: KingKeyboard

    private var isVibrationEffectEnabled = false

    private var beforeCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        // 初始化KingKeyboard
        kingKeyboard = KingKeyboard(this)
//        kingKeyboard = KingKeyboard(this, binding.keyboardParent)
        // 然后将EditText注册到KingKeyboard即可
        kingKeyboard.register(binding.et1, KeyboardType.NORMAL)
        kingKeyboard.register(binding.et2, KeyboardType.LETTER)
        kingKeyboard.register(binding.et3, KeyboardType.LOWERCASE_LETTER_ONLY)
        kingKeyboard.register(binding.et4, KeyboardType.UPPERCASE_LETTER_ONLY)
        kingKeyboard.register(binding.et5, KeyboardType.LETTER_NUMBER)
        kingKeyboard.register(binding.et6, KeyboardType.NUMBER)
        kingKeyboard.register(binding.et7, KeyboardType.NUMBER_DECIMAL)
        kingKeyboard.register(binding.et8, KeyboardType.PHONE)
        kingKeyboard.register(binding.et9, KeyboardType.ID_CARD)
        kingKeyboard.register(binding.et10, KeyboardType.LICENSE_PLATE)
        kingKeyboard.register(binding.et11, KeyboardType.LICENSE_PLATE_PROVINCE)

        // 通过监听输入框内容改变，来通过发送功能键来切换键盘（这里只是举例展示kingKeyboard.sendKey的用法，具体怎么用还需根据需求场景去决定）
        binding.et11.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                beforeCount = s?.length ?: 0
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                when (s?.length) {
                    0 -> {// 车牌键盘：如果输入的内容长度改变为0，并且当前的键盘不是省份键盘模式时，通过发送“返回”功能按键值，让键盘自动切换到省份键盘模式
                        if (kingKeyboard.getKeyboardType() != KeyboardType.LICENSE_PLATE_PROVINCE) {
                            kingKeyboard.sendKey(KingKeyboard.KEYCODE_BACK)
                        }
                    }

                    1 -> {// 车牌键盘：如果输入的内容长度从0改变为1，并且当前的键盘为省份键盘模式时，通过发送“模式改变”功能按键值，让键盘自动切换到字母键盘模式
                        if (beforeCount == 0 && kingKeyboard.getKeyboardType() == KeyboardType.LICENSE_PLATE_PROVINCE) {
                            kingKeyboard.sendKey(KingKeyboard.KEYCODE_MODE_CHANGE)
                        }
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })


        /*
         * 如果目前所支持的键盘满足不了您的需求，您也可以自定义键盘，KingKeyboard对外提供自定义键盘类型。
         * 自定义步骤也非常简单，只需自定义对应的键盘布局，然后将EditText注册到对应的自定义键盘类型即可
         *
         * `KeyboardType`中预留的自定义键盘类型说明：
         * 1. `KeyboardType.CUSTOM`，对应的自定义键盘布局方法：`kingKeyboard.setKeyboardCustom`
         * 2. `KeyboardType.CUSTOM_MODE_CHANGE`，对应的自定义键盘布局方法：`kingKeyboard.setKeyboardCustomModeChange`
         * 3. `KeyboardType.CUSTOM_MORE`，对应的自定义键盘布局方法：`kingKeyboard.setKeyboardCustomMore`
         *
         * xmlLayoutResId 键盘布局的资源文件，其中包含键盘布局和键值码等相关信息
         */
        kingKeyboard.setKeyboardCustom(R.xml.keyboard_custom)
//        kingKeyboard.setKeyboardCustomModeChange(xmlLayoutResId)
//        kingKeyboard.setKeyboardCustomMore(xmlLayoutResId)
        kingKeyboard.register(binding.et12, KeyboardType.CUSTOM)

        isVibrationEffectEnabled = kingKeyboard.isVibrationEffectEnabled()

        binding.btnDialog.setOnClickListener {
            showEditTextDialog()
        }

        binding.cbVibration.setOnCheckedChangeListener { _, isChecked ->
            isVibrationEffectEnabled = isChecked
            kingKeyboard.setVibrationEffectEnabled(isVibrationEffectEnabled)
        }

    }

    override fun onBackPressed() {
        if (kingKeyboard.isShow()) {
            kingKeyboard.hide()
        } else {
            super.onBackPressed()
        }
    }

    /**
     * 带输入的对话框
     */
    private fun showEditTextDialog() {
        kingKeyboard.hide()

        val dialog = Dialog(this, R.style.dialogStyle)

        val view = LayoutInflater.from(this).inflate(R.layout.dialog_edit, null)

        val keyboardParent = view.findViewById<ViewGroup>(R.id.keyboardParent)
        val etDialogContent = view.findViewById<EditText>(R.id.etDialogContent)

        val btnDialogConfirm = view.findViewById<Button>(R.id.btnDialogConfirm)
        val btnDialogCancel = view.findViewById<Button>(R.id.btnDialogCancel)
        btnDialogConfirm.setOnClickListener {
            if (etDialogContent.text.isNotEmpty()) {
                Toast.makeText(this, etDialogContent.text, Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }
        btnDialogCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setContentView(view)

        dialog.window?.apply {
            val lp = attributes
            lp.width = view.layoutParams.width
            lp.height = view.layoutParams.height
            attributes = lp
        }

        // 初始化KingKeyboard
        val kingKeyboard = KingKeyboard(dialog, keyboardParent)
        kingKeyboard.register(etDialogContent, KeyboardType.NORMAL)
        kingKeyboard.setVibrationEffectEnabled(isVibrationEffectEnabled)

        dialog.setOnKeyListener { _, keyCode, event ->
            when {
                keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP && kingKeyboard.isShow() -> {
                    kingKeyboard.hide()
                    true
                }

                else -> false
            }
        }

        dialog.show()

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
