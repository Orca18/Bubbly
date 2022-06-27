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
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class WalletFragment_Tab3_ActivityHistory extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;

    View v;
    private RecyclerView myrecyclerview;
    private List<Fragment_Tab1_Item> postsItem;

    String uid;

    public WalletFragment_Tab3_ActivityHistory() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static WalletFragment_Tab3_ActivityHistory newInstance(String param1, String param2) {
        WalletFragment_Tab3_ActivityHistory fragment = new WalletFragment_Tab3_ActivityHistory();
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
//                    String page = "http://146.56.188.188/app/notice_list.php";
//                    // URL 객체 생성
//                    URL url = new URL(page);
//                    // 연결 객체 생성
//                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//
//                    // Post 파라미터
////                    String params = "ccode="+code+"&start="+start+"&list="+list;
//                    String params = "";
//                    // 결과값 저장 문자열
//                    final StringBuilder sb = new StringBuilder();
//
//                    // 연결되면
//                    if (conn != null) {
//                        Log.i("tag", "conn 연결");
//                        // 응답 타임아웃 설정
//                        conn.setRequestProperty("Accept", "application/json");
//                        conn.setConnectTimeout(10000);
//                        // POST 요청방식
//                        conn.setRequestMethod("POST");
//                        // 포스트 파라미터 전달
//                        conn.getOutputStream().write(params.getBytes("utf-8"));
//
//
//                        // url에 접속 성공하면 (200)
//                        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
//
//                            // 결과 값 읽어오는 부분
//                            BufferedReader br = new BufferedReader(new InputStreamReader(
//                                    conn.getInputStream(), "utf-8"
//                            ));
//                            String line;
//                            while ((line = br.readLine()) != null) {
//                                sb.append(line);
//                            }
//                            // 버퍼리더 종료
//                            br.close();
//                        }
//                        // 연결 끊기
//                        conn.disconnect();
//                    }
//
//                    //백그라운드 스레드에서는 메인화면을 변경 할 수 없음
//                    // runOnUiThread(메인 스레드영역)
//                    getActivity().runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            fillList2(sb.toString());
//
//                            Log.i("testt", "testt :" + sb.toString());
//
//                        }
//                    });
//                } catch (Exception e) {
//                    Log.i("tag", "error :" + e);
//                }
//            }
//        });
//        th.start();
//    }
//
//    private void fillList2(String memberinfo) {
//        JsonParser Parser = new JsonParser();
//        JsonObject jsonObj = (JsonObject) Parser.parse(memberinfo);
//        JsonArray info = (JsonArray) jsonObj.get("result");
//
//        postsItem = new ArrayList<>();
//
//        for (int i = 0; i < info.size(); i++) {
//            JsonObject object = (JsonObject) info.get(i);
//            // String imgurl = "http://146.56.188.188/app/images/";
//            postsItem.add(new Fragment_Tab1_Item("아아 테스트"));
//        }
//
//        setUpRecyclerView();
//    }
//
//    private void setUpRecyclerView() {
//        myrecyclerview = v.findViewById(R.id.fragment_profile_tab1_recyclerview);
//        Profile_Tab1_Adapter noticesAdapter = new Profile_Tab1_Adapter(getContext(), postsItem);
//        myrecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
//        myrecyclerview.setAdapter(noticesAdapter);
//    }


    @Override
    public void onResume() {
        super.onResume();
//        fillList();
    }
}
