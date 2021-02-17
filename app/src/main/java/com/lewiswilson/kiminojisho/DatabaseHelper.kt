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

class DatabaseHelper internal constructor(private val myContext: Context) : SQLiteOpenHelper(myContext, DATABASE_NAME, null, 5) {

    private var db: SQLiteDatabase? = null

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE jisho_data (ID INTEGER PRIMARY KEY AUTOINCREMENT, WORD TEXT, KANA TEXT, MEANING TEXT, EXAMPLE TEXT, UNIQUE(WORD))")
    }

    //Changes made for Importing
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (newVersion > oldVersion) {
            //try{
            //	copyDatabase();
            //} catch (IOException e) {
            //	e.printStackTrace();
            //}
            db.execSQL("ALTER TABLE $TABLE_NAME ADD COLUMN $COL5 TEXT")
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
        val myInput = MainActivity.fileUri?.let { myContext.contentResolver.openInputStream(it) } //myContext.getAssets().open("kiminojisho.db");
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

    fun updateData(list_selection: String, new_kana: String?, new_meaning: String?, new_example: String?, new_notes: String?) {
        db = writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COL2, new_kana)
        contentValues.put(COL3, new_meaning)
        contentValues.put(COL4, new_example)
        contentValues.put(COL5, new_notes)
        val sb = "WORD='$list_selection'"
        db?.update(TABLE_NAME, contentValues, sb, null)
    }

    fun deleteData(list_selection: String?) {
        //PreparedStatement (Avoiding SQL Injection & Crash)
        try {
            db = writableDatabase
            db?.beginTransaction()
            val sql = "DELETE FROM jisho_data WHERE WORD = ?"
            val statement = db?.compileStatement(sql)
            statement?.bindString(1, list_selection)
            statement?.executeUpdateDelete()
            db?.setTransactionSuccessful()
        } catch (e: SQLException) {
            Log.w("Exception:", e)
        } finally {
            db?.endTransaction()
        }
    }

    fun readData(list_selection: String): String {
        db = readableDatabase
        //PreparedStatement (Avoiding SQL Injection)
        val cursor = db?.rawQuery("SELECT * FROM jisho_data WHERE WORD=?", arrayOf(list_selection))
        var getWord = ""
        var getKana = ""
        var getMeaning = ""
        var getExample = ""
        var getNotes = ""
        if (cursor?.moveToFirst() == true) {
            getWord = cursor.getString(cursor.getColumnIndex(COL1))
            getKana = cursor.getString(cursor.getColumnIndex(COL2))
            getMeaning = cursor.getString(cursor.getColumnIndex(COL3))
            getExample = cursor.getString(cursor.getColumnIndex(COL4))
            getNotes = cursor.getString(cursor.getColumnIndex(COL5))
        }
        cursor?.close()
        val str2 = ";"
        return getWord +
                str2 +
                getKana +
                str2 +
                getMeaning +
                str2 +
                getExample +
                str2 +
                getNotes
    }

    fun addData(word: String, kana: String?, meaning: String?, example: String?, notes: String?): Boolean {
        db = writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COL1, word)
        contentValues.put(COL2, kana)
        contentValues.put(COL3, meaning)
        contentValues.put(COL4, example)
        contentValues.put(COL5, notes)
        val str = TABLE_NAME
        val sb = "addData: Adding " +
                word +
                " to " +
                str
        Log.d(TAG, sb)
        return db?.insert(str, null, contentValues) != -1L
    }

    val listContents: Cursor
        get() = writableDatabase.rawQuery("SELECT * FROM jisho_data ORDER BY MEANING COLLATE NOCASE ASC", null)

    fun random(flag: Int): String {
        var rand_word = ""
        val cursor = readableDatabase.rawQuery("SELECT WORD, KANA, MEANING FROM jisho_data WHERE ID IN (SELECT ID FROM jisho_data ORDER BY RANDOM() LIMIT 1)", null)
        val str = COL1
        if (flag == 0) {
            if (cursor.moveToFirst()) {
                rand_word = cursor.getString(cursor.getColumnIndex(str))
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
            rand_word = sb.toString()
        }
        cursor.close()
        return rand_word
    }

    companion object {
        private const val COL1 = "WORD"
        private const val COL2 = "KANA"
        private const val COL3 = "MEANING"
        private const val COL4 = "EXAMPLE"
        private const val COL5 = "NOTES"
        private const val DATABASE_NAME = "kiminojisho.db"
        private val DATABASE_PATH = MyApplication.appContext?.getDatabasePath(DATABASE_NAME)//"/data/data/com.lewiswilson.kiminojisho/databases/"
        private const val TABLE_NAME = "jisho_data"
        private const val TAG = "DatabaseHelper"
    }

    init {
        Log.e("Database Path:", DATABASE_PATH.toString())
    }
}