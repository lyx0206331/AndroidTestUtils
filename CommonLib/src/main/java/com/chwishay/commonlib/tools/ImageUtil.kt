package com.chwishay.commonlib.tools

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.view.View
import java.io.File

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
 * date:2020/12/15 0015 14:04
 * description:
 */
object ImageUtil {

    /**
     * 此方法调用后可完成布局，并通过测量可获取宽高
     */
    fun layoutView(v: View, width: Int, height: Int) {
        v.layout(0, 0, width, height)
        val measureWidth = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY)
        val measureHeight = View.MeasureSpec.makeMeasureSpec(10000, View.MeasureSpec.AT_MOST)
        v.measure(measureWidth, measureHeight)
        //measure后不会改变View的实际大小，需要layout进行重布局
        v.layout(0, 0, v.measuredWidth, v.measuredHeight)
    }

    private fun loadBmpFromView(v: View): Bitmap = v.let {
        val w = it.width
        val h = it.height
        val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val c = Canvas(bmp)
        //不设置则为透明
        c.drawColor(Color.WHITE)

        v.layout(0, 0, w, h)
        v.draw(c)
        bmp
    }

    fun save2sdcard(v: View, dirPath: String, fileName: String): String {
        return File(dirPath).apply { mkdirs() }.run {
            File(this, "$fileName").let {
                if (it.exists()) {
                    it.delete()
                    File(this, "$fileName")
                } else {
                    it
                }
            }
        }.let { file ->
            file.outputStream().use { fos ->
                loadBmpFromView(v).let { bmp ->
                    bmp.compress(
                        if (fileName.endsWith(".png")) Bitmap.CompressFormat.PNG else Bitmap.CompressFormat.JPEG,
                        90,
                        fos
                    )
                }
                fos.flush()
            }
            v.destroyDrawingCache()
            file.absolutePath
        }
    }
}