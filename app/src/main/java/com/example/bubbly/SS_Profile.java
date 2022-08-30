package com.example.bubbly;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.bubbly.chatting.service.ChatService;
import com.example.bubbly.chatting.util.ChatUtil;
import com.example.bubbly.config.Config;
import com.example.bubbly.controller.FragmentAdapter_SS;
import com.example.bubbly.model.Chat_Member_FCM_Sub;
import com.example.bubbly.model.Chat_Room_Cre_Or_Del;
import com.example.bubbly.model.OtherUserInfo;
import com.example.bubbly.model.UserInfo;
import com.example.bubbly.retrofit.ApiClient;
import com.example.bubbly.retrofit.ApiInterface;
import com.example.bubbly.retrofit.ChatApiClient;
import com.example.bubbly.retrofit.ChatApiInterface;
import com.example.bubbly.retrofit.follower_Response;
import com.example.bubbly.retrofit.following_Response;
import com.example.bubbly.retrofit.user_Response;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SS_Profile extends AppCompatActivity {

    // 뒤로가기 시간
    private long backKeyPressedTime = 0;
    private Toast toast;

    // 바텀 메뉴
    LinearLayout bthome, btissue, btwallet, btmessage, btprofile;

    // 툴바, 사이드 메뉴
    androidx.appcompat.widget.Toolbar toolbar;
    TextView tv_title;
    ImageView bt_search;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageView sidemenu;

    // 새로고침, 프로그레스바
    SwipeRefreshLayout swipeRefreshLayout;
    ProgressBar progressBar;

    // 탭 레이아웃
    TabLayout tabLayout;
    ViewPager2 pager2;
    FragmentAdapter_SS adapter;

    String uid;

    //프로필 데이터 보여주기
    ImageView iv_user_image,iv_chat;
    TextView tv_user_nick, tv_user_id, tv_user_intro, tv_following,tv_follower;
    LinearLayout bt_following, bt_unfollowing; //팔로잉 버튼
    
    // 사용자 정보
    String user_id ;
    String login_id;
    String email_addr;
    String novaland_account_addr;
    String phone_num;
    String user_nick;
    String self_info;
    String token;
    String profile_file_name;
    String profile_file_name_for_chat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sub_profile);

        // 유저 아이디 uid 받음
        Bundle extras = getIntent().getExtras();
        uid = extras.getString("user_id");
        System.out.println("user_id in post"+uid);

        // (기본) 리소스 ID 선언
        initiallize();
        // (추가) 탭 레이아웃
        tabInit();
        // 클릭 리스너 모음 - 스택 O
        clickListeners();
    }


    //프로필데이터 가져와서 디스플레이하기
    private void setProfileData(){
        //회원정보를 요청한다.
        ApiInterface api = ApiClient.getApiClient().create(ApiInterface.class);
        Call<List<user_Response>> call_userInfo = api.selectUserInfo(uid);
        call_userInfo.enqueue(new Callback<List<user_Response>>()
        {
            @Override
            public void onResponse(@NonNull Call<List<user_Response>> call, @NonNull Response<List<user_Response>> response) {
                System.out.println(response.body());
                List<user_Response> responseResult = response.body();
                user_id = responseResult.get(0).getUser_id();
                login_id = responseResult.get(0).getLogin_id();
                email_addr = responseResult.get(0).getEmail_addr();
                novaland_account_addr = responseResult.get(0).getNovaland_account_addr();
                phone_num = responseResult.get(0).getPhone_num();
                user_nick = responseResult.get(0).getUser_nick();
                self_info = responseResult.get(0).getSelf_info();
                token = responseResult.get(0).getToken();
                profile_file_name_for_chat = responseResult.get(0).getProfile_file_name();
                profile_file_name = "";
                if (responseResult.get(0).getProfile_file_name() != null && !responseResult.get(0).getProfile_file_name().equals("")) {
                    profile_file_name = Config.cloudfront_addr + responseResult.get(0).getProfile_file_name();
                }
                System.out.println(uid);
                //이미지. 닉네임, id, 자기소개, 팔로잉, 팔로워
                if (profile_file_name != null && !profile_file_name.equals("")) {
                    Glide.with(SS_Profile.this)
                            .load(profile_file_name)
                            .circleCrop()
                            .into(iv_user_image);
                } else {
                    //아무런 처리하지 않음. 레이아웃에 설정된 default 값 표시
                }

                if (user_nick != null && !user_nick.equals("")) {
                    tv_user_nick.setText(user_nick);
                } else {
                    tv_user_nick.setText(login_id);
                }

                tv_user_id.setText(login_id);

                if (self_info != null && !self_info.equals("")) {
                    tv_user_intro.setText(self_info);
                } else {
                    tv_user_intro.setText("");
                }
                //툴바 타이틀
                tv_title.setText(user_nick);

                //팔로워리스트 가져오기
                ApiInterface selectFollowerList_api = ApiClient.getApiClient().create(ApiInterface.class);
                Call<List<follower_Response>> call_follower = selectFollowerList_api.selectFollowerList(user_id);
                call_follower.enqueue(new Callback<List<follower_Response>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<follower_Response>> call, @NonNull Response<List<follower_Response>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<follower_Response> responseResult = response.body();
                            //만약 그 사람을 팔로잉하는 사람 중에 내가 있으면 팔로우 버튼을 언팔로우로 바꾼다.
                            for(int i = 0; i<responseResult.size(); i++){
                                System.out.println(responseResult.get(i).getFollower_id());
                                if(UserInfo.user_id.equals(responseResult.get(i).getFollower_id())){
                                    bt_following.setVisibility(View.GONE);
                                    bt_unfollowing.setVisibility(View.VISIBLE);
                                    break;
                                }
                            }
                            tv_follower.setText("" + responseResult.size());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<follower_Response>> call, @NonNull Throwable t) {
                        Log.e("에러", t.getMessage());
                    }
                });

                //팔로잉 리스트 가져오기
                ApiInterface selectFolloweeList_api = ApiClient.getApiClient().create(ApiInterface.class);
                Call<List<following_Response>> call_following = selectFolloweeList_api.selectFolloweeList(user_id);
                call_following.enqueue(new Callback<List<following_Response>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<following_Response>> call, @NonNull Response<List<following_Response>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<following_Response> responseResult = response.body();
                            tv_following.setText("" + responseResult.size());
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<List<following_Response>> call, @NonNull Throwable t) {
                        Log.e("에러", t.getMessage());
                    }
                });
            }
            @Override
            public void onFailure(@NonNull Call<List<user_Response>> call, @NonNull Throwable t)
            {
                Log.e("에러", t.getMessage());
            }
        });
    }


    private void tabInit() {
        tabLayout = findViewById(R.id.profile_tab_layout);
        pager2 = findViewById(R.id.profile_view_pager2);

        FragmentManager fm = getSupportFragmentManager();
        adapter = new FragmentAdapter_SS(fm, getLifecycle(), uid);
        pager2.setAdapter(adapter);

        tabLayout.addTab(tabLayout.newTab().setText("모든 글"));
        tabLayout.addTab(tabLayout.newTab().setText("답글"));
        tabLayout.addTab(tabLayout.newTab().setText("NFT"));
        tabLayout.addTab(tabLayout.newTab().setText("좋아요"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        pager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });

    }


    // ========================================================

    // 리소스 아이디 선언
    private void initiallize() {
        // 툴바
        toolbar = findViewById(R.id.ss_profile_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //서치 버튼
        tv_title = findViewById(R.id.tv_title_ss_profile);
        // 툴바 안 검색 버튼
        bt_search = findViewById(R.id.bt_search_ss_profile);
        swipeRefreshLayout = findViewById(R.id.ss_profile_refresh);


        //프로필 데이터 보여주기
        iv_user_image = findViewById(R.id.iv_user_image_ss_profile);
        tv_user_nick = findViewById(R.id.tx_user_nick_ss_profile);
        tv_user_id = findViewById(R.id.ss_profile_id);
        tv_user_intro = findViewById(R.id.tv_self_info_ss_profile);
        tv_following = findViewById(R.id.tv_following_ss_profile);
        tv_follower = findViewById(R.id.tv_follower_ss_profile);

        bt_following = findViewById(R.id.bt_following_ss_profile);
        bt_unfollowing = findViewById(R.id.bt_unfollowing_ss_profile);
        iv_chat = findViewById(R.id.iv_chat_ss_profile);
    }



    // 클릭 이벤트 모음
    private void clickListeners() {
        bt_following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApiInterface createFollowing_api = ApiClient.getApiClient().create(ApiInterface.class);
                Call<String> call = createFollowing_api.createFollowing(uid,UserInfo.user_id);
                call.enqueue(new Callback<String>()
                {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response)
                    {
                        if (response.isSuccessful() && response.body() != null)
                        {

                            Toast.makeText(getApplicationContext(), "팔로워로 추가되었습니다.", Toast.LENGTH_SHORT).show();
                            setProfileData();
                            swipeRefreshLayout.setRefreshing(false);
                            Log.e("팔로우 추가 성공", response.body().toString());

                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t)
                    {
                        Log.e("팔로우 추가 에러", t.getMessage());
                    }
                });
            }
        });


        bt_unfollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ApiInterface deleteFollowing_api = ApiClient.getApiClient().create(ApiInterface.class);
                Call<String> call = deleteFollowing_api.deleteFollowing(uid,UserInfo.user_id);
                call.enqueue(new Callback<String>()
                {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response)
                    {
                        if (response.isSuccessful() && response.body() != null)
                        {
                            Toast.makeText(getApplicationContext(), "팔로잉이 취소되었습니다.", Toast.LENGTH_SHORT).show();
                            setProfileData();
                            swipeRefreshLayout.setRefreshing(false);
                            Log.e("팔로우 삭제 성공", response.body().toString());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t)
                    {
                        Log.e("팔로우 삭제 에러", t.getMessage());
                    }
                });
            }
        });

        iv_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 사용자와 채팅을 요청하는 사람의 아이디 가져오기
                ArrayList<OtherUserInfo> chatMemberList = new ArrayList<>();

                // 채팅방 생성자 정보 생성
                OtherUserInfo creator = new OtherUserInfo(UserInfo.user_id, UserInfo.login_id,
                        UserInfo.email_addr, UserInfo.phone_num, UserInfo.novaland_account_addr,
                        UserInfo.profile_file_name, UserInfo.user_nick, UserInfo.self_info, UserInfo.token);
                
                chatMemberList.add(creator);
                
                // 채팅 요청자 정보 생성
                OtherUserInfo other = new OtherUserInfo(user_id, login_id, email_addr, phone_num, novaland_account_addr, profile_file_name_for_chat, user_nick, self_info, token);

                chatMemberList.add(other);

                // 채팅방 정보 저장할 객체
                Chat_Room_Cre_Or_Del chatRoomCre = new Chat_Room_Cre_Or_Del();

                // 두사람의 아이디로 만들어진 채팅방 있는지 조회
                // 이미 생성된 채팅방이 있는지 조회한다.
                ApiInterface apiClient = ApiClient.getApiClient().create(ApiInterface.class);
                Call<String> call = apiClient.selectExistingChatRoomId(chatMemberList.get(0).getUser_id(), chatMemberList.get(1).getUser_id());

                call.enqueue(new Callback<String>()
                {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response)
                    {
                        if (response.isSuccessful() && response.body() != null)
                        {
                            // 채팅방 아이디
                            String chatRoomId = response.body();

                            // 두 참여자가 참여하고 있는 채팅방이 존재한다면
                            if(!chatRoomId.equals("null")) {
                                chatRoomCre.setChatRoomNameCreator(chatMemberList.get(1).getUser_nick());
                                // 기존 채팅방으로 이동!
                                moveToChatListAct(chatRoomId, chatRoomCre, chatMemberList, null, false);
                            } else { // 두 참여자가 참여하고 있는 채팅방이 존재하지 않는다면
                                OtherUserInfo other = chatMemberList.get(1);
                                // 1-1. 채팅방 생성자 채팅방명 만들기(채팅상대의 닉네임)
                                chatRoomCre.setChatRoomNameCreator(other.getUser_nick());
                                // 1-2. 다른사람 채팅방명 만들기(채팅방 생성자의 닉네임)
                                chatRoomCre.setChatRoomNameOther(UserInfo.user_nick);

                                // 2-1. 채팅방 생성자의 아이디 - 프로필 사진 조회위해 필요
                                chatRoomCre.setChatCreatorId(UserInfo.user_id);
                                // 2-2. 다른사람 채팅방 프로필 - 프로필 사진 조회위해 필요
                                chatRoomCre.setChatOtherId(other.getUser_id());

                                // 3. 채팅 참여자들 user_id 리스트 만들기
                                ArrayList<String> list = new ArrayList<>();
                                list.add(UserInfo.user_id);
                                list.add(other.getUser_id());
                                chatRoomCre.setChatRoomMemberList(list);

                                // 새로운 채팅방 생성
                                createNewChatRoom(chatRoomCre, chatMemberList);
                            }
                        } else {
                            Log.e("채팅방 정보 저장 성공 했지만 데이터 없음: ", "1111");
                        }
                        return;
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t)
                    {
                        Log.e("채팅방 정보 저장 실패", t.getMessage());
                        return;
                    }
                });

                // 있다면 기존 채팅방으로 이동
                // 없다면 채팅방 생성
            }
        });

        //툴바 서치 버튼
        bt_search.setOnClickListener(v -> {
            Intent mIntent = new Intent(getApplicationContext(), SS_SearchMode.class);
            mIntent.putExtra("keyword", "");
            startActivity(mIntent);
        });

        // 리사이클러뷰 새로고침 인식
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        setProfileData();
                        //Toast.makeText(getApplicationContext(), "TODO 새로고침", Toast.LENGTH_SHORT).show();
                        /* 업데이트가 끝났음을 알림 */
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
    }

    // 채팅방리스트 화면으로 다시 이동한다.
    public void moveToChatListAct(String chatRoomId, Chat_Room_Cre_Or_Del chatRoomCre, ArrayList<OtherUserInfo> chatMemberList, Chat_Member_FCM_Sub chatMemberFcmSub, boolean isNew) {
        // 채팅방 액티비티로 이동
        Intent mIntent = new Intent(getApplicationContext(), ChattingRoom.class);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

        // 채팅방 아이디
        mIntent.putExtra("chatRoomId",chatRoomId);
        Log.d("chatRoomId",chatRoomId);

        // 채팅방 명
        mIntent.putExtra("chatRoomName",chatRoomCre.getChatRoomNameCreator());
        Log.d("chatRoomName",chatRoomCre.getChatRoomNameCreator());

        // 새로 생성된 채팅방인지 여부
        mIntent.putExtra("isNew",isNew);
        Log.d("chatRoomId","" + isNew);

        // 메시지 전송여부 - 메시지가 전송되지 않고 채팅방이 제거되면 MQTT와 FCM 구독을 해지해야 함
        if(isNew){
            mIntent.putExtra("isMsgTransfered",false);
        } else {
            mIntent.putExtra("isMsgTransfered",true);
        }

        // 채팅멤버 리스트
        mIntent.putExtra("chatMemberList", chatMemberList);
        Log.d("chatMemberList","" + chatMemberList.size());

        // FCM 토큰리스트
        mIntent.putExtra("chatMemberFcmTokenList", chatMemberFcmSub);
        //Log.d("chatMemberFcmTokenList", chatMemberFcmSub.toString());

        // 채팅방 생성 혹은 파괴 객체정보
        mIntent.putExtra("chatRoomCreOrDel", chatRoomCre);
        //Log.d("chatRoomCreOrDel",chatRoomCre.toString());

        // ChattingRoom화면으로 이동
        startActivity(mIntent);
    }

    // 새로운 채팅방을 만든다.
    public void createNewChatRoom(Chat_Room_Cre_Or_Del chatRoomCre, ArrayList<OtherUserInfo> chatMemberList){
        // 4. 서버로 전송
        ApiInterface apiClient = ApiClient.getApiClient().create(ApiInterface.class);
        Call<String> call = apiClient.createChatRoom(chatRoomCre);
        call.enqueue(new Callback<String>()
        {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response)
            {
                if (response.isSuccessful() && response.body() != null)
                {
                    // 채팅방 아이디
                    String chatRoomId = response.body();

                    Log.e("채팅방 정보 저장 성공 - 채팅방 아이디: ", chatRoomId);

                    // chatRoomCre에 채팅방 아이디 저장
                    chatRoomCre.setChatRoomId(chatRoomId);
                    // 채팅방 생성 메시지: 0
                    chatRoomCre.setMsgType(0);

                    // 브로커에게 채팅방 정보 publish
                    new ChatUtil().publishChatCreOrDelMsg(chatRoomCre, ChatService.mqttClient);

                    // FCM서버에게 구독요청
                    // fcm_token list
                    ArrayList<String> tokenList = new ArrayList<>();
                    for(int i = 0; i < chatMemberList.size(); i++){
                        tokenList.add(chatMemberList.get(i).getToken());
                    }

                    // 토큰 정보 찍어보기
                    for(int i = 0; i < tokenList.size(); i++){
                        System.out.println("토큰" + i + ": " + tokenList.get(i)); ;
                    }

                    // FCM 서버에게 구독요청을 하기 위한 데이터
                    Chat_Member_FCM_Sub chatMemberFcmSub = new Chat_Member_FCM_Sub();
                    chatMemberFcmSub.setTopic("/topics/" + chatRoomId);
                    chatMemberFcmSub.setTokenList(tokenList);

                    ChatApiInterface chatApiClient = ChatApiClient.getApiClient().create(ChatApiInterface.class);
                    Call<String> call2 = chatApiClient.subscribeChatMemberToFCMServer(chatMemberFcmSub);
                    call2.enqueue(new Callback<String>()
                    {
                        @Override
                        public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response)
                        {
                            if (response.isSuccessful() && response.body().equals("success")){
                                //
                                String responseResult = response.body();
                                Log.d("채팅멤버 FCM 구독 완료",responseResult);

                                // 채팅리스트 액티비티로 이동한다.
                                moveToChatListAct(chatRoomId, chatRoomCre, chatMemberList, chatMemberFcmSub,true);

                            } else {
                                Log.d("채팅멤버 FCM 구독 완료 결과: ", response.body());
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<String> call, @NonNull Throwable t)
                        {
                            Log.e("채팅방 멤버 FCM 등록 실패", t.getMessage());
                        }
                    });
                } else {
                    Log.e("채팅방 정보 저장 성공 했지만 데이터 없음: ", "1111");
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t)
            {
                Log.e("채팅방 정보 저장 실패", t.getMessage());
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                // 왼쪽 상단 버튼 눌렀을 때
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    // 액티비티 종료 시, 애니메이션 효과 없애기
    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    // 프래그먼트 어답터에서 Uid 받기 위해서 필요
    public String getUid() {
        return uid;
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("리줌");
        //프로필 데이터 표시
        setProfileData();
    }

}
