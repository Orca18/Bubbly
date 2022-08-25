package com.example.bubbly.tabFragments;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.bubbly.R;
import com.example.bubbly.SS_SearchResult;
import com.example.bubbly.controller.Post_Adapter;
import com.example.bubbly.controller.SearchedCom_Adapter;
import com.example.bubbly.kim_util_test.Kim_JoinedCom_Response;
import com.example.bubbly.model.UserInfo;
import com.example.bubbly.retrofit.ApiClient;
import com.example.bubbly.retrofit.ApiInterface;
import com.example.bubbly.retrofit.post_Response;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentSR_Tab5_Community extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;

    View v;

    SearchedCom_Adapter adapter;
    ArrayList<Kim_JoinedCom_Response> list;
    LinearLayoutManager linearLayoutManager;
    RecyclerView recyclerView;
    private Parcelable recyclerViewState;
    String keyword;

    public FragmentSR_Tab5_Community() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static FragmentSR_Tab5_Community newInstance(String param1, String param2) {
        FragmentSR_Tab5_Community fragment = new FragmentSR_Tab5_Community();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        uid = ((SS_SearchResult) getActivity()).getUid();
        keyword = ((SS_SearchResult) getActivity()).getKeyword();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // 레이아웃
        v = inflater.inflate(R.layout.fragment_ss_search_result, container, false);
        recyclerView = v.findViewById(R.id.rv_searchResult);

        selectNFTPost();
        return v;
    }

    public void selectNFTPost(){
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        //위치 유지
        recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();
        //위치 유지
        recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);

        list = new ArrayList<>();
        adapter = new SearchedCom_Adapter(getActivity().getApplicationContext() , list );
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();


        ApiInterface api = ApiClient.getApiClient().create(ApiInterface.class);
        Call<List<Kim_JoinedCom_Response>> call = api.selectCommunitySearchResultList(keyword);
        call.enqueue(new Callback<List<Kim_JoinedCom_Response>>()
        {
            @Override
            public void onResponse(@NonNull Call<List<Kim_JoinedCom_Response>> call, @NonNull Response<List<Kim_JoinedCom_Response>> response)
            {
                if (response.isSuccessful() && response.body() != null)
                {
                    List<Kim_JoinedCom_Response> responseResult = response.body();
                    // 결과
                    for(int i=0; i<responseResult.size(); i++){
                        list.add(new Kim_JoinedCom_Response(responseResult.get(i).getCommunity_id(),
                                responseResult.get(i).getCommunity_owner_id(),
                                responseResult.get(i).getCommunity_name(),
                                responseResult.get(i).getCommunity_desc(),
                                responseResult.get(i).getProfile_file_name()));
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Kim_JoinedCom_Response>> call, @NonNull Throwable t)
            {
                Log.e("게시물 아이디로 게시물 조회", t.getMessage());
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

}

