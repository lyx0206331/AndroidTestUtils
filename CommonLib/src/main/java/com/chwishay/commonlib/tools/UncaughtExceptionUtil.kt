package com.chwishay.commonlib.tools

import android.content.Context
import android.os.Environment
import android.os.Looper
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import kotlin.concurrent.thread
import kotlin.system.exitProcess

//                       _ooOoo_
//                      o8888888o
//                      88" . "88
//                      (| -_- |)
//                       O\ = /O
//                   ____/`---'\____
//                 .   ' \\| |// `.
//                  / \\||| : |||// \
//                / _||||| -:- |||||- \
//                  | | \\\ - /// | |
//                | \_| ''\---/'' | |
//                 \ .-\__ `-` ___/-. /
//              ______`. .' /--.--\ `. . __
//           ."" '< `.___\_<|>_/___.' >'"".
//          | | : `- \`.;`\ _ /`;.`/ - ` : | |
//            \ \ `-. \_ __\ /__ _/ .-` / /
//    ======`-.____`-.___\_____/___.-`____.-'======
//                       `=---='
//
//    .............................................
//             佛祖保佑             永无BUG
/**
 * author:RanQing
 * date:2020/9/27 0027 15:43
 * description: 全局异常捕获
 * 需要文件读写权限：
 * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
 * <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
 */
class UncaughtCrashHandler : Thread.UncaughtExceptionHandler {

    companion object {
        val instance: UncaughtCrashHandler by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { UncaughtCrashHandler() }
    }

    private lateinit var context: Context
    private val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()

    fun init(context: Context) {
        this.context = context
        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    override fun uncaughtException(t: Thread, e: Throwable) {
        if (!ifhandleException(e) && defaultHandler != null) {
            defaultHandler.uncaughtException(t, e)
        } else {
            Thread.sleep(1000)
            android.os.Process.killProcess(android.os.Process.myPid())
            exitProcess(1)
        }
    }

    private fun ifhandleException(ex: Throwable?): Boolean {
        if (ex == null) return false

        thread {
            Looper.prepare()
            context.showShortToast("抱歉，程序出现异常，即将退出")
            Looper.loop()
        }

        saveCrashLog2File(ex)
        return true
    }

    /**
     * 崩溃日志存入文件
     */
    private fun saveCrashLog2File(ex: Throwable) {
        val writer = StringWriter()
        PrintWriter(writer).use {
            ex.printStackTrace(it)
        }

        val fileName = "crash-${System.currentTimeMillis()}.log"
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            val path = "${Environment.getExternalStorageDirectory().path}/crashLog/"
            val dir = File(path)
            if (!dir.exists()) {
                dir.mkdirs()
            }
            File("${path}$fileName").apply { createNewFile() }.outputStream().use {
                it.write(writer.toString().toByteArray())
            }
//            FileOutputStream("${path}$fileName").apply {
//                write(writer.toString().toByteArray())
//                close()
//            }
        }

    }
}