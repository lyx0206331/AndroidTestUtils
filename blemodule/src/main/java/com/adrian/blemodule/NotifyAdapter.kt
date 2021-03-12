package com.adrian.blemodule

import android.content.Context
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.method.ScrollingMovementMethod
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.RecyclerView
import com.adrian.blemodule.CmdUtil.parseImuData
import com.chwishay.commonlib.tools.logE
import com.chwishay.commonlib.tools.orDefault
import com.chwishay.commonlib.tools.showShortToast
import com.clj.fastble.BleManager
import com.clj.fastble.callback.BleNotifyCallback
import com.clj.fastble.callback.BleRssiCallback
import com.clj.fastble.exception.BleException
import com.clj.fastble.utils.HexUtil
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import org.jetbrains.anko.find
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.sdk27.coroutines.onCheckedChange
import org.jetbrains.anko.sdk27.coroutines.onClick
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit

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
        val chart = itemView.find<LineChart>(R.id.chart).also {
            it.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                override fun onValueSelected(e: Entry?, h: Highlight?) {}

                override fun onNothingSelected() {}
            })
            it.description.isEnabled = true
            it.setTouchEnabled(true)
            it.isDragEnabled = true
            it.setScaleEnabled(true)
            it.setDrawGridBackground(false)
            it.setPinchZoom(true)
            it.setBackgroundColor(Color.LTGRAY)
            it.data = LineData().apply { setValueTextColor(Color.WHITE) }
            it.legend.apply {
                form = Legend.LegendForm.LINE
                textColor = Color.WHITE
            }
            it.xAxis.apply {
                textColor = Color.WHITE
                setDrawGridLines(false)
                setAvoidFirstLastClipping(false)
                isEnabled = true
            }
//            it.axisLeft.apply {
//                textColor = Color.WHITE
//                axisMaximum = 40f
//                axisMinimum = -40f
//                setDrawGridLines(true)
//            }
            //根据数据自动缩放展示最大最小值,不能设置axisLeft,否则自动缩放无效
            it.isAutoScaleMinMaxEnabled = true
            it.axisRight.isEnabled = false
            it.description.isEnabled = false
        }
    }

    fun LineChart.addEntry(imuData: ByteArray) = this.data?.let { d ->
        fun parseValue(value: UShort) = value.toFloat()

        //        fun getGyrValue(gyr: UShort) = (gyr.toInt() - 32768) * 3.14f / 16.4f / 180
        fun getDataSet(index: Int, @ColorInt color: Int, name: String) =
            d.getDataSetByIndex(index) ?: LineDataSet(null, name).also { lds ->
                lds.axisDependency = YAxis.AxisDependency.LEFT
                lds.color = color
                lds.setCircleColor(Color.WHITE)
                lds.lineWidth = 1f
                lds.circleRadius = 2f
                lds.fillAlpha = 65
                lds.fillColor = color
                lds.highLightColor = Color.rgb(244, 177, 177)
                lds.valueTextColor = Color.WHITE
                lds.valueTextSize = 9f
                lds.setDrawCircles(false)
                d.addDataSet(lds)
            }
        imuData.parseImuData()?.resultList.takeIf { !it.isNullOrEmpty() }?.run { this[0] }
            ?.let { entity ->
                val value1Set = getDataSet(0, ColorTemplate.getHoloBlue(), "value1")
//            val gyrX1Set = getDataSet(1, context.getColor1(R.color.green01FD01), "gyrX1")
//            val accY1Set = getDataSet(2, context.getColor1(R.color.yellowFFFF00), "accY1")
//            val gyrY1Set = getDataSet(3, context.getColor1(R.color.purple7E2E8D), "gyrY1")
//            val accZ1Set = getDataSet(4, context.getColor1(R.color.colorPrimary), "accZ1")
//            val gyrZ1Set = getDataSet(5, context.getColor1(R.color.redFE0000), "gyrZ1")
                val value1 = parseValue(entity.value1)
//            val gyrX1 = getGyrValue(entity.gyrX1)
//            val accY1 = getAccValue(entity.accY1)
//            val gyrY1 = getGyrValue(entity.gyrY1)
//            val accZ1 = getAccValue(entity.accZ1)
//            val gyrZ1 = getGyrValue(entity.gyrZ1)
//            "IMU_Value".logE("accX1:$accX1, gyrX1:$gyrX1")
                d.addEntry(Entry(value1Set.entryCount.toFloat(), value1), 0)
//            d.addEntry(Entry(gyrX1Set.entryCount.toFloat(), gyrX1), 1)
//            d.addEntry(Entry(accY1Set.entryCount.toFloat(), accY1), 2)
//            d.addEntry(Entry(gyrY1Set.entryCount.toFloat(), gyrY1), 3)
//            d.addEntry(Entry(accZ1Set.entryCount.toFloat(), accZ1), 4)
//            d.addEntry(Entry(gyrZ1Set.entryCount.toFloat(), gyrZ1), 5)
                d.notifyDataChanged()

                this.notifyDataSetChanged()

                this.setVisibleXRangeMaximum(1000f)

                this.moveViewToX(d.entryCount.toFloat())

            }
    }

    var devices: ArrayList<BleDeviceInfo>? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    val exec by lazy { ScheduledThreadPoolExecutor(1) }

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
            exec.scheduleAtFixedRate({
                BleManager.getInstance().readRssi(this, object : BleRssiCallback() {
                    override fun onRssiFailure(exception: BleException?) {
                        "BLE".logE("信号读取失败")
                    }

                    override fun onRssiSuccess(rssi: Int) {
//                        "BLE".logE("信号强度:${rssi}dBm")
                        val colorResId = when {
                            rssi >= -60 -> R.color.green01FD01
                            rssi >= -70 -> R.color.yellowEAB11D
                            rssi >= -80 -> R.color.redFE0000
                            else -> R.color.brownD95218
                        }
                        val ssb = SpannableStringBuilder("信号强度:${rssi}dBm")
                        ssb.setSpan(
                            ForegroundColorSpan(context.resources.getColor(colorResId)),
                            4,
                            ssb.length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                        holder.tvRssi.text = ssb
                    }
                })
            }, 1000, 1000, TimeUnit.MILLISECONDS)
            BleManager.getInstance().getBluetoothGatt(this).services.let {
//                it.forEachIndexed { index, bluetoothGattService ->
//                    if (index == it.size - 1) {
//                        bluetoothGattService.characteristics.forEach { gattCharacteristic ->
//                            if (gattCharacteristic.properties.and(BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                holder.switchNotify.onCheckedChange { buttonView, isChecked ->
                    holder.etFileName.isEnabled = !isChecked
                    bleDeviceInfo.serviceUUID = BleUUID.instance.serviceUUID
                    bleDeviceInfo.notifyUUID = BleUUID.instance.charactNotifyUUID
                    "BLE".logE("serviceUUID:${bleDeviceInfo.serviceUUID}  notifyUUID:${bleDeviceInfo.notifyUUID}")
                    if (isChecked) {
                        bleDeviceInfo.startReceive()
                        BleManager.getInstance().notify(
                            this@apply,
                            bleDeviceInfo.serviceUUID.toString(),
                            bleDeviceInfo.notifyUUID.toString(),
                            object : BleNotifyCallback() {
                                override fun onNotifySuccess() {
                                    appendData(holder.tvData, "notify success")
                                }

                                override fun onNotifyFailure(exception: BleException?) {
                                    appendData(holder.tvData, exception.toString())
                                }

                                override fun onCharacteristicChanged(data: ByteArray?) {
                                    holder.tvRssi.text = "信号强度:${rssi}dBm"
                                    data?.let { d ->
                                        bleDeviceInfo.lastData = d
                                        holder.tvReceiveSpeed.text =
                                            "${bleDeviceInfo.speed}byte/s"
                                        holder.tvTotalData.text =
                                            "总接收数据:${bleDeviceInfo.totalSize}byte"
                                        holder.chart.addEntry(d)
                                        appendData(
                                            holder.tvData,
                                            HexUtil.formatHexString(d, true)
                                        )
                                    }
                                }
                            })
                    } else {
                        bleDeviceInfo.stopReceive()
                        holder.tvReceiveTimeLen.text = "总接收时长:${bleDeviceInfo.totalReceiveTime}ms"
                        BleManager.getInstance().stopNotify(
                            this@apply,
                            bleDeviceInfo.serviceUUID.toString(),
                            bleDeviceInfo.notifyUUID.toString()
                        )
                    }
                }
                holder.btnSave.onClick {
//                                    if (!bleDeviceInfo.needSave && holder.switchNotify.isChecked) {
//                                        context.showShortToast("请先关闭接收通知")
//                                        return@onClick
//                                    }
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
                            holder.tvReceiveTimeLen.text =
                                "总接收时长:${bleDeviceInfo.totalReceiveTime}ms"
                        }
                    }
                }
                holder.btnClear.onClick {
                    bleDeviceInfo.totalSize = 0
                    holder.tvData.text = ""
                    holder.tvTotalData.text = "总接收数据:${bleDeviceInfo.totalSize}byte"
                    holder.tvReceiveSpeed.text = "0byte/s"
                }
//                            }
//                        }
//                    }
//                }
            }
        }
    }

    /**
     * 追加数据并滚动到最后
     */
    private fun appendData(tv: TextView, data: String?) {
        if (data == null) return
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

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        "detachedRV".logE("onDetachedFromRecyclerView")
        devices?.forEach {
            if (it.serviceUUID != null && it.notifyUUID != null) {
                BleManager.getInstance()
                    .stopNotify(it.bleDevice, it.serviceUUID.toString(), it.notifyUUID.toString())
            }
        }
    }
}