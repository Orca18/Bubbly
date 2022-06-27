package com.example.novarand_sns;
import android.content.SharedPreferences;
import android.util.Log;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.example.novarand_sns.retrofit.ApiClient;
import com.example.novarand_sns.retrofit.ApiInterface;
import com.example.novarand_sns.retrofit.follower_Response;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SS_SearchMode extends AppCompatActivity {

    Toolbar toolbar;

    EditText editText;
    String keywordset;

    SharedPreferences preferences;
    String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ss_search_mode);

        Bundle extra = getIntent().getExtras();
        keywordset = extra.getString("keyword","");

        toolbar = findViewById(R.id.searchmode_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editText = findViewById(R.id.searchmode_edittext);
        editText.setText(keywordset);

        editText.requestFocus();

        // 키보드 보이기
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        editText.setOnKeyListener((v, keyCode, event) -> {
            String keyword = editText.getText().toString();
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                /////////////////////////////////////
                preferences = getSharedPreferences("novarand",MODE_PRIVATE);
                user_id = preferences.getString("user_id", ""); // 로그인한 user_id값
                ApiInterface createSerarchText_api = ApiClient.getApiClient().create(ApiInterface.class);
                Call<String> call = createSerarchText_api.createSerarchText(user_id,keyword);
                call.enqueue(new Callback<String>()
                {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response)
                    {
                        if (response.isSuccessful() && response.body() != null)
                        {

                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t)
                    {
                        Log.e("에러", t.getMessage());
                    }
                });

                /////////////////////////////////////
                Intent mIntent = new Intent(getApplicationContext(), SS_SearchResult.class);
                mIntent.putExtra("keyword", keyword);
                Log.i("정보태그", "xxx"+keyword);
                startActivity(mIntent);
                finish();
            } else {

            }
            return false;
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                // 왼쪽 상단 버튼 눌렀을 때
                finish();
//                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                // TODO 애니메이션 효과 : 현재 액티비티로 올 때는 안먹혀서 일단 지움
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

}