package com.lewiswilson.kiminojisho;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DatabaseHelper extends SQLiteOpenHelper {
    //COL0 = "ID";
    private static final String COL1 = "WORD";
    private static final String COL2 = "KANA";
    private static final String COL3 = "MEANING";
    private static final String COL4 = "EXAMPLE";
    private static final String COL5 = "NOTES";
    private static final String DATABASE_NAME = "kiminojisho.db";
    private static final String TABLE_NAME = "jisho_data";
    private static final String TAG = "DatabaseHelper";
    private final Context myContext;
    private SQLiteDatabase db;

    private final String DATABASE_PATH;

    DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 4);
        this.myContext = context;
        this.DATABASE_PATH = "/data/data/com.lewiswilson.kiminojisho/databases/";
        Log.e("Database Path:", DATABASE_PATH);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE jisho_data (ID INTEGER PRIMARY KEY AUTOINCREMENT, WORD TEXT, KANA TEXT, MEANING TEXT, EXAMPLE TEXT, UNIQUE(WORD))");
    }


	//Changes made for Importing
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if(newVersion > oldVersion){
			try{
				//copyDatabase();
                db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL5 + " TEXT");
			} catch (Exception e) {
                e.printStackTrace();
            }
		}
	}

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.setVersion(oldVersion);
    }

    void createDatabase() {
            this.getReadableDatabase();
            try {
                copyDatabase();
            } catch (IOException e) {
                throw new Error("Error Importing Database");
            }
    }
	
	//Import DB from Assets folder
    private void copyDatabase() throws IOException {
		InputStream myInput = myContext.getContentResolver().openInputStream(MainActivity.fileUri);//myContext.getAssets().open("kiminojisho.db");
		String outFileName = DATABASE_PATH + DATABASE_NAME;
		OutputStream myOutput = new FileOutputStream(outFileName);
		byte[] buffer = new byte[10];
		int length;
        assert myInput != null;
        while ((length = myInput.read(buffer)) > 0) {
			myOutput.write(buffer, 0 , length);
		}
		myOutput.flush();
		myOutput.close();
		myInput.close();
	}

	@Override
	public synchronized void close() {
		SQLiteDatabase db = getWritableDatabase();
		if(db != null)
			db.close();
		super.close();
	}

    void updateData(String list_selection, String new_kana, String new_meaning, String new_example, String new_notes) {
        db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2, new_kana);
        contentValues.put(COL3, new_meaning);
        contentValues.put(COL4, new_example);
        contentValues.put(COL5, new_notes);
        String sb = "WORD='" +
                list_selection +
                "'";
        db.update(TABLE_NAME, contentValues, sb, null);
    }

    void deleteData(String list_selection) {
        //PreparedStatement (Avoiding SQL Injection & Crash)
        try {
            db = getWritableDatabase();
            db.beginTransaction();
            String sql = "DELETE FROM jisho_data WHERE WORD = ?";
            SQLiteStatement statement = db.compileStatement(sql);
            statement.bindString(1, list_selection);
            statement.executeUpdateDelete();
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.w("Exception:", e);
        } finally {
            db.endTransaction();
        }
    }

    String readData(String list_selection) {
        db = getReadableDatabase();
        //PreparedStatement (Avoiding SQL Injection)
        Cursor cursor = db.rawQuery("SELECT * FROM jisho_data WHERE WORD=?", new String[]{list_selection});
        String getWord = "";
        String getKana = "";
        String getMeaning = "";
        String getExample = "";
        String getNotes = "";
        if (cursor.moveToFirst()) {
            getWord = cursor.getString(cursor.getColumnIndex(COL1));
            getKana = cursor.getString(cursor.getColumnIndex(COL2));
            getMeaning = cursor.getString(cursor.getColumnIndex(COL3));
            getExample = cursor.getString(cursor.getColumnIndex(COL4));
            getNotes = cursor.getString(cursor.getColumnIndex(COL5));
        }
        cursor.close();
        String str2 = ";";
        return getWord +
                str2 +
                getKana +
                str2 +
                getMeaning +
                str2 +
                getExample +
                str2 +
                getNotes;
    }

    public boolean addData(String word, String kana, String meaning, String example, String notes) {
        db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL1, word);
        contentValues.put(COL2, kana);
        contentValues.put(COL3, meaning);
        contentValues.put(COL4, example);
        contentValues.put(COL5, notes);
        String str = TABLE_NAME;
        String sb = "addData: Adding " +
                word +
                " to " +
                str;
        Log.d(TAG, sb);
        return db.insert(str, null, contentValues) != -1;
    }

    Cursor getListContents() {
        return getWritableDatabase().rawQuery("SELECT * FROM jisho_data ORDER BY MEANING COLLATE NOCASE ASC", null);
    }

    String random(int flag) {
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

            //check case where WORD == KANA (in case of 'no kanji' mode)
            if(!cursor.getString(cursor.getColumnIndex(str)).equals(cursor.getString(cursor.getColumnIndex(COL2)))) {
                sb.append(cursor.getString(cursor.getColumnIndex(COL2)));
                sb.append(str2);
            }

            sb.append(cursor.getString(cursor.getColumnIndex(COL3)));
            rand_word = sb.toString();
        }
        cursor.close();
        return rand_word;
    }
}