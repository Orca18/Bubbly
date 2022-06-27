package com.example.novarand_sns.tabFragments;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.novarand_sns.MM_Profile;
import com.example.novarand_sns.MM_Wallet;
import com.example.novarand_sns.R;
import com.example.novarand_sns.controller.Profile_Tab1_Adapter;
import com.example.novarand_sns.model.Fragment_Tab1_Item;

import java.util.ArrayList;
import java.util.List;

public class WalletFragment_Tab1_Activity extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;

    View v;
    private RecyclerView myrecyclerview;
    private List<Fragment_Tab1_Item> postsItem;

    String uid;

    public WalletFragment_Tab1_Activity() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static WalletFragment_Tab1_Activity newInstance(String param1, String param2) {
        WalletFragment_Tab1_Activity fragment = new WalletFragment_Tab1_Activity();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        uid = ((MM_Wallet)getActivity()).getUid();

        postsItem = new ArrayList<>();
//        fillList();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        // 레이아웃
        v = inflater.inflate(R.layout.fragment_profile_tab1, container, false);

//        swipeRefreshLayout = v.findViewById(R.id.refresh_notice);
//
//        swipeRefreshLayout.setOnRefreshListener(
//                new SwipeRefreshLayout.OnRefreshListener() {
//                    @Override
//                    public void onRefresh() {
//                        fillList();
//                        /* 업데이트가 끝났음을 알림 */
//                        swipeRefreshLayout.setRefreshing(false);
//                    }
//                });
        return v;
    }




//    private void fillList() {
//
//        // HttpUrlConnection
//        Thread th = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    //백그라운드 스레드에서는 메인화면을 변경 할 수 없음
//                    // runOnUiThread(메인 스레드영역)
//                    getActivity().runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            postsItem.add(new Fragment_Tab1_Item("테스트용입니다."));
//                            postsItem.add(new Fragment_Tab1_Item("테스트용입니다."));
//                            postsItem.add(new Fragment_Tab1_Item("테스트용입니다."));
//                            postsItem.add(new Fragment_Tab1_Item("테스트용입니다."));
//                            postsItem.add(new Fragment_Tab1_Item("테스트용입니다."));
//                            postsItem.add(new Fragment_Tab1_Item("테스트용입니다."));
//                            postsItem.add(new Fragment_Tab1_Item("테스트용입니다."));
//                            postsItem.add(new Fragment_Tab1_Item("테스트용입니다."));
//                            postsItem.add(new Fragment_Tab1_Item("테스트용입니다."));
//                            postsItem.add(new Fragment_Tab1_Item("테스트용입니다."));
//                            postsItem.add(new Fragment_Tab1_Item("테스트용입니다."));
//                            postsItem.add(new Fragment_Tab1_Item("테스트용입니다."));
//                            postsItem.add(new Fragment_Tab1_Item("테스트용입니다."));
//                            postsItem.add(new Fragment_Tab1_Item("테스트용입니다."));
//                            postsItem.add(new Fragment_Tab1_Item("테스트용입니다."));
//                            postsItem.add(new Fragment_Tab1_Item("테스트용입니다."));
//
//                            fill();
//
//                        }
//                    });
//                } catch (Exception e) {
//                    Log.i("tag", "error :" + e);
//                }
//            }
//        });
//        th.start();
//
//
//    }

//    private void fill() {
//        myrecyclerview = v.findViewById(R.id.fragment_profile_tab1_recyclerview);
//
//        Profile_Tab1_Adapter tab1_adapter = new Profile_Tab1_Adapter(getContext(), postsItem);
//        myrecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
//        myrecyclerview.setAdapter(tab1_adapter);
//    }


    @Override
    public void onResume() {
        super.onResume();
//        fillList();
    }
}
