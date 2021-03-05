package com.adrian.chwsdblib.dao

import androidx.room.*
import com.adrian.chwsdblib.entity.AccountInfo
import com.adrian.chwsdblib.entity.AnalysisResultInfo
import com.adrian.chwsdblib.entity.FileBriefInfo
import com.adrian.chwsdblib.entity.PatientInfo
import com.adrian.chwsdblib.relation.AccountWithPatients
import com.adrian.chwsdblib.relation.BriefAndAnalysis
import com.adrian.chwsdblib.relation.PatientWithBriefs

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
    /**
     * 插入账户
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertAccount(accounts: AccountInfo): Long

    @Delete
    fun deleteAccounts(accounts: List<AccountInfo>): Int

    @Update
    fun updateAccount(account: AccountInfo): Int

    @Query("SELECT * FROM account_info WHERE account_id = :id")
    fun queryAccountById(id: String): AccountInfo

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertPatient(patient: PatientInfo): Long

    @Delete
    fun deletePatients(patients: List<PatientInfo>): Int

    @Update
    fun updatePatient(patient: PatientInfo): Int

    @Query("SELECT * FROM patient_info")
    fun queryPatientsAll(): List<PatientInfo>

    @Query("SELECT * FROM patient_info WHERE name = :name")
    fun queryPatientsByName(name: String): List<PatientInfo>

    @Query("SELECT * FROM patient_info WHERE account_id = :accountId")
    fun queryPatientsByAccountId(accountId: String): List<PatientInfo>

    @Query("SELECT * FROM patient_info WHERE record_num = :recordNum")
    fun queryPatientByRecordNum(recordNum: String): PatientInfo

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertBrief(brief: FileBriefInfo): Long

    @Delete
    fun deleteBriefs(vararg briefs: FileBriefInfo): Int

    @Update
    fun updateBrief(brief: FileBriefInfo): Int

    @Query("SELECT * FROM file_brief_info")
    fun queryBriefsAll(): List<FileBriefInfo>

    @Query("SELECT * FROM file_brief_info WHERE account_id = :accountId")
    fun queryBriefsByAccountId(accountId: String): List<FileBriefInfo>

    @Query("SELECT * FROM file_brief_info WHERE record_num = :recordNum ORDER BY create_time DESC")
    fun queryBriefsByRecordNum(recordNum: String): List<FileBriefInfo>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertAnalysisResult(analysisResultInfo: AnalysisResultInfo)

    @Delete
    fun deleteAnalysisResults(vararg results: AnalysisResultInfo): Int

    @Update
    fun updateAnalysisResult(result: AnalysisResultInfo): Int

    @Query("SELECT * FROM analysis_result_info")
    fun queryAnalysisResultsAll(): List<AnalysisResultInfo>

    @Query("SELECT * FROM analysis_result_info WHERE brief_id = :briefId")
    fun queryAnalysisResultById(briefId: Int): AnalysisResultInfo

    @Transaction
    @Query("SELECT * FROM file_brief_info WHERE id = :briefId")
    fun queryBriefAndAnalysisResult(briefId: Int): BriefAndAnalysis

    @Transaction
    @Query("SELECT * FROM account_info WHERE account_id = :accountId")
    fun queryAccountWithPatients(accountId: String): AccountWithPatients

    @Transaction
    @Query("SELECT * FROM patient_info WHERE record_num = :recordNum")
    fun queryPatientWithBriefs(recordNum: String): PatientWithBriefs
}