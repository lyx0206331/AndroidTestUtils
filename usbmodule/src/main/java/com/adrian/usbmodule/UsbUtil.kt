package com.adrian.usbmodule

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.*
import com.chwishay.commonlib.tools.logE
import com.chwishay.commonlib.tools.orDefault
import com.chwishay.commonlib.tools.showShortToast
import kotlinx.coroutines.delay
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.concurrent.thread
import kotlin.math.ceil
import kotlin.math.min

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
 * date:2020/9/16 0016 11:12
 * description:
 */
const val ACTION_USB_PERMISSION = "com.android.chws.USB_PERMISSION"

class UsbUtil {

    companion object {
        val instance: UsbUtil by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { UsbUtil() }
    }

    private lateinit var context: Context
    private var usbMngr: UsbManager? = null
    private var usbDevice: UsbDevice? = null
    private var usbInterface: UsbInterface? = null
//    private val usbEndpoint = Array(5) { arrayOfNulls<UsbEndpoint>(5)}
    private var usbEndpointIn: UsbEndpoint? = null
    private var usbEndpointOut: UsbEndpoint? = null
    private var usbConnection: UsbDeviceConnection? = null
//    private val transfer = DataTransfer(1024)

    private var permissionIntent: PendingIntent? = null
    private var usbReceiver: UsbReceiver? = null

    var isReading: Boolean = false

    private var isOpenPort = false

    var usbCallback: IUsbCallback? = null

    fun init(context: Context): UsbUtil {
        this.context = context
        usbMngr = context.getSystemService(Context.USB_SERVICE) as? UsbManager
        usbReceiver = UsbReceiver()
        return this
    }

    fun getDeviceList(): List<UsbDevice> {
        val devices = arrayListOf<UsbDevice>()
        usbMngr?.deviceList?.let {
            it.iterator().apply {
                while (hasNext()) {
                    val usbDevice = this.next().value
                    usbCallback?.outputLog("device name:${usbDevice.deviceName}")
                    devices.add(usbDevice)
                }
            }
        }
        return devices
    }

    /**
     * 根据供应商ID和产品ID获取设备
     * @param vendorId
     * @param productId
     */
    fun getUsbDevice(vendorId: Int, productId: Int): UsbDevice? {
        getDeviceList().forEach {
            if (it.vendorId == vendorId && it.productId == productId){
                return it
            }
        }
        return null
    }

    fun hasPermission(usbDevice: UsbDevice) = usbMngr?.hasPermission(usbDevice).orDefault()

    fun requestPermission(usbDevice: UsbDevice) {
        if (hasPermission(usbDevice)) {
            context.showShortToast("已获得USB权限")
            usbCallback?.outputLog("已获得USB权限")
        } else {
            if (permissionIntent != null) {
                usbMngr?.requestPermission(usbDevice, permissionIntent)
                context.showShortToast("请求USB权限")
                usbCallback?.outputLog("请求USB权限")
            } else {
                context.showShortToast("请注册USB广播")
                usbCallback?.outputLog("请注册USB广播")
            }
        }
    }

    fun openPort(device: UsbDevice?): Boolean {
        if (device == null){
            isOpenPort = false
            return false
        }
        usbCallback?.outputLog("usb interface count: ${device.interfaceCount}")
        usbInterface = device.getInterface(1)
        if (hasPermission(device)) {
            usbConnection = usbMngr?.openDevice(device)
            if (usbConnection == null) {
                isOpenPort = false
                return false
            }
            if (usbConnection!!.claimInterface(usbInterface, true)) {
                context.showShortToast("找到USB设备接口")
                usbCallback?.outputLog("找到USB设备接口")
            } else {
                usbConnection!!.close()
                context.showShortToast("未找到USB设备接口")
                usbCallback?.outputLog("未找到USB设备接口")
                isOpenPort = false
                return false
            }
        } else {
            context.showShortToast("无USB权限")
            isOpenPort = false
            return false
        }

        for (i in 0 until usbInterface!!.endpointCount) {
            val end = usbInterface!!.getEndpoint(i)
            if (end.type == UsbConstants.USB_ENDPOINT_XFER_BULK) {
                if (end.direction == UsbConstants.USB_DIR_IN) {
                    usbEndpointIn = end
                    usbCallback?.outputLog("找到输入接口")
                } else {
                    usbEndpointOut = end
                    usbCallback?.outputLog("找到输出接口")
                }
            }
        }
        isOpenPort = true
        return true
    }

    fun closePort(timeout: Long) {
        usbConnection?.apply {
            Thread.sleep(timeout)
            close()
            releaseInterface(usbInterface)
        }
        usbConnection = null
        usbEndpointIn = null
        usbEndpointOut = null
        usbMngr = null
        usbInterface = null
        isOpenPort = false
    }

    private fun write(bytes: ByteArray) =
        usbConnection?.bulkTransfer(usbEndpointOut, bytes, bytes.size, 500).orDefault(-1)

    suspend fun write2Usb(data: ByteArray, pkgSize: Int) {
        val pkgNum = ceil(data.size.toFloat() / pkgSize).toInt()
        for (i in 0 until pkgNum) {
            val d = data.copyOfRange(i * pkgSize, min((i + 1) * pkgSize, data.size))
            val size = write(d)
            usbCallback?.outputData(d, d.size * 100 / data.size)
            if (size < 0) {
                usbCallback?.onException(msg = "升级失败,请点击重试!")
                return
            }
            delay(20)
        }
        usbCallback?.onSuccess(false)
    }

    fun readMsg(device: UsbDevice?) {
        if (isOpenPort || openPort(device)) {
            usbCallback?.outputLog("开始读取数据")
            isReading = true
            val inMax = usbEndpointIn!!.maxPacketSize * 80
            usbCallback?.outputLog("inMax:${inMax}bytes")
            val request = UsbRequest()
            request.initialize(usbConnection, usbEndpointIn)
            val byteBuffer = ByteBuffer.allocateDirect(inMax)
            byteBuffer.order(ByteOrder.LITTLE_ENDIAN)
            var cycleTimes = 0
            thread(start = true) {
                while (isReading) {
                    synchronized(this) {
//                        val bytes = ByteArray(usbEndpointIn!!.maxPacketSize) {0}
//                        val ret = usbConnection?.bulkTransfer(usbEndpointIn!!, bytes, bytes.size, 50).orDefault()
//                        "USB".logE("输入数据长度为：${usbEndpointIn!!.maxPacketSize}, 数据大小为：${ret}, 时间：${System.currentTimeMillis()}")
//                        if (ret > 0) {
////                            logCallback?.outputLog(bytes.contentToString())
//                            logCallback?.outputData(bytes)
//                        }
//                        "USB".logE("buffer size:${byteBuffer.capacity()}, remaining:${byteBuffer.remaining()}, position:${byteBuffer.position()}, limit:${byteBuffer.limit()}, 时间：${System.currentTimeMillis()}")
                        if (request.queue(byteBuffer, inMax)) {
                            if (usbConnection?.requestWait() == request) {
//                                if (cycleTimes == 0) {
//                                    byteBuffer.put(0, 0x45)
//                                    byteBuffer.put(1, 0x45)
//                                    byteBuffer.put(2, 0x45)
//                                    byteBuffer.put(3, 0x45)
//                                } else if (cycleTimes == 1) {
//                                    byteBuffer.put(4, 0x46)
//                                    byteBuffer.put(5, 0x46)
//                                    byteBuffer.put(6, 0x46)
//                                    byteBuffer.put(7, 0x46)
//                                }
                                cycleTimes++
                                byteBuffer.flip()
                                val bytes = ByteArray(byteBuffer.limit())
                                byteBuffer.get(bytes)
                                byteBuffer.clear()
                                val size = bytes.size
                                "USB".logE(
                                    "第$cycleTimes 次读取数据 bytes size:$size, last byte:${
                                        if (size >= 4) bytes.copyOfRange(
                                            size - 4,
                                            size
                                        ).decodeToString() else "长度不足4个字节"
                                    }, 时间：${System.currentTimeMillis()}"
                                )
                                usbCallback?.inputData(bytes, -1)
                            }
                        }
                    }
                }
                usbCallback?.onSuccess(true)
            }
        }
    }

    fun registerReceiver() {
        permissionIntent = PendingIntent.getBroadcast(context, 0, Intent(ACTION_USB_PERMISSION), 0)
        val filter = IntentFilter(ACTION_USB_PERMISSION).apply {
            addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
            addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
        }
        context.registerReceiver(usbReceiver, filter)
    }

    fun unregisterReceiver() {
        context.unregisterReceiver(usbReceiver)
        permissionIntent = null
    }

    class UsbReceiver: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when(intent?.action) {
                ACTION_USB_PERMISSION -> {
                    synchronized(this) {
                        intent.getParcelableExtra<UsbDevice?>(UsbManager.EXTRA_DEVICE)?.apply {
                            if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                                "USB".logE("获取权限成功:$deviceName")
                            } else {
                                "USB".logE("获取权限失败:$deviceName")
                            }
                        }
                    }
                }
                UsbManager.ACTION_USB_DEVICE_ATTACHED -> {
                    context?.showShortToast("已插入USB设备")
                }
                UsbManager.ACTION_USB_DEVICE_DETACHED -> {
                    context?.showShortToast("USB设备已拔出")
                }
            }
        }
    }

    interface IUsbCallback {
        fun outputLog(msg: String?)
        fun outputData(bytes: ByteArray, progress: Int = -1)
        fun inputData(bytes: ByteArray, progress: Int = -1)
        fun onSuccess(isInput: Boolean)
        fun onException(e: Exception? = null, msg: String? = null)
    }
}
