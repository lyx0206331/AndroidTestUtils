package com.adrian.blemodule

import androidx.annotation.IntDef
import com.chwishay.commonlib.tools.logE
import com.chwishay.commonlib.tools.orDefault
import com.chwishay.commonlib.tools.toBytesBE
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import java.text.SimpleDateFormat
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
 * date:2021/1/11 0011 11:46
 * description:
 */
object CmdUtil {

    const val TYPE_IMU = 0xA0
    const val TYPE_POWER = 0xA1
    const val TYPE_RECHARGE_STATE = 0xA2
    const val TYPE_SYNC_START = 0xA3
    const val TYPE_SYNC_STOP = 0xA4
    const val TYPE_CONN_STATE = 0xA5
    const val TYPE_SYNC_TIME = 0xA6

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(
        TYPE_IMU,
        TYPE_POWER,
        TYPE_RECHARGE_STATE,
        TYPE_SYNC_START,
        TYPE_SYNC_STOP,
        TYPE_CONN_STATE,
        TYPE_SYNC_TIME
    )
    annotation class CmdType

    private val cmdByteBuf = Unpooled.buffer()

    private val receiveByteBuf = Unpooled.buffer()

    /**
     * 包头数据
     */
    var headBytes: ByteArray? = byteArrayOf(0xAA.toByte(), 0xBB.toByte())
    var tailBytes: ByteArray? = byteArrayOf(0xCC.toByte(), 0xDD.toByte())

    private fun createCmdData(cmdData: ByteArray): ByteBuf {
        cmdByteBuf.clear()
        cmdByteBuf.capacity(headBytes?.size.orDefault() + cmdData.size + tailBytes?.size.orDefault())
        cmdByteBuf.writeBytes(headBytes)
        cmdByteBuf.writeBytes(cmdData)
//        cmdByteBuf.writeByte(cmdData.cmdVerify())
        cmdByteBuf.writeBytes(tailBytes)
        return cmdByteBuf
    }

    private fun createReceiveData(receiveData: ByteArray): ByteBuf {
        receiveByteBuf.clear()
        receiveByteBuf.capacity(receiveData.size)
        receiveByteBuf.writeBytes(receiveData)
        return receiveByteBuf
    }

    /**
     * 指令校验
     */
    fun ByteArray.cmdVerify() = this.sum().inv() + 1

    /**
     * 判断是IMU数据。由于IMU数据一帧包含三组有效数据，每一组都包含包头，类型，长度，数据及校验，所以单独做判断
     */
    fun ByteArray.isIMUData() =
        this[0] == headBytes!![0] && this[1] == headBytes!![1] && this[size - 2] == tailBytes!![0] && this[size - 1] == tailBytes!![1]

    /**
     * 获取开始同步指令
     */
    fun getStartSyncCmd() =
        createCmdData(byteArrayOf(TYPE_SYNC_START.toByte(), 0x01, 0x5A.toByte()))

    /**
     * 获取停止同步指令
     */
    fun getStopSyncCmd() = createCmdData(byteArrayOf(TYPE_SYNC_STOP.toByte(), 0x01, 0xAA.toByte()))

    /**
     * 获取连接状态指令
     */
    fun getStateCmd() = createCmdData(byteArrayOf(TYPE_RECHARGE_STATE.toByte(), 0x01, 0x01))

    /**
     * 获取时间同步指令
     */
    fun getTimeSyncCmd(millis: Long = System.currentTimeMillis()): ByteArray =
        if (millis < 0) {
            throw IllegalArgumentException("输入参数不合法")
        } else {
            SimpleDateFormat("yy:MM:dd:HH:mm:ss", Locale.CHINESE).format(Date(millis)).split(":")
                .let {
                    val time = it[0].toInt().shl(26)
                        .or(it[1].toInt().shl(22))
                        .or(it[2].toInt().shl(17))
                        .or(it[3].toInt().shl(12))
                        .or(it[4].toInt().shl(6))
                        .or(it[5].toInt()).toBytesBE()
                    createCmdData(byteArrayOf(TYPE_SYNC_TIME.toByte(), *time)).array()
                }
        }

    /**
     * 数据解析
     */
    fun parseCmdData(data: ByteArray): DataEntity? =
        if (data.size >= 6) {
            if (data.isIMUData() && data.sliceArray(
                    IntRange(
                        2,
                        data.size - 2
                    )
                ).cmdVerify().toByte() == data[data.size - 1]
            ) {
                DataEntity(
                    data[2].toInt(),
                    data[3].toInt(),
                    data.sliceArray(IntRange(4, data.size - 2))
                )
            } else {
                null
            }
        } else {
            null
        }

    fun ByteArray.parseImuData0(): List<ByteArray>? = if (this.size <= 44) {
        "IMUValues".logE("数据不完整: ${this.contentToString()}")
        null
    } else {
        val list = arrayListOf<ByteArray>()
        val startIndex = this.indexOfFirst { it.toUByte() == headBytes!![0].toUByte() }.let { i ->
            if (i < 0 || i > this.size - 44 || this[i + 1].toUByte() != headBytes!![1].toUByte()) {
                -1
            } else {
                i
            }
        }
        val endIndex = this.indexOfLast { it.toUByte() == tailBytes!![1].toUByte() }.let { i ->
            if (i < 43 || this[i - 1].toUByte() != tailBytes!![0].toUByte()) {
                -1
            } else {
                i
            }
        }
        "IMU_DATA".logE("startIndex: $startIndex, endIndex: $endIndex")
        if (startIndex != -1 && endIndex != -1 && startIndex < endIndex) {
            this.sliceArray(IntRange(startIndex, endIndex)).also { d ->
                if (d.size % 44 == 0) {
                    val frameCount = d.size / 44
                    for (i in 0 until frameCount) {
                        d.sliceArray(IntRange(i * 44, (i + 1) * 44 - 1)).also { item ->
                            if (item.isIMUData()) {
                                list.add(item)
                            }
                        }
                    }
                }
            }
        }
        list
    }

    fun ByteArray.parseImuData(): D82Entity? = if (this.size <= 44) {
        "IMUValues".logE("数据不完整: ${this.contentToString()}")
        null
    } else {
        val d82Entity = D82Entity(arrayListOf(), arrayListOf())
//        val list = arrayListOf<IMUEntity>()
        val startIndex = this.indexOfFirst { it.toUByte() == headBytes!![0].toUByte() }.let { i ->
            if (i < 0 || i > this.size - 44 || this[i + 1].toUByte() != headBytes!![1].toUByte()) {
                -1
            } else {
                i
            }
        }
        val endIndex = this.indexOfLast { it.toUByte() == tailBytes!![1].toUByte() }.let { i ->
            if (i < 43 || this[i - 1].toUByte() != tailBytes!![0].toUByte()) {
                -1
            } else {
                i
            }
        }
        "IMU_DATA".logE("startIndex: $startIndex, endIndex: $endIndex")
        if (startIndex != -1 && endIndex != -1 && startIndex < endIndex) {
            this.sliceArray(IntRange(startIndex, endIndex)).also { d ->
                if (d.size % 44 == 0) {
                    val frameCount = d.size / 44
                    for (i in 0 until frameCount) {
                        d.sliceArray(IntRange(i * 44, (i + 1) * 44 - 1)).also { item ->
                            if (item.isIMUData()) {
                                d82Entity.origList.add(item)
                                createReceiveData(item).let { data ->
                                    data.readUnsignedShortLE()
                                    val imuEntity = IMUEntity(
                                        data.readUnsignedShortLE().toUShort(),
                                        data.readUnsignedShortLE().toUShort(),
                                        data.readUnsignedShortLE().toUShort(),
                                        data.readUnsignedShortLE().toUShort(),
                                        data.readUnsignedShortLE().toUShort(),
                                        data.readUnsignedShortLE().toUShort(),
                                        data.readUnsignedShortLE().toUShort(),
                                        data.readUnsignedShortLE().toUShort(),
                                        data.readUnsignedShortLE().toUShort(),
                                        data.readUnsignedShortLE().toUShort(),
                                        data.readUnsignedShortLE().toUShort(),
                                        data.readUnsignedShortLE().toUShort(),
                                        data.readUnsignedShortLE().toUShort(),
                                        data.readUnsignedShortLE().toUShort(),
                                        data.readUnsignedShortLE().toUShort(),
                                        data.readUnsignedShortLE().toUShort(),
                                        data.readUnsignedShortLE().toUShort(),
                                        data.readUnsignedShortLE().toUShort(),
                                        data.readUnsignedShortLE().toUShort(),
                                        data.readUnsignedShortLE().toUShort()
                                    )
//                                    "imuEntity".logE("IMU_Entity:${imuEntity}")
                                    d82Entity.resultList.add(imuEntity)
                                }
                            }
                        }
                    }
                }
            }
        }
        d82Entity
    }
}

/**
 * @param type 报文类型。0xA0:IMU数据; 0xA1:电量; 0xA2:充电状态;0xA5:应答蓝牙状态
 * @param length 报文数据长度
 * @param content 报文内容
 */
data class DataEntity(@CmdUtil.CmdType val type: Int, val length: Int, val content: ByteArray) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DataEntity

        if (type != other.type) return false
        if (length != other.length) return false
        if (!content.contentEquals(other.content)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type
        result = 31 * result + length
        result = 31 * result + content.contentHashCode()
        return result
    }
}

data class D82Entity(val origList: ArrayList<ByteArray>, val resultList: ArrayList<IMUEntity>)

data class IMUEntity(
    val imuSysState: UShort, val modeState: UShort, val attResult: UShort,
    val value1: UShort, val value2: UShort, val value3: UShort,
    val value4: UShort, val value5: UShort, val value6: UShort,
    val value7: UShort, val value8: UShort, val value9: UShort,
    val value10: UShort, val value11: UShort, val value12: UShort,
    val value13: UShort, val value14: UShort, val value15: UShort,
    val value16: UShort, val value17: UShort
) {
    fun valuesString() =
        "$imuSysState\t$modeState\t$attResult\t$value1\t$value2\t$value3\t$value4\t$value5\t$value6\t$value7\t$value8\t$value9\t$value10\t$value11\t$value12\t$value13\t$value14\t$value15\t$value16\t$value17"
}