package com.mainnet.bubbly.tabFragments;


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

import com.mainnet.bubbly.R;
import com.mainnet.bubbly.SS_Profile;
import com.mainnet.bubbly.controller.NFTSell_Adapter;
import com.mainnet.bubbly.controller.NFT_ss_Adapter;
import com.mainnet.bubbly.model.NFTSell_Item;
import com.mainnet.bubbly.model.NFT_Item;
import com.mainnet.bubbly.model.UserInfo;
import com.mainnet.bubbly.retrofit.ApiClient;
import com.mainnet.bubbly.retrofit.ApiInterface;

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
    private NFT_ss_Adapter adapter;

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
        ViewGroup view = (ViewGroup) v.findViewById(android.R.id.content);
        list = new ArrayList<>();
        adapter = new NFT_ss_Adapter(getActivity().getApplicationContext(), this.list, getActivity(),view);
        recyclerView.setHasFixedSize(false);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        // 보유한 nft 목록을 가져온다
        ApiInterface api = ApiClient.getApiClient(requireActivity()).create(ApiInterface.class);
        System.out.println("nfthold"+ UserInfo.user_id);
        Call<List<NFT_Item>> call = api.selectNftUsingHolderId(UserInfo.user_id);
        call.enqueue(new Callback<List<NFT_Item>>() {
            @Override
            public void onResponse(Call<List<NFT_Item>> call, Response<List<NFT_Item>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<NFT_Item> responseResult = response.body();
                    for(int i=0; i<responseResult.size(); i++){
                        System.out.println("nft 보유 목록"+responseResult.get(i).getNft_id());
                        list.add(new NFTSell_Item(responseResult.get(i).getNft_id(),
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
                Log.e("nft 보유목록 가져오기 실패", t.getMessage());
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
    }

}
