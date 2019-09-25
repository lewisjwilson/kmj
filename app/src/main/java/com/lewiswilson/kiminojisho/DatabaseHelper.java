package com.lewiswilson.kiminojisho;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String COL0 = "ID";
    private static final String COL1 = "WORD";
    private static final String COL2 = "KANA";
    private static final String COL3 = "MEANING";
    private static final String COL4 = "EXAMPLE";
    private static final String DATABASE_NAME = "kiminojisho.db";
    private static final String TABLE_NAME = "jisho_data";
    private static final String TAG = "DatabaseHelper";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 3);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE jisho_data (ID INTEGER PRIMARY KEY AUTOINCREMENT, WORD TEXT, KANA TEXT, MEANING TEXT, EXAMPLE TEXT, UNIQUE(WORD))");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS jisho_data");
    }

    public void updateData(String list_selection, String new_kana, String new_meaning, String new_example) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2, new_kana);
        contentValues.put(COL3, new_meaning);
        contentValues.put(COL4, new_example);
        StringBuilder sb = new StringBuilder();
        sb.append("WORD='");
        sb.append(list_selection);
        sb.append("'");
        db.update(TABLE_NAME, contentValues, sb.toString(), null);
    }

    public void deleteData(String list_selection) {
        SQLiteDatabase db = getWritableDatabase();
        StringBuilder sb = new StringBuilder();
        sb.append("WORD='");
        sb.append(list_selection);
        sb.append("'");
        db.delete(TABLE_NAME, sb.toString(), null);
    }

    public String readData(String list_selection) {
        SQLiteDatabase db = getReadableDatabase();
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM jisho_data WHERE WORD='");
        sb.append(list_selection);
        sb.append("'");
        Cursor cursor = db.rawQuery(sb.toString(), null);
        String getWord = "";
        String getKana = "";
        String getMeaning = "";
        String getExample = "";
        if (cursor.moveToFirst()) {
            getWord = cursor.getString(cursor.getColumnIndex(COL1));
            getKana = cursor.getString(cursor.getColumnIndex(COL2));
            getMeaning = cursor.getString(cursor.getColumnIndex(COL3));
            getExample = cursor.getString(cursor.getColumnIndex(COL4));
        }
        cursor.close();
        String str = getWord;
        StringBuilder sb2 = new StringBuilder();
        sb2.append(getWord);
        String str2 = ";";
        sb2.append(str2);
        sb2.append(getKana);
        sb2.append(str2);
        sb2.append(getMeaning);
        sb2.append(str2);
        sb2.append(getExample);
        return sb2.toString();
    }

    public boolean addData(String word, String kana, String meaning, String example) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL1, word);
        contentValues.put(COL2, kana);
        contentValues.put(COL3, meaning);
        contentValues.put(COL4, example);
        StringBuilder sb = new StringBuilder();
        sb.append("addData: Adding ");
        sb.append(word);
        sb.append(" to ");
        String str = TABLE_NAME;
        sb.append(str);
        Log.d(TAG, sb.toString());
        if (db.insert(str, null, contentValues) == -1) {
            return false;
        }
        return true;
    }

    public Cursor getListContents() {
        return getWritableDatabase().rawQuery("SELECT * FROM jisho_data ORDER BY MEANING ASC", null);
    }

    public String random(int flag) {
        String rand_word = "";
        Cursor cursor = getReadableDatabase().rawQuery("SELECT WORD, KANA, MEANING FROM jisho_data WHERE ID IN (SELECT ID FROM jisho_data ORDER BY RANDOM() LIMIT 1)", null);
        String str = COL1;
        if (flag == 0) {
            if (cursor.moveToFirst()) {
                rand_word = cursor.getString(cursor.getColumnIndex(str));
            }
        } else if (cursor.moveToFirst()) {
            StringBuilder sb = new StringBuilder();
            sb.append(cursor.getString(cursor.getColumnIndex(str)));
            String str2 = " ; ";
            sb.append(str2);
            sb.append(cursor.getString(cursor.getColumnIndex(COL2)));
            sb.append(str2);
            sb.append(cursor.getString(cursor.getColumnIndex(COL3)));
            rand_word = sb.toString();
        }
        cursor.close();
        return rand_word;
    }
}