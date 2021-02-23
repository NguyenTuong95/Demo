
package com.example.toeic;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.toeic.database.Database;
import com.example.toeic.database.QueryDatabase;
import com.example.toeic.databinding.ActivityUserBinding;
import com.example.toeic.shared_preferences.PublicSharedPreferences;
import com.example.toeic.vocabulary.EnglishWord;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.review.testing.FakeReviewManager;
import com.google.android.play.core.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class UserActivity extends AppCompatActivity implements View.OnClickListener {

    ActivityUserBinding binding;
    SharedPreferences mySharedPreferences;
    PublicSharedPreferences sp;
    static Database db;
    static QueryDatabase queryDatabase;
    List<EnglishWord> listLearnedWord;
    List<EnglishWord> listReviewWord;

    //private ReviewManager reviewManager;


    private final int MAX_SCORE = 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user);
        binding.setClickHandler(this);
        listLearnedWord = new ArrayList<>();
        listReviewWord = new ArrayList<>();
        queryDatabase = new QueryDatabase(this);

        init();
    }


    private void init(){
        //reviewManager = ReviewManagerFactory.create(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        sp = new PublicSharedPreferences();
        mySharedPreferences = getSharedPreferences(sp.NAME_SHARE_PREFERENCES, MODE_PRIVATE);

        String userName = mySharedPreferences.getString(sp.KEY_USER_NAME,"");
        int userScore = mySharedPreferences.getInt(sp.KEY_USER_SCORE, 0);

        String str = "1";
        String query = String.format("SELECT * FROM %s WHERE %s = %s", db.EN_TABLE_NAME, db.EN_COLUMN_CHECK, str);
        listLearnedWord = queryDatabase.getListWord(query);

        str = "0";
        query = String.format("SELECT * FROM %s WHERE %s <> %s", db.EN_TABLE_NAME, db.EN_COLUMN_WRONG_NUMBER, str);

        binding.cbReviewWordFavourite.setChecked(mySharedPreferences.getBoolean(sp.KEY_CHECKBOX_REVIEW_FAVOURITE, false));
        listReviewWord = queryDatabase.getListWord(query);
        binding.tvName.setText(userName);
        binding.tvScore.setText("" + userScore);
        binding.tvLearnedWords.setText(""+ listLearnedWord.size());
        binding.tvReviewWords.setText("" + listReviewWord.size());

        initColorBtn();
    }


    private void initColorBtn(){
        binding.btnPractice10.setBackgroundColor(getResources().getColor(R.color.colorBtnDisable));
        binding.btnPractice15.setBackgroundColor(getResources().getColor(R.color.colorBtnDisable));
        binding.btnPractice30.setBackgroundColor(getResources().getColor(R.color.colorBtnDisable));

        binding.btnLesson10.setBackgroundColor(getResources().getColor(R.color.colorBtnDisable));
        binding.btnLesson15.setBackgroundColor(getResources().getColor(R.color.colorBtnDisable));
        binding.btnLesson30.setBackgroundColor(getResources().getColor(R.color.colorBtnDisable));

        int userNumWordForLesson = mySharedPreferences.getInt(sp.KEY_USER_NUM_WORD_FOR_LESSON, 0);
        int userNumWordForPractice = mySharedPreferences.getInt(sp.KEY_USER_NUM_WORD_FOR_PRACTICE, 0);

        switch (userNumWordForPractice){
            case 10:
                binding.btnPractice10.setBackgroundColor(getResources().getColor(R.color.colorBtnEnabled));
                break;
            case 15:
                binding.btnPractice15.setBackgroundColor(getResources().getColor(R.color.colorBtnEnabled));
                break;
            case 30:
                binding.btnPractice30.setBackgroundColor(getResources().getColor(R.color.colorBtnEnabled));
                break;

        }

        switch (userNumWordForLesson){
            case 10:
                binding.btnLesson10.setBackgroundColor(getResources().getColor(R.color.colorBtnEnabled));
                break;
            case 15:
                binding.btnLesson15.setBackgroundColor(getResources().getColor(R.color.colorBtnEnabled));
                break;
            case 30:
                binding.btnLesson30.setBackgroundColor(getResources().getColor(R.color.colorBtnEnabled));
                break;

        }

    }



    @Override
    public void onClick(View view) {
        SharedPreferences.Editor editor = mySharedPreferences.edit();

        switch (view.getId()){
            case R.id.btn_lesson_10:
                editor.putInt(sp.KEY_USER_NUM_WORD_FOR_LESSON, 10);
                break;
            case R.id.btn_lesson_15:
                editor.putInt(sp.KEY_USER_NUM_WORD_FOR_LESSON, 15);
                break;
            case R.id.btn_lesson_30:
                editor.putInt(sp.KEY_USER_NUM_WORD_FOR_LESSON, 30);
                break;
            case R.id.btn_practice_10:
                editor.putInt(sp.KEY_USER_NUM_WORD_FOR_PRACTICE, 10);
                break;
            case R.id.btn_practice_15:
                editor.putInt(sp.KEY_USER_NUM_WORD_FOR_PRACTICE, 15);
                break;
            case R.id.btn_practice_30:
                editor.putInt(sp.KEY_USER_NUM_WORD_FOR_PRACTICE, 30);
                break;

            case R.id.btn_list_word_favourite:
                Intent intent = new Intent(UserActivity.this, ListWordActivity.class);
                intent.putExtra("KEY", "btn_list_word_favourite");
                startActivity(intent);
                //finish();
                break;
            case R.id.cb_review_word_favourite:
                int userScore = mySharedPreferences.getInt(sp.KEY_USER_SCORE, 0);
                if(userScore < MAX_SCORE){
                    Toast.makeText(this, "Bạn cần đạt tối thiểu " + MAX_SCORE + " điểm!", Toast.LENGTH_SHORT).show();
                    binding.cbReviewWordFavourite.setChecked(false);
                }else{
                    editor.putBoolean(sp.KEY_CHECKBOX_REVIEW_FAVOURITE, binding.cbReviewWordFavourite.isChecked());
                }
                break;
            case R.id.btn_rate_review:
                //showRateApp();
                showRateAppFallbackDialog();
                break;
        }

        editor.commit();
        initColorBtn();


    }


    /**
     * Shows rate app bottom sheet using In-App review API
     * The bottom sheet might or might not shown depending on the Quotas and limitations
     * https://developer.android.com/guide/playcore/in-app-review#quotas
     * We show fallback dialog if there is any error
     */
    public void showRateApp() {

        //ReviewManager manager = ReviewManagerFactory.create(this);
        ReviewManager manager = new FakeReviewManager(UserActivity.this);
        Task<ReviewInfo> request = manager.requestReviewFlow();

        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // We can get the ReviewInfo object
                ReviewInfo reviewInfo = task.getResult();
                Task<Void> flow = manager.launchReviewFlow(this, reviewInfo);
                flow.addOnCompleteListener(task1 -> {
                    // The flow has finished. The API does not indicate whether the user
                    // reviewed or not, or even whether the review dialog was shown. Thus, no
                    // matter the result, we continue our app flow.
                });
            } else {
                showRateAppFallbackDialog();
                // There was some problem, continue regardless of the result.
            }
        });

    }

    /**
     * Showing native dialog with three buttons to review the app
     * Redirect user to playstore to review the app
     */
    private void showRateAppFallbackDialog() {
        //Toast.makeText(this, "showRateAppFallbackDialog", Toast.LENGTH_SHORT).show();
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.rate_app_title)
                .setMessage(R.string.rate_app_message)
                .setPositiveButton(R.string.rate_btn_pos, (dialog, which) -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=com.tuongnv.android.toeic"));
                    startActivity(intent);
                })
                .setNegativeButton(R.string.rate_btn_neg,
                        (dialog, which) -> {
                        })
                .setNeutralButton(R.string.rate_btn_nut,
                        (dialog, which) -> {
                        })
                .setOnDismissListener(dialog -> {
                })
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                Log.d("test", "back to home");
                onBackPressed();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);

    }


}
