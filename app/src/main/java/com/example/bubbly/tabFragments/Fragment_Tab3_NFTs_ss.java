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
import com.example.bubbly.SS_Profile;
import com.example.bubbly.controller.NFTSell_Adapter;
import com.example.bubbly.controller.NFT_Adapter;
import com.example.bubbly.model.Fragment_Tab1_Item;
import com.example.bubbly.model.NFTSell_Item;
import com.example.bubbly.model.NFT_Item;
import com.example.bubbly.model.UserInfo;
import com.example.bubbly.retrofit.ApiClient;
import com.example.bubbly.retrofit.ApiInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Tab3_NFTs_ss extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;

    View v;
    LinearLayoutManager linearLayoutManager;
    RecyclerView recyclerView;
    private Parcelable recyclerViewState;
    private ArrayList<NFTSell_Item> list;
    private NFTSell_Adapter adapter;

    String uid;

    public Fragment_Tab3_NFTs_ss() {
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

        uid = ((SS_Profile)getActivity()).getUid();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        // 레이아웃
        v = inflater.inflate(R.layout.fragment_profile_tab1, container, false);
        recyclerView = v.findViewById(R.id.tab_recyclerview);

        selectNFTSell();

        return v;
    }

    private void selectNFTSell() {

        list = new ArrayList<>();
        adapter = new NFTSell_Adapter(getActivity().getApplicationContext(), this.list, getActivity());
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        //판매중인 nft 목록 가져오기
        ApiInterface api = ApiClient.getApiClient().create(ApiInterface.class);
        Call<List<NFTSell_Item>> call = api.selectSelledNftListUsingSellerId(uid);
        call.enqueue(new Callback<List<NFTSell_Item>>() {
            @Override
            public void onResponse(Call<List<NFTSell_Item>> call, Response<List<NFTSell_Item>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<NFTSell_Item> responseResult = response.body();
                    for (int i = 0; i < responseResult.size(); i++) {
                        System.out.println("nft 보유 목록"+responseResult.get(i).getNft_id());
                        list.add(new NFTSell_Item(responseResult.get(i).getNft_id(),
                                responseResult.get(i).getSeller_id(),
                                responseResult.get(i).getSell_price(),
                                responseResult.get(i).getApp_id(),
                                responseResult.get(i).getNft_des(),
                                responseResult.get(i).getNft_name(),
                                responseResult.get(i).getFile_save_url(),
                                responseResult.get(i).getNovaland_account_addr()));
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<NFTSell_Item>> call, Throwable t) {
                Log.e("nft 생성 실패", t.getMessage());
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
    }

}
