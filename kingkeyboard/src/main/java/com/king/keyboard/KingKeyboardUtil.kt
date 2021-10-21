package com.king.keyboard

import android.content.Context
import android.util.SparseArray
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi

/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */

/**
 * 判断是否是小写字母
 */
fun Char.isLowerCase(c: Char): Boolean{
    return c.toInt() in 97..122
}

/**
 * 判断是否是大写字母
 */
fun Char.isUpperCase(c: Char): Boolean{
    return c.toInt() in 65..90
}

/**
 * 显示系统输入法
 */
fun View.showSystemInputMethod(){
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(this,InputMethodManager.SHOW_IMPLICIT)
}

/**
 * 隐藏系统输入法
 */
fun View.hideSystemInputMethod(){
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken,0)
}

var View.isVisible: Boolean
    get() = visibility == View.VISIBLE
    set(value) {
        visibility = if (value) View.VISIBLE else View.GONE
    }

/** Returns true if the collection contains [key]. */
@RequiresApi(16)
fun <T> SparseArray<T>.containsKey(key: Int) = indexOfKey(key) >= 0

/** Allows the use of the index operator for storing values in the collection. */
@RequiresApi(16)
operator fun <T> SparseArray<T>.set(key: Int, value: T) = put(key, value)
