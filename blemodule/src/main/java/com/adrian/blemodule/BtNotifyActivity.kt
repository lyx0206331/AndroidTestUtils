package com.adrian.blemodule

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chwishay.commonlib.baseComp.BaseAct
import com.chwishay.commonlib.tools.formatHexString
import com.chwishay.commonlib.tools.showShortToast
import com.clj.fastble.BleManager
import com.clj.fastble.callback.BleWriteCallback
import com.clj.fastble.data.BleDevice
import com.clj.fastble.exception.BleException
import com.clj.fastble.utils.HexUtil
import kotlinx.android.synthetic.main.activity_bt_notify.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import java.util.*

class BtNotifyActivity : BaseAct(), Observer {

    companion object {

        @JvmStatic
        fun startActivity(context: Context, devices: ArrayList<BleDevice>) {
            val intent = Intent(context, BtNotifyActivity::class.java)
            intent.putParcelableArrayListExtra("devices", devices)
            context.startActivity(intent)
        }
    }

    private val adapter by lazy {
        NotifyAdapter(this)
    }

    private var devices: ArrayList<BleDevice>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_bt_notify
    }

    override fun initVariables() {
        devices = intent.getParcelableArrayListExtra("devices")

        BleUUID.instance.let {
            it.serviceUUID = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb")
            it.charactWriteUUID = UUID.fromString("0000fff3-0000-1000-8000-00805f9b34fb")
            it.charactNotifyUUID = UUID.fromString("0000fff1-0000-1000-8000-00805f9b34fb")
        }
    }

    private fun getDeviceInfos(): ArrayList<BleDeviceInfo> {
        val list = arrayListOf<BleDeviceInfo>()
        devices?.forEach {
            list.add(BleDeviceInfo(it))
        }
        return list
    }

    override fun initViews() {
        btnSend.onClick {
            if (etInputCmd.text.isNullOrEmpty()) {
                showShortToast("请输入十六进制指令")
                return@onClick
            } else {
                val cmd = etInputCmd.text.toString()
                devices?.forEach {
                    BleManager.getInstance().write(it,
                        BleUUID.instance.serviceUUID.toString(),
                        BleUUID.instance.charactWriteUUID.toString(),
                        HexUtil.hexStringToBytes(cmd),
                        object : BleWriteCallback() {
                            override fun onWriteSuccess(
                                current: Int,
                                total: Int,
                                justWrite: ByteArray?
                            ) {
                                showShortToast("向${it.name}发送指令${justWrite?.formatHexString(" ")}成功,时间:${System.currentTimeMillis()}")
                            }

                            override fun onWriteFailure(exception: BleException?) {
                                showShortToast("向${it.name}发送指令失败:${exception.toString()}")
                            }
                        })
                }
            }
        }
        rvNotify.adapter = adapter
        adapter.devices = getDeviceInfos()
        //处理RecyclerView内外滑动冲突问题
        rvNotify.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                rv.findChildViewUnder(e.x, e.y)?.apply {
//                    val holder = rv.getChildViewHolder(this) as NotifyAdapter.NotifyViewHolder
                    (this as ViewGroup).requestDisallowInterceptTouchEvent(true)
                }
                return false
            }

            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
            }

            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
            }
        })
    }

    override fun loadData() {
        ObserverManager.getInstance().addObserver(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        devices?.forEach {
            BleManager.getInstance().clearCharacterCallback(it)
        }
        ObserverManager.getInstance().deleteObserver(this)
    }

    override fun disConnected(bleDevice: BleDevice) {
        devices?.forEach {
            if (bleDevice != null && it.key == bleDevice.key) {
                showShortToast("设备${it.key}已断开")
            }
        }
    }
}