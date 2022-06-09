package com.example.novarand_sns;
import android.util.Log;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class SS_SearchMode extends AppCompatActivity {

    Toolbar toolbar;

    EditText editText;
    String keywordset;

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