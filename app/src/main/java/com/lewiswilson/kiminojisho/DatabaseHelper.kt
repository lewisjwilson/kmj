package com.lewiswilson.kiminojisho

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.lewiswilson.MyApplication
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class DatabaseHelper internal constructor(private val myContext: Context) : SQLiteOpenHelper(myContext, DATABASE_NAME, null, 9) {

    private var db: SQLiteDatabase? = null

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE IF NOT EXISTS jisho_data ($COL0 INTEGER PRIMARY KEY AUTOINCREMENT, " + //id
                "$COL1 TEXT, " + //word
                "$COL2 TEXT, " + //kana
                "$COL3 TEXT, " + //meaning
                "$COL4 TEXT, " + //example
                "$COL5 TEXT, " + //notes
                "$COL6 INTEGER, " + //flashcard_box
                "$COL7 TEXT, " + //date_reviewed
                "$COL8 TEXT, " + //next_review
                "$COL9 INTEGER, " + //times_seen
                "$COL10 INTEGER, " + //times_correct
                "UNIQUE($COL1))")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        Log.d(TAG, "DATABASE UPGRADE FROM ${oldVersion} to ${newVersion}")
        if (oldVersion < 8) {
            //unused table
            db.execSQL("DROP TABLE IF EXISTS examples")
            //add new columns (flashcard_box, date_reviewed, next_review, times_seen, times_correct)
            db.execSQL("ALTER TABLE $TABLE1_NAME ADD $COL6 INTEGER DEFAULT \"0\" NOT NULL")
            db.execSQL("ALTER TABLE $TABLE1_NAME ADD $COL7 TEXT")
            db.execSQL("UPDATE $TABLE1_NAME SET $COL7 = CURRENT_TIMESTAMP")
            db.execSQL("ALTER TABLE $TABLE1_NAME ADD $COL8 TEXT")
            db.execSQL("UPDATE $TABLE1_NAME SET $COL8 = CURRENT_TIMESTAMP")
            db.execSQL("ALTER TABLE $TABLE1_NAME ADD $COL9 INTEGER DEFAULT \"0\" NOT NULL")
            db.execSQL("ALTER TABLE $TABLE1_NAME ADD $COL10 INTEGER DEFAULT \"0\" NOT NULL")
        }
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.version = oldVersion
    }

    fun createDatabase() {
        this.readableDatabase
        try {
            copyDatabase()
        } catch (e: IOException) {
            throw Error("Error Importing Database")
        }
    }

    //Import DB from Assets folder
    @Throws(IOException::class)
    private fun copyDatabase() {
        val myInput =
            MyList.fileUri?.let { myContext.contentResolver.openInputStream(it) } //myContext.getAssets().open("kiminojisho.db");
        val outFileName = DATABASE_PATH.toString() + DATABASE_NAME
        val myOutput: OutputStream = FileOutputStream(outFileName)
        val buffer = ByteArray(10)
        var length: Int
        if (BuildConfig.DEBUG && myInput == null) {
            error("Assertion failed")
        }
        while (myInput!!.read(buffer).also { length = it } > 0) {
            myOutput.write(buffer, 0, length)
        }
        myOutput.flush()
        myOutput.close()
        myInput.close()
    }

    @Synchronized
    override fun close() {
        val db = writableDatabase
        db?.close()
        super.close()
    }

    fun deleteData(itemId: String?) {
        //PreparedStatement (Avoiding SQL Injection & Crash)
        try {
            db = writableDatabase
            db?.beginTransaction()
            val sql = "DELETE FROM jisho_data WHERE " + COL0 + " = ?"
            val statement = db?.compileStatement(sql)
            statement?.bindString(1, itemId)
            statement?.executeUpdateDelete()
            db?.setTransactionSuccessful()
        } catch (e: SQLException) {
            Log.w("Exception:", e)
        } finally {
            db?.endTransaction()
        }
    }

    fun deleteFromRemote(kanji: String) {
        //PreparedStatement (Avoiding SQL Injection & Crash)
        try {
            db = writableDatabase
            db?.beginTransaction()
            val sql = "DELETE FROM jisho_data WHERE " + COL1 + " = ?"
            val statement = db?.compileStatement(sql)
            statement?.bindString(1, kanji)
            statement?.executeUpdateDelete()
            db?.setTransactionSuccessful()
        } catch (e: SQLException) {
            Log.w("Exception:", e)
        } finally {
            db?.endTransaction()
        }
    }

    fun getData(itemId: Int): HashMap<String, String>{
        db = readableDatabase

        val cur = db?.rawQuery("SELECT * FROM " + TABLE1_NAME +
        " WHERE " + COL0 + "=?", arrayOf(itemId.toString()))

        val hashMap = HashMap<String, String>()

        if (cur?.moveToFirst() == true) {
            hashMap.put("kanji", cur.getString(cur.getColumnIndex(COL1)))
            hashMap.put("kana", cur.getString(cur.getColumnIndex(COL2)))
            hashMap.put("english", cur.getString(cur.getColumnIndex(COL3)))
            hashMap.put("example", cur.getString(cur.getColumnIndex(COL4)))
            hashMap.put("notes", cur.getString(cur.getColumnIndex(COL5)))
        }
        cur?.close()
        return hashMap
    }

    fun checkStarred(kanji: String): Boolean {
        db = readableDatabase
        val cur = db?.rawQuery("SELECT EXISTS(SELECT 1 FROM " + TABLE1_NAME +
                " WHERE " + COL1 + "=?)", arrayOf(kanji))
        var bool = false
        if (cur?.moveToFirst() == true) {
            bool = cur.getInt(0) == 1
        }
        cur?.close()
        return bool
    }

    fun addData(word: String, kana: String?, meaning: String?, example: String?, notes: String?): Boolean {
        db = writableDatabase

        val df: DateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
        val date: Calendar = Calendar.getInstance()
        val dateNow: String = df.format(date.time)
        //increment day by 1
        date.add(Calendar.DAY_OF_MONTH, 1)
        val dateTmrw: String = df.format(date.time)

        Log.d(TAG, "addData: $date")

        val contentValues = ContentValues()
        contentValues.put(COL1, word)
        contentValues.put(COL2, kana)
        contentValues.put(COL3, meaning)
        contentValues.put(COL4, example)
        contentValues.put(COL5, notes)
        contentValues.put(COL6, 1) //flashcard_box
        contentValues.put(COL7, dateNow) //date_reviewed
        contentValues.put(COL8, dateTmrw) //next_review
        contentValues.put(COL9, 0) //times_seen
        contentValues.put(COL10, 0) //times_correct
        val str = TABLE1_NAME
        val sb = "addData: Adding " +
                word +
                " to " +
                str
        Log.d(TAG, sb)
        return db?.insert(str, null, contentValues) != -1L
    }

    fun listContents(column: String): Cursor {
        return writableDatabase.rawQuery("SELECT * FROM jisho_data " +
                "ORDER BY $column " +
                "COLLATE NOCASE ASC", null)
    }

    fun random(flag: Int): String {
        var randWord = ""
        val cursor = readableDatabase.rawQuery("SELECT WORD, KANA, MEANING FROM jisho_data " +
                "WHERE ID IN (SELECT ID FROM jisho_data ORDER BY RANDOM() LIMIT 1)", null)
        val str = COL1
        if (flag == 0) {
            if (cursor.moveToFirst()) {
                randWord = cursor.getString(cursor.getColumnIndex(str))
            }
        } else if (cursor.moveToFirst()) {
            val sb = StringBuilder()
            sb.append(cursor.getString(cursor.getColumnIndex(str)))
            sb.append(" ; ")

            //check case where WORD == KANA (in case of 'no kanji' mode)
            if (cursor.getString(cursor.getColumnIndex(str)) != cursor.getString(cursor.getColumnIndex(COL2))) {
                sb.append(cursor.getString(cursor.getColumnIndex(COL2)))
                sb.append(" ; ")
            }
            sb.append(cursor.getString(cursor.getColumnIndex(COL3)))
            randWord = sb.toString()
        }
        cursor.close()
        return randWord
    }

     companion object {
        private const val COL0 = "ID"
        private const val COL1 = "WORD"
        private const val COL2 = "KANA"
        private const val COL3 = "MEANING"
        private const val COL4 = "EXAMPLE"
        private const val COL5 = "NOTES"
        private const val COL6 = "flashcard_box"
        private const val COL7 = "date_reviewed"
        private const val COL8 = "next_review"
        private const val COL9 = "times_seen"
        private const val COL10 = "times_correct"
        private const val DATABASE_NAME = "kiminojisho.db"
        private val DATABASE_PATH = MyApplication.appContext?.getDatabasePath(DATABASE_NAME)//"/data/data/com.lewiswilson.kiminojisho/databases/"
        private const val TABLE1_NAME = "jisho_data"
        private const val TAG = "DatabaseHelper"
    }

    init {
        Log.e("Database Path:", DATABASE_PATH.toString())
    }
}