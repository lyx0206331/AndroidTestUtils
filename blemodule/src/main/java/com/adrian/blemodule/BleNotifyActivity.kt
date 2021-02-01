package com.adrian.blemodule

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.adrian.blemodule.databinding.ActivityBleNotifyBinding

class BleNotifyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_ble_notify)
        val binding = DataBindingUtil.setContentView<ActivityBleNotifyBinding>(
            this,
            R.layout.activity_ble_notify
        )
    }
}