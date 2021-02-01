package com.adrian.blemodule

import android.Manifest
import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.content.Intent
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.adrian.blemodule.CmdUtil.parseImuData
import com.chwishay.commonlib.baseComp.BaseAct
import com.chwishay.commonlib.tools.PermissionUtil
import com.chwishay.commonlib.tools.logE
import com.chwishay.commonlib.tools.showShortToast
import com.clj.fastble.BleManager
import com.clj.fastble.callback.BleGattCallback
import com.clj.fastble.callback.BleScanCallback
import com.clj.fastble.data.BleDevice
import com.clj.fastble.exception.BleException
import com.clj.fastble.scan.BleScanRuleConfig
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.textResource

class MainActivity : BaseAct() {

    private val permissionUtil: PermissionUtil by lazy { PermissionUtil(this) }
    private val permissions = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    private val operatingAnim by lazy {
        AnimationUtils.loadAnimation(this, R.anim.rotate)
            .apply { interpolator = LinearInterpolator() }
    }
    private val deviceAdapter by lazy {
        DeviceAdapter(this).apply {
            setOnDeviceClickListener(object : DeviceAdapter.OnDeviceClickListener {
                override fun onConnect(bleDevice: BleDevice?) {
                    if (!BleManager.getInstance().isConnected(bleDevice)) {
                        BleManager.getInstance().cancelScan()
                        connect(bleDevice)
                    }
                }

                override fun onDisConnect(bleDevice: BleDevice?) {
                    if (BleManager.getInstance().isConnected(bleDevice)) {
                        BleManager.getInstance().disconnect(bleDevice)
                    }
                }

                override fun onDetail(bleDevice: BleDevice?) {
                    if(BleManager.getInstance().isConnected(bleDevice)) {
                        val intent = Intent(this@MainActivity, OperationActivity::class.java)
                        intent.putExtra(OperationActivity.KEY_DATA, bleDevice)
                        startActivity(intent)
                    }
                }
            })
        }
    }
    private val connectedAdapter by lazy {
        ConnectedDevAdapter(this)
    }
    private val progressDialog by lazy { ProgressDialog(this) }

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun initVariables() {
    }

    override fun initViews() {
        btn_scan.textResource = R.string.start_scan
        btn_scan.onClick {
            if (btn_scan.text == getString(R.string.start_scan)) {
                checkPermissions()
            } else {
                BleManager.getInstance().cancelScan()
            }
        }
        btn_entry.onClick {
            if (connectedAdapter.allDevices.isNullOrEmpty()) {
                showShortToast("请先连接设备")
            } else {
                BtNotifyActivity.startActivity(
                    this@MainActivity,
                    connectedAdapter.allDevices
                )
            }
        }
        list_device.adapter = deviceAdapter
        list_connected.adapter = connectedAdapter
    }

    override fun loadData() {
    }

    private fun test() {
        byteArrayOf(/*0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0xff.toByte(), 0xaa.toByte(),*/
            0xaa.toByte(),
            0xff.toByte(),
            0x2d.toByte(),
            0xa0.toByte(),
            0xdc.toByte(),
            0x5f.toByte(),
            14,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0xff.toByte(),
            0xaa.toByte(),
            0xaa.toByte(),
            0xff.toByte(),
            0x2d.toByte(),
            0xa0.toByte(),
            0xdc.toByte(),
            0x5f.toByte(),
            14,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0xff.toByte(),
            0xaa.toByte(),
            0xaa.toByte(),
            0xff.toByte(),
            0x2d.toByte(),
            0xa0.toByte(),
            0xdc.toByte(),
            0x5f.toByte(),
            14,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0xff.toByte(),
            0xaa.toByte(),
            0xaa.toByte(),
            0xff.toByte(),
            0x2d.toByte(),
            0xa0.toByte(),
            0xdc.toByte(),
            0x5f.toByte(),
            14,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0xff.toByte(),
            0xaa.toByte(),
            0xaa.toByte(),
            0xff.toByte(),
            0x2d.toByte(),
            0xa0.toByte(),
            0xdc.toByte(),
            0x5f.toByte(),
            14,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0xff.toByte(),
            0xaa.toByte()/*, 0xaa.toByte(), 0xff.toByte(), 0x2d.toByte(), 0xa0.toByte(), 0xdc.toByte(), 0x5f.toByte(), 0x14, 0x00*/
        ).parseImuData()
    }

    override fun onResume() {
        super.onResume()
        showConnectedDevice()
    }

    override fun onDestroy() {
        super.onDestroy()
        BleManager.getInstance().disconnectAllDevice()
        BleManager.getInstance().destroy()
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
        val btAdapter = BluetoothAdapter.getDefaultAdapter()
        if (!btAdapter.isEnabled) {
            showShortToast("请先打开蓝牙")
        } else {
            permissionUtil.requestPermission(
                permissions,
                object : PermissionUtil.IPermissionCallback {
                    override fun allowedPermissions() {
                        startScan()
                    }

                    override fun deniedPermissions() {
                        permissionUtil.showTips("当前手机扫描蓝牙需要打开定位功能, 读写文件需要读写权限，是否手动设置?")
                    }
                })
        }
    }

    private fun showConnectedDevice() {
        deviceAdapter.clearConnectedDevice()
        BleManager.getInstance().allConnectedDevice.forEach {
            deviceAdapter.addDevice(it)
        }
        deviceAdapter.notifyDataSetChanged()
    }

    private fun startScan() {
        BleManager.getInstance().initScanRule(
            BleScanRuleConfig.Builder().setServiceUuids(null)
                .setDeviceName(true, etBleFilter.text.toString())
                .setDeviceMac("").setAutoConnect(false).setScanTimeOut(10000).build()
        )
        BleManager.getInstance().scan(object : BleScanCallback() {
            override fun onScanStarted(success: Boolean) {
                deviceAdapter.clearScanDevice()
                deviceAdapter.notifyDataSetChanged()
                img_loading.startAnimation(operatingAnim)
                img_loading.isVisible = true
                btn_scan.textResource = R.string.stop_scan
                TAG.logE("onScanStarted")
            }

            override fun onScanning(bleDevice: BleDevice?) {
                TAG.logE(bleDevice.toString())
                deviceAdapter.addDevice(bleDevice)
                deviceAdapter.notifyDataSetChanged()
            }

            override fun onScanFinished(scanResultList: MutableList<BleDevice>?) {
                img_loading.clearAnimation()
                img_loading.isInvisible = true
                btn_scan.textResource = R.string.start_scan
                TAG.logE("onScanFinished")
            }
        })
    }

    private fun connect(bleDevice: BleDevice?) {
        BleManager.getInstance().connect(bleDevice, object : BleGattCallback() {
            override fun onStartConnect() {
                progressDialog.show()
            }

            override fun onConnectFail(bleDevice: BleDevice?, exception: BleException?) {
                img_loading.clearAnimation()
                img_loading.isInvisible = true
                btn_scan.textResource = R.string.start_scan
                progressDialog.dismiss()
                showShortToast(R.string.connect_fail)
            }

            override fun onConnectSuccess(
                bleDevice: BleDevice?,
                gatt: BluetoothGatt?,
                status: Int
            ) {
                progressDialog.dismiss()
                deviceAdapter.addDevice(bleDevice)
                deviceAdapter.notifyDataSetChanged()
                connectedAdapter.addDevice(bleDevice)
                connectedAdapter.notifyDataSetChanged()
//                BleManager.getInstance().setMtu(bleDevice, 37, object : BleMtuChangedCallback() {
//                    override fun onSetMTUFailure(exception: BleException?) {
//                        showShortToast("MTU修改失败")
//                    }
//
//                    override fun onMtuChanged(mtu: Int) {
//                        TAG.logE("MTU修改为$mtu")
//                    }
//                })
            }

            override fun onDisConnected(
                isActiveDisConnected: Boolean,
                device: BleDevice?,
                gatt: BluetoothGatt?,
                status: Int
            ) {
                progressDialog.dismiss()
                deviceAdapter.removeDevice(bleDevice)
                deviceAdapter.notifyDataSetChanged()
                connectedAdapter.removeDevice(bleDevice)
                connectedAdapter.notifyDataSetChanged()
                if (isActiveDisConnected) {
                    showShortToast("${getString(R.string.active_disconnected)}:${bleDevice?.mac}")
                } else {
                    showShortToast("${bleDevice?.mac}:${R.string.disconnected}")
                    bleDevice?.apply { ObserverManager.getInstance().notifyObserver(this) }
                }
            }
        })
    }
}
