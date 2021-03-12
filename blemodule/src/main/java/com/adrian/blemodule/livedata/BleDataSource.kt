package com.adrian.blemodule.livedata

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineDispatcher

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
 * date:2020/11/10 0010 16:06
 * description:
 */
class BleDataVM() : ViewModel() {
    val speed: MutableLiveData<Int>
        get() = TODO("Not yet implemented")
    val totalSize: MutableLiveData<Int>
        get() = TODO("Not yet implemented")
    val lastData: MutableLiveData<ByteArray>
        get() = TODO("Not yet implemented")
    val needSave: MutableLiveData<Boolean>
        get() = TODO("Not yet implemented")
    val fileName: MutableLiveData<String>
        get() = TODO("Not yet implemented")
    val totalReceiveTime: MutableLiveData<Long>
        get() = TODO("Not yet implemented")

    fun startReceive() {
        TODO("Not yet implemented")
    }

    fun stopReceive() {
        TODO("Not yet implemented")
    }
}

class BleDataSource(private var ioDispatcher: CoroutineDispatcher) : DataSource {
    override var speed: LiveData<Int>
        get() = TODO("Not yet implemented")
        set(value) {}
    override var totalSize: LiveData<Int>
        get() = TODO("Not yet implemented")
        set(value) {}
    override var lastData: LiveData<ByteArray>
        get() = TODO("Not yet implemented")
        set(value) {}
    override var needSave: LiveData<Boolean>
        get() = TODO("Not yet implemented")
        set(value) {}
    override var fileName: LiveData<String>
        get() = TODO("Not yet implemented")
        set(value) {}
    override var totalReceiveTime: LiveData<Long>
        get() = TODO("Not yet implemented")
        set(value) {}

    override fun startReceive() {
        TODO("Not yet implemented")
    }

    override fun stopReceive() {
        TODO("Not yet implemented")
    }
}

interface DataSource {
    var speed: LiveData<Int>
    var totalSize: LiveData<Int>
    var lastData: LiveData<ByteArray>
    var needSave: LiveData<Boolean>
    var fileName: LiveData<String>
    var totalReceiveTime: LiveData<Long>

    fun startReceive()
    fun stopReceive()
}