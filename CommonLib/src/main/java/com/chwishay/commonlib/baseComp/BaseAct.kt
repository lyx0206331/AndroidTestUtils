package com.chwishay.commonlib.baseComp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * Author:RanQing
 * Create time:20-8-3 上午10:28
 * Description:
 **/
abstract class BaseAct : AppCompatActivity() {

    companion object {
        val TAG = "P00_TAG"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initVariables()
        setContentView(getLayoutId())
        initViews()
        loadData()
    }

    protected abstract fun getLayoutId(): Int

    protected abstract fun initVariables()

    protected abstract fun initViews()

    protected abstract fun loadData()

}