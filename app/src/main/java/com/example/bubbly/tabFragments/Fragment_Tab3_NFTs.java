package com.example.bubbly.tabFragments;


import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.bubbly.MM_Profile;
import com.example.bubbly.R;
import com.example.bubbly.controller.JoinedCom_Adapter;
import com.example.bubbly.controller.NFT_Adapter;
import com.example.bubbly.kim_util_test.Kim_JoinedCom_Response;
import com.example.bubbly.model.Fragment_Tab1_Item;
import com.example.bubbly.model.NFT_Item;
import com.example.bubbly.model.UserInfo;
import com.example.bubbly.retrofit.ApiClient;
import com.example.bubbly.retrofit.ApiInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Tab3_NFTs extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;

    View v;
    LinearLayoutManager linearLayoutManager;
    RecyclerView recyclerView;
    private Parcelable recyclerViewState;
    private ArrayList<NFT_Item> list;
    private ArrayList<NFT_Item> list_sell;
    private NFT_Adapter adapter;

    String uid;

    public Fragment_Tab3_NFTs() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static Fragment_Tab3_NFTs newInstance(String param1, String param2) {
        Fragment_Tab3_NFTs fragment = new Fragment_Tab3_NFTs();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        uid = ((MM_Profile)getActivity()).getUid();


//        fillList();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        // 레이아웃
        v = inflater.inflate(R.layout.fragment_profile_tab3, container, false);
        recyclerView = v.findViewById(R.id.tab_recyclerview_nft);

        selectNFT();

        return v;
    }

    private void selectNFT(){

        list = new ArrayList<>();
        adapter = new NFT_Adapter(getActivity().getApplicationContext(), this.list, getActivity());
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        //전체 nft 목록 가져오기
        ApiInterface api = ApiClient.getApiClient().create(ApiInterface.class);
        Call<List<NFT_Item>> call = api.selectNftUsingHolderId(UserInfo.user_id);
        call.enqueue(new Callback<List<NFT_Item>>() {
            @Override
            public void onResponse(Call<List<NFT_Item>> call, Response<List<NFT_Item>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    System.out.println("nft 보유 목록"+response.body());
                    List<NFT_Item> responseResult = response.body();
                    for(int i=0; i<responseResult.size(); i++){
                        list.add(new NFT_Item(responseResult.get(i).getNft_id(),
                                responseResult.get(i).getNft_des(),
                                responseResult.get(i).getNft_name(),
                                responseResult.get(i).getHolder_id(),
                                responseResult.get(i).getFile_save_url()));
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<NFT_Item>> call, Throwable t) {
                Log.e("nft 생성 실패", t.getMessage());
            }
        });
    }



    @Override
    public void onResume() {
        super.onResume();
//        fillList();
    }
}
