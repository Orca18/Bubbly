package com.mainnet.bubbly.Kim_Bottom;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
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

import com.mainnet.bubbly.MainActivity;
import com.mainnet.bubbly.R;
import com.mainnet.bubbly.SS_SearchMode;
import com.mainnet.bubbly.chatting.util.GetDate;
import com.mainnet.bubbly.controller.Ranking_Adapter;
import com.mainnet.bubbly.controller.Searched_Adapter_Callback;
import com.mainnet.bubbly.model.Ranking_Item;
import com.mainnet.bubbly.model.UserInfo;
import com.mainnet.bubbly.retrofit.ApiClient;
import com.mainnet.bubbly.retrofit.ApiInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

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


    Searched_Adapter_Callback callback = new Searched_Adapter_Callback() {
        @Override
        public void updateListRecentlySearched(String keyword) {
            //검색 키워드 저장하기 - 서버
            ApiInterface api = ApiClient.getApiClient(requireActivity()).create(ApiInterface.class);
            Call<String> call = api.createSerarchText(UserInfo.user_id,keyword);
            call.enqueue(new Callback<String>()
            {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response)
                {
                    if (response.isSuccessful() && response.body() != null)
                    {
                        ArrayList<String> recentlySearchedList = new ArrayList<String>();
                        SharedPreferences preferences = getActivity().getSharedPreferences("novarand",MODE_PRIVATE);
                        String recentlySearched = preferences.getString("recentlySearched", "");
                        try {
                            JSONArray jsonArray = new JSONArray(recentlySearched);
                            for(int i=0;i<jsonArray.length();i++){
                                String recentlySearchedItem = jsonArray.getString(i);
                                recentlySearchedList.add(recentlySearchedItem);
                            }
                            //리스트뷰 역순으로 보이게 하기(최신 검색어가 맨 상위로)
                            Collections.reverse(recentlySearchedList);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //역순으로 보여주었던 것을 저장시 올바른 순서로 저장하기 위해서 다시 역순(원래 순서)로 재배치
                        Collections.reverse(recentlySearchedList);
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
                public void onFailure(Call<String> call, Throwable t) {
                    Log.e("에러", t.getMessage());
                }
            });
        }
    };

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
                MainActivity.drawerLayout.openDrawer(GravityCompat.START);
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
        final Ranking_Adapter adapter = new Ranking_Adapter(getContext(), rankingList, callback);
        listView.setAdapter(adapter);

        //실시간 트랜드 데이터 가져오기
        //현재시간
        String strNow = GetDate.getTodayDateWithTime();
        ApiInterface api = ApiClient.getApiClient(requireActivity()).create(ApiInterface.class);
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
