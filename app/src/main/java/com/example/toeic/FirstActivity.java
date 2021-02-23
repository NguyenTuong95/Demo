package com.example.toeic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import com.example.toeic.databinding.ActivityFirstBinding;
import com.example.toeic.shared_preferences.PublicSharedPreferences;

public class FirstActivity extends AppCompatActivity implements View.OnClickListener{

    ActivityFirstBinding binding;
    SharedPreferences spUser;
    PublicSharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_first);

        init();

    }

    void init(){
        binding.setClickHandler(this);
        binding.btnNext.setEnabled(false);
        sp = new PublicSharedPreferences();

        binding.edtName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() ==0) {
                    binding.btnNext.setEnabled(false);
                    binding.edtName.setError("Bạn bắt buộc phải nhập tên!");
                } else {
                    binding.btnNext.setEnabled(true);
                    binding.edtName.setError(null);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }

        });

    }


    void process(){
        String strName = binding.edtName.getText().toString();

        spUser = getSharedPreferences(sp.NAME_SHARE_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = spUser.edit();

        editor.putString(sp.KEY_USER_NAME, strName);
        editor.putInt(sp.KEY_USER_LEVEL, 1);
        editor.putInt(sp.KEY_USER_SCORE,0);
        editor.putInt(sp.KEY_USER_NUM_WORD_FOR_PRACTICE, 10);
        editor.putInt(sp.KEY_USER_NUM_WORD_FOR_LESSON, 10);
        editor.putBoolean(sp.KEY_IS_FIRST_RUN, true);

        editor.commit();


        Intent intent = new Intent(FirstActivity.this, MainActivity.class);
        startActivity(intent);
        finish();

    }


    @Override
    public void onClick(View view) {
        if(binding.btnNext.isEnabled() == false)
            return;

        if(view.getId() == R.id.btn_next){
            process();
        }

    }
}
