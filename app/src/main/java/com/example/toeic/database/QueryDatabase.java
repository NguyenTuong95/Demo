
package com.example.toeic.database;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.toeic.vocabulary.EnglishWord;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class QueryDatabase extends Database {


    private static final int INT_NOUN                       = 0;
    private static final int INT_ADJECTIVE                  = 1;
    private static final int INT_VERB                       = 2;
    private static final int INT_ADVERB                     = 3;
    private static final int INT_PREPOSITION                = 4;
    private static final int INT_DEFAULT                    = 5;

    private static final String STR_NOUN                    = "n";
    private static final String STR_ADJECTIVE               = "adj";
    private static final String STR_VERB                    = "v";
    private static final String STR_ADVERB                  = "adv";
    private static final String STR_PREPOSITION             = "pre";

    private static final int MODE_SUBJECT                   = 0;
    private static final int MODE_WORD                      = 1;
    private static final int MODE_TYPE                      = 2;
    private static final int MODE_INSERT                    = 3;
    private static final int MODE_DEFAULT                   = 4;


    private static String gSubject                           = "";
    private static String gScore                             = "150";
    private static String gType                              = "";
    private static String gWord                              = "";
    private static String gNote                              = "";

    Database db;

    private static final String TAG = "QueryDatabase" ;

    private EnglishWord englishWord = new EnglishWord();


    public QueryDatabase(Context context) {
        super(context);
        db = new Database(context);
    }


    public List<EnglishWord> getListWord( String query){

        db.open();
        Log.d(TAG, "getListWord");
        List<EnglishWord> list = new ArrayList<>();

        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(query , null);

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
        db.close();
        return list;
    }


    public void insertNewWord(EnglishWord englishWord){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(EN_COLUMN_WORD, englishWord.getWord());
        contentValues.put(EN_COLUMN_TYPE, englishWord.getType());
        contentValues.put(EN_COLUMN_SUBJECT, englishWord.getSubject());
        contentValues.put(EN_COLUMN_SCORE, englishWord.getScore());
        contentValues.put(EN_COLUMN_CHECK, englishWord.getCheck());
        contentValues.put(EN_COLUMN_WRONG_NUMBER, englishWord.getWrong_number());
        contentValues.put(EN_COLUMN_NOTE, englishWord.getNote());
        contentValues.put(EN_COLUMN_FAVOURITE, englishWord.getIs_favourite());
        try{
            sqLiteDatabase.insert(EN_TABLE_NAME, null, contentValues);

            sqLiteDatabase.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Log.d(TAG, "insert New Word done!");

    }

    public void updateTableEnglishWord(EnglishWord englishWord) {
        Log.d(TAG, "updateTableEnglishWord");
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        contentValues.put(EN_COLUMN_WORD, englishWord.getWord());
        contentValues.put(EN_COLUMN_TYPE, englishWord.getType());
        contentValues.put(EN_COLUMN_SUBJECT, englishWord.getSubject());
        contentValues.put(EN_COLUMN_SCORE, englishWord.getScore());
        contentValues.put(EN_COLUMN_CHECK, englishWord.getCheck());
        contentValues.put(EN_COLUMN_WRONG_NUMBER, englishWord.getWrong_number());
        contentValues.put(EN_COLUMN_NOTE, englishWord.getNote());
        contentValues.put(EN_COLUMN_FAVOURITE, englishWord.getIs_favourite());
        //update where column_id=?

        sqLiteDatabase.update(EN_TABLE_NAME, contentValues,
                EN_COLUMN_ID + " =?",
                new String[]{String.valueOf(englishWord.getId())});
        sqLiteDatabase.close();
    }

    public int convertStringToType(String type){
        //Log.d(TAG, "convertStringToType: " + type);
        switch (type){
            case STR_NOUN:
                return INT_NOUN;
            case STR_ADJECTIVE:
                return INT_ADJECTIVE;
            case STR_ADVERB:
                return INT_ADVERB;
            case STR_PREPOSITION:
                return INT_PREPOSITION;
            case STR_VERB:
                return INT_VERB;


        }

        return INT_DEFAULT;
    }

    public String readFileWord(Context ctx, int resId)
    {
        int mode = 0;

        InputStream inputStream = ctx.getResources().openRawResource(resId);
        InputStreamReader inputreader = new InputStreamReader(inputStream);
        BufferedReader buffreader = new BufferedReader(inputreader);
        String line = "";
        StringBuilder text = new StringBuilder();

        while (true) {
            try {
                if (!((line = buffreader.readLine()) != null)) break;
            } catch (IOException e) {
                e.printStackTrace();
            }

            char[] temp = line.toCharArray();
            Log.d("line ", line);

            Log.d("mode: ", "" + mode);

            if(temp.length == 0) {
                Log.d("error ", "break");
                continue;
            }

            if(temp[0] == '[')
                mode = MODE_SUBJECT;


            char[] temp_;
            int index;
            switch(mode){

                case MODE_SUBJECT:
                    temp_ = line.toCharArray();
                    String subject_ = "";
                    index = 1;
                    while(temp_[index] != '\0'){
                        if(temp_[index] == ']') break;

                        subject_ += temp_[index];
                        index++;
                    }
                    gSubject = subject_;
                    mode = MODE_WORD;

                    break;
                case MODE_WORD:
                    gWord = line;
                    mode = MODE_TYPE;

                    break;
                case MODE_TYPE:
                    gNote = line;
                    if(temp[0] == '('){ // subject
                        temp_ = line.toCharArray();
                        String type = "";
                        index = 1;
                        while(temp_[index] != '\0'){
                            if(temp_[index] == ')') break;

                            type += temp_[index];
                            index++;
                        }
                        if(index < 5)
                            gType = type;
                        else
                            gType = "";

                    }else{
                        String[] tmpStyle = line.split("[, .]+");
                        if(tmpStyle.length > 0) {
                            if (convertStringToType(tmpStyle[0]) != INT_DEFAULT) {
                                gType = tmpStyle[0];
                                gNote = line;

                            }else gType = "";
                        }
                    }

                    mode = MODE_INSERT;

                    break;
                case MODE_INSERT:
                    englishWord.setCheck("0");
                    englishWord.setScore(gScore);
                    englishWord.setWord(gWord);
                    englishWord.setSubject(gSubject);
                    englishWord.setType(gType);
                    englishWord.setWrong_number("0");
                    englishWord.setNote(gNote);
                    englishWord.setIs_favourite("0");

                    insertNewWord(englishWord);
                    Log.d(TAG, "******************************************");
                    Log.d(TAG, "subject: " + gSubject);
                    Log.d(TAG, "word: " + gWord);
                    Log.d(TAG, "type: " + gType);
                    Log.d(TAG, "note: " + gNote);

                    mode = MODE_WORD;

                    break;
                case MODE_DEFAULT:

                    mode = MODE_WORD;

                    break;

            }

            text.append(line);
            text.append('\n');

        }


        return text.toString();
    }

}
