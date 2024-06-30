package kr.ac.kopo.we_can_be_rich

import android.content.ContentValues
import android.content.Context

class LotteryDao(context: Context) {
    private val dbHelper = LotteryDatabaseHelper(context)

    fun insertNumbers(round: String, numbers: List<Int>) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("round", round)
            put("numbers", numbers.joinToString(","))
        }
        db.insert("LotteryNumbers", null, values)
    }

    fun getAllNumbers(): List<List<Int>> {
        val numbersList = mutableListOf<List<Int>>()
        val db = dbHelper.readableDatabase
        val cursor = db.query("LotteryNumbers", null, null, null, null, null, null)
        while (cursor.moveToNext()) {
            val numbers = cursor.getString(cursor.getColumnIndexOrThrow("numbers"))
                .split(",")
                .map { it.trim().toInt() }
            numbersList.add(numbers)
        }
        cursor.close()
        return numbersList
    }

    fun deleteNumbers(numbers: List<Int>) {
        val db = dbHelper.writableDatabase
        val selection = "numbers = ?"
        val selectionArgs = arrayOf(numbers.joinToString(","))
        db.delete("LotteryNumbers", selection, selectionArgs)
    }
}
