package com.example.toeic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.toeic.adapter.WordAdapter;
import com.example.toeic.database.Database;
import com.example.toeic.database.QueryDatabase;
import com.example.toeic.databinding.ActivityListWordBinding;
import com.example.toeic.vocabulary.EnglishWord;

import java.util.ArrayList;
import java.util.List;

public class ListWordActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int CODE_LIST_WORD_ACTIVITY = 2;
    ActivityListWordBinding binding;
    WordAdapter adapter;
    private static List<EnglishWord> data = null;
    private static Database db;
    private static QueryDatabase queryDatabase;

    WordAdapter.IOnWordItemClickListener listener =
            new WordAdapter.IOnWordItemClickListener() {
                @Override
                public void onItemClick(int index) {
                    Bundle bundle = new Bundle();
                    data.get(index).setActivity(CODE_LIST_WORD_ACTIVITY);
                    bundle.putSerializable("KEY", data.get(index));
                    Intent intent = new Intent(ListWordActivity.this, WordActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_list_word);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_list_word);
        db = new Database(ListWordActivity.this);

        queryDatabase = new QueryDatabase(this);
        data = new ArrayList<>();

        String valueIntent;
        Intent intent;
        intent = getIntent();
        valueIntent = intent.getStringExtra("KEY");

        if(valueIntent.compareTo("btn_list_word") == 0){
            data = db.getAllWord();
        }else{
            String query = String.format("SELECT * FROM %s WHERE %s = 1",
                    db.EN_TABLE_NAME, db.EN_COLUMN_FAVOURITE);

            data = queryDatabase.getListWord(query);

            if(data.size() == 0){
                Toast.makeText(this, "Không có từ yêu thích nào!", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
        }



        init();
    }

    void init(){

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        adapter = new WordAdapter(this, data, listener);
        binding.rvWord.setAdapter(adapter);
        //RecyclerView scroll vertical
        LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        binding.rvWord.setLayoutManager(linearLayoutManager);

    }


    @Override
    public void onClick(View view) {

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
}
