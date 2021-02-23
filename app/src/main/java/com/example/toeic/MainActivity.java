
package com.example.toeic;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.toeic.database.Database;
import com.example.toeic.database.QueryDatabase;
import com.example.toeic.databinding.ActivityMainBinding;
import com.example.toeic.shared_preferences.PublicSharedPreferences;
import com.example.toeic.vocabulary.EnglishWord;

import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ActivityMainBinding binding;
    SharedPreferences mySharedPreferences;
    PublicSharedPreferences sp;

    public static final Integer RecordAudioRequestCode = 1;

    QueryDatabase queryDatabase = new QueryDatabase(this);


    Thread thread = new Thread(){
        @Override
        public void run() {

            Database db = new Database(MainActivity.this);
            List<EnglishWord> list = db.getAllWord();
            queryDatabase.readFileWord(MainActivity.this, R.raw.data);

            super.run();

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(MainActivity.this, R.layout.activity_main);

        init();
    }


    protected void init(){
        binding.setClickHandler(this);
        sp = new PublicSharedPreferences();
        mySharedPreferences = getSharedPreferences(sp.NAME_SHARE_PREFERENCES, MODE_PRIVATE);

        boolean isFirstRun = mySharedPreferences.getBoolean(sp.KEY_IS_FIRST_RUN, false);

        //
        //if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
        // != PackageManager.PERMISSION_GRANTED){
        //checkPermission();
        // }

        if(!isFirstRun){
            thread.start();
            Intent intent = new Intent(MainActivity.this, FirstActivity.class);
            startActivity(intent);

            finish();
        }

        String userName = mySharedPreferences.getString(sp.KEY_USER_NAME,"");


        binding.tvWelcome.setText("Xin chào " + userName + "!");

    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this,new String[]
                    {Manifest.permission.RECORD_AUDIO},RecordAudioRequestCode);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RecordAudioRequestCode && grantResults.length > 0 ){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this,"Permission Granted",Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onClick(View view) {
        Intent intent = null;
        Log.d("test", "btn: "+ view.getId());
        switch (view.getId()){
            case R.id.btn_study_new_word:
                Log.d("test","btn_study_new_word");
                intent = new Intent(MainActivity.this, StudyActivity.class);
                intent.putExtra("KEY", "btn_study_new_word");
                break;
            case R.id.btn_practice:
                intent = new Intent(MainActivity.this, StudyActivity.class);
                intent.putExtra("KEY", "btn_practice");
                break;
            case R.id.btn_hard_word:
                intent = new Intent(MainActivity.this, StudyActivity.class);
                intent.putExtra("KEY", "btn_hard_word");
                break;
            case R.id.btn_my_information:
                intent = new Intent(MainActivity.this, UserActivity.class);
                break;
            case R.id.btn_list_word:
                intent = new Intent(MainActivity.this, ListWordActivity.class);
                intent.putExtra("KEY", "btn_list_word");
                break;

        }

        if(intent != null) {
            startActivity(intent);
            //finish();
        }

    }
}
