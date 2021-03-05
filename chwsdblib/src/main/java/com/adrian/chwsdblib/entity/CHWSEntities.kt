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
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    @ColumnInfo(name = "account_id") var accountId: String,
    @ColumnInfo(name = "hospital_no") var hospitalNo: String,
    @ColumnInfo(name = "department_no") var departmentNo: String,
    var type: Byte
)

@Entity(tableName = "patient_info", indices = [Index(value = ["record_num"], unique = true)])
data class PatientInfo(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    @ColumnInfo(name = "record_num") var recordNum: String,
    @ColumnInfo(name = "account_id") var accountId: String,
    @ColumnInfo(name = "affected_side") var affectedSide: Byte,
    var name: String,
    @ColumnInfo(defaultValue = "0") var age: Int,
    @ColumnInfo(defaultValue = "0") var gender: Byte,
    @ColumnInfo(name = "pro_gear_type") var proGearType: Byte,
    @ColumnInfo(name = "walk_type") var walkType: Byte,
    @ColumnInfo(name = "create_time") var createTime: Long,
    @ColumnInfo(name = "device_id") var deviceId: String,   //SN
    var cause: String
)

@Entity(tableName = "file_brief_info", indices = [Index(value = ["file_name"], unique = true)])
data class FileBriefInfo(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
//    @PrimaryKey @ColumnInfo(name = "file_id") var fileId: String,
//    @ColumnInfo(name = "detail_id") var detailId: Int,
    @ColumnInfo(name = "record_num") var recordNum: String,
    @ColumnInfo(name = "account_id") var accountId: String,
    @ColumnInfo(name = "file_name") var fileName: String,
    @ColumnInfo(name = "create_time", defaultValue = "0") var createTime: Long
)

@Entity(tableName = "analysis_result_info", indices = [Index(value = ["brief_id"], unique = true)])
data class AnalysisResultInfo(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    @ColumnInfo(name = "brief_id") var briefId: Int = 0,
    @ColumnInfo(name = "test_time") var testTime: Long = 0, //millis
    @ColumnInfo(name = "test_time_len") var testTimeLen: Int = 0,   //min
    @ColumnInfo(name = "test_distance") var testDistance: Int = 0, //m
    @ColumnInfo(name = "step_count") var stepCount: Int = 0,
    var speed: Float = 0f,   //m/s
    @ColumnInfo(name = "left_step_len") var leftStepLen: Int = 0,   //mm
    @ColumnInfo(name = "right_step_len") var rightStepLen: Int = 0, //mm
    @ColumnInfo(name = "step_stride") var stepStride: Int = 0, //mm
    @ColumnInfo(name = "step_cadence") var stepCadence: Int = 0,    //step/s
    @ColumnInfo(name = "left_swing_time") var leftSwingTime: Int = 0,   //s
    @ColumnInfo(name = "right_swing_time") var rightSwingTime: Int = 0, //s
    @ColumnInfo(name = "left_brace_time") var leftBraceTime: Int = 0,   //s
    @ColumnInfo(name = "right_brace_time") var rightBraceTime: Int = 0, //s
    @ColumnInfo(name = "left_brace_swing_ratio") var leftBraceSwingRatio: Float = 0f,    //%
    @ColumnInfo(name = "right_brace_swing_ratio") var rightBraceSwingRatio: Float = 0f,  //%
    @ColumnInfo(name = "double_brace_time") var doubleBraceTime: Int = 0,   //s
    @ColumnInfo(name = "gait_cycle") var gaitCycle: Int = 0,    //s
    @ColumnInfo(name = "lr_brace_time_ratio") var lrBraceTimeRatio: Float = 0f,  //%
    @ColumnInfo(name = "lr_swing_time_ratio") var lrSwingTimeRatio: Float = 0f,  //%
    @ColumnInfo(name = "symmetry_test_time") var symmetryTestTime: Int = 0, //min
    @ColumnInfo(name = "symmetry_average_speed") var symmetryAverageSpeed: Float = 0f  //m/s
)