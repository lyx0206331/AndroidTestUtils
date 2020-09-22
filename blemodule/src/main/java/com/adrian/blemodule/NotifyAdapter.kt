package com.adrian.blemodule

import android.bluetooth.BluetoothGattCharacteristic
import android.content.Context
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chwishay.commonlib.tools.orDefault
import com.chwishay.commonlib.tools.showShortToast
import com.clj.fastble.BleManager
import com.clj.fastble.callback.BleNotifyCallback
import com.clj.fastble.exception.BleException
import com.clj.fastble.utils.HexUtil
import org.jetbrains.anko.find
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.sdk27.coroutines.onCheckedChange
import org.jetbrains.anko.sdk27.coroutines.onClick

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
 * date:2020/9/8 0008 16:23
 * description:
 */
class NotifyAdapter(val context: Context) :
    RecyclerView.Adapter<NotifyAdapter.NotifyViewHolder>() {

    class NotifyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDevName = itemView.findViewById<TextView>(R.id.tvDeviceName)
        val tvReceiveTimeLen = itemView.find<TextView>(R.id.tvReceiveTimeLen)
        val tvReceiveSpeed = itemView.find<TextView>(R.id.tvReceiveSpeed)
        val tvTotalData = itemView.find<TextView>(R.id.tvTotalData)
        val switchNotify = itemView.find<Switch>(R.id.switchNotify)
        val etFileName = itemView.find<EditText>(R.id.etFileName)
        val tvRssi = itemView.find<TextView>(R.id.tvRssi)
        val btnSave = itemView.find<Button>(R.id.btnSave)
        val btnClear = itemView.find<Button>(R.id.btnClear)
        val tvData = itemView.find<TextView>(R.id.tvData)
            .apply { movementMethod = ScrollingMovementMethod.getInstance() }
    }

    var devices: ArrayList<BleDeviceInfo>? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotifyViewHolder {
        return NotifyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_bt_notify, parent, false)
        )
    }

    override fun onBindViewHolder(holder: NotifyViewHolder, position: Int) {
        val bleDeviceInfo = devices?.get(position)
        bleDeviceInfo?.bleDevice?.apply {
            holder.tvDevName.text = "$name($mac)"
            holder.tvRssi.text = "信号强度:${rssi}dBm"
            BleManager.getInstance().getBluetoothGatt(this).services.let {
                it.forEachIndexed { index, bluetoothGattService ->
                    if (index == it.size - 1) {
                        bluetoothGattService.characteristics.forEach { gattCharacteristic ->
                            if (gattCharacteristic.properties.and(BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                                holder.switchNotify.onCheckedChange { buttonView, isChecked ->
                                    holder.etFileName.isEnabled = !isChecked
                                    if (isChecked) {
                                        bleDeviceInfo.startReceive()
                                        BleManager.getInstance().notify(
                                            this@apply,
                                            gattCharacteristic.service.uuid.toString(),
                                            gattCharacteristic.uuid.toString(),
                                            object : BleNotifyCallback() {
                                                override fun onNotifySuccess() {
                                                    appendData(holder.tvData, "notify success")
                                                }

                                                override fun onNotifyFailure(exception: BleException?) {
                                                    appendData(holder.tvData, exception.toString())
                                                }

                                                override fun onCharacteristicChanged(data: ByteArray?) {
                                                    holder.tvRssi.text = "信号强度:${rssi}dBm"
                                                    val data = gattCharacteristic.value
                                                    bleDeviceInfo.lastData = data
                                                    holder.tvReceiveSpeed.text =
                                                        "${bleDeviceInfo.speed}byte/s"
                                                    holder.tvTotalData.text =
                                                        "总接收数据:${bleDeviceInfo.totalSize}byte"
                                                    appendData(
                                                        holder.tvData,
                                                        HexUtil.formatHexString(data, true)
                                                    )
                                                }
                                            })
                                    } else {
                                        bleDeviceInfo.stopReceive()
                                        holder.tvReceiveTimeLen.text = "总接收时长:${bleDeviceInfo.totalReceiveTime}ms"
                                        BleManager.getInstance().stopNotify(
                                            this@apply,
                                            gattCharacteristic.service.uuid.toString(),
                                            gattCharacteristic.uuid.toString()
                                        )
                                    }
                                }
                                holder.btnSave.onClick {
                                    if (!bleDeviceInfo.needSave && holder.switchNotify.isChecked) {
                                        context.showShortToast("请先关闭接收通知")
                                        return@onClick
                                    }
                                    val fileName = holder.etFileName.text
                                    if (fileName.isNullOrEmpty() || fileName.trim()
                                            .isNullOrEmpty()
                                    ) {
                                        holder.btnSave.context.showShortToast("请输入文件名")
                                    } else {
                                        bleDeviceInfo.needSave = !bleDeviceInfo.needSave
                                        if (bleDeviceInfo.needSave) {
                                            bleDeviceInfo.fileName = fileName.toString()
                                            context.showShortToast("开始保存数据")
                                            holder.btnSave.text = "停止保存"
                                        } else {
                                            context.showShortToast("停止保存数据")
                                            holder.btnSave.text = "开始保存"
                                            holder.tvReceiveTimeLen.text = "总接收时长:${bleDeviceInfo.totalReceiveTime}ms"
                                        }
                                    }
                                }
                                holder.btnClear.onClick {
                                    bleDeviceInfo.totalSize = 0
                                    holder.tvData.text = ""
                                    holder.tvTotalData.text = "总接收数据:${bleDeviceInfo.totalSize}byte"
                                    holder.tvReceiveSpeed.text = "0byte/s"
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 追加数据并滚动到最后
     */
    private fun appendData(tv: TextView, data: String) {
        context.runOnUiThread {
            tv.apply {
                if (text.length >= 4000) {
                    text = ""
                }
                append(data)
                append("\n")
                val offset = lineCount * lineHeight
                if (offset > height) {
                    scrollTo(0, offset - height)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return devices?.size.orDefault()
    }
}