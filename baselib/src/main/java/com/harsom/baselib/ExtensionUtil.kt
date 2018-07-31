package com.harsom.baselib

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.Toast

var Activity.mContext:Context
    get(): Context {
        return this
    }
    set(value) {
        this.mContext = value
    }

fun Context.showToast(msg:String){
    Toast.makeText(applicationContext,msg, Toast.LENGTH_SHORT).show()
}

@SuppressLint("PrivateApi")
fun Context.getStatusBarHeight(): Int {
    var height = 0
    try {
        val c = Class.forName("com.android.internal.R\$dimen")
        val o = c.newInstance()
        val field = c.getField("status_bar_height")
        val x = (field.get(o) as Int).toInt()
        height = resources.getDimensionPixelSize(x)
    } catch (var6: Exception) {
        var6.printStackTrace()
    }
    return height
}

fun Context.getHeightInPx(): Int {
    return resources.displayMetrics.heightPixels
}

fun Context.getWidthInPx(): Int {
    return resources.displayMetrics.widthPixels
}

fun View.dp2px(dipValue: Float): Int {
    val scale = this.resources.displayMetrics.density
    return (dipValue * scale + 0.5f).toInt()
}

fun View.px2dp(pxValue: Float): Int {
    val scale = this.resources.displayMetrics.density
    return (pxValue / scale + 0.5f).toInt()
}

//无符号整形转成有符号的长整型
fun Int.toLongId(): Long {
    return this.toLong() and 0xffffffffL
}
