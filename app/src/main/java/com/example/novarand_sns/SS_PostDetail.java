package com.example.novarand_sns;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.novarand_sns.controller.Post_Adapter;
import com.example.novarand_sns.controller.Reply_Adapter;
import com.example.novarand_sns.model.Reply_Item;
import com.example.novarand_sns.retrofit.ApiClient;
import com.example.novarand_sns.retrofit.ApiInterface;
import com.example.novarand_sns.retrofit.post_Response;
import com.example.novarand_sns.retrofit.reply_Response;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SS_PostDetail extends AppCompatActivity {

    SharedPreferences preferences;
    String user_id,post_id;

    ImageView iv_user_image,iv_media;
    TextView tv_user_nick,tv_user_id,tv_content,tv_time,tv_like_count,tv_reply_count,tv_retweet_count;
    EditText et_reply;
    Button bt_reply_add;
    Toolbar toolbar;

    RecyclerView recyclerView;
    Reply_Adapter reply_adapter;
    ArrayList<reply_Response> replyList;
    LinearLayoutManager linearLayoutManager;
    private Parcelable recyclerViewState;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_details);

        toolbar = findViewById(R.id.post_details_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        iv_user_image = findViewById(R.id.iv_user_image);
        iv_media = findViewById(R.id.iv_media);

        tv_user_nick = findViewById(R.id.tv_user_nick);
        tv_user_id = findViewById(R.id.tv_user_id);
        tv_content = findViewById(R.id.tv_content);
        tv_time = findViewById(R.id.tv_time);
        tv_like_count = findViewById(R.id.tv_like_count);
        tv_reply_count = findViewById(R.id.tv_reply_count);
        tv_retweet_count = findViewById(R.id.tv_retweet_count);

        et_reply = findViewById(R.id.et_reply);
        bt_reply_add = findViewById(R.id.bt_reply_add);

        recyclerView = findViewById(R.id.post_details_recyclerview);

        Intent intent = getIntent();
        post_id = intent.getStringExtra("post_id");

        preferences = getSharedPreferences("novarand",MODE_PRIVATE);
        user_id = preferences.getString("user_id", ""); // 로그인한 user_id값

        bt_reply_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createComment();
            }
        });

        selectCommentUsingPostId(); // 게시글 아이디로 댓글 조회
        selectPostUsingPostId(); // 게시글 아이디로 조회

    } // onCreate 닫는곳

    public void selectCommentUsingPostId(){ // 게시글 아이디로 댓글 조회
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        //위치 유지
        recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();
        //위치 유지
        recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);

        replyList = new ArrayList<>();
        reply_adapter = new Reply_Adapter(getApplicationContext(), this.replyList,getApplicationContext());
        recyclerView.setAdapter(reply_adapter);
        reply_adapter.notifyDataSetChanged();

        ApiInterface selectCommentUsingPostId_api = ApiClient.getApiClient().create(ApiInterface.class);
        Call<List<reply_Response>> call = selectCommentUsingPostId_api.selectCommentUsingPostId(post_id);
        call.enqueue(new Callback<List<reply_Response>>()
        {
            @Override
            public void onResponse(@NonNull Call<List<reply_Response>> call, @NonNull Response<List<reply_Response>> response)
            {
                if (response.isSuccessful() && response.body() != null)
                {
//                    progressBar.setVisibility(View.GONE);
                    List<reply_Response> responseResult = response.body();
                    for(int i=0; i<responseResult.size(); i++){;
                        replyList.add(new reply_Response(responseResult.get(i).getPost_id(),
                                responseResult.get(i).getComment_writer_id(),
                                responseResult.get(i).getComment_depth(),
                                responseResult.get(i).getComment_contents(),
                                responseResult.get(i).getNick_name(),
                                responseResult.get(i).getProfile_file_name(),
                                responseResult.get(i).getMentioned_user_list()));
                    }
                    reply_adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<reply_Response>> call, @NonNull Throwable t)
            {
                Log.e("게시물 아이디로 게시물 조회", t.getMessage());
            }
        });
    }

    public void createComment(){ // 댓글 생성
        ApiInterface createComment_api = ApiClient.getApiClient().create(ApiInterface.class);
        Call<String> call = createComment_api.createComment(post_id,"1",user_id,et_reply.getText().toString(),"!");
        call.enqueue(new Callback<String>()
        {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response)
            {
                if (response.isSuccessful() && response.body() != null)
                {
                    Log.e("createComment 성공",response.body().toString());
                    selectCommentUsingPostId();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t)
            {
                Log.e("createComment 에러", t.getMessage());
            }
        });
    }

    public void selectPostUsingPostId(){ // 게시글 아이디로 조회
        ApiInterface selectPostUsingPostId_api = ApiClient.getApiClient().create(ApiInterface.class);
        Call<List<post_Response>> call = selectPostUsingPostId_api.selectPostUsingPostId(post_id,user_id);
        call.enqueue(new Callback<List<post_Response>>()
        {
            @Override
            public void onResponse(@NonNull Call<List<post_Response>> call, @NonNull Response<List<post_Response>> response)
            {
                if (response.isSuccessful() && response.body() != null)
                {
                    List<post_Response> responseResult = response.body();
                    tv_user_id.setText(responseResult.get(0).getPost_writer_id());
                    tv_user_nick.setText(responseResult.get(0).getNick_name());
                    tv_content.setText(responseResult.get(0).getPost_contents());
                    tv_like_count.setText(responseResult.get(0).getLike_count());
                    tv_time.setText(responseResult.get(0).getCre_datetime());

                    Glide.with(SS_PostDetail.this)
                            .load("https://d2gf68dbj51k8e.cloudfront.net/"+responseResult.get(0).getFile_save_names())
                            .into(iv_media);

                    Glide.with(SS_PostDetail.this)
                            .load("https://d2gf68dbj51k8e.cloudfront.net/e3b15554f15354b5bc31e3e535a59d70.jpeg")
                            .circleCrop()
                            .into(iv_user_image);

                }
            }

            @Override
            public void onFailure(@NonNull Call<List<post_Response>> call, @NonNull Throwable t)
            {
                Log.e("에러", t.getMessage());
            }
        });
    }








    ////////////////////////////////////////////////////


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