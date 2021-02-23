package com.tuongnv.tiengnhatit.view.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.tuongnv.tiengnhatit.R;
import com.tuongnv.tiengnhatit.presenter.MyPresenter;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private MyPresenter myPresenter;
    private EditText edtUserName, edtPassword;
    private TextView tvNotice;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        initView();

    }

    private void initView() {
        myPresenter = new MyPresenter();

        tvNotice = (TextView)findViewById(R.id.tv_notice);
        edtUserName = (EditText)findViewById(R.id.edt_username);
        edtPassword = (EditText)findViewById(R.id.edt_password);
        findViewById(R.id.btn_sign_in).setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.btn_sign_in:
                if(myPresenter.login(edtUserName.getText().toString(), edtPassword.getText().toString())){
                    tvNotice.setText("Login success!");
                }else{

                    tvNotice.setText("Invalid Information!");
                }

                break;

        }
    }
}