package com.chwishay.commonlib.tools

import androidx.annotation.IntDef
import jxl.Workbook
import jxl.WorkbookSettings
import jxl.format.Alignment
import jxl.format.Border
import jxl.format.BorderLineStyle
import jxl.format.Colour
import jxl.write.Label
import jxl.write.WritableCellFormat
import jxl.write.WritableFont
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
 * date:2020/11/26 0026 11:10
 * description:Excel数据导出工具
 */
class ExcelUtil private constructor() {

    companion object {

        //对齐方式常量
        const val ALIGNMENT_GENERAL = 0
        const val ALIGNMENT_LEFT = 1
        const val ALIGNMENT_CENTER = 2
        const val ALIGNMENT_RIGHT = 3
        const val ALIGNMENT_FILL = 4
        const val ALIGNMENT_JUSTIFY = 5

        //背景色常量
        const val BG_COLOR_GRAY_25 = 0
        const val BG_COLOR_GRAY_50 = 1
        const val BG_COLOR_GRAY_80 = 2

        val instance: ExcelUtil by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { ExcelUtil() }
    }

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(
        ALIGNMENT_GENERAL,
        ALIGNMENT_LEFT,
        ALIGNMENT_CENTER,
        ALIGNMENT_RIGHT,
        ALIGNMENT_FILL,
        ALIGNMENT_JUSTIFY
    )
    annotation class AlignmentType

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(BG_COLOR_GRAY_25, BG_COLOR_GRAY_50, BG_COLOR_GRAY_80)
    annotation class BackgroundColor

    private var headerFont = WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD)
    private var headerFormat = WritableCellFormat(headerFont).apply {
        alignment = Alignment.CENTRE
        setBorder(Border.ALL, BorderLineStyle.THIN)
        setBackground(Colour.GRAY_25)
    }

    private var contentFont = WritableFont(WritableFont.ARIAL, 12)
    private var contentFormat = WritableCellFormat(contentFont).apply {
        setBorder(Border.ALL, BorderLineStyle.THIN)
    }

    //文件名
    private var fileName: String = ""
    private var sheetCount: Int = 0

    //每一页的列名数组。对应表数组下的表列名
    private var colNamesPerPage: Array<Array<String>?>? = null

    //表头文字大小
    var headerFontSize = 10

    //表头是否加粗
    var headerIsBold = true

    //表头对齐方式
    @AlignmentType
    var headerAlignment = ALIGNMENT_CENTER

    //表头背景色
    @BackgroundColor
    var headerBgColor = BG_COLOR_GRAY_25

    //内容字体大小
    var contentFontSize = 12

    //内容是否加粗
    var contentIsBold = false

    //内容对齐方式
    @AlignmentType
    var contentAlignment = ALIGNMENT_LEFT

    /**
     * 设置表头格式
     */
    private fun setHeaderFormat() {
        headerFont = WritableFont(
            WritableFont.ARIAL,
            headerFontSize,
            if (headerIsBold) WritableFont.BOLD else WritableFont.NO_BOLD
        )
        headerFormat = WritableCellFormat(headerFont).also {
            it.alignment = getAlignmentType(headerAlignment)
            it.setBorder(Border.ALL, BorderLineStyle.THIN)
            it.setBackground(getBackgroundColor(headerBgColor))
        }
    }

    /**
     * 设置内容格式
     */
    private fun setContentFormat() {
        contentFont = WritableFont(
            WritableFont.ARIAL,
            contentFontSize,
            if (contentIsBold) WritableFont.BOLD else WritableFont.NO_BOLD
        )
        contentFormat = WritableCellFormat(contentFont).also {
            it.alignment = getAlignmentType(contentAlignment)
            it.setBorder(Border.ALL, BorderLineStyle.THIN)
        }
    }

    /**
     * 获取对齐类型
     */
    private fun getAlignmentType(@AlignmentType alignment: Int) = when (alignment) {
        ALIGNMENT_LEFT -> Alignment.LEFT
        ALIGNMENT_CENTER -> Alignment.CENTRE
        ALIGNMENT_RIGHT -> Alignment.RIGHT
        ALIGNMENT_FILL -> Alignment.FILL
        ALIGNMENT_JUSTIFY -> Alignment.JUSTIFY
        else -> Alignment.GENERAL
    }

    /**
     * 获取背景颜色
     */
    private fun getBackgroundColor(@BackgroundColor background: Int) = when (background) {
        BG_COLOR_GRAY_25 -> Colour.GRAY_25
        BG_COLOR_GRAY_50 -> Colour.GRAY_50
        BG_COLOR_GRAY_80 -> Colour.GRAY_80
        else -> Colour.DEFAULT_BACKGROUND
    }

    /**
     * 设置文件信息
     * @param fileName  生成文件名称
     * @param sheetNames 表名
     * @param colNamesPerPage 每张表中的列名
     */
    suspend fun setFileInfo(
        fileName: String,
        sheetNames: Array<String>,
        colNamesPerPage: Array<Array<String>?>?
    ): ExcelUtil {
        this.fileName = fileName
        this.sheetCount = sheetNames.size
        this.colNamesPerPage = colNamesPerPage
        setHeaderFormat()
        setContentFormat()
        val file = File(fileName)
        if (!file.exists() || file.isDirectory) {
            file.createNewFile()
        }
        withContext(Dispatchers.IO) {

            val workbook = Workbook.createWorkbook(file)
            sheetNames.forEachIndexed { index, sheetName ->
                workbook.createSheet(sheetName, index).also {
                    if (colNamesPerPage != null && index < colNamesPerPage?.size.orDefault()) {
                        colNamesPerPage[index]?.forEachIndexed { i, s ->
                            it.addCell(Label(i, 0, s, headerFormat))
                        }
                    }
                    it.setRowView(0, 340)
                }
            }
            workbook.write()
            workbook.close()
        }
        return this
    }

    /**
     * 是否存在表头
     * @param sheetIndex 表索引序列
     */
    private fun hasHeader(sheetIndex: Int): Boolean =
        colNamesPerPage != null && sheetIndex < colNamesPerPage?.size.orDefault() && colNamesPerPage!![sheetIndex] != null

    /**
     * 将数据导出为excel文件(行优先),适用于行数量不多，但列数很多的数据
     * @param dataList 三维列表,分属:表list->行list->列list。dataList[表index][行index][列index].
     */
    suspend fun exportRowFirst(dataList: List<List<List<String>>>): ExcelUtil {
        if (fileName.isNullOrEmpty()) {
            throw NullPointerException("请输入正确的文件名")
        }
        withContext(Dispatchers.IO) {
            //遍历表list
            dataList.forEachIndexed { index, list ->
                WorkbookSettings().encoding = "UTF-8"
                File(fileName).let { file ->
                    file.inputStream().use { fis ->
                        Workbook.createWorkbook(file, Workbook.getWorkbook(fis)).also { writebook ->
                            //有表头时，数据插入下移一行，无表头时，不下移
                            val rowOffset = if (hasHeader(index)) 1 else 0
                            writebook.getSheet(index).let { sheet ->
                                //遍历行
                                list.forEachIndexed { j, t ->
                                    //遍历列
                                    t.forEachIndexed { i, s ->
                                        sheet.addCell(Label(i, j + rowOffset, s, contentFormat))
                                        sheet.setColumnView(
                                            i,
                                            s.length + (if (s.length > 4) 5 else 8)
                                        )
                                    }
                                    sheet.setRowView(j + rowOffset, 350)
                                }
                                writebook.write()
                            }
                        }.close()
                    }
                }
            }
        }
        return this
    }

    /**
     * 将数据导出为excel文件(列优先),适用于列数量不多，但行数很多的数据
     * @param dataList 三维列表,分属:表list->列list->行list。dataList[表index][列index][行index].
     */
    suspend fun exportColFirst(dataList: List<List<List<String>>>): ExcelUtil {
        if (fileName.isNullOrEmpty()) {
            throw NullPointerException("请输入正确的文件名")
        }
        withContext(Dispatchers.IO) {
            //遍历表list
            dataList.forEachIndexed { index, list ->
                WorkbookSettings().encoding = "UTF-8"
                File(fileName).let { file ->
                    file.inputStream().use { fis ->
                        Workbook.createWorkbook(file, Workbook.getWorkbook(fis)).also { writebook ->
                            //有表头时，数据插入下移一行，无表头时，不下移
                            val rowOffset = if (hasHeader(index)) 1 else 0
                            writebook.getSheet(index).let { sheet ->
                                //遍历列
                                list.forEachIndexed { j, t ->
                                    //遍历行
                                    t.forEachIndexed { i, s ->
                                        sheet.addCell(Label(j, i + rowOffset, s, contentFormat))
                                        sheet.setColumnView(
                                            j,
                                            s.length + (if (s.length > 4) 5 else 8)
                                        )
                                        //只需要在遍历第一列的每一行时设置行高便可
                                        if (j == 0) {
                                            sheet.setRowView(i + rowOffset, 350)
                                        }
                                    }
                                }
                                writebook.write()
                            }
                        }.close()
                    }
                }
            }
        }
        return this
    }

    /**
     * 将数据导出为excel文件(列优先),适用于列数量不多，但行数很多的数据
     * @param dataList 数据列表.
     */
    suspend fun exportDataList(dataList: List<List<ExcelItem>>): ExcelUtil {
        if (fileName.isNullOrEmpty()) {
            throw NullPointerException("请输入正确的文件名")
        }
        withContext(Dispatchers.IO) {
            dataList.forEachIndexed { index, list ->
                WorkbookSettings().encoding = "UTF-8"
                File(fileName).let { file ->
                    file.inputStream().use { fis ->
                        Workbook.createWorkbook(file, Workbook.getWorkbook(fis))
                            .also { writableWorkbook ->
                                val rowOffset = if (hasHeader(index)) 1 else 0
                                writableWorkbook.getSheet(index).let { sheet ->
                                    list.forEach { item ->
                                        sheet.addCell(
                                            Label(
                                                item.col,
                                                item.row + rowOffset,
                                                item.content,
                                                contentFormat
                                            )
                                        )
                                        sheet.setColumnView(
                                            item.col,
                                            item.content.length + (if (item.content.length > 4) 5 else 8)
                                        )
                                    }
                                    writableWorkbook.write()
                                }
                            }.close()
                    }
                }
            }
        }
        return this
    }

    class ExcelItem(
        val row: Int,
        val col: Int,
        val content: String,
        val format: WritableCellFormat = WritableCellFormat(
            WritableFont(
                WritableFont.ARIAL,
                12
            )
        ).apply {
            setBorder(Border.ALL, BorderLineStyle.THIN)
        }
    )

//    fun insertImageFile(sheetIndex: Int, row: Int, column: Int, imageFile: File) {
//        if (imageFile.exists()) {
//            if (sheetIndex >= sheetCount) {
//                throw IndexOutOfBoundsException("表索引超出总表数量")
//            }
//            imageFile.let { file ->
//                file.inputStream().use { fis ->
//                    Workbook.createWorkbook(file, Workbook.getWorkbook(fis)).also { writebook ->
//                        writebook.getSheet(sheetIndex).let { sheet ->
//                            sheet.addImage(WritableImage(column*1.0, row*1.0, 400.0, 600.0, file))
//                        }
//                        writebook.write()
//                    }.close()
//                }
//            }
//        }
//    }
}