package com.adrian.usbmodule

import android.Manifest
import android.hardware.usb.UsbDevice
import android.os.Bundle
import android.os.Environment
import android.text.method.ScrollingMovementMethod
import androidx.appcompat.app.AppCompatActivity
import com.chwishay.commonlib.tools.PermissionUtil
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {

    private var usbDevice: UsbDevice? = null

    private val permissionUtil: PermissionUtil by lazy { PermissionUtil(this) }
    private val permissions = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    //接收数据总大小
    private var totalDataLength = 0

    //文件创建时间
    private var createTime = 0L

    //数据传输开始时间
    private var transStartTime = 0L

    //数据传输结束时间
    private var transStopTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        UsbUtil.instance.init(this).usbCallback = object : UsbUtil.IUsbCallback {
            override fun outputLog(msg: String?) {
                appendCotent(msg)
            }

            override fun outputData(bytes: ByteArray) {
                if (bytes.isEmpty()) {
                    transStopTime = System.currentTimeMillis()
                    appendCotent("结束数据传输:$transStopTime")
                } else if (transStartTime == 0L) {
                    transStartTime = System.currentTimeMillis()
                    appendCotent("开始传输数据:$transStartTime")
                }
                totalDataLength += bytes.size
                writeSrc2File("w66_$createTime", bytes)
            }
        }

        btnOpenPort.setOnClickListener {
            val device = UsbUtil.instance.getUsbDevice(1155, 22336)
            if (device == null) {
                appendCotent("未找到vendorId为1155, productId为22336的端口")
            } else {
                if (UsbUtil.instance.hasPermission(device)) {
                    usbDevice = device
                } else {
                    UsbUtil.instance.requestPermission(usbDevice!!)
                }
                val success = UsbUtil.instance.openPort(usbDevice!!)
                appendCotent("打开端口${if (success) "成功" else "失败"}")
            }
        }

        btnReadData.setOnClickListener {
            permissionUtil.requestPermission(
                permissions,
                object : PermissionUtil.IPermissionCallback {
                    override fun allowedPermissions() {
                        if (UsbUtil.instance.isReading) {
                            btnReadData.text = "开始读取"
                            UsbUtil.instance.isReading = false
                            val timeLen = (System.currentTimeMillis() - transStartTime) / 1000f
                            appendCotent("收到数据大小为:$totalDataLength bytes, 传输耗时:$timeLen s")
                            appendCotent("速度约为:${totalDataLength / 1024f / timeLen}kb/s")
                        } else {
                            btnReadData.text = "停止读取"
                            UsbUtil.instance.isReading = true
                            totalDataLength = 0
                            transStartTime = 0L
                            createTime = System.currentTimeMillis()
                            UsbUtil.instance.readMsg(usbDevice)
                        }
                    }

                    override fun deniedPermissions() {
                        permissionUtil.showTips("当前手机需要读写文件权限，是否手动设置?")
                    }
                })
        }

        btnClearLog.setOnClickListener {
            tvContent.text = ""
        }

        tvContent.movementMethod = ScrollingMovementMethod.getInstance()
    }

    private fun appendCotent(msg: String?) {
        runOnUiThread {
            tvContent.apply {
                append("$msg\n")
                val offset = lineCount * lineHeight
                if (offset > height) {
                    scrollTo(0, offset - height)
                }

            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionUtil.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onResume() {
        super.onResume()
        UsbUtil.instance.registerReceiver()
    }

    override fun onPause() {
        super.onPause()
        UsbUtil.instance.unregisterReceiver()
    }

    override fun onDestroy() {
        super.onDestroy()
        UsbUtil.instance.closePort(100)
    }

    private fun writeSrc2File(fileName: String, data: ByteArray?) {
        if (data == null) return
//        thread {
            val file = checkFileExists(fileName)
            FileOutputStream(file, true).apply {
                write(data)
                close()
            }
//        }

    }

    /**
     * 检测目标文件是否存在，不存在则自动创建
     */
    private fun checkFileExists(fileName: String): File {
        val dir = File("${Environment.getExternalStorageDirectory().absolutePath}/chws/")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        val file = File(dir, "$fileName.txt")
        if (!file.exists()) {
            file.createNewFile()
        }
        return file
    }
}
