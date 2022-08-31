package com.example.bubbly.tabFragments;


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

import com.example.bubbly.R;
import com.example.bubbly.SS_Profile;
import com.example.bubbly.controller.Reply_Adapter;
import com.example.bubbly.retrofit.ApiClient;
import com.example.bubbly.retrofit.ApiInterface;
import com.example.bubbly.retrofit.reply_Response;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Tab2_Replies_ss extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;

    View v;
    String uid;

    Reply_Adapter reply_adapter;
    ArrayList<reply_Response> replyList;
    LinearLayoutManager linearLayoutManager;
    RecyclerView recyclerView;
    private Parcelable recyclerViewState;
    SharedPreferences preferences;
    String user_id;

    public Fragment_Tab2_Replies_ss() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static Fragment_Tab2_Replies_ss newInstance(String param1, String param2) {
        Fragment_Tab2_Replies_ss fragment = new Fragment_Tab2_Replies_ss();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        uid = ((SS_Profile)getActivity()).getUid();
        Log.d("디버그태그", "user_id in post44"+uid);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // 레이아웃
        v = inflater.inflate(R.layout.fragment_profile_tab1, container, false);
        recyclerView = v.findViewById(R.id.tab_recyclerview);
        preferences = getActivity().getSharedPreferences("novarand",MODE_PRIVATE);

        selectCommentUsingCommentWriterId();
        return v;
    }


    public void selectCommentUsingCommentWriterId(){
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        //위치 유지
        recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();
        //위치 유지
        recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);

        replyList = new ArrayList<>();
        reply_adapter = new Reply_Adapter(getActivity().getApplicationContext(), replyList, getActivity().getApplicationContext() );
        recyclerView.setAdapter(reply_adapter);
        reply_adapter.notifyDataSetChanged();

        ApiInterface selectCommentUsingCommentWriterId_api = ApiClient.getApiClient().create(ApiInterface.class);
        Call<List<reply_Response>> call = selectCommentUsingCommentWriterId_api.selectCommentUsingCommentWriterId(uid);
        call.enqueue(new Callback<List<reply_Response>>()
        {
            @Override
            public void onResponse(@NonNull Call<List<reply_Response>> call, @NonNull Response<List<reply_Response>> response)
            {
                if (response.isSuccessful() && response.body() != null)
                {
                    List<reply_Response> responseResult = response.body();
                    for(int i=0; i<responseResult.size(); i++){;
                        replyList.add(new reply_Response(responseResult.get(i).getPost_id(),
                                responseResult.get(i).getComment_writer_id(),
                                responseResult.get(i).getComment_depth(),
                                responseResult.get(i).getComment_contents(),
                                responseResult.get(i).getNick_name(),
                                responseResult.get(i).getProfile_file_name(),
                                responseResult.get(i).getMentioned_user_list(),
                                responseResult.get(i).getCre_datetime_comment(),
                                responseResult.get(i).getComment_id()));
                    }
                    reply_adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<reply_Response>> call, @NonNull Throwable t)
            {
                Log.e("게시물 아이디로 게시물 조회", t.getMessage());
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
//        fillList();
    }
}
