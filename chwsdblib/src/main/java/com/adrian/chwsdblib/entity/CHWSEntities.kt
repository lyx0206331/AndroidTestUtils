package com.adrian.chwsdblib.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

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
 * date:2021/1/5 0005 16:01
 * description:
 */

@Entity(
    tableName = "account_info",
    indices = [Index(value = ["account_id", "hospital_no"], unique = true)]
)
data class AccountInfo(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @PrimaryKey @ColumnInfo(name = "account_id") val accountId: String,
    @ColumnInfo(name = "hospital_no") val hospitalNo: String,
    @ColumnInfo(name = "department_no") val departmentNo: String,
    val type: Byte
)

@Entity(tableName = "patient_info", indices = [Index(value = ["record_id"], unique = true)])
data class PatientInfo(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @PrimaryKey @ColumnInfo(name = "record_no") val recordNo: String,
    @ColumnInfo(name = "account_id") val accountId: String,
    @ColumnInfo(name = "affected_side") val affectedSide: Byte,
    val name: String,
    val age: Int,
    val gender: Byte,
    @ColumnInfo(name = "pro_gear_type") val proGearType: Byte,
    @ColumnInfo(name = "walk_type") val walkType: Byte,
    @ColumnInfo(name = "create_time") val createTime: Long,
    @ColumnInfo(name = "device_id") val deviceId: String,   //SN
    val cause: String
)

@Entity(tableName = "file_brief_info", indices = [Index(value = ["detail_id"], unique = true)])
data class FileBriefInfo(
    @PrimaryKey(autoGenerate = true) val id: Int,
//    @PrimaryKey @ColumnInfo(name = "file_id") val fileId: String,
    @ColumnInfo(name = "detail_id") val detailId: Int,
    @ColumnInfo(name = "record_no") val recordNo: String,
    @ColumnInfo(name = "account_id") val accountId: String,
    @ColumnInfo(name = "file_name") val fileName: String,
    @ColumnInfo(name = "create_time") val createTime: Long
)

@Entity(tableName = "file_detail_info", indices = [Index(value = ["brief_id"], unique = true)])
data class FileDetailInfo(
//    @PrimaryKey(autoGenerate = true) val id: Int,
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "brief_id") val briefId: Int,
    @ColumnInfo(name = "test_time") val testTime: Long, //millis
    @ColumnInfo(name = "test_time_len") val testTimeLen: Int,   //min
    @ColumnInfo(name = "test_distance") val testDistance: Int, //m
    @ColumnInfo(name = "step_count") val stepCount: Int,
    val speed: Float,   //m/s
    @ColumnInfo(name = "left_step_len") val leftStepLen: Int,   //mm
    @ColumnInfo(name = "right_step_len") val rightStepLen: Int, //mm
    @ColumnInfo(name = "step_stride") val stepStride: Int, //mm
    @ColumnInfo(name = "step_cadence") val stepCadence: Int,    //step/s
    @ColumnInfo(name = "left_swing_time") val leftSwingTime: Int,   //s
    @ColumnInfo(name = "right_swing_time") val rightSwingTime: Int, //s
    @ColumnInfo(name = "left_brace_time") val leftBraceTime: Int,   //s
    @ColumnInfo(name = "right_brace_time") val rightBraceTime: Int, //s
    @ColumnInfo(name = "left_brace_swing_ratio") val leftBraceSwingRatio: Float,    //%
    @ColumnInfo(name = "right_brace_swing_ratio") val rightBraceSwingRatio: Float,  //%
    @ColumnInfo(name = "double_brace_time") val doubleBraceTime: Int,   //s
    @ColumnInfo(name = "gait_cycle") val gaitCycle: Int,    //s
    @ColumnInfo(name = "lr_brace_time_ratio") val lrBraceTimeRatio: Float,  //%
    @ColumnInfo(name = "lr_swing_time_ratio") val lrSwingTimeRatio: Float,  //%
    @ColumnInfo(name = "symmetry_test_time") val symmetryTestTime: Int, //min
    @ColumnInfo(name = "symmetry_average_speed") val symmetryAverageSpeed: Float  //m/s
)