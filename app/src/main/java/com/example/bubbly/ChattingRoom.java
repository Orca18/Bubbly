package com.example.bubbly;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bubbly.chatting.service.ChatService;
import com.example.bubbly.chatting.util.ChatUtil;
import com.example.bubbly.chatting.util.GetDate;
import com.example.bubbly.controller.Chat_Adapter;
import com.example.bubbly.model.Chat_Item;
import com.example.bubbly.model.Chat_Item_From_Server;
import com.example.bubbly.model.Chat_Member_FCM_Sub;
import com.example.bubbly.model.Chat_Room_Cre_Or_Del;
import com.example.bubbly.model.Chat_Room_Info;
import com.example.bubbly.model.OtherUserInfo;
import com.example.bubbly.model.UserInfo;
import com.example.bubbly.retrofit.ApiClient;
import com.example.bubbly.retrofit.ApiInterface;
import com.example.bubbly.retrofit.ChatApiClient;
import com.example.bubbly.retrofit.ChatApiInterface;
import com.example.bubbly.retrofit.chat.SyncGetChatInfo;
import com.example.bubbly.retrofit.chat.SyncGetMqttId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 채팅방 화면
 * */
public class ChattingRoom extends AppCompatActivity {

    androidx.appcompat.widget.Toolbar toolbar;

    RecyclerView recyclerView;

    private Chat_Adapter adapter;
    private List<Chat_Item> chatList;
    private Parcelable recyclerViewState;
    private TextView chatRoomNameTextview;
    private String chatRoomId;
    private String chatRoomName;
    private String userId;

    private int position = 0;
    
    // 새로운 채팅방여부
    private boolean isNew;

    // 채팅 내용 입력창
    private EditText inputText;

    // 채팅 전송 버튼
    private ImageButton sendChat;

    // 서비스와 통신하기 위한 메신저
    private Messenger mServiceMessenger = null;

    // 서비스로 보낼 메신저
    final Messenger mActivityMessenger = new Messenger(new ActivityHandler());

    // 서비스와 연결됐는지 여부
    boolean isBound = false;

    // 서비스의 상태에 따라 콜백 함수를 호출하는 객체.
    private ServiceConnection conn;

    private ChatUtil chatUtil = new ChatUtil();

    // 채팅데이터 페이지
    private int pageNo = 1;
    // 10개씩 가져오기!
    private int pagingSize = 10;

    // SP
    private SharedPreferences preferences;

    // 채팅 메시지를 저장할 리스트
    private ArrayList<Chat_Item> chatItemList;

    // 채팅참여자의 프로필파일명을 저장한 hashMap
    private HashMap<String, String> profileMap;

    // 사용자명
    private String userName;

    // 채팅참여자 FCM 토큰리스트
    private Chat_Member_FCM_Sub chatMemberFcmTokenList;

    // 채팅방 생성 혹은 파괴시 사용하는 객체
    private Chat_Room_Cre_Or_Del chatRoomCreOrDel;
    
    /**
    * 새로운 채팅방인 경우 사용할 변수 - 메시지 전송여부
     * 메시지를 전송하지 않고 채팅방이 파괴되는 경우 채팅방 정보 삭제, MQTT, FCM 구독해지를 해야 함.
    * */
    private boolean isMsgTransfered;
    
    // 채팅에 참여하는 멤버 리스트
    private ArrayList<OtherUserInfo> chatMemberList;

    // 서비스로부터 받은 메시지를 처리할 핸들러
    class ActivityHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            // msg.what에는 메시지 종류가 들어있음(서비스에서 설정)
            switch(msg.what){
                // 메시지 수신
                case ChatService.MSG_RECEIVE_FROM_SERVER:
                    // 서비스로부터 받은 수신한 메시지 객체
                    Bundle bundle = msg.getData();
                    Chat_Item read = (Chat_Item)bundle.getSerializable("message");

                    // 안읽은 사용자 수 업데이트
                    if(read.getChatType() == 3) {
                        adapter.updateNotReadUserCount(Integer.parseInt(read.getChatText()));
                        break;
                    }

                    // 수신한 메시지 리사이클러뷰에 표시
                    int newPosition = adapter.addChatMsgInfo(read);
                    recyclerView.scrollToPosition(newPosition);

                    // 최신 메세지id 업데이트
                    saveLatestChatIdToSharedPreference(read);

                    // 메시지 출력
                    Log.e("ChattingRoomActivity - 서버로부터 메시지 수신", "1-1. 수신한 메시지 => " + read.getChatText());

                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatting_room);

        initialize();
    }

    public void initialize(){
        toolbar = findViewById(R.id.chatroom_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        // 컴포넌트 초기화
        inputText = findViewById(R.id.input_text);
        sendChat = findViewById(R.id.send_chat);
        chatRoomNameTextview = findViewById(R.id.chatRoomName);

        // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Intent에서 받아온 값 세팅
        getIntentFromAct();

        recyclerView = findViewById(R.id.chatroom_recyclerview);

        // 사용자 아이디 sp에서 가져오기
        preferences = getSharedPreferences("novarand", MODE_PRIVATE);
        userId = preferences.getString("user_id", ""); // 로그인한 user_id값

        // 서비스 시작&연결
        startChatService();

        // 리사이클러뷰 세팅
        chatItemList = new ArrayList<>();
        setUpRecyclerView(chatItemList);

        // 클릭리스너 세팅
        setClickListener();

        // 해당 채팅방에서 마지막으로 읽은 메시지id 가져오기
        int lastReadMsgId = getLastMsgIdx(chatRoomId);

        // 리사이클러뷰 데이터 가져오기
        int updateDiv;
        if(!isNew){
            // 채팅방 입장
            loadrecycler(pageNo);
            updateDiv = 0;
        } else {
            // 채팅방 생성
            updateDiv = 3;
        }
        // 서버에 사용자수 업데이트!
        chatUtil.updateUserCountMap(chatRoomId,updateDiv,chatMemberList.size());

        // 마지막으로 읽은 메시지id가 있다면
        if(lastReadMsgId != 99999999){
            // 브로커에게 마지막으로 읽은 메시지의 id를 전달한다 => 이 메시지 다음 id부터 안읽은 사용자수 -1 을 해주기 위해서
            sendLastReadIdToBroker(lastReadMsgId);
        } else {
            // 마디막으로 읽은 메시지가 없다면 채팅방을 새로 만들거나 기존에 만들어진 채팅방에 처음 들어올 때이다.
            // 채팅방 생성시는 업데이트할 필요가 없고 기존에 있던 채팅방에 처음들어왔을 때만 업데이트 해주면 된다.
            if(!isNew) {
                // 모든 메시지 업데이트 (서버에서 lastIdx + 1부터 업데이트 하기 때문에 -1을 보낸다!)
                sendLastReadIdToBroker(-1);
            }
        }
    }

    // 데이터 http 요청
    private void loadrecycler(int pageNo) {
        // 쓰레드 http 요청 & run 데이터 넣기
        fillList(chatRoomId, pageNo);
        recyclerView.scrollToPosition(0);
    }

    // loadrecycler 에서 요청/응답 받은 데이터 채워넣기
    private void fillList(String chatRoomId, int page_no) {
        ChatApiInterface chatApiClient = ChatApiClient.getApiClient().create(ChatApiInterface.class);
        Call<ArrayList<Chat_Item>> call = chatApiClient.selectChatData(chatRoomId, page_no, pagingSize);
        call.enqueue(new Callback<ArrayList<Chat_Item>>()
        {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(@NonNull Call<ArrayList<Chat_Item>> call, @NonNull Response<ArrayList<Chat_Item>> response)
            {
                if (response.isSuccessful() && response.body() != null)
                {
                    Log.e("조회 성공", "11");
                    ArrayList<Chat_Item> chatInfoList = response.body();
                    if(chatInfoList.size() > 0){
                        // 채팅 리스트에 모든 데이터 추가
                        chatItemList.addAll(chatInfoList);
                        adapter.setPageNo(page_no);

                        // 페이징해서 10개씩 가져오므로!
                        adapter.notifyItemRangeInserted(((page_no - 1) * 10), pagingSize);

                        // 첫번째 페이지의 0번째 인덱스의 값이 최신 메시지이므로 SP에 저장한다.
                        if(page_no == 1){
                            saveLatestChatIdToSharedPreference(chatInfoList.get(0));
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<Chat_Item>> call, @NonNull Throwable t)
            {
                Log.e("데이터 에러", t.getMessage());
            }
        });
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

        recyclerView.addOnScrollListener(onScrollListener);
    }

    // 리사이클러뷰의 스크롤이 최상단에 도달했을 때 새로운 채팅 데이터를 가져오기 위함!
    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            // 리사의 맨위에 도달했다면 데이터 가져오기
            if(!recyclerView.canScrollVertically(-1)){
                Log.d("맨위입니까?", "111");

                pageNo++;
                fillList(chatRoomId, pageNo);

                 /*// 리사이클러뷰에서 눈에
                int firstVisibleItemPos = ((LinearLayoutManager)recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();

                Log.d("firstVisibleItemPos", "" + firstVisibleItemPos);
                Log.d("pageNo", "" + pageNo);

                // 페이지의 끝에 도달했다면 데이터 가져오기!
                if(firstVisibleItemPos % 10 == 2){
                    pageNo++;
                    fillList(chatRoomId, pageNo);
                }*/
            }
        }
    };
    /*private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (recyclerView.computeVerticalScrollOffset() == 0) {
                // 최상단인 경우 페이징해서 메시지 가져오기
                fillList(chatRoomId, (pageNo++));
            }

        }
    };*/

    // 인텐트에서 받아온 값 세팅
    public void getIntentFromAct(){
        Intent getIntentFromAct = getIntent();
        
        // 새로운 채팅방 여부 - 채팅방 리스트 화면에서 넘어온 경우 값이 없으므로 false로 세팅
        isNew = getIntentFromAct.getBooleanExtra("isNew", false);

        // 메시지 전송됐는지 여부 - 채팅방 리스트 화면에서 넘어온 경우 값이 없으므로 true로 세팅
        isMsgTransfered = getIntentFromAct.getBooleanExtra("isMsgTransfered", true);
        
        // 채팅 참여자 리스트
        chatMemberList = (ArrayList<OtherUserInfo>) getIntentFromAct.getSerializableExtra("chatMemberList");

        // chatRoomId 가져오기
        chatRoomId = getIntentFromAct.getStringExtra("chatRoomId");

        // chatRoomName 가져오기
        chatRoomName = getIntentFromAct.getStringExtra("chatRoomName");
        chatRoomNameTextview.setText(chatRoomName);

        // 채팅참여자 FCM 토큰리스트
        chatMemberFcmTokenList = (Chat_Member_FCM_Sub) getIntentFromAct.getSerializableExtra("chatMemberFcmTokenList");

        // 채팅방 생성 혹은 파괴 시 사용하는 객체
        chatRoomCreOrDel = (Chat_Room_Cre_Or_Del) getIntentFromAct.getSerializableExtra("chatRoomCreOrDel");
        
        // 프로필맵 세팅
        setProfileMap(chatMemberList);
        
        Log.d("채팅방 입장 시 Intent에서 데이터 잘 가져오나 확인 - isNew: ","" + isNew);
        Log.d("채팅방 입장 시 Intent에서 데이터 잘 가져오나 확인 - isMsgTransfered: ","" + isMsgTransfered);
        Log.d("채팅방 입장 시 Intent에서 데이터 잘 가져오나 확인 - chatRoomId: ",chatRoomId);
        Log.d("채팅방 입장 시 Intent에서 데이터 잘 가져오나 확인 - chatRoomName: ", chatRoomName);
        Log.d("채팅방 입장 시 Intent에서 데이터 잘 가져오나 확인 - chatMemberList: ", "" + chatMemberList.size());
        Log.d("채팅방 입장 시 Intent에서 데이터 잘 가져오나 확인 - chatRoomCreOrDel: ", isNew == true ?chatRoomCreOrDel.toString() : "null");
        Log.d("채팅방 입장 시 Intent에서 데이터 잘 가져오나 확인 - chatMemberFcmTokenList: ", isNew == true ?"" + chatMemberFcmTokenList.getTokenList().size() : "null");
    }

    // 사용자 프로필 해시맵에 저장
    public void setProfileMap(ArrayList<OtherUserInfo> chatMemberList){
        profileMap = new HashMap<>();

        for(OtherUserInfo chatMemberInfo : chatMemberList){
            String userId = chatMemberInfo.getUser_id();
            String profile = chatMemberInfo.getProfile_file_name();

            profileMap.put(userId, profile);
            Log.d("채팅방 생성 시 사용자 프로필 해시맵에 저장: profileMap.get(" + userId + "): ", (profileMap.get(userId) != null)?profileMap.get(userId) : "null");
        }
    }

    // 서비스 시작하고 연결
    public void startChatService(){
        // 서비스 커넥션 생성
        conn = new ServiceConnection() {
            // 서비스와 연결된 경우 호출
            // IBinder 서비스의 onBind에서 반환한 메신저 객체
            public void onServiceConnected(ComponentName className, IBinder service) {
                // 서비스와 연결된 메신저 객체 생성
                mServiceMessenger = new Messenger(service);

                Log.e("ChattingRoom입장 시 ChatService와 연결 - mServiceMessenger 생성", mServiceMessenger.toString());

                // 연결여부를 true로 변경해준다.
                isBound = true;

                try {
                    // 서비스에게 보낼 메시지를 생성한다.
                    // 파라미터: 핸들러, msg.what에 들어갈 int값
                    Message msg = Message.obtain(null, ChatService.CONNECT_CHAT_ACT);
                    Log.e("ChattingRoom입장 시 ChatService와 연결 - 서비스에 보내기 위한 메시지 객체 생성 ", msg.toString());

                    // msg객체에 들어있는 Bundle 객체를 가져옴
                    Bundle bundle = msg.getData();
                    Log.e("ChattingRoom입장 시 ChatService와 연결 - 데이터를 전달하기 위한 Bundle객체 생성 ", bundle.toString());

                    // 채팅방 아이디 넘겨 줌
                    bundle.putString("chatRoomId", chatRoomId);

                    // 서비스로 사용자아이디 보내기
                    bundle.putString("userId", userId);

                    // 메시지의 송신자를 넣어준다.
                    msg.replyTo = mActivityMessenger;
                    Log.e("ChattingRoom입장 시 ChatService와 연결 - 서비스와 통신하기 위한 메신저: ", mActivityMessenger.toString());

                    // 서비스에 연결됐다는 메시지를 전송한다.
                    mServiceMessenger.send(msg);
                    Log.e("ChattingRoom입장 시 ChatService와 연결 - 서비스에 메시지 전송: ", msg.toString());

                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            // 예상치 못하게 연결이 종료되는 경우 호출되는 콜백 함수
            public void onServiceDisconnected(ComponentName className) {
                // 서비스에게 메시지를 전송하는 메신저를 null로 변경
                mServiceMessenger = null;
                // 연결이 종료되었으므로 연결여부를 false로 봐꿔준다.
                isBound = false;
            }
        };

        // 서비스에 바인드 한다.
        // Intent와 ServiceConnection 객체를 파라미터로 넣는다.
        getApplicationContext().bindService(new Intent(getApplicationContext(), ChatService.class), conn, Context.BIND_AUTO_CREATE);
        Log.e("ChattingRoom입장 시 ChatService와 연결 - bindService", "true");
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

                // 서비스에게 텍스트 메시지 전송
                if(!sendMsg.equals("")) {
                    sendTextMsg(sendMsg);
                }

                inputText.setText("");
                Log.e("텍스트 메시지 송신", "8. 텍스트박스 비워주기");
            }
        });
    }

    public void sendTextMsg(String sendMsg){
        // 채팅방 생성 후 처음 보내는 메시지인 경우 msgTransffered값을 true로 변경!
        if(isNew && !isMsgTransfered){
            isMsgTransfered = true;
        }

        // Chat_Item 만들기
        Chat_Item chat_item = new Chat_Item(chatRoomId, userId, sendMsg,null, GetDate.getDateWithYMDAndWeekDay(), GetDate.getAmPmTime(), UserInfo.user_nick, 0);

        // 사용자 프로필 세팅: null이면 null이 들어가나? => ""(공백)이 들어감 명확하게 null을 넣어주자!
        if(profileMap.get(userId) == null || profileMap.get(userId).equals("")){
            chat_item.setProfileImageURL(null);
        }else {
            chat_item.setProfileImageURL(profileMap.get(userId));
        }


        Log.e("텍스트 메시지 송신", "1. 작성한 메시지 sendMsg에 저장 => sendMsg: " + sendMsg);

        // 작성한 메시지 리사이클러뷰에 표시
        int newPosition = adapter.addChatMsgInfo(chat_item);
        recyclerView.scrollToPosition(newPosition);

        String chatItemRoomId = chat_item.getChatRoomId();
        String chatItemStr = chatUtil.chatItemToString(chat_item);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    chatUtil.publishChatMsg(chatItemStr, chatItemRoomId, ChatService.mqttClient);
                }
                catch (Exception e) {

                }
            }
        }).start();

        // 서비스에게 보낼 메시지를 생성한다.
        // 파라미터: 핸들러, msg.what에 들어갈 int값
        // 텍스트 메시지 전송
       /* Message msg = Message.obtain(null, ChatService.SEND_TEXT);

        Bundle bundle = msg.getData();

        // 서비스에 전달할 Chat_Item을 넣는다.
        bundle.putSerializable("sendMsg", chat_item);
        Log.e("텍스트 메시지 송신", "2. 서비스에 보낼 msg객체 생성 및 sendMsg 저장 => bundle.get(\"sendMsg\"): " + bundle.get("sendMsg"));

        // 서버에 전송할 텍스트 메시지를 서비스로 전송한다.
        try {
            mServiceMessenger.send(msg);
            Log.e("텍스트 메시지 송신", "3. 서비스로 메시지 전송");
        } catch (RemoteException e) {
            e.printStackTrace();
        }*/
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

    // 뒤로가기 버튼 클릭 시 서버에 종료신호 보내기
    @Override
    public void onBackPressed() {
        // 새로 생성됐고 메시지 전송을 한번이라도 한 경우 뒤로가기를 했을 때 MM_Message의 뷰모델에게 정보 전달!
        if(isNew && isMsgTransfered){
            // 뒤로가기 시 새로 생성된 방 정보 보내기
            ApiInterface apiClient = ApiClient.getApiClient().create(ApiInterface.class);
            Call<ArrayList<Chat_Room_Info>> call = apiClient.selectChatRoomInfo(chatRoomId);

            SyncGetChatInfo t = new SyncGetChatInfo(call);

            // 스레드 시작
            t.start();

            try {
                // 쓰레드에서 데이터를 조회할 때 main 스레드는 중지를 시켜야 하므로 join()사용
                t.join();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // 채팅방 정보 조회 완료
            Chat_Room_Info chatRoomInfo = t.getChatRoomInfo();

            Intent intent = null;

            intent = new Intent(ChattingRoom.this, MM_Message.class);

            intent.putExtra("chatRoomInfo", chatRoomInfo);

            setResult(Activity.RESULT_OK, intent);

            finish();

        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        // 액티비티 종료 시 바인딩도 해제하기
        // 바인딩 된 서비스에 해제한다고 알려주기 => 소켓연결 해제하기
        // 서비스에 전달할 메시지를 생성한다.
        // 파라미터: 핸들러, msg.what에 들어갈 int값
        Message msg = Message.obtain(null, ChatService.DISCONNECTED_CHAT_ACT);

        try {
            mServiceMessenger.send(msg);
            Log.e("채팅방 나갈 때 서비스와의 연결 제거 - 서비스로 메시지 전송", msg.toString());

        } catch (RemoteException e) {
            e.printStackTrace();
        }

        // 서비스와 연결여부
        isBound = false;
        Log.e("채팅방 나갈 때 서비스와의 연결 제거 - isBound false로 변경", "" + isBound);

        // 채팅방이 생성되고 메시지를 보내지 않은 상태에서 채팅방 액트가 종료된 경우 db에 저장된 채팅 관련 데이터 삭제 및 MQTT, FCM 구족 정보를 해지해야 한다.
        // 1. db의 채팅방 정보 삭제
        // 2. MQTT 구독 해지
        // 3. FCM 구독 해지
        if(isNew && !isMsgTransfered){
            Log.e("채팅방이 새로 생성되고 메시지를 전송하지 않은채로 파괴되어 db저장정보 삭제!", "isNew: " + isBound + "isMsgTransfered: " + isMsgTransfered);

            ApiInterface apiClient = ApiClient.getApiClient().create(ApiInterface.class);
            Call<String> call = apiClient.deleteChatRoom(chatRoomId);
            call.enqueue(new Callback<String>()
            {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response)
                {
                    if (response.isSuccessful() && response.body() != null)
                    {
                        // 채팅방 아이디
                        String chatRoomId = response.body();
                        Log.e("채팅방 정보 삭제 성공 - 채팅방 아이디: ", chatRoomId);

                        // 브로커에게 채팅방 정보 publish
                        // 채팅방 삭제(1)
                        chatRoomCreOrDel.setMsgType(1);
                        new ChatUtil().publishChatCreOrDelMsg(chatRoomCreOrDel, ChatService.mqttClient);

                        // FCM서버에게 구독 해지요청
                        // FCM 서버에게 구독요청을 하기 위한 데이터
                        ChatApiInterface chatApiClient = ChatApiClient.getApiClient().create(ChatApiInterface.class);
                        Call<String> call2 = chatApiClient.unsubscribeChatMemberToFCMServer(chatMemberFcmTokenList);
                        call2.enqueue(new Callback<String>()
                        {
                            @Override
                            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response)
                            {
                                if (response.isSuccessful() && response.body() != null)
                                {
                                    //
                                    String responseResult = response.body();
                                    Log.d("채팅멤버 FCM 구독 해지 완료",responseResult);

                                } else {
                                    Log.d("채팅멤버 FCM 구독 해지 완료","지만 데이터 없음!");

                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t)
                            {
                                Log.e("채팅멤버 FCM 구독 해지 완료", t.getMessage());
                            }
                        });
                    } else {
                        Log.e("채팅멤버 FCM 구독 해지 완료 데이터 없음: ", "1111");
                    }
                }

                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t)
                {
                    Log.e("채팅멤버 FCM 구독 해지 실패", t.getMessage());
                }
            });
        }

        // 채팅방 나가기
        chatUtil.updateUserCountMap(chatRoomId,1,chatMemberList.size());

        super.onDestroy();
    }

    /** 메시지 수신 시 SharedPreference에 메시지 정보 저장*/
    public void saveLatestChatIdToSharedPreference(Chat_Item chatItem){
        SharedPreferences sp = getSharedPreferences("chat", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        // 해당 채팅방 id로 메시지 인덱스를 저장해준다
        editor.putInt(chatItem.getChatRoomId(), chatItem.getChatId());

        editor.commit();
        Log.e("수신한 메시지 쉐어드에 저장: ", "" + sp.getInt(chatItem.getChatRoomId(),999999));
    }

    /** 특정 채팅방의 마지막 메시지 idx를 가져와라*/
    public int getLastMsgIdx(String chatRoomId){
        SharedPreferences sp = getSharedPreferences("chat", Activity.MODE_PRIVATE);
        int idx = sp.getInt(chatRoomId,99999999);

        return idx;
    }


    /** 채팅방에 입장했을 때 다른 채팅방의 안읽은사용자수 -- 해주기위해 브로커에게 채팅방 id와 마지막 인덱스를 보내준다.
     */
    public void sendLastReadIdToBroker(int lastIdx){
        Chat_Item chatItem = new Chat_Item(chatRoomId, userId, "" + lastIdx,null, GetDate.getDateWithYMDAndWeekDay(), GetDate.getAmPmTime(), UserInfo.user_nick, 3);
        String chatItemStr = chatUtil.chatItemToString(chatItem);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    chatUtil.publishChatMsg(chatItemStr, chatRoomId, ChatService.mqttClient);
                }
                catch (Exception e) {

                }
            }
        }).start();
    }
}