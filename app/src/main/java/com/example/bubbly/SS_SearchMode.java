package com.example.bubbly;
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
import android.widget.ListView;

import com.example.bubbly.controller.Ranking_Adapter;
import com.example.bubbly.controller.RecentlySearched_Adapter;
import com.example.bubbly.model.Ranking_Item;
import com.example.bubbly.model.UserInfo;
import com.example.bubbly.retrofit.ApiClient;
import com.example.bubbly.retrofit.ApiInterface;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SS_SearchMode extends AppCompatActivity {

    Toolbar toolbar;

    EditText editText;
    String keywordset; //search result에서 다시 검색 버튼을 누르면 여기로 돌아오기 때문에, 이를 위해 keyword 수신

    ListView listView;
    ArrayList<String> recentlySearchedList;

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
        listView = findViewById(R.id.lv_recentlySearched);
        recentlySearchedList = new ArrayList<String>();
        // 리스트뷰 어답터 - 리스트뷰 연결
        final RecentlySearched_Adapter adapter = new RecentlySearched_Adapter(this, recentlySearchedList);
        listView.setAdapter(adapter);

        editText.setText(keywordset);

        editText.requestFocus();


        //최근 검색 키워드 가져오기
        preferences = getSharedPreferences("novarand",MODE_PRIVATE);
        String recentlySearched = preferences.getString("recentlySearched", "");
        try {
            JSONArray jsonArray = new JSONArray(recentlySearched);
            for(int i=0;i<jsonArray.length();i++){
                String recentlySearchedItem = jsonArray.getString(i);
                recentlySearchedList.add(recentlySearchedItem);
            }
            Collections.reverse(recentlySearchedList);
            adapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // 키보드 보이기
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        editText.setOnKeyListener((v, keyCode, event) -> {
            String keyword = editText.getText().toString();
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                user_id = UserInfo.user_id;


                //검색 키워드 저장하기 - 서버
                ApiInterface api = ApiClient.getApiClient().create(ApiInterface.class);
                Call<String> call = api.createSerarchText(user_id,keyword);
                call.enqueue(new Callback<String>()
                {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response)
                    {
                        if (response.isSuccessful() && response.body() != null)
                        {
                            //검색 결과 페이지 이동
                            Intent mIntent = new Intent(getApplicationContext(), SS_SearchResult.class);
                            mIntent.putExtra("keyword", keyword);
                            Log.i("정보태그", "xxx"+keyword);
                            startActivity(mIntent);
                            finish();

                            //검색 키워드 저장하기 - 로컬 (화면 이동 후 업데이트)
                            //만약 동일 키워드가 이미 존재하면 해당 키워드를 지우고 새로 추가
                            for(int i =0;i<recentlySearchedList.size();i++){
                                if(recentlySearchedList.get(i).equals(keyword)){
                                    recentlySearchedList.remove(i);
                                }
                            }
                            recentlySearchedList.add(keyword);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("recentlySearched", recentlySearchedList.toString());
                            editor.commit();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t)
                    {
                        Log.e("에러", t.getMessage());
                    }
                });


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