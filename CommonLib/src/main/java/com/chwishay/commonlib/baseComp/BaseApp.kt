package com.chwishay.commonlib.baseComp

import android.app.Application

//  ┏┓　　　┏┓
//┏┛┻━━━┛┻┓
//┃　　　　　　　┃
//┃　　　━　　　┃
//┃　┳┛　┗┳　┃
//┃　　　　　　　┃
//┃　　　┻　　　┃
//┃　　　　　　　┃
//┗━┓　　　┏━┛
//   ┃　　　┃   神兽保佑
//   ┃　　　┃   代码无BUG！
//   ┃　　　┗━━━┓
//   ┃　　　　　　　┣┓
//   ┃　　　　　　　┏┛
//   ┗┓┓┏━┳┓┏┛
//     ┃┫┫　┃┫┫
//     ┗┻┛　┗┻┛
/**
 * author:RanQing
 * date:2020/5/7 0007 17:01
 * description:
 **/
open class BaseApp: Application() {

    companion object {
        val instance: BaseApp by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { BaseApp() }
    }

    override fun onCreate() {
        super.onCreate()
    }
}