package com.chwishay.commonlib.tools

import java.io.File
import java.util.regex.Pattern

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
 * date:2021/1/7 0007 10:27
 * description:
 */
object ExoLogUtil {

    fun parseLogFile(filePath: String): List<LogEntity>? =
        File(filePath).let { file ->
            if (!file.exists() || !file.isFile) {
                null
            } else {
                val list = arrayListOf<LogEntity>()
                file.reader().useLines { lines ->
                    var logEntity: LogEntity? = null
                    var i = 0
                    lines.forEach { line ->
                        if (line == "####%%%%&&&&") {
                            logEntity = LogEntity()
                            i = 0
                            return@forEach
                        }
                        if (!line.trim().isNullOrEmpty()) {
                            i++
                            when (i) {
                                1 -> line.split(Pattern.compile("\\t")).let {
                                    if (it.size == 4) {
                                        logEntity?.index = it[0].toInt()
                                        logEntity?.type = it[1].toInt()
                                        logEntity?.millis = it[2].toLong()
                                        logEntity?.dataLength = it[3].toInt()
                                    }
                                }
                                2 -> line.split(":").let {
                                    if (it.size == 2) {
                                        logEntity?.data?.put(it[0].trim(), hashMapOf())
                                    }
                                }
                                else -> line.split(":").let {
                                    if (it.size == 2) {
                                        logEntity?.data?.let { map ->
                                            map[map.keys.first()]?.put(it[0].trim(), it[1].trim())
                                        }
                                    }
                                }
                            }
                        } else if (i > 0 && logEntity != null) {
                            list.add(logEntity!!)
                            logEntity = null
                        }
                    }
                }
                list
            }
        }
}

data class LogEntity(
    var index: Int = 0,
    var type: Int = 0,
    var millis: Long = 0,
    var dataLength: Int = 0,
    var data: HashMap<String, HashMap<String, String>> = hashMapOf()
)