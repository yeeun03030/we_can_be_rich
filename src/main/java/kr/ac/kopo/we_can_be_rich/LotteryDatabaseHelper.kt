package kr.ac.kopo.we_can_be_rich

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class LotteryDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }

    companion object {
        const val DATABASE_VERSION = 2 // 버전 변경
        const val DATABASE_NAME = "Lottery.db"

        private const val SQL_CREATE_ENTRIES =
            "CREATE TABLE LotteryNumbers (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "round TEXT," +  // round 열 추가
                    "numbers TEXT)"

        private const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS LotteryNumbers"
    }
}
