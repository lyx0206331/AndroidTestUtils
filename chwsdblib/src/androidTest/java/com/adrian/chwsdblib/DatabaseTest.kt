package com.adrian.chwsdblib

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.adrian.chwsdblib.dao.CHWSDao
import com.adrian.chwsdblib.database.CHWSDatabase
import com.adrian.chwsdblib.entity.AccountInfo
import com.adrian.chwsdblib.entity.AnalysisResultInfo
import com.adrian.chwsdblib.entity.FileBriefInfo
import com.adrian.chwsdblib.entity.PatientInfo
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

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
 * date:2021/2/25 0025 15:03
 * description:
 */
@RunWith(AndroidJUnit4::class)
class DatabaseTest {
    private lateinit var chwsDao: CHWSDao
    private lateinit var db: CHWSDatabase

    @Before
    fun createDb() {
        var context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, CHWSDatabase::class.java).build()
        chwsDao = db.chwsDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun writeAccountsAndRead() {
        insertAccounts()
        var byId = chwsDao.queryAccountById("account1")
        Assert.assertEquals(byId.hospitalNo, "hospital1")
    }

    private fun insertAccounts() {
        val account1 = AccountInfo(
            accountId = "account0",
            hospitalNo = "hospital0",
            departmentNo = "department1",
            type = 0.toByte()
        )
        val account2 = AccountInfo(
            accountId = "account1",
            hospitalNo = "hospital0",
            departmentNo = "department2",
            type = 1.toByte()
        )
        val account3 = AccountInfo(
            accountId = "account2",
            hospitalNo = "hospital2",
            departmentNo = "department1",
            type = 2.toByte()
        )
        chwsDao.insertAccount(account1)
        chwsDao.insertAccount(account2)
        chwsDao.insertAccount(account3)
    }

    @Test
    @Throws(Exception::class)
    fun writePatientsAndRead() {
        insertPatients()
        chwsDao.queryPatientsByAccountId("account1").forEach {
            it.accountId = "account2"
            chwsDao.updatePatient(it)
        }
//        var patient = chwsDao.queryPatientByRecordNum("recordNum55")
//        Assert.assertEquals(patient.cause, "cause6")
        chwsDao.queryPatientsByAccountId("account2").apply { chwsDao.deletePatients(this) }
        val patients = chwsDao.queryPatientsAll()
        Assert.assertEquals(patients.size, 34)
    }

    private fun insertPatients() {
        for (i in 0..99) {
            chwsDao.insertPatient(
                PatientInfo(
                    recordNum = "recordNum$i",
                    accountId = "account${i % 3}",
                    affectedSide = 0.toByte(),
                    age = i,
                    name = "name${i % 5}",
                    gender = (i % 2).toByte(),
                    proGearType = 0.toByte(),
                    walkType = (i % 5).toByte(),
                    createTime = System.currentTimeMillis() + i,
                    deviceId = "device${i % 3}",
                    cause = "cause${i % 7}"
                )
            )
        }
    }

    @Test
    @Throws(Exception::class)
    fun writeBriefsAndRead() {
        for (i in 0..100) {
            chwsDao.insertBrief(
                FileBriefInfo(
                    recordNum = "recordNum${i % 4}",
                    accountId = "account${i % 3}",
                    fileName = "file$i",
                    createTime = System.currentTimeMillis() + i
                )
            )
        }
//        val briefs = chwsDao.queryBriefsByRecordNum("recordNum3")
//        Assert.assertEquals(briefs.size, 25)

        val briefs = chwsDao.queryBriefsByAccountId("account1")
        Assert.assertEquals(briefs.size, 34)
    }

    @Test
    @Throws(Exception::class)
    fun writeBriefAndAnalysisResultAndRead() {
        insertBriefs()
        insertAnalysisResults()

        val analysisResultInfo = chwsDao.queryAnalysisResultById(100)
        Assert.assertEquals(analysisResultInfo.speed, 101.4f)
    }

    @Test
    @Throws(Exception::class)
    fun testRelationData() {
        insertAccounts()
        insertPatients()
        insertBriefs()
        insertAnalysisResults()

//        val briefAndAnalysis = chwsDao.queryBriefAndAnalysisResult(2)
//        Assert.assertEquals(briefAndAnalysis.result.speed, 3.4f)

//        val accountWithPatients = chwsDao.queryAccountWithPatients("account1")
//        Assert.assertEquals(accountWithPatients.patients.size, 33)

        val patientWithBriefs = chwsDao.queryPatientWithBriefs("recordNum2")
        Assert.assertEquals(patientWithBriefs.briefs.size, 25)
    }

    private fun insertAnalysisResults() {
        chwsDao.queryBriefsAll().forEachIndexed { i, brief ->
            chwsDao.insertAnalysisResult(
                AnalysisResultInfo(
                    briefId = brief.id,
                    testTime = brief.createTime,
                    testTimeLen = i,
                    testDistance = 100,
                    stepCount = 200,
                    speed = i + 2.4f
                )
            )
        }
    }

    private fun insertBriefs() {
        for (i in 0..99) {
            chwsDao.insertBrief(
                FileBriefInfo(
                    recordNum = "recordNum${i % 4}",
                    accountId = "account${i % 2}",
                    fileName = "file$i",
                    createTime = System.currentTimeMillis() + i
                )
            )
        }
    }
}