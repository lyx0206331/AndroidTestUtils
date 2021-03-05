package com.adrian.chwsdblib.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.adrian.chwsdblib.entity.AccountInfo
import com.adrian.chwsdblib.entity.AnalysisResultInfo
import com.adrian.chwsdblib.entity.FileBriefInfo
import com.adrian.chwsdblib.entity.PatientInfo

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
 * date:2021/2/24 0024 15:49
 * description:
 */
data class BriefAndAnalysis(
    @Embedded var brief: FileBriefInfo,
    @Relation(parentColumn = "id", entityColumn = "brief_id")
    var result: AnalysisResultInfo
)

data class AccountWithPatients(
    @Embedded var account: AccountInfo,
    @Relation(parentColumn = "account_id", entityColumn = "account_id")
    var patients: List<PatientInfo>
)

data class PatientWithBriefs(
    @Embedded var patient: PatientInfo,
    @Relation(parentColumn = "record_num", entityColumn = "record_num")
    var briefs: List<FileBriefInfo>
)