package com.mainnet.bubbly.tabFragments;


import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
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
import com.mainnet.bubbly.SS_Profile;
import com.mainnet.bubbly.controller.Post_Adapter;
import com.mainnet.bubbly.retrofit.ApiClient;
import com.mainnet.bubbly.retrofit.ApiInterface;
import com.mainnet.bubbly.retrofit.post_Response;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Tab4_Likes_ss extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;

    View v;
    String uid;

    Post_Adapter post_adapter;
    ArrayList<post_Response> postList;
    LinearLayoutManager linearLayoutManager;
    RecyclerView recyclerView;
    private Parcelable recyclerViewState;
    SharedPreferences preferences;
    String user_id;

    public Fragment_Tab4_Likes_ss() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static Fragment_Tab4_Likes_ss newInstance(String param1, String param2) {
        Fragment_Tab4_Likes_ss fragment = new Fragment_Tab4_Likes_ss();
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
        // ????????????
        v = inflater.inflate(R.layout.fragment_profile_tab1, container, false);
        recyclerView = v.findViewById(R.id.tab_recyclerview);
        preferences = getActivity().getSharedPreferences("novarand",MODE_PRIVATE);

        selectLikePost();
        return v;
    }


    public void selectLikePost(){
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        //?????? ??????
        recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();
        //?????? ??????
        recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);

        postList = new ArrayList<>();
        post_adapter = new Post_Adapter(getActivity().getApplicationContext() , postList,getActivity().getApplicationContext(),getActivity() );
        recyclerView.setAdapter(post_adapter);
        post_adapter.notifyDataSetChanged();

        user_id = preferences.getString("user_id", ""); // ???????????? user_id???

        ApiInterface selectLikedPostUsingUserId_api = ApiClient.getApiClient(requireActivity()).create(ApiInterface.class);
        Call<List<post_Response>> call = selectLikedPostUsingUserId_api.selectLikedPostUsingUserId(uid);

        call.enqueue(new Callback<List<post_Response>>()
        {
            @Override
            public void onResponse(@NonNull Call<List<post_Response>> call, @NonNull Response<List<post_Response>> response)
            {
                if (response.isSuccessful() && response.body() != null)
                {
                    List<post_Response> responseResult = response.body();
                    for(int i=0; i<responseResult.size(); i++){;
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
                    post_adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<post_Response>> call, @NonNull Throwable t)
            {
                Log.e("????????? ???????????? ????????? ??????", t.getMessage());
            }
        });
    }








    @Override
    public void onResume() {
        super.onResume();
    }
}
