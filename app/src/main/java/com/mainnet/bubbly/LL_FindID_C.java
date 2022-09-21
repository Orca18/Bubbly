package com.mainnet.bubbly;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.mainnet.bubbly.R;

public class LL_FindID_C extends AppCompatActivity {
    Toolbar toolbar;
    LinearLayout bt_forgotPW_findID;
    TextView tv_id_findID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ll_findid_c);
        // 툴바
        toolbar = findViewById(R.id.toolbar_findID_C);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        bt_forgotPW_findID = findViewById(R.id.bt_forgotPW_findID);
        tv_id_findID = findViewById(R.id.tv_id_findID);

        Intent mIntent = getIntent();
        String login_id = mIntent.getStringExtra("login_id");
        String user_id = mIntent.getStringExtra("user_id");

        //각 길이별 세번째 자리부터 끝에서 마지막 2자리 전까지 * 치환해서 보여준다.
        StringBuffer sb = new StringBuffer();
        sb.append(login_id);
        int length = sb.length();
        int start = 2;
        int repeat = 0;
        StringBuilder stringBuilder = new StringBuilder();
        if((length-2) > start){ //5자리 이상의 경우
            repeat = (length-2) - start;
            stringBuilder = repeatToHiddenChar(repeat,stringBuilder);
            sb.replace(start,(length-2),stringBuilder.toString()); //AB*DE
        }else if((length-1) > start){ //4자리 이상의 경우
            repeat = (length-1) - start;
            stringBuilder = repeatToHiddenChar(repeat,stringBuilder);
            sb.replace(start,length,stringBuilder.toString()); //AB*D
        }else if((length-1) == 2){
            //3자리의 경우
            repeat = length - start; //*
            stringBuilder = repeatToHiddenChar(repeat,stringBuilder);
            sb.replace(start,length,stringBuilder.toString()); //AB*
        }else{
            //2자리수 이하면 아무 처리도 하지 않고 그대로 보여준다
        }

        tv_id_findID.setText(sb);

        // 비밀번호 잊은 경우
        bt_forgotPW_findID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(getApplicationContext(), LL_FindID_D.class);
                mIntent.putExtra("user_id",user_id);
                startActivity(mIntent);
            }
        });
    }

    private StringBuilder repeatToHiddenChar(int repeat, StringBuilder sb){
        for (int i = 0; i < repeat; i++) {
            sb.append("*");
        }
        return sb;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

}
