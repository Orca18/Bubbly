package com.mainnet.bubbly.tabFragments;

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

import com.mainnet.bubbly.R;
import com.mainnet.bubbly.SS_SearchResult;
import com.mainnet.bubbly.controller.SearchedUser_Adapter;
import com.mainnet.bubbly.model.SearchedUser_Item;
import com.mainnet.bubbly.model.UserInfo;
import com.mainnet.bubbly.retrofit.ApiClient;
import com.mainnet.bubbly.retrofit.ApiInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentSR_Tab3_Users extends Fragment {

    View v;

    SearchedUser_Adapter adapter;
    ArrayList<SearchedUser_Item> userList;
    LinearLayoutManager linearLayoutManager;
    RecyclerView recyclerView;
    private Parcelable recyclerViewState;
    String keyword;

    public FragmentSR_Tab3_Users() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static FragmentSR_Tab3_Users newInstance(String param1, String param2) {
        FragmentSR_Tab3_Users fragment = new FragmentSR_Tab3_Users();
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

        // ????????????
        v = inflater.inflate(R.layout.fragment_ss_search_result, container, false);
        recyclerView = v.findViewById(R.id.rv_searchResult);
        //???????????? ?????????
        // ?????????????????? ???????????? ??????
        SwipeRefreshLayout swipeRefreshLayout = v.findViewById(R.id.refresh_searchResult);
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        selectUser();
                        swipeRefreshLayout.setRefreshing(false);
                    }});
        return v;
    }

    public void selectUser(){
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        //?????? ??????
        recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();
        //?????? ??????
        recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);

        userList = new ArrayList<>();
        adapter = new SearchedUser_Adapter(userList, getActivity().getApplicationContext() );
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();


        ApiInterface api = ApiClient.getApiClient(requireActivity()).create(ApiInterface.class);
        Call<String> call = api.selectUserSearchResultList(UserInfo.user_id,keyword);
        call.enqueue(new Callback<String>()
        {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response)
            {
                if (response.isSuccessful() && response.body() != null)
                {
                    try {
                        JSONArray jsonArray = new JSONArray(response.body().toString());
                        for(int i=0; i<jsonArray.length(); i++){
                            String jsonString = jsonArray.getString(i);
                            JSONObject jsonObject = new JSONObject(jsonString);
                            userList.add(new SearchedUser_Item(jsonObject.getString("user_id"),
                                    jsonObject.getString("nick_name"),
                                    jsonObject.getString("profile_file_name"),
                                    jsonObject.getString("login_id"),
                                    jsonObject.getString("self_info")));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t)
            {
                Log.e("????????? ???????????? ????????? ??????", t.getMessage());
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        selectUser();
    }

}

