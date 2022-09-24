package com.mainnet.bubbly;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mainnet.bubbly.R;
import com.mainnet.bubbly.chatting.service.ChatService;
import com.mainnet.bubbly.chatting.util.ChatUtil;
import com.mainnet.bubbly.controller.Chat_Clicked_User_Adapter;
import com.mainnet.bubbly.controller.Chat_Searched_User_Adapter;
import com.mainnet.bubbly.model.Chat_Member_FCM_Sub;
import com.mainnet.bubbly.model.Chat_Room_Cre_Or_Del;
import com.mainnet.bubbly.model.OtherUserInfo;
import com.mainnet.bubbly.model.UserInfo;
import com.mainnet.bubbly.retrofit.ApiClient;
import com.mainnet.bubbly.retrofit.ApiInterface;
import com.mainnet.bubbly.retrofit.ChatApiClient;
import com.mainnet.bubbly.retrofit.ChatApiInterface;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
* 채팅에 참여할 사용자를 선택하는 액티비티.
* */
public class ChatMemberSelect extends AppCompatActivity {
    private Toolbar toolbar;

    // 사용자 id를 저장하고 있는 sp
    private SharedPreferences preferences;

    // 사용자 아이디
    private String userId;

    // 검색창
    private EditText searchText;

    private TextView btChat;

    /** 선택한 사용자를 표시하는 상단 리사이클러뷰 */
    // 체크한 사용자 아이템을 표현할 리사이클러뷰
    private RecyclerView clickedUserInfoRecyclerview;
    private LinearLayoutManager linearLayoutManager;
    private Chat_Clicked_User_Adapter chatClickedUserAdapter;

    /** 클릭된 사용자 정보를 저장하는 리스트*/
    private ArrayList<OtherUserInfo> clickedUserList;

    /** 검색한 결과를 표시하는 하단 리사이클러뷰 */
    // 체크한 사용자 아이템을 표현할 리사이클러뷰
    private RecyclerView searchUserInfoRecyclerview;
    private LinearLayoutManager linearLayoutManager2;
    private Chat_Searched_User_Adapter chatSearchedUserAdapter;

    /** 선택된 사용자 정보를 저장하는 리스트*/
    private ArrayList<OtherUserInfo> searchedUserList;

    /** 채팅방 생성 로딩바*/
    private ProgressBar progressBar_chat_room_create;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_member_select);

        initialize();
    }

    public void initialize(){
        toolbar = findViewById(R.id.chat_member_select_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        preferences = getSharedPreferences("novarand",MODE_PRIVATE);
        userId = preferences.getString("user_id", ""); // 로그인한 user_id값

        // 검색어 입력 에딧텍스트
        searchText = findViewById(R.id.chat_member_select_search_bar);

        // 채팅하기 버튼
        btChat = findViewById(R.id.chat_member_select_btn_chat);

        // 채팅 로딩바
        progressBar_chat_room_create = findViewById(R.id.progressBar_chat_room_create);

        // 선택한 사용자를 표시하는 상단 리사이클러뷰 초기화
        clickedUserInfoRecyclerviewInit();

        // 검색결과를 표시하는 하단 리사이클러뷰 초기화화
        searchUserInfoRecyclerviewInit();

        // 클릭이벤트 등록
        clickEvent();
    }

    // 클릭된 사용자 아이템을 표시하는 리사이클러뷰
    private void clickedUserInfoRecyclerviewInit(){
        // 클릭된 사용자를 표시하는 리사이클러뷰 객체
        clickedUserInfoRecyclerview = findViewById(R.id.chat_member_select_clicked_recyclerView);

        // 레이아웃 매니저
        linearLayoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        // 리사이클러뷰를 가로로 세팅
        clickedUserInfoRecyclerview.setLayoutManager(linearLayoutManager);

        // 사용자를 클릭했을 때 상단 리사이클러뷰에 데이터를 추가하기 위한 리스너
        Chat_Clicked_User_Adapter.ChatClickedUserDeleteListener listener = new Chat_Clicked_User_Adapter.ChatClickedUserDeleteListener() {
            @Override
            public void onItemDeleted(OtherUserInfo userInfo) {
                chatSearchedUserAdapter.checkRemove(userInfo);
            }
        };


        clickedUserList = new ArrayList<>();
        chatClickedUserAdapter = new Chat_Clicked_User_Adapter(getApplicationContext(), this.clickedUserList, listener);
        clickedUserInfoRecyclerview.setAdapter(chatClickedUserAdapter);
        chatClickedUserAdapter.notifyDataSetChanged();
    }

    // 검색한 사용자를 결과를 표시하는 리사이클러뷰
    private void searchUserInfoRecyclerviewInit(){
        // 검색된 사용자를 표시하는 리사이클러뷰 객체
        searchUserInfoRecyclerview = findViewById(R.id.chat_member_select_searched_recyclerView);

        // 레이아웃 매니저
        linearLayoutManager2 = new LinearLayoutManager(this);

        // 리사이클러뷰를 세팅
        searchUserInfoRecyclerview.setLayoutManager(linearLayoutManager2);

        // 사용자를 클릭했을 때 상단 리사이클러뷰에 데이터를 추가하기 위한 리스너
        Chat_Searched_User_Adapter.ChatSearchedUserClickListener listener = new Chat_Searched_User_Adapter.ChatSearchedUserClickListener() {
            @Override
            public void onItemClicked(OtherUserInfo userInfo, boolean isClicked) {
                // 클릭됐다면 해당 사용자 상단 리사이클러뷰에 추가
                if(isClicked){
                    chatClickedUserAdapter.addClickedUser(userInfo);
                } else { // 클릭이 해제 됐다면 상단 리사이클러뷰에서 제거
                    chatClickedUserAdapter.deleteClickedUser(userInfo);
                }

                Log.d("리스트 반환잘되나?", chatClickedUserAdapter.getList().toString());
            }
        };

        searchedUserList = new ArrayList<>();
        chatSearchedUserAdapter = new Chat_Searched_User_Adapter(getApplicationContext(), searchedUserList, listener);
        searchUserInfoRecyclerview.setAdapter(chatSearchedUserAdapter);
        chatSearchedUserAdapter.notifyDataSetChanged();
    }

    public void clickEvent(){
        // 검색어 입력 시
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            // 텍스트 입력 완료 후 이벤트
            @Override
            public void afterTextChanged(Editable s) {
                Log.d("sdf",s.toString());

                if(!s.toString().equals("")){
                    selectSearchedUserList(userId,s.toString());
                }
            }
        });

        // 채팅하기 버튼 클릭 시
        btChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 로딩바 시작
                progressBar_chat_room_create.setVisibility(View.VISIBLE);

                // 화면터치 금지
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                // 선택된 사용자들
                ArrayList<OtherUserInfo> chatMemberList = chatClickedUserAdapter.getList();

                // 채팅방 생성자 정보 생성
                OtherUserInfo creator = new OtherUserInfo(UserInfo.user_id, UserInfo.login_id,
                        UserInfo.email_addr, UserInfo.phone_num, UserInfo.novaland_account_addr,
                        UserInfo.profile_file_name, UserInfo.user_nick, UserInfo.self_info, UserInfo.token);

                // 생성자 정보를 채팅멤버리스트의 첫번째에 넣는다.
                chatMemberList.add(0,creator);

                // 채팅방 정보 저장할 객체
                Chat_Room_Cre_Or_Del chatRoomCre = new Chat_Room_Cre_Or_Del();

                /**
                 * 채팅방 정보 저장
                 * */
                // 채팅방 생성자를 제외하고 선택된 사용자가 한명보다 많다면 그룹 채팅!
                if(chatMemberList.size() > 2){
                    // 단체 채팅명 만들기
                    String chatName = "";
                    for(int i = 0; i < chatMemberList.size(); i++){
                        chatName += chatMemberList.get(i).getUser_nick() + ",";
                    }
                    // 마지막 ',' 빼기
                    chatName = chatName.substring(0,chatName.length() -1);

                    // 1-1. 채팅방 생성자 채팅방명 만들기(단체방 명)
                    chatRoomCre.setChatRoomNameCreator(chatName);
                    // 1-2. 다른사람 채팅방명 만들기(단체방 명)
                    chatRoomCre.setChatRoomNameOther(chatName);

                    // 2-1. 채팅방 생성자의 userId
                    chatRoomCre.setChatCreatorId(chatMemberList.get(0).getUser_id());
                    // 2-2. 다른사람 userId - 상대방 중 첫번째 사람의 id를 넣어줌
                    chatRoomCre.setChatOtherId(chatMemberList.get(1).getUser_id());

                    // 3. 참여자들 user_id 리스트 만들기
                    ArrayList<String> list = new ArrayList<>();
                    for(int i = 0; i < chatMemberList.size(); i++){
                        list.add(chatMemberList.get(i).getUser_id());
                    }
                    chatRoomCre.setChatRoomMemberList(list);

                    // 새로운 채팅방 생성
                    createNewChatRoom(chatRoomCre, chatMemberList);

                    Log.d("ChatRoomCre데이터 확인: ", chatRoomCre.toString());
                } else if(chatMemberList.size() == 2){ // 개인 채팅
                    // 이미 생성된 채팅방이 있는지 조회한다.
                    ApiInterface apiClient = ApiClient.getApiClient(ChatMemberSelect.this).create(ApiInterface.class);
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
                                    // 화면터치 금지
                                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

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
                }
            }
        });
    }

    // 채팅에 참여할 사용자 검색 - 내가 팔로잉한 사람, 나를 팔로잉한 사람, 그 외 모든 사람 순서대로 출력
    public void selectSearchedUserList(String user_id, String searchText){
        ApiInterface apiClient = ApiClient.getApiClient(ChatMemberSelect.this).create(ApiInterface.class);
        Call<ArrayList<OtherUserInfo>> call = apiClient.selectSearchedUserList(user_id, searchText);
        call.enqueue(new Callback<ArrayList<OtherUserInfo>>()
        {
            @Override
            public void onResponse(@NonNull Call<ArrayList<OtherUserInfo>> call, @NonNull Response<ArrayList<OtherUserInfo>> response)
            {
                if (response.isSuccessful() && response.body() != null)
                {
                    //
                    ArrayList<OtherUserInfo> responseResult = response.body();

                    // 검색 결과가 있다면
                    if(responseResult.size() != 0) {
                        chatSearchedUserAdapter.dataSetChanged(responseResult, chatClickedUserAdapter.getList());
                    } else { // 검색결과가 없다면
                        chatSearchedUserAdapter.clearAllUser();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<OtherUserInfo>> call, @NonNull Throwable t)
            {
                Log.e("게시물 아이디로 게시물 조회", t.getMessage());
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

        // 로딩바 제거
        progressBar_chat_room_create.setVisibility(View.GONE);
        // 화면터치 금지해제
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        // MM_Message화면으로 돌아감
        setResult(Activity.RESULT_OK, mIntent);

        finish();
    }

    // 새로운 채팅방을 만든다.
    public void createNewChatRoom(Chat_Room_Cre_Or_Del chatRoomCre, ArrayList<OtherUserInfo> chatMemberList){
        // 4. 서버로 전송
        ApiInterface apiClient = ApiClient.getApiClient(ChatMemberSelect.this).create(ApiInterface.class);
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
                    new ChatUtil(ChatMemberSelect.this).publishChatCreOrDelMsg(chatRoomCre, ChatService.mqttClient);

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

                    ChatApiInterface chatApiClient = ChatApiClient.getApiClient(ChatMemberSelect.this).create(ChatApiInterface.class);
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

    // function to the button on press
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}