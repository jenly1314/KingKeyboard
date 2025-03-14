package com.king.keyboard.app

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.king.keyboard.KingKeyboard
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var kingKeyboard : KingKeyboard

    private var isVibrationEffectEnabled = false

    private var beforeCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //初始化KingKeyboard
        kingKeyboard = KingKeyboard(this,keyboardParent)
        val config = kingKeyboard.getKeyboardViewConfig()
        config?.let {c->
            c.spaceDrawable = ContextCompat.getDrawable(this, R.mipmap.ic_launcher)
            kingKeyboard.setKeyboardViewConfig(c)
        }
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


        //通过监听输入框内容改变，来通过发送功能键来切换键盘（这里只是举例展示kingKeyboard.sendKey的用法，具体怎么用还需根据需求场景去决定）
        et11.addTextChangedListener(object : TextWatcher{
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


        btnDialog.setOnClickListener {
            showEditDialog()
        }

        btn.setOnClickListener{
            btn.text = if(isVibrationEffectEnabled) "Enabled" else "Disabled"
            isVibrationEffectEnabled = !isVibrationEffectEnabled
            kingKeyboard.setVibrationEffectEnabled(isVibrationEffectEnabled)
        }

    }

    /**
     * 带输入的对话框
     */
    private fun showEditDialog(){
        val dialog = Dialog(this,R.style.dialogStyle)

        val view = LayoutInflater.from(this).inflate(R.layout.dialog_edit,null)

        val keyboardParent = view.findViewById<ViewGroup>(R.id.keyboardParent)
        val etDialogContent = view.findViewById<EditText>(R.id.etDialogContent)

        val btnDialogConfirm = view.findViewById<Button>(R.id.btnDialogConfirm)
        val btnDialogCancel = view.findViewById<Button>(R.id.btnDialogCancel)
        btnDialogConfirm.setOnClickListener {
            if(!TextUtils.isEmpty(etDialogContent.text)){
                Toast.makeText(MainActivity@this,etDialogContent.text,Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }
        btnDialogCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setContentView(view)

        dialog.window?.apply {
            val lp = attributes
            lp.width = WindowManager.LayoutParams.MATCH_PARENT
            lp.height = WindowManager.LayoutParams.MATCH_PARENT

            attributes = lp
        }

        //初始化KingKeyboard
        val kingKeyboard = KingKeyboard(dialog,keyboardParent)
        kingKeyboard.register(etDialogContent,KingKeyboard.KeyboardType.NORMAL)

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
