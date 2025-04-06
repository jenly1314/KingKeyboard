@file:Suppress("unused")

package com.king.keyboard

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

/**
 * 扩展函数
 *
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 * <p>
 * <a href="https://dgithub.xyz/jenly1314">Follow me</a>
 */

/**
 * 显示系统输入法
 */
fun View.showSystemInputMethod() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}

/**
 * 隐藏系统输入法
 */
fun View.hideSystemInputMethod() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

/**
 * 是否显示
 */
var View.isVisible: Boolean
    get() = visibility == View.VISIBLE
    set(value) {
        visibility = if (value) View.VISIBLE else View.GONE
    }
