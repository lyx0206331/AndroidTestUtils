package com.adrian.blemodule

import com.chwishay.commonlib.baseComp.BaseApp
import com.clj.fastble.BleManager
import com.clj.fastble.BuildConfig

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
 * date:2020/9/17 0017 9:58
 * description:
 */
class BleApplication: BaseApp() {

    override fun onCreate() {
        super.onCreate()

        BleManager.getInstance().init(this)
        BleManager.getInstance().enableLog(BuildConfig.DEBUG).setReConnectCount(1, 5000)
            .setConnectOverTime(20000).setOperateTimeout(5000)
    }
}