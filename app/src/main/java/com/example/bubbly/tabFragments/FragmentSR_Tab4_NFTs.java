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
import com.example.bubbly.model.UserInfo;
import com.example.bubbly.retrofit.ApiClient;
import com.example.bubbly.retrofit.ApiInterface;
import com.example.bubbly.retrofit.post_Response;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentSR_Tab4_NFTs extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;

    View v;

    Post_Adapter post_adapter;
    ArrayList<post_Response> postList;
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

        postList = new ArrayList<>();
        post_adapter = new Post_Adapter(getActivity().getApplicationContext() , postList,getActivity().getApplicationContext(),getActivity() );
        recyclerView.setAdapter(post_adapter);
        post_adapter.notifyDataSetChanged();


        ApiInterface api = ApiClient.getApiClient(requireActivity()).create(ApiInterface.class);
        Call<List<post_Response>> call = api.selectPostUsingPostContents(keyword, UserInfo.user_id);
        call.enqueue(new Callback<List<post_Response>>()
        {
            @Override
            public void onResponse(@NonNull Call<List<post_Response>> call, @NonNull Response<List<post_Response>> response)
            {
                if (response.isSuccessful() && response.body() != null)
                {
                    List<post_Response> responseResult = response.body();
                    for(int i=0; i<responseResult.size(); i++){;
                        if(responseResult.get(i).getNft_post_yn()!=null){//nft가 빈칸이 아니면=nft데이터만 표시한다
                            postList.add(new post_Response(responseResult.get(i).getPost_id(),
                                    responseResult.get(i).getPost_writer_id(),
                                    responseResult.get(i).getWriter_name(),
                                    responseResult.get(i).getPost_contents(),
                                    responseResult.get(i).getFile_save_names(),
                                    responseResult.get(i).getLike_count(),
                                    responseResult.get(i).getLike_yn(),
                                    responseResult.get(i).getShare_post_yn(),
                                    responseResult.get(i).getNft_post_yn(),
                                    responseResult.get(i).getNick_name(),
                                    responseResult.get(i).getProfile_file_name(),
                                    responseResult.get(i).getCre_datetime(),
                                    responseResult.get(i).getMentioned_user_list(),
                                    responseResult.get(i).getCommunity_id(),
                                    responseResult.get(i).getLogin_id(),
                                    responseResult.get(i).getPost_type()));
                        }
                    }
                    post_adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<post_Response>> call, @NonNull Throwable t)
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
