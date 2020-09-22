package com.adrian.blemodule

import com.clj.fastble.data.BleDevice

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
 * date:2020/9/4 0004 18:10
 * description:
 */
class ObserverManager : Observable {

    companion object {
        private var instance: ObserverManager? = null
        @JvmStatic fun getInstance() = instance ?: synchronized(this) {
            instance ?: ObserverManager().also {
                instance = it
            }
        }
    }

    private val observers = arrayListOf<Observer>()

    override fun addObserver(obs: Observer) {
        observers.add(obs)
    }

    override fun deleteObserver(obs: Observer) {
        val index = observers.indexOf(obs)
        if (index >= 0) {
            observers.remove(obs)
        }
    }

    override fun notifyObserver(bleDevice: BleDevice) {
        observers.forEach {
            it.disConnected(bleDevice)
        }
    }
}

interface Observer {
    fun disConnected(bleDevice: BleDevice)
}

interface Observable {
    fun addObserver(obs: Observer)
    fun deleteObserver(obs: Observer)
    fun notifyObserver(bleDevice: BleDevice)
}