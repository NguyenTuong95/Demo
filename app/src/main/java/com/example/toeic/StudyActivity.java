package com.example.toeic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.toeic.database.Database;
import com.example.toeic.database.QueryDatabase;

import com.example.toeic.databinding.ActivityStudyBinding;
import com.example.toeic.shared_preferences.PublicSharedPreferences;
import com.example.toeic.vocabulary.EnglishWord;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;


public class StudyActivity extends AppCompatActivity  implements View.OnClickListener {

    private static final int CODE_STUDY_ACTIVITY = 1;

    private static TextToSpeech textToSpeech;
    private static SharedPreferences mySharedPreferences;
    private static PublicSharedPreferences sp;
    private static ActivityStudyBinding binding;
    private  QueryDatabase queryDatabase;
    private static Database db;
    private  List<EnglishWord> listNewWord = null;
    private static MyAsyncTask myAsyncTask = null;
    private static String valueIntent = "";
    private static int score = 0;
    private static int index = 0;
    private static int count = 0;
    private static int maxScope;
    private final int MAX = 1000;
    private final int SCORE = 50;
    private static int queue[];
    private static int q_end;
    private static boolean flagCheck = false;
    private static boolean enableImageSound = false;
    private  EnglishWord englishWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("test", "onCreate");
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_study);
        binding.setClickHandler(this);
        englishWord = new EnglishWord();
        queue = new int[MAX];
        q_end = 0;
        for(int i = 0; i < MAX; i++){
            queue[i] = -1;
        }
        init();

    }

    void init(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            binding.edtEnterWord.setBackground(getResources()
                    .getDrawable(R.drawable.rounded_edittext));
        }
        binding.edtEnterWord.setText("");
        binding.progressTimes.setMax(MAX);
        binding.ivSound.setImageResource(R.drawable.ic_sound_disable);

        queryDatabase = new QueryDatabase(this);
        flagCheck = true;
        enableImageSound = false;
        listNewWord = new ArrayList<>();

        Intent intent;
        intent = getIntent();
        valueIntent = intent.getStringExtra("KEY");
        if(valueIntent == null)
            return;

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        sp = new PublicSharedPreferences();

        mySharedPreferences = getSharedPreferences(sp.NAME_SHARE_PREFERENCES, MODE_PRIVATE);

        if(valueIntent.compareTo("btn_study_new_word") == 0) {

            maxScope = mySharedPreferences.getInt(sp.KEY_USER_NUM_WORD_FOR_LESSON, 0);
            String str = "0";
            String query = String.format("SELECT * FROM %s WHERE %s = %s",
                    db.EN_TABLE_NAME, db.EN_COLUMN_CHECK, str);

            listNewWord = queryDatabase.getListWord(query);

            if(listNewWord.size() == 0){

                Toast.makeText(this, "Bạn đã học hết từ mới rồi!", Toast.LENGTH_SHORT).show();
                backToMainActivity();
            }else{
                actionBar.setTitle("Học từ mới");
                setSharedPreferencesIsNewWord(true);

            }
        }
        else if(valueIntent.compareTo("btn_practice") == 0){

            maxScope = mySharedPreferences.getInt(sp.KEY_USER_NUM_WORD_FOR_PRACTICE, 0);

            boolean checkboxFavourite = mySharedPreferences.getBoolean(sp.KEY_CHECKBOX_REVIEW_FAVOURITE, false);
            String query = "";
            if(checkboxFavourite) {
                query = String.format("SELECT * FROM %s WHERE %s = 1",
                        db.EN_TABLE_NAME, db.EN_COLUMN_FAVOURITE);
            }else{
                query = String.format("SELECT * FROM %s WHERE %s = 1",
                        db.EN_TABLE_NAME, db.EN_COLUMN_CHECK);
            }

            listNewWord = queryDatabase.getListWord(query);

            if(listNewWord.size() == 0){

                Toast.makeText(this, "Không có từ luyện tập nào!", Toast.LENGTH_SHORT).show();
                backToMainActivity();
            }else{
                actionBar.setTitle("Luyện tập");
                setSharedPreferencesIsNewWord(false);
                processNextNewWord();
            }
        }else{

            maxScope = mySharedPreferences.getInt(sp.KEY_USER_NUM_WORD_FOR_PRACTICE, 0);
            String str = "0";
            String query = String.format("SELECT * FROM %s WHERE %s <> %s",
                    db.EN_TABLE_NAME, db.EN_COLUMN_WRONG_NUMBER, str);
            listNewWord = queryDatabase.getListWord(query);
            if(listNewWord.size() == 0){
                Toast.makeText(this, "Không có từ khó nào!", Toast.LENGTH_SHORT).show();
                backToMainActivity();
            }else{
                actionBar.setTitle("Từ khó");
                setSharedPreferencesIsNewWord(false);
                processNextNewWord();

            }
        }

        textToSpeech = new TextToSpeech(getApplicationContext(),
                new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        try {
                            textToSpeech.setLanguage(Locale.ENGLISH);
                        }catch (Exception e){
                            Toast.makeText(StudyActivity.this, "" +e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void speakOut(String str) {

        String utteranceId = UUID.randomUUID().toString();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textToSpeech.speak(str, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
        }
    }

    private boolean checkVisitQueue(int _index){
        if(index == listNewWord.size())
            return true;

        for(int i = 0; i < q_end; i++){
            if(queue[i] == _index)
                return true;
        }
        return false;
    }

    private int getIndexRandom(){
        int _index = 0;
        int max_count = 101;
        int _count = 0;
        Random random = new Random();
        do {
            _count++;
            _index = random.nextInt(listNewWord.size());
            if(_count > max_count){
                return -1;
            }
        }while (checkVisitQueue(_index) == true);
        
        queue[q_end++] = _index;
        return _index;
    }

    void nextToConclusive(){
        englishWord.setWord_number(count);
        englishWord.setScore("" + score);

        score +=  mySharedPreferences.getInt(sp.KEY_USER_SCORE,0);

        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putInt(sp.KEY_USER_SCORE, score);
        editor.commit();

        count = 0; score = 0;
        index = 0; q_end = 0;

        Bundle bundle = new Bundle();
        bundle.putSerializable("KEY", englishWord);
        Intent intent = new Intent(StudyActivity.this, ConclusiveActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    void processNextNewWord() {

        cancelAsyncTask();
        myAsyncTask = new MyAsyncTask(StudyActivity.this);
        executeAsyncTask();


        Log.d("test", "processNextNewWord: " + count);

        if (count >= maxScope || (count >= listNewWord.size())) {
            nextToConclusive();
            return;
        }

        if(valueIntent.compareTo("btn_study_new_word") != 0) {
            index = getIndexRandom();
            if(index == -1){
                nextToConclusive();
                return;
            }
        }else{
            index = count;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            binding.edtEnterWord.setBackground(getResources()
                    .getDrawable(R.drawable.rounded_edittext));
        }
        binding.ivSound.setImageResource(R.drawable.ic_sound_disable);

        englishWord = listNewWord.get(index);

        boolean _isNewWord = mySharedPreferences.getBoolean(sp.KEY_IS_NEW_WORD, false);
        if(_isNewWord){
            cancelAsyncTask();
            Bundle bundle = new Bundle();
            englishWord.setActivity(CODE_STUDY_ACTIVITY);
            listNewWord.get(index).setCheck("1");
            bundle.putSerializable("KEY", listNewWord.get(index));
            Intent intent = new Intent(StudyActivity.this, WordActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);

            return;
        }

        binding.edtEnterWord.setText("");
        enableImageSound = false;
        flagCheck = true;
        setEditable(true);

        String vnWord;
        String typeWord;
        String subject;
        typeWord = listNewWord.get(index).getType();
        subject = listNewWord.get(index).getSubject();
        vnWord = listNewWord.get(index).getNote();
        binding.tvVnWord.setText(vnWord);
        binding.tvTypeWord.setText(typeWord);
        binding.tvSubject.setText(subject);

        String query = String.format("SELECT * FROM %s WHERE %s = %d",
                db.EN_TABLE_NAME, db.EN_COLUMN_ID, englishWord.getId());
        List<EnglishWord> listNewWord_ = new ArrayList<>();;
        listNewWord_ = queryDatabase.getListWord(query);
        if(listNewWord.size() > 0){
            englishWord.setIs_favourite(listNewWord_.get(0).getIs_favourite());
        }

        count ++;

    }

    public void checkWordInput(){

        String inputText = binding.edtEnterWord.getText().toString().trim();
        Log.d("input_text", inputText);
        int number = Integer.parseInt(listNewWord.get(index).getWrong_number());

        if(inputText.compareToIgnoreCase(englishWord.getWord()) == 0){
            score += SCORE;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                binding.edtEnterWord.setBackground(getResources()
                        .getDrawable(R.drawable.rounded_edittext_exact));
            }
            number = (number > 0) ? (number - 1) : (number = 0);

        }else {
            Toast.makeText(this, "Sai rồi! đúng là: " + englishWord.getWord()  +
                    " input: " + inputText,  Toast.LENGTH_SHORT).show();
            number += 1;

        }

        englishWord.setWrong_number("" + number);
        queryDatabase.updateTableEnglishWord(englishWord);

    }

    private void setEditable(boolean editable) {
        binding.edtEnterWord.setFocusable(editable);
        binding.edtEnterWord.setClickable(editable);
        binding.edtEnterWord.setCursorVisible(editable);
        binding.edtEnterWord.setFocusableInTouchMode(editable);
        binding.edtEnterWord.setEnabled(editable);
    }



    @Override
    public void onClick(View view) {
        if(binding.btnNextWord.isEnabled() == false)
            return;

        switch (view.getId()){
            case R.id.btn_next_word:
                binding.btnNextWord.setEnabled(false);
                if(flagCheck) {
                    checkWordInput();
                }

                if(valueIntent.compareTo("btn_study_new_word") == 0)
                    setSharedPreferencesIsNewWord(true);

                processNextNewWord();

                break;
            case R.id.iv_sound:
                if(!enableImageSound)
                    break;

                speakOut(englishWord.getWord());
                break;
        }
    }

    private void nextWord(){

        if(valueIntent.compareTo("btn_study_new_word") == 0)
            setSharedPreferencesIsNewWord(true);

        processNextNewWord();
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);

    }

    void backToMainActivity(){
        Intent intent = new Intent(StudyActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void executeAsyncTask(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            myAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else
            myAsyncTask.execute();
    }

    private void cancelAsyncTask(){
        if(myAsyncTask != null)
            myAsyncTask.cancel(true);
    }

    //============================ MyAsyncTask ===========================

    public class MyAsyncTask extends AsyncTask<Integer, Integer, Integer> {

        int valSet = MAX - 50;
        int valNextWord = 0;
        Activity contextParent;

        public MyAsyncTask(Activity contextParent) {
            this.contextParent = contextParent;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Integer doInBackground(Integer... integers) {
            // do something in worker / background thread
            // heavy action

            for (int i = MAX; i >= 0; i--) {
                //khi gọi hàm này thì onProgressUpdate sẽ thực thi

                if(isCancelled())
                    break;

                SystemClock.sleep(30);
                publishProgress(i);

            }
            return null;
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            int val = values[0];
            if (val < valSet) {
                binding.btnNextWord.setEnabled(true);
            } else {
                binding.btnNextWord.setEnabled(false);
            }
            binding.progressTimes.setProgress(val);

            String inputText = "";
            inputText = binding.edtEnterWord.getText().toString().trim();
            int number = Integer.parseInt(listNewWord.get(index).getWrong_number());

            try {
            if ( inputText.compareToIgnoreCase(englishWord.getWord()) == 0) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    binding.edtEnterWord.setBackground(getResources()
                            .getDrawable(R.drawable.rounded_edittext_exact));
                }

                if (valNextWord == 0) {
                    binding.ivSound.setImageResource(R.drawable.ic_sound_enable);
                    enableImageSound = true;
                    setEditable(false);
                    speakOut(englishWord.getWord());
                }

                valNextWord++;

                if (valNextWord == 20) {
                    number = (number > 0) ? (number - 1) : (number = 0);
                    englishWord.setWrong_number("" + number);
                    queryDatabase.updateTableEnglishWord(englishWord);
                    score += SCORE;
                    nextWord();
                }

            }
        } catch (Exception e){

            }
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
        @Override
        protected void onPostExecute(Integer result) {

            super.onPostExecute(result);
            checkWordInput();

            flagCheck = false;
            enableImageSound = true;

        }
    }

    void setSharedPreferencesIsNewWord(boolean _isNewWord){

        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putBoolean(sp.KEY_IS_NEW_WORD, _isNewWord);
        editor.commit();
    }

    // ============================ Activity ================================


    @Override
    protected void onPause() {
        Log.d("test", "onPause");
        super.onPause();
        cancelAsyncTask();
    }

    @Override
    protected void onStop() {
        Log.d("test", "onStop");
        super.onStop();
    }

    @Override
    protected void onStart() {
        Log.d("test", "onStart");
        super.onStart();

    }

    @Override
    protected void onRestart() {
        Log.d("test", "onRestart");
        super.onRestart();
    }

    @Override
    protected void onResume() {
        Log.d("test", "onResume");
        super.onResume();
        if(valueIntent == "" || valueIntent.compareTo("btn_study_new_word") == 0)
            processNextNewWord();
        else{
            cancelAsyncTask();
            myAsyncTask = new MyAsyncTask(StudyActivity.this);
            executeAsyncTask();
         }

    }

    @Override
    protected void onDestroy() {
        cancelAsyncTask();
        super.onDestroy();

        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }

}
