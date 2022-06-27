package com.example.novarand_sns;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.novarand_sns.R;
import com.example.novarand_sns.retrofit.ApiClient;
import com.example.novarand_sns.retrofit.ApiInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReplyModify extends AppCompatActivity {

    ImageView iv_back;
    TextView tv_add;
    EditText et_reply_modify;

    String comment_id,comment_content,comment_mention,post_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply_modify);

        iv_back = findViewById(R.id.iv_back);
        tv_add = findViewById(R.id.tv_add);
        et_reply_modify = findViewById(R.id.et_reply_modify);

        Intent intent = getIntent();
        comment_id = intent.getStringExtra("comment_id");
        comment_content = intent.getStringExtra("comment_content");
        comment_mention = intent.getStringExtra("comment_mention");
        post_id = intent.getStringExtra("post_id");

        et_reply_modify.setText(comment_content);

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateComment();
            }
        });

    }


    public void updateComment(){
        ApiInterface updateComment_api = ApiClient.getApiClient().create(ApiInterface.class);
        Call<String> call = updateComment_api.updateComment("13",et_reply_modify.getText().toString(),comment_mention,comment_mention,comment_mention);
        call.enqueue(new Callback<String>()
        {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response)
            {
                if (response.isSuccessful() && response.body() != null)
                {
                    Intent intent = new Intent(getApplicationContext(), SS_PostDetail.class);
                    intent.putExtra("post_id",post_id);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t)
            {
                Log.e("에러", t.getMessage());
            }
        });
    }


}