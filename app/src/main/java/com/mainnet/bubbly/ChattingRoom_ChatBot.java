package com.mainnet.bubbly;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.icu.text.UnicodeSet;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcelable;
import android.os.RemoteException;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.mainnet.bubbly.chatting.service.ChatService;
import com.mainnet.bubbly.chatting.util.ChatUtil;
import com.mainnet.bubbly.chatting.util.GetDate;
import com.mainnet.bubbly.controller.Chat_Adapter;
import com.mainnet.bubbly.model.Chat_Item;
import com.mainnet.bubbly.model.Chat_Member_FCM_Sub;
import com.mainnet.bubbly.model.Chat_Room_Cre_Or_Del;
import com.mainnet.bubbly.model.Chat_Room_Info;
import com.mainnet.bubbly.model.OtherUserInfo;
import com.mainnet.bubbly.model.UserInfo;
import com.mainnet.bubbly.retrofit.ApiClient;
import com.mainnet.bubbly.retrofit.ApiInterface;
import com.mainnet.bubbly.retrofit.ChatApiClient;
import com.mainnet.bubbly.retrofit.ChatApiInterface;
import com.mainnet.bubbly.retrofit.FileUtils;
import com.mainnet.bubbly.retrofit.chat.SyncGetChatInfo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Query;

/**
 * 채팅방 화면
 * */
public class ChattingRoom_ChatBot extends AppCompatActivity {

    androidx.appcompat.widget.Toolbar toolbar;

    RecyclerView recyclerView;

    private Chat_Adapter adapter;
    private List<Chat_Item> chatList;
    private Parcelable recyclerViewState;
    private String userId;

    private int position = 0;

    // 채팅 내용 입력창
    private EditText inputText;

    // 채팅 전송 버튼
    private ImageButton sendChat;

    private ChatUtil chatUtil = new ChatUtil(this);

    // SP
    private SharedPreferences preferences;

    // 채팅 메시지를 저장할 리스트
    private ArrayList<Chat_Item> chatItemList;

    // 채팅참여자의 프로필파일명을 저장한 hashMap
    private HashMap<String, String> profileMap;

    // 사용자명
    private String userName;

    // 채팅에 참여하는 멤버 리스트
    private ArrayList<OtherUserInfo> chatMemberList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatting_room_chatbot);

        initialize();
    }

    public void initialize(){
        toolbar = findViewById(R.id.chatroom_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        // 컴포넌트 초기화
        inputText = findViewById(R.id.input_text);
        sendChat = findViewById(R.id.send_chat);

        // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // 리사이클러뷰
        recyclerView = findViewById(R.id.chatroom_recyclerview);

        userId = UserInfo.user_id;

        // 리사이클러뷰 세팅
        chatItemList = new ArrayList<>();
        setUpRecyclerView(chatItemList);

        // 클릭리스너 세팅
        setClickListener();
    }

    private void setUpRecyclerView(ArrayList<Chat_Item> chatItemList) {
        recyclerView.setHasFixedSize(true);
        // 채팅방의 경우 데이터를 보여줄 때 가장 최신 데이터(밑에있는 데이터!)부터 보여줘야 하므로 해당 설정을 적용시켜준다.
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        this.adapter = new Chat_Adapter(getApplicationContext(), chatItemList);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(this.adapter);

        recyclerView.scrollToPosition(position);

        //위치 유지
        recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();
        //위치 유지
        recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);

    }

    // clickListener 등록
    public void setClickListener(){
        /** 메시지 송신
         *  전송버튼 클릭 시 실행되는 부분
         * */
        sendChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 입력한 텍스트 메시지
                String sendMsg = inputText.getText().toString();
                Log.e("텍스트 메시지 송신 - 서비스에게 전달할 메시지", sendMsg);

                // 리사이클러뷰에 메시지 뿌리고 ai 서버에게 메시지 전송
                sendChatMsg(sendMsg);

                inputText.setText("");
                Log.e("텍스트 메시지 송신", "8. 텍스트박스 비워주기");
            }
        });
    }

    public void sendChatMsg(String sendMsg) {
        // Chat_Item 만들기
        Chat_Item chat_item = new Chat_Item(null, userId, sendMsg, null, GetDate.getDateWithYMDAndWeekDay(), GetDate.getAmPmTime(), UserInfo.user_nick, 0);

        // 사용자 프로필 세팅: null이면 null이 들어가나? => ""(공백)이 들어감 명확하게 null을 넣어주자!
        String profile = UserInfo.profile_file_name;
        if (profile == null || profile.equals("")) {
            chat_item.setProfileImageURL(null);
        } else {
            chat_item.setProfileImageURL(profile);
        }

        // 리사이클러뷰에 메시지 표시
        adapter.addChatMsgInfo(chat_item);

        ((LinearLayoutManager) recyclerView.getLayoutManager()).scrollToPosition(0);

        // ai서버에게 메시지 전송 후 콜백 받은 내용 상대방 채팅창에 뿌려주기
        ApiInterface apiClient = ApiClient.getChatBotClientWithUrlInput("http://116.45.9.25:5000").create(ApiInterface.class);
        Call<String> call = apiClient.requestChatBotAnswer(sendMsg);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {

                    // 챗봇의 답변
                    String answer = response.body();

                    // 어댑터에 뿌리기
                    Chat_Item chatItem = new Chat_Item(null, "chatBot", answer, null, GetDate.getDateWithYMDAndWeekDay(), GetDate.getAmPmTime(), "버블리 챗봇", 0);
                    adapter.addChatMsgInfo(chatItem);

                } else {
                    Log.d("채팅봇과 채팅 실패", "empty");
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Log.e("채팅봇과 채팅 실패", t.getMessage());
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}