
package com.example.toeic;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.toeic.database.Database;
import com.example.toeic.database.QueryDatabase;
import com.example.toeic.databinding.ActivityWordBinding;
import com.example.toeic.shared_preferences.PublicSharedPreferences;
import com.example.toeic.vocabulary.EnglishWord;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;


public class WordActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int CODE_STUDY_ACTIVITY = 1;
    private static final int CODE_LIST_WORD_ACTIVITY = 2;

    ActivityWordBinding binding;
    private TextToSpeech _textToSpeech;
    private int code_activity = 0;
    private QueryDatabase queryDatabase;
    private EnglishWord englishWord;
    private boolean isFavourite = false;
    private  List<EnglishWord> listNewWord = null;
    private static Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_word);
        binding.setClickHandler(this);
        queryDatabase = new QueryDatabase(this);

        init();

    }

    void init(){

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        englishWord = new EnglishWord();
        listNewWord = new ArrayList<>();

        binding.ivSpeech.setImageResource(R.drawable.ic_sound_enable);

        _textToSpeech = new TextToSpeech(getApplicationContext(),
                new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        try {
                            _textToSpeech.setLanguage(Locale.ENGLISH);
                        }catch (Exception e){
                            Toast.makeText(WordActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle != null){
            englishWord = (EnglishWord) bundle.getSerializable("KEY");
            //binding.tvEnWord.setText(englishWord.getWord());
            binding.tvType.setText(englishWord.getType());
            binding.tvVnDescription.setText(englishWord.getNote());
            binding.tvWordSubject.setText(englishWord.getSubject());
            mySpeakOut(englishWord.getWord());

            SpannableString ss = new SpannableString(englishWord.getWord());
            String url = "https://dictionary.cambridge.org/vi/dictionary/english/" + englishWord.getWord();
            ss.setSpan(new URLSpan(url), 0,
                    englishWord.getWord().length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE );

            binding.tvEnWord.setText(ss);
            binding.tvEnWord.setMovementMethod(LinkMovementMethod.getInstance());

            code_activity = englishWord.getActivity();
            if(code_activity == CODE_STUDY_ACTIVITY){
                binding.btnNextNewWord.setText("Tiếp");
            }else{
                binding.btnNextNewWord.setText("Trở lại");
            }

            String data_favourite = "";
            String query = String.format("SELECT * FROM %s WHERE %s = %d",
                    db.EN_TABLE_NAME, db.EN_COLUMN_ID, englishWord.getId());
            listNewWord = queryDatabase.getListWord(query);

            // Log.d("WordActivity_test", "size: " + listNewWord.size());
            for(int i = 0; i < listNewWord.size(); i++)
            //Log.d("WordActivity_test", "favourite_"+ listNewWord.get(i).getId() + ": " + listNewWord.get(i).getIs_favourite());

            if(listNewWord.size() > 0){
                data_favourite = listNewWord.get(0).getIs_favourite();
            }

            if(Integer.parseInt(data_favourite) == 0){
                binding.ivFavourite.setImageResource(R.drawable.ic_favourite_disable);
                isFavourite = false;
            }else{
                binding.ivFavourite.setImageResource(R.drawable.ic_favourite_enable);
                isFavourite = true;
            }

        }
    }


    private void mySpeakOut(String str) {
        String utteranceId = UUID.randomUUID().toString();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            _textToSpeech.speak(str, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_next_new_word:

                queryDatabase.updateTableEnglishWord(englishWord);

                if(code_activity == CODE_STUDY_ACTIVITY) {

                    PublicSharedPreferences sp;
                    sp = new PublicSharedPreferences();
                    SharedPreferences mySharedPreferences = getSharedPreferences(sp.NAME_SHARE_PREFERENCES, MODE_PRIVATE);
                    SharedPreferences.Editor editor = mySharedPreferences.edit();
                    editor.putBoolean(sp.KEY_IS_NEW_WORD, false);
                    editor.commit();

                    Intent intent = new Intent(WordActivity.this, StudyActivity.class);
                    startActivity(intent);
                    finish();
                }else if(code_activity == CODE_LIST_WORD_ACTIVITY){
                    Intent intent = new Intent(WordActivity.this, ListWordActivity.class);
                    startActivity(intent);
                    finish();
                }

                break;
            case R.id.iv_speech:
                String word = binding.tvEnWord.getText().toString();
                mySpeakOut(word);
                break;
            case R.id.iv_favourite:
                isFavourite = !isFavourite;

                if(isFavourite){
                    binding.ivFavourite.setImageResource(R.drawable.ic_favourite_enable);
                    englishWord.setIs_favourite("1");
                }else{
                    binding.ivFavourite.setImageResource(R.drawable.ic_favourite_disable);
                    englishWord.setIs_favourite("0");
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {


        super.onDestroy();

        if (_textToSpeech != null) {
            _textToSpeech.stop();
            _textToSpeech.shutdown();
        }

    }
}
