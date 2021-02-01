package com.adrian.chwsdblib.base

import androidx.room.ColumnInfo
import androidx.room.Entity
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
@Entity(tableName = "user_info")
class UserInfoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "record_no") val recordNo: String,
    @ColumnInfo(name = "owner_id") val ownerId: String,
    @ColumnInfo(name = "affected_side") val affectedSide: Byte,
    val name: String,
    val age: Int,
    val gender: Byte,
    @ColumnInfo(name = "pro_gear_type") val proGearType: Byte,
    @ColumnInfo(name = "walk_type") val walkType: Byte,
    @ColumnInfo(name = "create_time") val createTime: Long,
    @ColumnInfo(name = "device_id") val deviceId: String,
    val cause: String
)