package com.chwishay.commonlib.tools

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.annotation.StringRes
import com.chwishay.commonlib.BuildConfig
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*
import kotlin.experimental.and

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
 * Create time:20-8-4 下午1:46
 * Description:Kotlin常用扩展函数
 **/

object DateFormatStr {
    const val FORMAT_YMDHMS_CN = "yyyy年MM月dd日 HH时mm分ss秒"
    const val FORMAT_YMDHMS = "yyyy/MM/dd HH:mm:ss"
    const val FORMAT_YMD_CN = "yyyy年MM月dd日"
    const val FORMAT_YMD = "yyyy/MM/dd"
    const val FORMAT_HMS_CN = "HH时mm分ss秒"
    const val FORMAT_HMS = "HH:mm:ss"
    const val FORMAT_YM_CN = "yyyy年MM月"
    const val FORMAT_YM = "yyyy/MM"
    const val FORMAT_MD_CN = "MM月dd日"
    const val FORMAT_MD = "MM/dd"
    const val FORMAT_HM_CN = "HH时mm分"
    const val FORMAT_HM = "HH:mm"
    const val FORMAT_MS_CN = "mm分ss秒"
    const val FORMAT_MS = "mm:ss"

    //带毫秒数格式化
    const val FORMAT_HMS_SSS = "HH:mm:ss.SSS"
}

fun Short?.orDefault(default: Short = 0): Short = this ?: default

fun Int?.orDefault(default: Int = 0): Int = this ?: default

fun Long?.orDefault(default: Long = 0L): Long = this ?: default

fun Float?.orDefault(default: Float = .0f): Float = this ?: default

fun Double?.orDefault(default: Double = .0): Double = this ?: default

fun Boolean?.orDefault(default: Boolean = false): Boolean = this ?: default

fun String?.orDefault(default: String = ""): String = this ?: default

fun String.logE(msg: String, tr: Throwable? = null) =
    if (BuildConfig.DEBUG) Log.e(this, msg, tr) else 0

fun String.logV(msg: String, tr: Throwable? = null) =
    if (BuildConfig.DEBUG) Log.v(this, msg, tr) else 0

fun String.logD(msg: String, tr: Throwable? = null) =
    if (BuildConfig.DEBUG) Log.d(this, msg, tr) else 0

fun String.logW(msg: String, tr: Throwable? = null) =
    if (BuildConfig.DEBUG) Log.w(this, msg, tr) else 0

fun String.logI(msg: String, tr: Throwable? = null) =
    if (BuildConfig.DEBUG) Log.i(this, msg, tr) else 0

fun Context.showShortToast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

fun Context.showLongToast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_LONG).show()

fun Context.showShortToast(@StringRes resId: Int) =
    Toast.makeText(this, resId, Toast.LENGTH_SHORT).show()

fun Context.showLongToast(@StringRes resId: Int) =
    Toast.makeText(this, resId, Toast.LENGTH_LONG).show()

fun Long.formatDateString(
    formatStr: String = DateFormatStr.FORMAT_YMD_CN,
    locale: Locale = Locale.CHINESE
): String = SimpleDateFormat(formatStr, locale).format(Date(this))

fun Date.formatDateString(
    formatStr: String = DateFormatStr.FORMAT_YMD_CN,
    locale: Locale = Locale.CHINESE
): String = SimpleDateFormat(formatStr, locale).format(this)

/**
 * 字节数组转换为浮点型(大端模式)
 */
fun ByteArray.read2FloatBE(offset: Int) =
    if (this == null || this.size < offset + 4) throw IllegalArgumentException("传入参数不正确")
    else java.lang.Float.intBitsToFloat(
        0xff000000.and(this[offset].toInt().shl(24).toLong())
            .or(0x00ff0000.and(this[offset + 1].toInt().shl(16)).toLong())
            .or(0x0000ff00.and(this[offset + 2].toInt().shl(8)).toLong())
            .or(0x000000ff.and(this[offset + 3].toInt()).toLong()).toInt()
    )

/**
 * 字节数组转换为浮点型(小端模式)
 */
fun ByteArray.read2FloatLE(offset: Int) =
    if (this == null || this.size < offset + 4) throw IllegalArgumentException("传入参数不正确")
    else java.lang.Float.intBitsToFloat(
        0xff000000.and(this[offset + 3].toInt().shl(24).toLong())
            .or(0x00ff0000.and(this[offset + 2].toInt().shl(16)).toLong())
            .or(0x0000ff00.and(this[offset + 1].toInt().shl(8)).toLong())
            .or(0x000000ff.and(this[offset].toInt()).toLong()).toInt()
    )

/**
 * 字节数组转换为字符串
 * @param seperator 间隔符
 */
fun ByteArray?.formatHexString(seperator: String = ""): String? =
    if (this == null || this.isEmpty()) {
        null
    } else {
        val sb = StringBuilder()
        this.forEach { item ->
            var hex = Integer.toHexString(item.toInt().and(0xff))
            if (hex.length == 1) hex = "0$this"
            sb.append("$hex$seperator")
        }
        sb.toString().trim()
    }