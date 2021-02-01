package com.chwishay.commonlib.tools

import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import com.chwishay.commonlib.BuildConfig
import com.chwishay.commonlib.baseComp.BaseApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

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

    const val FORMAT_HMS_SSS = "HH:mm:ss.SSS"
}

fun Byte?.orDefault(default: Byte = 0): Byte = this ?: default

fun Short?.orDefault(default: Short = 0): Short = this ?: default

fun Int?.orDefault(default: Int = 0): Int = this ?: default

fun Long?.orDefault(default: Long = 0L): Long = this ?: default

fun Float?.orDefault(default: Float = .0f): Float = this ?: default

fun Double?.orDefault(default: Double = .0): Double = this ?: default

fun Boolean?.orDefault(default: Boolean = false): Boolean = this ?: default

fun String?.orDefault(default: String = ""): String = this ?: default

fun String.logE(msg: String, tr: Throwable? = null, isForce: Boolean = false) =
    if (BuildConfig.DEBUG || isForce) Log.e(this, msg, tr) else 0

fun String.logV(msg: String, tr: Throwable? = null, isForce: Boolean = false) =
    if (BuildConfig.DEBUG || isForce) Log.v(this, msg, tr) else 0

fun String.logD(msg: String, tr: Throwable? = null, isForce: Boolean = false) =
    if (BuildConfig.DEBUG || isForce) Log.d(this, msg, tr) else 0

fun String.logW(msg: String, tr: Throwable? = null, isForce: Boolean = false) =
    if (BuildConfig.DEBUG || isForce) Log.w(this, msg, tr) else 0

fun String.logI(msg: String, tr: Throwable? = null, isForce: Boolean = false) =
    if (BuildConfig.DEBUG || isForce) Log.i(this, msg, tr) else 0

fun Context.showShortToast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

suspend fun showShortToast(msg: String) {
    withContext(Dispatchers.Main) {
        Toast.makeText(BaseApp.instance, msg, Toast.LENGTH_SHORT).show()
    }
}

fun Context.showLongToast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_LONG).show()

fun showLongToast(msg: String) = Toast.makeText(BaseApp.instance, msg, Toast.LENGTH_LONG).show()

fun Context.showShortToast(@StringRes resId: Int) =
    Toast.makeText(this, resId, Toast.LENGTH_SHORT).show()

fun Context.showLongToast(@StringRes resId: Int) =
    Toast.makeText(this, resId, Toast.LENGTH_LONG).show()

/**
 * 获取版本码
 */
fun Context.getVersionCode(): Int = packageManager.getPackageInfo(packageName, 0).versionCode

/**
 * 获取版本号
 */
fun Context.getVersionName(): String = packageManager.getPackageInfo(packageName, 0).versionName

inline fun Context.getColor1(@ColorRes colorId: Int) = this.resources.getColor(colorId)

/**
 * 格式化日期字符串
 */
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
fun ByteArray.read2FloatBE(offset: Int = 0) =
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
fun ByteArray.read2FloatLE(offset: Int = 0) =
    if (this == null || this.size < offset + 4) throw IllegalArgumentException("传入参数不正确")
    else java.lang.Float.intBitsToFloat(
        0xff000000.and(this[offset + 3].toInt().shl(24).toLong())
            .or(0x00ff0000.and(this[offset + 2].toInt().shl(16)).toLong())
            .or(0x0000ff00.and(this[offset + 1].toInt().shl(8)).toLong())
            .or(0x000000ff.and(this[offset].toInt()).toLong()).toInt()
    )

fun Int.toBytesLE() =
    byteArrayOf(this.toByte(), this.shr(8).toByte(), this.shr(16).toByte(), this.shr(24).toByte())

fun Int.toBytesBE() =
    byteArrayOf(this.shr(24).toByte(), this.shr(16).toByte(), this.shr(8).toByte(), this.toByte())

fun Short.toBytesLE() = byteArrayOf(this.toByte(), this.toInt().shr(8).toByte())

fun Short.toBytesBE() = byteArrayOf(this.toInt().shr(8).toByte(), this.toByte())

/**
 * 字节数组转换成Short(大端)
 */
fun ByteArray.read2ShortBE() =
    if (this == null) throw IllegalArgumentException("传入参数不正确")
    else {
        val bytes = when (this.size) {
            0 -> byteArrayOf(0, 0)
            1 -> byteArrayOf(0, this[0])
            else -> this
        }
        0xff00.and(bytes[0].toInt().shl(8)).or(0x00ff.and(bytes[1].toInt())).toShort()
    }

/**
 * 字节数组转换成Short(大端)
 */
fun ByteArray.read2UShortBE() =
    if (this == null) throw IllegalArgumentException("传入参数不正确")
    else {
        val bytes = when (this.size) {
            0 -> byteArrayOf(0, 0)
            1 -> byteArrayOf(0, this[0])
            else -> this
        }
        0xff00.and(bytes[0].toInt().shl(8)).or(0x00ff.and(bytes[1].toInt())).toUShort()
    }

/**
 * 字节数组转换成Short(小端)
 */
fun ByteArray.read2ShortLE() =
    if (this == null) throw IllegalArgumentException("传入参数不正确")
    else {
        val bytes = when (this.size) {
            0 -> byteArrayOf(0, 0)
            1 -> byteArrayOf(this[0], 0)
            else -> this
        }
        0x00ff.and(bytes[0].toInt()).or(0xff00.and(bytes[1].toInt().shl(8))).toShort()
    }

/**
 * 字节数组转换为整型(大端模式)
 */
fun ByteArray.read2IntBE(offset: Int = 0) =
    if (this == null) throw IllegalArgumentException("传入参数不正确")
    else {
        val arr = when (this.size) {
            0 -> byteArrayOf(0, 0, 0, 0)
            1 -> byteArrayOf(0, 0, 0, this[0])
            2 -> byteArrayOf(0, 0, this[1], this[0])
            3 -> byteArrayOf(0, this[2], this[1], this[0])
            else -> this
        }
        0xff000000.and(arr[offset].toInt().shl(24).toLong())
            .or(0x00ff0000.and(arr[offset + 1].toInt().shl(16)).toLong())
            .or(0x0000ff00.and(arr[offset + 2].toInt().shl(8)).toLong())
            .or(0x000000ff.and(arr[offset + 3].toInt()).toLong()).toInt()
    }

/**
 * 字节数组转换为整型(小端模式)
 */
fun ByteArray.read2IntLE(offset: Int = 0) =
    if (this == null) throw IllegalArgumentException("传入参数不正确")
    else {
        val arr = when (this.size) {
            0 -> byteArrayOf(0, 0, 0, 0)
            1 -> byteArrayOf(this[0], 0, 0, 0)
            2 -> byteArrayOf(this[1], this[0], 0, 0)
            3 -> byteArrayOf(this[2], this[1], this[0], 0)
            else -> this
        }
        0xff000000.and(arr[offset + 3].toInt().shl(24).toLong())
            .or(0x00ff0000.and(arr[offset + 2].toInt().shl(16)).toLong())
            .or(0x0000ff00.and(arr[offset + 1].toInt().shl(8)).toLong())
            .or(0x000000ff.and(arr[offset].toInt()).toLong()).toInt()
    }

/**
 * 字节数组转换为整型(大端模式)
 */
fun ByteArray.read2LongBE(offset: Int = 0) =
    if (this == null) throw IllegalArgumentException("传入参数不正确")
    else {
        val arr = if (this.size >= 8) {
            this
        } else {
            val size = this.size
            ByteArray(8).apply {
                for (i in 0 until 8) {
                    this[i] = if (i < 8 - size) {
                        0
                    } else {
                        this@read2LongBE[size - i]
                    }
                }
            }
        }
        (0xffL.and(arr[offset].toLong())).shl(56)
            .or((0xffL.and(arr[offset + 1].toLong())).shl(48))
            .or((0xffL.and(arr[offset + 2].toLong())).shl(40))
            .or((0xffL.and(arr[offset + 3].toLong())).shl(32))
            .or((0xffL.and(arr[offset + 4].toLong())).shl(24))
            .or((0xffL.and(arr[offset + 5].toLong())).shl(16))
            .or((0xffL.and(arr[offset + 6].toLong())).shl(8))
            .or((0xffL.and(arr[offset + 7].toLong())))
    }

/**
 * 字节数组转换为整型(小端模式)
 */
fun ByteArray.read2LongLE(offset: Int = 0) =
    if (this == null) throw IllegalArgumentException("传入参数不正确")
    else {
        val arr = if (this.size >= 8) {
            this
        } else {
            val size = this.size
            ByteArray(8).apply {
                for (i in 0 until 8) {
                    this[i] = if (i >= size) {
                        0
                    } else {
                        this@read2LongLE[size - i - 1]
                    }
                }
            }
        }
//        val arr = when (this.size) {
//            0 -> byteArrayOf(0, 0, 0, 0)
//            1 -> byteArrayOf(this[0], 0, 0, 0)
//            2 -> byteArrayOf(this[1], this[0], 0, 0)
//            3 -> byteArrayOf(this[2], this[1], this[0], 0)
//            else -> this
//        }
        (0xffL.and(arr[offset + 7].toLong())).shl(56)
            .or((0xffL.and(arr[offset + 6].toLong())).shl(48))
            .or((0xffL.and(arr[offset + 5].toLong())).shl(40))
            .or((0xffL.and(arr[offset + 4].toLong())).shl(32))
            .or((0xffL.and(arr[offset + 3].toLong())).shl(24))
            .or((0xffL.and(arr[offset + 2].toLong())).shl(16))
            .or((0xffL.and(arr[offset + 1].toLong())).shl(8))
            .or((0xffL.and(arr[offset].toLong())))
    }

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

/**
 * 十六进制字符串转字节数组
 */
fun String?.hexString2Bytes(): ByteArray? =
    if (this.isNullOrEmpty() || !this.isHexString()) null
    else {
        val src = this.trim().toUpperCase()
        val len = src.length / 2
        val result = ByteArray(len) { 0 }
        val hexChars = src.toCharArray()
        for (i in 0 until len) {
            val pos = i * 2
            result[i] = (hexChars[pos].char2Byte().toInt().shl(4)
                .or(hexChars[pos + 1].char2Byte().toInt())).toByte()
        }
        result
    }

/**
 * 字符转字节
 */
fun Char.char2Byte() = "0123456789ABCDEF".indexOf(this).toByte()

/**
 * 字条串转换为二进制字符串，以空格相隔
 */
fun String?.str2BinStr(): String? =
    if (this.isNullOrEmpty()) null
    else {
        val strChar = this.toCharArray()
        var result = ""
        strChar.forEach {
            result += "${Integer.toBinaryString(it.toInt())} "
        }
        result.trim()
    }

/**
 * 判断是否十六进制字符串
 */
fun String?.isHexString() =
    if (this.isNullOrEmpty()) false
    else {
        Pattern.compile("^[0-9A-Fa-f]+$").matcher(this).matches()
    }

/**
 * 判断是否邮箱地址
 */
fun String?.isEmailString() =
    if (this.isNullOrEmpty()) false
    else {
        Pattern.compile("^[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w\\.-]*\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]\$")
            .matcher(this).matches()
    }

/**
 * 判断是否Wi-Fi SSID
 */
fun String?.isSsidString() =
    if (this.isNullOrEmpty()) false
    else {
        Pattern.compile("^[A-Za-z]+[\\w\\-\\:\\.]*\$").matcher(this).matches()
    }

val CHINESE_UNICODE = "[\\u4e00-\\u9fa5]"

/**
 * 是否包含汉字
 */
fun String?.containtChinese() = Pattern.compile(CHINESE_UNICODE).matcher(this).find()

/**
 * 是否全是汉字
 */
fun String?.isAllChinese() = Pattern.compile(CHINESE_UNICODE).matcher(this).matches()

/**
 * 是否只包含字母数字及汉字
 */
fun String.isNumOrCharOrChinese() = this.matches(Regex("^[a-z0-9A-Z\\u4e00-\\u9fa5]+$"))

fun File.createFile(delOld: Boolean = false): File =
    if (!exists() || !isFile) {
        File(this.parent).also {
            it.mkdirs()
        }
        createNewFile()
        this
    } else if (delOld) {
        delete()
        createNewFile()
        this
    } else {
        this
    }

/**
 * 根据时间范围获取文件
 */
fun File.getFilesByTimeRange(from: Long = 0, to: Long = System.currentTimeMillis()): Array<File>? =
    if (exists()) {
//    "time".logE("from:${from.formatDateString()}, to:${to.formatDateString()}")
        listFiles { pathname -> pathname?.lastModified() in from..to }
    } else {
        null
    }

/**
 * 是否有可用剩余空间
 */
@RequiresApi(Build.VERSION_CODES.GINGERBREAD)
fun File.hasAvailableSpace(cacheByteCount: Long = 50 * 1024 * 1024L): Boolean =
    usableSpace > cacheByteCount