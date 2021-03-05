package com.adrian.chwsdblib.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.adrian.chwsdblib.dao.CHWSDao
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
 * date:2021/2/24 0024 18:08
 * description:
 */
@Database(
    entities = [AccountInfo::class, PatientInfo::class, FileBriefInfo::class, AnalysisResultInfo::class],
    version = 1
)
abstract class CHWSDatabase : RoomDatabase() {
    abstract fun chwsDao(): CHWSDao

    companion object {
        @Volatile
        var instance: CHWSDatabase? = null

        fun getInstance(context: Context): CHWSDatabase = instance ?: synchronized(this) {
            instance ?: buildDatabase(context).also {
                instance = it
            }
        }

        private fun buildDatabase(context: Context): CHWSDatabase =
            Room.databaseBuilder(context, CHWSDatabase::class.java, "chws-test.db")
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                    }
                })
//            .createFromAsset("database/init.db")
//            .createFromFile(File("filePath"))
//            .addMigrations(MIGRATION_1_2)
                .fallbackToDestructiveMigration()
                .build()
    }
}

var MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        TODO("Not yet implemented")
    }
}