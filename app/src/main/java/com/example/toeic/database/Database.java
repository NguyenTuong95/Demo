
package com.example.toeic.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.example.toeic.vocabulary.EnglishWord;
import com.example.toeic.vocabulary.Practice;

import java.util.ArrayList;
import java.util.List;


public class Database extends SQLiteOpenHelper {

    public static final String EN_TABLE_NAME = "English";
    public static final String EN_COLUMN_ID = "id"; // primary language
    public static final String EN_COLUMN_WORD = "word";
    public static final String EN_COLUMN_TYPE= "type"; // foreign language
    public static final String EN_COLUMN_SCORE = "score"; // foreign language
    public static final String EN_COLUMN_SUBJECT = "subject"; // foreign language
    public static final String EN_COLUMN_CHECK = "word_check";
    public static final String EN_COLUMN_WRONG_NUMBER = "wrong_number";
    public static final String EN_COLUMN_NOTE = "note";
    public static final String EN_COLUMN_FAVOURITE = "favourite";

    public static final String DB_NAME = "VOCABULARY";
    public static final int DB_VERSION = 2;
    private SQLiteDatabase db;

    private static final String TAG = "database";


    public Database(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        Log.d(TAG,"onCreate Database");

        String script_english_word = String.format
                ("Create table %s (%s integer primary key autoincrement, " +
                                "%s TEXT, " +
                                "%s TEXT, " +
                                "%s TEXT, " +
                                "%s TEXT, " +
                                "%s TEXT, " +
                                "%s TEXT, " +
                                "%s VARCHAR," +
                                "%s TEXT);",
                        EN_TABLE_NAME,
                        EN_COLUMN_ID,
                        EN_COLUMN_WORD,
                        EN_COLUMN_TYPE,
                        EN_COLUMN_SUBJECT,
                        EN_COLUMN_SCORE,
                        EN_COLUMN_CHECK,
                        EN_COLUMN_WRONG_NUMBER,
                        EN_COLUMN_NOTE,
                        EN_COLUMN_FAVOURITE);


        try {
            sqLiteDatabase.execSQL(script_english_word);

        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        Log.d("ContactLog", "onUpgrade DB");
        //sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SUBJECT_TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + EN_TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public SQLiteDatabase openDataBase(SQLiteDatabase database) {
        if(database.isOpen()){
            db.execSQL("PRAGMA foreign_keys=ON;");
            return db;
        }
        db = getWritableDatabase();
        db.execSQL("PRAGMA foreign_keys=ON;");

        return db;
    }

    /**
     * open database
     */
    public void open() {

        try {
            db = getWritableDatabase();
            db.execSQL("PRAGMA foreign_keys=ON;");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * close database
     */
    public void close() {
        if (db != null && db.isOpen()) {
            try {
                db.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

/************************* method work with database *******************/

    /**
     * get all row of table with sql command then return cursor
     * cursor move to frist to redy for get data
     */
    public Cursor getAll(String sql) {
        open();
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        close();
        return cursor;
    }

    /**
     * insert contentvaluse to table
     *
     * @param values value of data want insert
     * @return index row insert
     */
    public long insert(String table, ContentValues values) {
        open();

        long index = db.insert(table, null, values);

        close();

        return index;
    }

    /**
     * update values to table
     *
     * @return index row update
     */
    public boolean update(String table, ContentValues values, String where) {
        open();
        long index = db.update(table, values, where, null);
        close();
        return index > 0;
    }

    /**
     * delete id row of table
     */
    public boolean delete(String table, String where) {
        open();
        long index = db.delete(table, where, null);
        close();
        return index > 0;
    }
    /************************* end of method work with database *******************/

    public  List<EnglishWord> getAllWord() {
        open();
        Log.d(TAG, "getAllContact");
        List<EnglishWord> list = new ArrayList<>();
        String query = "SELECT * FROM " + EN_TABLE_NAME;

        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        Cursor cursor = sqLiteDatabase.rawQuery(query, null);


        if (cursor.moveToFirst()) {
            do {
                EnglishWord englishWord = new EnglishWord();
                englishWord.setId(Integer.parseInt(cursor.getString(0)));
                englishWord.setWord(cursor.getString(1));
                englishWord.setType((cursor.getString(2)));
                englishWord.setSubject((cursor.getString(3)));
                englishWord.setScore((cursor.getString(4)));
                englishWord.setCheck((cursor.getString(5)));
                englishWord.setWrong_number(cursor.getString(6));
                englishWord.setNote(cursor.getString(7));
                englishWord.setIs_favourite(cursor.getString(8));
                list.add(englishWord);

            } while (cursor.moveToNext());
        }
        close();
        return list;

    }


}
