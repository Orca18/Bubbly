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

import com.mainnet.bubbly.Kim_Bottom.Bottom4_Fragment;
import com.mainnet.bubbly.R;
import com.mainnet.bubbly.controller.NFT_Adapter;
import com.mainnet.bubbly.model.NFT_Item;
import com.mainnet.bubbly.model.UserInfo;
import com.mainnet.bubbly.retrofit.ApiClient;
import com.mainnet.bubbly.retrofit.ApiInterface;

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

        uid = Bottom4_Fragment.getUid();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        // 레이아웃
        v = inflater.inflate(R.layout.fragment_profile_tab1, container, false);
        recyclerView = v.findViewById(R.id.tab_recyclerview);
        selectNFT();

        return v;
    }

    private void selectNFT(){
        //nft결과값 customer toast로 띄우기 위해 adapter로 viewgroup 넘겨줌
        ViewGroup view = (ViewGroup) v.findViewById(android.R.id.content);
        recyclerView.setHasFixedSize(false); //nft마다 크기가 달라서 매번 다시 레이아웃 계산하도록 추가
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        //위치 유지
        recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();
        //위치 유지
        recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);

        list = new ArrayList<>();
        adapter = new NFT_Adapter(getActivity().getApplicationContext(), this.list, getActivity(),view);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        //전체 nft 목록 가져오기
        ApiInterface api = ApiClient.getApiClient(requireActivity()).create(ApiInterface.class);
        System.out.println("nfthold"+UserInfo.user_id);
        Call<List<NFT_Item>> call = api.selectNftUsingHolderId(UserInfo.user_id);
        call.enqueue(new Callback<List<NFT_Item>>() {
            @Override
            public void onResponse(Call<List<NFT_Item>> call, Response<List<NFT_Item>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<NFT_Item> responseResult = response.body();
                    for(int i=0; i<responseResult.size(); i++){
                        System.out.println("nft 보유 목록"+responseResult.get(i).getNft_id());
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
                Log.e("nft 보유목록 가져오기 실패", t.getMessage());
            }
        });
    }



    @Override
    public void onResume() {
        super.onResume();
//        fillList();
    }
}
