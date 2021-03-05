package com.adrian.testutils

import android.Manifest
import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.fastjson.JSONArray
import com.chwishay.commonlib.tools.ExoLogUtil
import com.chwishay.commonlib.tools.PermissionUtil
import com.chwishay.commonlib.tools.logE
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private var permissionUtil: PermissionUtil by lazy { PermissionUtil(this) }
    private var permissions = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnParseLog.setOnClickListener {
            checkPermissions()
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

    private fun checkPermissions() {
        permissionUtil.requestPermission(
            permissions,
            object : PermissionUtil.IPermissionCallback {
                override fun allowedPermissions() {
                    thread {
                        var data =
                            ExoLogUtil.parseLogFile("${Environment.getExternalStorageDirectory().absolutePath}/chws/Test/t20210105113711_u30032_hnlsyy5710_m00000000540040010000005001003500_i10165320191106_l.txt")
                        runOnUiThread {
//                                showShortToast(JSONArray.toJSON(data).toString())
                            var content = JSONArray.toJSON(data).toString()
                            tvContent.text = content
                            "parseLog".logE(content)
                        }

                    }
                }

                override fun deniedPermissions() {
                    permissionUtil.showTips("当前功能需要文件读写权限，是否手动设置?")
                }
            })
    }
}