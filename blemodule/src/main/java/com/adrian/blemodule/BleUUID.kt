package com.adrian.blemodule

import java.util.*

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
 * date:2021/1/28 0028 13:40
 * description:
 */

data class BleUUID private constructor(
    var serviceUUID: UUID? = null,
    var charactWriteUUID: UUID? = null,
    var charactNotifyUUID: UUID? = null
) {
    companion object {
        val instance: BleUUID by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { BleUUID() }
    }

    private constructor() : this(null, null, null)

    fun createExample() {
        serviceUUID = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb")
        charactWriteUUID = UUID.fromString("0000fff3-0000-1000-8000-00805f9b34fb")
        charactNotifyUUID = UUID.fromString("0000fff1-0000-1000-8000-00805f9b34fb")
    }
}