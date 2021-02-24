package com.adrian.chwsdblib.dao

import androidx.room.*
import com.adrian.chwsdblib.entity.AccountInfo
import com.adrian.chwsdblib.entity.PatientInfo
import com.adrian.chwsdblib.relation.BriefAndDetail
import kotlinx.coroutines.flow.Flow

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
 * date:2021/2/23 0023 16:06
 * description:
 */
@Dao
interface CHWSDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAccount(vararg accounts: AccountInfo): Flow<List<Long>>

    @Delete
    suspend fun deleteAccounts(vararg accounts: AccountInfo): Int

    @Update
    suspend fun updateAccounts(vararg accounts: AccountInfo): Int

    @Query("SELECT * FROM account_info WHERE account_id IN (:ids)")
    suspend fun queryAccountsByIds(vararg ids: String): Flow<List<AccountInfo>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertPatients(vararg patients: PatientInfo): List<Long>

    @Delete
    suspend fun deletePatients(vararg patients: PatientInfo): Int

    @Update
    suspend fun updatePatients(vararg patients: PatientInfo): Int

    @Query("SELECT * FROM patient_info WHERE record_no IN (:recordNums)")
    suspend fun queryPatientsByRecordNums(vararg recordNums: String): Flow<List<PatientInfo>>

    @Query("SELECT * FROM patient_info WHERE name IN (:recordNames)")
    suspend fun queryPatientsByNames(vararg recordNames: String): Flow<List<PatientInfo>>

    @Transaction
    @Query("SELECT * FROM file_brief_info WHERE record_no = :recordNum")
    suspend fun queryBriefsAndDetails(recordNum: String): Flow<List<BriefAndDetail>>
}