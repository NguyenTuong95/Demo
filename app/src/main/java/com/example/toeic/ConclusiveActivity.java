
package com.example.toeic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.toeic.databinding.ActivityConclusiveBinding;
import com.example.toeic.vocabulary.EnglishWord;

public class ConclusiveActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityConclusiveBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_conclusive);
        binding.setClickHandler(this);
        init();

    }

    void init(){
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle != null){

            EnglishWord englishWord = new EnglishWord();
            englishWord = (EnglishWord) bundle.getSerializable("KEY");
            int nwRight = Integer.parseInt(englishWord.getScore()) / 50;
            int nwAccuracy = 0;
            if(englishWord.getWord_number() != 0){
                nwAccuracy = (nwRight*100)/englishWord.getWord_number();
            }

            binding.tvNumberWordRight.setText("" + nwRight);
            binding.tvAccuracy.setText(nwAccuracy + "%");
            binding.tvScore.setText(englishWord.getScore());

        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_back_to_main:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
        }

    }
}
