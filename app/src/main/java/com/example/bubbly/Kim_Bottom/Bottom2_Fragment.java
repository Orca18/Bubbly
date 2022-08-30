package com.example.bubbly.Kim_Bottom;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;

import com.example.bubbly.R;
import com.example.bubbly.SS_SearchMode;
import com.example.bubbly.controller.Ranking_Adapter;
import com.example.bubbly.model.Ranking_Item;
import com.example.bubbly.retrofit.ApiClient;
import com.example.bubbly.retrofit.ApiInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Bottom2_Fragment extends Fragment {

    private View view;
    androidx.appcompat.widget.Toolbar toolbar;
    ImageView sidemenu;

    // 실시간 트렌드
    ListView listView;
    ArrayList<Ranking_Item> rankingList;
    ScrollView scrollView;

    // 검색을 위한 중앙 검색
    LinearLayout search;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.bottom2_frame, container, false);

        initialize();

        clickListeners();




        return view;
    }

    private void initialize() {
        toolbar = view.findViewById(R.id.issue_toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("");

        sidemenu = view.findViewById(R.id.issue_sidemenu);
        listView = view.findViewById(R.id.issue_ranking_listview);

        scrollView = view.findViewById(R.id.issue__scrollview);
        search = view.findViewById(R.id.action_search);
    }


    private void clickListeners() {
        // 좌측 상단 메뉴 버튼
        sidemenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        search.setOnClickListener(v -> {
            Intent mIntent = new Intent(getContext(), SS_SearchMode.class);
            mIntent.putExtra("keyword","");
            startActivity(mIntent);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        RankingList();
    }

    // 랭킹 리스트 채우기
    private void RankingList() {
        rankingList = new ArrayList<Ranking_Item>();
        // 리스트뷰 어답터 - 리스트뷰 연결
        final Ranking_Adapter adapter = new Ranking_Adapter(getContext(), rankingList);
        listView.setAdapter(adapter);

        //실시간 트랜드 데이터 가져오기
        //현재시간
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date now = new Date();
        String strNow = sdfDate.format(now);
        ApiInterface api = ApiClient.getApiClient().create(ApiInterface.class);
        Call<String> call = api.selectRealTimeTrends(strNow);
        call.enqueue(new Callback<String>()
        {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response)
            {
                if(response.body()!=null){
                    try {
                        JSONArray jsonArray = new JSONArray(response.body());
                        for(int i = 0; i<jsonArray.length(); i++){
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            int rank = i+1;
                            String keyword = jsonObject.getString("name");
                            int count = jsonObject.getInt("cnt");
                            Ranking_Item ri = new Ranking_Item(rank,keyword,count);
                            rankingList.add(ri);
                        }
                        adapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t)
            {
                Log.e("실시간 검색어 트랜드 에러", t.getMessage());
            }
        });

    }

}
