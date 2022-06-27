package com.example.novarand_sns.tabFragments;

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
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.novarand_sns.Following;
import com.example.novarand_sns.R;
import com.example.novarand_sns.controller.Follower_Adapter;
import com.example.novarand_sns.controller.Following_Adapter;
import com.example.novarand_sns.retrofit.ApiClient;
import com.example.novarand_sns.retrofit.ApiInterface;
import com.example.novarand_sns.retrofit.follower_Response;
import com.example.novarand_sns.retrofit.following_Response;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Tab2_following extends Fragment {

    View v;
    String uid;

    Following_Adapter following_adapter;
    ArrayList<following_Response> followingList;
    LinearLayoutManager linearLayoutManager;
    RecyclerView recyclerView;
    private Parcelable recyclerViewState;
    SharedPreferences preferences;
    String user_id;

    public Fragment_Tab2_following() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static Fragment_Tab2_following newInstance(String param1, String param2) {
        Fragment_Tab2_following fragment = new Fragment_Tab2_following();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uid = ((Following) getActivity()).getUid();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_profile_tab1, container, false);

        recyclerView = v.findViewById(R.id.tab_recyclerview);
        preferences = getActivity().getSharedPreferences("novarand",MODE_PRIVATE);

        selectFolloweeList();

        return v;
    }

    public void selectFolloweeList(){
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        //위치 유지
        recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();
        //위치 유지
        recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity().getApplicationContext(), DividerItemDecoration.VERTICAL));

        followingList = new ArrayList<>();
        following_adapter = new Following_Adapter(getActivity().getApplicationContext() , followingList,getActivity().getApplicationContext() );
        recyclerView.setAdapter(following_adapter);
        following_adapter.notifyDataSetChanged();
        user_id = preferences.getString("user_id", ""); // 로그인한 user_id값

        ApiInterface selectFolloweeList_api = ApiClient.getApiClient().create(ApiInterface.class);
        Call<List<following_Response>> call = selectFolloweeList_api.selectFolloweeList(user_id);
        call.enqueue(new Callback<List<following_Response>>()
        {
            @Override
            public void onResponse(@NonNull Call<List<following_Response>> call, @NonNull Response<List<following_Response>> response)
            {
                if (response.isSuccessful() && response.body() != null)
                {
                    List<following_Response> responseResult = response.body();
                    for(int i=0; i<responseResult.size(); i++){;
                        followingList.add(new following_Response(responseResult.get(i).getFollowee_id(),
                                responseResult.get(i).getNick_name(),
                                responseResult.get(i).getProfile_file_name()));
                    }
                    following_adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<following_Response>> call, @NonNull Throwable t)
            {
                Log.e("에러", t.getMessage());
            }
        });
    }




}
