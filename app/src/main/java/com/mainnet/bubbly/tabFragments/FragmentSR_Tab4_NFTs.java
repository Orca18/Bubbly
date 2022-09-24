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
import com.mainnet.bubbly.controller.SearchedNFT_Adapter;
import com.mainnet.bubbly.model.NFTSearched_Item;
import com.mainnet.bubbly.model.NFTSell_Item;
import com.mainnet.bubbly.model.NFT_Item;
import com.mainnet.bubbly.model.SearchedUser_Item;
import com.mainnet.bubbly.model.UserInfo;
import com.mainnet.bubbly.retrofit.ApiClient;
import com.mainnet.bubbly.retrofit.ApiInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.swagger.annotations.Api;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentSR_Tab4_NFTs extends Fragment {
    View v;

    private SearchedNFT_Adapter adapter;
    private ArrayList<NFTSearched_Item> list;
    LinearLayoutManager linearLayoutManager;
    RecyclerView recyclerView;
    private Parcelable recyclerViewState;
    String keyword;

    public FragmentSR_Tab4_NFTs() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static FragmentSR_Tab4_NFTs newInstance(String param1, String param2) {
        FragmentSR_Tab4_NFTs fragment = new FragmentSR_Tab4_NFTs();
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
            //스와이프 리스너
        // 리사이클러뷰 새로고침 인식
        SwipeRefreshLayout swipeRefreshLayout = v.findViewById(R.id.refresh_searchResult);
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        selectNFTPost();
                        swipeRefreshLayout.setRefreshing(false);
                    }});
        return v;
    }

    public void selectNFTPost(){
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        //위치 유지
        recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();
        //위치 유지
        recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);

        ViewGroup view = (ViewGroup) v.findViewById(android.R.id.content);
        list = new ArrayList<>();
        adapter = new SearchedNFT_Adapter(getActivity().getApplicationContext(), this.list, getActivity(),view);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        ApiInterface api = ApiClient.getApiClient(requireActivity()).create(ApiInterface.class);
        Call<List<NFTSearched_Item>> call_nft = api.selectUserNftList(UserInfo.user_id,keyword);
        call_nft.enqueue(new Callback<List<NFTSearched_Item>>() {
            @Override
            public void onResponse(Call<List<NFTSearched_Item>> call, Response<List<NFTSearched_Item>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<NFTSearched_Item> responseResult = response.body();
                    for(int i=0; i<responseResult.size(); i++){
                        System.out.println("nft 보유 목록"+responseResult.get(i).getNft_id());
                        list.add(new NFTSearched_Item(responseResult.get(i).getProfileImageURL(),responseResult.get(i).getUserName(),responseResult.get(i).getLoginId(),
                                responseResult.get(i).getUserId(),responseResult.get(i).getNft_id(),responseResult.get(i).getHolder_id(),
                                responseResult.get(i).getCreation_time(), responseResult.get(i).getIsSell(),responseResult.get(i).getSeller_id(),
                                responseResult.get(i).getSell_price(),responseResult.get(i).getApp_id(),responseResult.get(i).getNft_des(),
                                responseResult.get(i).getNft_name(), responseResult.get(i).getFile_save_url(),responseResult.get(i).getNovaland_account_addr()));
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<NFTSearched_Item>> call, Throwable t) {
                Log.e("nft 보유목록 가져오기 실패", t.getMessage());
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        selectNFTPost();
    }

}
