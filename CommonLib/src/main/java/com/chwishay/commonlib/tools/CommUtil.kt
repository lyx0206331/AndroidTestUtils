package com.chwishay.commonlib.tools

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.chwishay.commonlib.BuildConfig
import org.jetbrains.annotations.NotNull

//  ┏┓　　　┏┓
//┏┛┻━━━┛┻┓
//┃　　　　　　　┃
//┃　　　━　　　┃
//┃　┳┛　┗┳　┃
//┃　　　　　　　┃
//┃　　　┻　　　┃
//┃　　　　　　　┃
//┗━┓　　　┏━┛
//   ┃　　　┃   神兽保佑
//   ┃　　　┃   代码无BUG！
//   ┃　　　┗━━━┓
//   ┃　　　　　　　┣┓
//   ┃　　　　　　　┏┛
//   ┗┓┓┏━┳┓┏┛
//     ┃┫┫　┃┫┫
//     ┗┻┛　┗┻┛
/**
 * Author:RanQing
 * Create time:20-8-4 上午11:53
 * Description:
 **/
object CommUtil {
    @JvmOverloads
    @JvmStatic
    fun logD(tag: String, msg: String, tr: Throwable? = null) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, msg, tr)
        }
    }

    @JvmOverloads
    @JvmStatic
    fun logE(tag: String, msg: String, tr: Throwable? = null) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, msg, tr)
        }
    }

    @JvmOverloads
    @JvmStatic
    fun logI(tag: String, msg: String, tr: Throwable? = null) {
        if (BuildConfig.DEBUG) {
            Log.i(tag, msg, tr)
        }
    }

    @JvmOverloads
    @JvmStatic
    fun logW(tag: String, msg: String, tr: Throwable? = null) {
        if (BuildConfig.DEBUG) {
            Log.w(tag, msg, tr)
        }
    }

    @JvmOverloads
    @JvmStatic
    fun toastShort(@NotNull context: Context, msg: String = "") {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    @JvmOverloads
    @JvmStatic
    fun toastLong(@NotNull context: Context, msg: String = "") {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
    }
}