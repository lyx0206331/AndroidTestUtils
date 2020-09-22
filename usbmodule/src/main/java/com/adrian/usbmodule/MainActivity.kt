package com.adrian.usbmodule

import android.Manifest
import android.hardware.usb.UsbDevice
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.text.method.ScrollingMovementMethod
import com.chwishay.commonlib.tools.PermissionUtil
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileOutputStream
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private var usbDevice: UsbDevice? = null

    private val permissionUtil: PermissionUtil by lazy { PermissionUtil(this) }
    private val permissions = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    private var totalDataLength = 0
    private var createTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        UsbUtil.instance.init(this).logCallback = object : UsbUtil.ILogCallback {
            override fun outputLog(msg: String?) {
                appendCotent(msg)
            }

            override fun outputData(bytes: ByteArray) {
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
                            appendCotent("收到数据大小为:$totalDataLength bytes")
                        } else {
                            btnReadData.text = "停止读取"
                            UsbUtil.instance.isReading = true
                            totalDataLength = 0
                            createTime = System.currentTimeMillis()
                            UsbUtil.instance.readMsg(usbDevice)
                        }
                    }

                    override fun deniedPermissions() {
                        permissionUtil.showTips("当前手机需要读写文件权限，是否手动设置?")
                    }
                })
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
