package com.adrian.usbmodule

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.*
import com.chwishay.commonlib.tools.formatHexString
import com.chwishay.commonlib.tools.logE
import com.chwishay.commonlib.tools.orDefault
import com.chwishay.commonlib.tools.showShortToast
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.concurrent.thread

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

    var logCallback: ILogCallback? = null

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
                    logCallback?.outputLog("device name:${usbDevice.deviceName}")
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
            logCallback?.outputLog("已获得USB权限")
        } else {
            if (permissionIntent != null) {
                usbMngr?.requestPermission(usbDevice, permissionIntent)
                context.showShortToast("请求USB权限")
                logCallback?.outputLog("请求USB权限")
            } else {
                context.showShortToast("请注册USB广播")
                logCallback?.outputLog("请注册USB广播")
            }
        }
    }

    fun openPort(device: UsbDevice?): Boolean {
        if (device == null){
            isOpenPort = false
            return false
        }
        logCallback?.outputLog("usb interface count: ${device.interfaceCount}")
        usbInterface = device.getInterface(1)
        if (hasPermission(device)) {
            usbConnection = usbMngr?.openDevice(device)
            if (usbConnection == null) {
                isOpenPort = false
                return false
            }
            if (usbConnection!!.claimInterface(usbInterface, true)) {
                context.showShortToast("找到USB设备接口")
                logCallback?.outputLog("找到USB设备接口")
            } else {
                usbConnection!!.close()
                context.showShortToast("未找到USB设备接口")
                logCallback?.outputLog("未找到USB设备接口")
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
                    logCallback?.outputLog("找到输入接口")
                } else {
                    usbEndpointOut = end
                    logCallback?.outputLog("找到输出接口")
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

    fun writeMsg(bytes: ByteArray) = usbConnection?.bulkTransfer(usbEndpointOut, bytes, bytes.size, 500)

    fun readMsg(device: UsbDevice?) {
        if (isOpenPort || openPort(device)) {
            logCallback?.outputLog("开始读取数据")
            isReading = true
            thread(start = true) {
                val inMax = usbEndpointIn!!.maxPacketSize*40
                val buffer = ByteBuffer.allocateDirect(inMax)
                buffer.order(ByteOrder.BIG_ENDIAN)
                val request = UsbRequest()
                request.initialize(usbConnection, usbEndpointIn)
                while (isReading) {
                    synchronized(this) {
//                        val bytes = ByteArray(usbEndpointIn!!.maxPacketSize) {0}
//                        val ret = usbConnection?.bulkTransfer(usbEndpointIn!!, bytes, bytes.size, 50).orDefault()
//                        "USB".logE("输入数据长度为：${usbEndpointIn!!.maxPacketSize}, 数据大小为：${ret}, 时间：${System.currentTimeMillis()}")
//                        if (ret > 0) {
////                            logCallback?.outputLog(bytes.contentToString())
//                            logCallback?.outputData(bytes)
//                        }
                        request.queue(buffer, inMax)
                        val bytes = buffer.array().filterNot {
                            it == null
                        }.toByteArray()
                        if (usbConnection?.requestWait() == request && bytes.isNotEmpty()) {
                            logCallback?.outputData(bytes)
                        }
                    }
                }
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

    interface ILogCallback {
        fun outputLog(msg: String?)
        fun outputData(bytes: ByteArray)
    }
}
