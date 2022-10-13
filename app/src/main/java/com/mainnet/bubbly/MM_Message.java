package com.mainnet.bubbly;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.mainnet.bubbly.R;
import com.mainnet.bubbly.chatting.service.ChatService;
import com.mainnet.bubbly.chatting.util.ChatUtil;
import com.mainnet.bubbly.chatting.util.GetDate;
import com.mainnet.bubbly.chatting.viewmodel.ChattingRoomViewModel;
import com.mainnet.bubbly.config.Config;
import com.mainnet.bubbly.controller.Messages_Adapter;
import com.mainnet.bubbly.model.AccessAndRefreshToken;
import com.mainnet.bubbly.model.Chat_Item;
import com.mainnet.bubbly.model.Chat_Member_FCM_Sub;
import com.mainnet.bubbly.model.Chat_Room_Cre_Or_Del;
import com.mainnet.bubbly.model.Chat_Room_Info;
import com.mainnet.bubbly.model.OtherUserInfo;
import com.mainnet.bubbly.model.UserInfo;
import com.mainnet.bubbly.retrofit.ApiClient;
import com.mainnet.bubbly.retrofit.ApiInterface;
import com.mainnet.bubbly.retrofit.user_Response;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.messaging.FirebaseMessaging;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MM_Message extends AppCompatActivity {

    // 뒤로가기 시간
    private long backKeyPressedTime = 0;
    private Toast toast;

    // 바텀 메뉴
    LinearLayout bthome, btissue, btwallet, btmessage, btprofile;

    // 툴바, 사이드 메뉴
    androidx.appcompat.widget.Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageView sidemenu;

    // 새로고침, 프로그레스바
    SwipeRefreshLayout swipeRefreshLayout;
    ProgressBar progressBar;

    // 채팅방 추가 버튼
    ImageView btnChatRoomAdd;

    // 리사이클러 뷰
    private Messages_Adapter adapter;
    private List<Chat_Room_Info> chatRoomList;
    RecyclerView recyclerView;

    private Parcelable recyclerViewState;

    CircleImageView myAccount;
    LinearLayout myActivity, myList, myCommunity;
    TextView settingOption, info, logout;
    View view;

    SharedPreferences preferences;
    String userId;

    // 채팅방리스트를 관리할 뷰모델
    private ChattingRoomViewModel chattingRoomViewModel;
    
    // 해당 사용자가 참여하고 있는 채팅방의 아이디와 사용자수를 관리하는 맵(메시지 수신 시 해당하는 채팅방이 있는지 여부를 쉽게 관리하기 위한 맵)
    private HashMap<String, Integer> chatRoomIdMap;


    // ChatMemberSelect화면으로 이동해서 채팅방 생성 시 다 시돌아와 채팅방을 생성하기 위한 객체
    private ActivityResultLauncher<Intent> startActResultForChatMemberSelect;

    // 채팅방 멤버 선택화면에서 다시 이 액트로 돌아온 후 채팅방 화면을 생성하기 위한 객체
    private ActivityResultLauncher<Intent> startActResultForChattingRoom;

    private String divisionVal = "@@@@@";

    /**
     * 채팅서비스 연결 로직
     * */
    // 서비스와 통신하기 위한 메신저
    private Messenger mServiceMessenger = null;

    // 서비스로 보낼 메신저
    final Messenger mActivityMessenger = new Messenger(new ActivityHandler());

    // 서비스의 상태에 따라 콜백 함수를 호출하는 객체.
    private ServiceConnection conn;

    private ChatUtil chatUtil = new ChatUtil(this);

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

                    String chatRoomId = read.getChatRoomId();

                    Log.d("브로커에게 메시지 수신 시 ChatService에서 MM_Message로 메시지 전달", read.toString());

                    /**
                     * 서비스로부터 받은 메시지 처리
                     * */
                    // 1. 해당 메시지의 chatRoomId에 해당하는 채팅방 정보가 있는지 확인
                    Integer latestMsgId = chatRoomIdMap.get(chatRoomId);
                    
                    Log.d("MM_Message에서 관리하고 있는 채팅방인지 여부 null이면 없는 채팅방!: ", latestMsgId != null ? latestMsgId.toString() : "null");
                    
                    // 이 메시지와 동일한 채팅방 아이디를 가지는 채팅방이 없음 => 새로 생성!
                    if(latestMsgId == null) {
                        ApiInterface apiClient = ApiClient.getApiClient(MM_Message.this).create(ApiInterface.class);
                        Call<ArrayList<Chat_Room_Info>> call = apiClient.selectChatRoomInfo(chatRoomId);
                        call.enqueue(new retrofit2.Callback<ArrayList<Chat_Room_Info>>() {
                            @Override
                            public void onResponse(@NonNull Call<ArrayList<Chat_Room_Info>> call, @NonNull Response<ArrayList<Chat_Room_Info>> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    // 채팅방 정보 조회 완료
                                    ArrayList<Chat_Room_Info> chatRoomInfoList = response.body();
                                    Chat_Room_Info chatRoomInfo = chatRoomInfoList.get(0);

                                    // viewModel에 채팅방 정보를 업데이트 해준다!
                                    // 기존의 채팅방정보 리스트
                                    ArrayList<Chat_Room_Info> chatRoomList = chattingRoomViewModel.getChatRoomList().getValue();

                                    Log.d("새로운 채팅방 생성전 사이즈: ", "" + chatRoomList.size());

                                    chatRoomList.add(0,chatRoomInfo);

                                    chattingRoomViewModel.getChatRoomList().setValue(chatRoomList);

                                    Log.d("새로운 채팅방 생성후 사이즈: ", "" + chatRoomList.size());

                                    // 채팅방아이디를 관리하는 맵에도 데이터를 넣어준다!
                                    chatRoomIdMap.put(chatRoomId, chatRoomInfo.getLatestMsgId());
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<ArrayList<Chat_Room_Info>> call, @NonNull Throwable t) {
                                Log.e("게시글 생성 에러", t.getMessage());
                            }
                        });
                    } else { // 해당 메시지의 채팅방 아이디와 동일한 채팅방 아이디를 가지는 채팅방 객체가 이미 존재!
                        ArrayList<Chat_Room_Info> chatRoomList = chattingRoomViewModel.getChatRoomList().getValue();
                        Log.e("이미 있는 채팅방 => 갱신전", "" + chatRoomList.get(0).getLatestMsg());

                        for(int i = 0; i < chatRoomList.size(); i++){
                            Chat_Room_Info chatRoomInfo = chatRoomList.get(i);

                            if(chatRoomInfo.getChatRoomId().equals(chatRoomId)){
                                chatRoomInfo.setLatestMsg(read.getChatText());
                                chatRoomInfo.setLatestMsgId(read.getChatId());

                                // 브로커로부터 받은 메시지의 시간은 yyyy년 mm월 dd일 (요일) 오전/오후 hh:mm의 형태로 보여짐
                                // db에서 조회할 땐 yyyy.MM.dd hh:mm의 형태로 될 것임 => 둘을 맞춰줘야 함!!
                                // 최신 메시지 수신 시간
                                chatRoomInfo.setLatestMsgTime(read.getChatTime());
                                // 해당 위치의 아이템 삭제
                                chatRoomList.remove(i);
                                // 가장 최근에 메시지를 받았으므로 0번쨰 인덱스로 이동!
                                chatRoomList.add(0,chatRoomInfo);

                                Log.e("이미 있는 채팅방 => 갱신 후", "" + chatRoomList.get(0).getLatestMsg());

                                // 뷰모델 갱신!
                                chattingRoomViewModel.getChatRoomList().setValue(chatRoomList);
                                break;
                            }
                        }
                    }

                    // 메시지 출력
                    Log.e("MM_Message - 서버로부터 메시지 수신", "1-1. 수신한 메시지 => " + read.getChatText());

                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 결과값을 가지고 있는 인텐트 설정
        setUpStartActForResult();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_c_message);

        // 스플래시 화면을 거치지 않고 채팅 리스트 화면으로 왔다면 설정파일의 데이터를 가져온다.
        if(Config.api_server_addr == null){
            try {
                new Config(getApplicationContext()).getConfigData();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        preferences = getSharedPreferences("novarand", MODE_PRIVATE);
        userId = preferences.getString("user_id", ""); // 로그인한 user_id값

        // noti를 클릭해서 넘어왔다면!
        if(UserInfo.user_id.equals("")){
            //쉐어드프리퍼런스에서 로그인정보 가져오기
            String id="";
            String pw="";
            MasterKey masterkey = null;
            try {
                masterkey = new MasterKey.Builder(getApplicationContext(), MasterKey.DEFAULT_MASTER_KEY_ALIAS)
                        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                        .build();
                SharedPreferences sharedPreferences = EncryptedSharedPreferences
                        .create(getApplicationContext(),
                                "account",
                                masterkey,
                                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);
                id = sharedPreferences.getString("id","");
                pw = sharedPreferences.getString("pw","");
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(id.equals("")&pw.equals("")){
                //만약 쉐어드프리퍼런스에 저장된 사용자 정보가 없으면 로그인 페이지로 이동
                startActivity(new Intent(MM_Message.this, LL_Login.class));
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                finish();
            }else{
                //만약 쉐어드프리퍼런스에 저장된 사용자 정보기 있으면 login api 요청 후 Home으로 이동
                ApiInterface login_api = ApiClient.getApiClient(MM_Message.this).create(ApiInterface.class);
                Call<String> call = login_api.login(id,pw);
                call.enqueue(new Callback<String>()
                {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response)
                    {
                        if (response.isSuccessful() && response.body() != null)
                        {
                            Log.e("로그인 데이터", response.body().toString());
                            if(response.body().toString().equals("fail")){
                                Toast.makeText(getApplicationContext(), "로그인 실패",Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(getApplicationContext(), "로그인 성공",Toast.LENGTH_SHORT).show();
                                //수신한 데이터를 json으로 파싱한다.
                                JSONObject json = null;
                                try {
                                    //수신한 토큰을 static으로 저장한다.
                                    json = new JSONObject(response.body().toString());
                                    AccessAndRefreshToken.accessToken = json.getString("accessToken");
                                    AccessAndRefreshToken.refreshToken = json.getString("refreshToken");
                                    int user_id = json.getInt("userId");
                                    //암호화 쉐어드 프리퍼런스 복호화해 주소와 니모니 불러온다 (공동작업 위해 복호화 주석처리 이후 다시 주석 해제 예정)
                                    MasterKey masterkey = null;
                                    try {
                                        masterkey = new MasterKey.Builder(getApplicationContext(), MasterKey.DEFAULT_MASTER_KEY_ALIAS)
                                                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                                                .build();
                                        SharedPreferences sharedPreferences = EncryptedSharedPreferences
                                                .create(getApplicationContext(),
                                                        "account",
                                                        masterkey,
                                                        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                                                        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);
                                        String mnemonic = sharedPreferences.getString("mnemonic","");
                                        Log.e("니모닉",mnemonic);
                                        UserInfo.mnemonic = mnemonic;
                                    } catch (GeneralSecurityException e) {
                                        e.printStackTrace();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    //회원정보를 요청한다.
                                    Call<List<user_Response>> call_userInfo = login_api.selectUserInfo(""+user_id);
                                    call_userInfo.enqueue(new Callback<List<user_Response>>()
                                    {
                                        @Override
                                        public void onResponse(@NonNull Call<List<user_Response>> call, @NonNull Response<List<user_Response>> response)
                                        {
                                            System.out.println(response.body());
                                            //수신한 회원정보를 스태틱으로 저장한다.
                                            List<user_Response> responseResult = response.body();
                                            UserInfo.user_id = responseResult.get(0).getUser_id();
                                            UserInfo.login_id = responseResult.get(0).getLogin_id();
                                            UserInfo.email_addr = responseResult.get(0).getEmail_addr();
                                            UserInfo.novaland_account_addr = responseResult.get(0).getNovaland_account_addr();
                                            UserInfo.phone_num = responseResult.get(0).getPhone_num();
                                            UserInfo.user_nick = responseResult.get(0).getUser_nick();
                                            UserInfo.self_info = responseResult.get(0).getSelf_info();
                                            UserInfo.token = responseResult.get(0).getToken();
                                            if(responseResult.get(0).getProfile_file_name()!=null && !responseResult.get(0).getProfile_file_name().equals("")){
                                                UserInfo.profile_file_name = Config.cloudfront_addr+responseResult.get(0).getProfile_file_name();
                                            }

                                            initiallize();
                                        }
                                        @Override
                                        public void onFailure(@NonNull Call<List<user_Response>> call, @NonNull Throwable t)
                                        {
                                            Log.e("에러", t.getMessage());
                                        }
                                    });
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t)
                    {
                        Log.e("로그인 에러", t.getMessage());
                    }
                });
            }
        } else {
            // 리소스 ID 선언
            initiallize();
        }
    }


    // ========================================================

    // 리소스 아이디 선언
    private void initiallize() {
        // 채팅서비스와 연결한다.
        if(!ChatService.IS_BOUND_MAIN_ACTIVITY) {
            connectToService();
        }

        // 툴바
        toolbar = findViewById(R.id.message_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        drawerLayout = findViewById(R.id.drawer_layout);
        // 네비게이션뷰를 저장하기 위한 변수
        navigationView = findViewById(R.id.message_navigation_view);
        // 사이드바 메뉴를 열기위한 변수
        sidemenu = findViewById(R.id.message_sidemenu);
        // 리사이클러뷰를 새로고침할 수 있게하기 위한 레이아웃
        swipeRefreshLayout = findViewById(R.id.message_refresh);
        // 채팅방리스트를 저장하기 위한 리사이클러뷰
        recyclerView = findViewById(R.id.message_recyclerView);

        // 내비 안 메뉴
        view = navigationView.getHeaderView(0);
        // 내 프로필 이미지
        myAccount = view.findViewById(R.id.navi_header_profileimg);
        // 내 활동
        myActivity = view.findViewById(R.id.navi_header_myActivity);
        // 리스트
        //myList = view.findViewById(R.id.navi_header_myList);
        // 커뮤니티
        myCommunity = view.findViewById(R.id.navi_header_myCommunity);
        // 설정
        settingOption = view.findViewById(R.id.navi_header_setting_option);
        // 고객센터
        info = view.findViewById(R.id.navi_header_info);
        // 로그아웃
        logout = view.findViewById(R.id.navi_header_logout);

        // 채팅방 리스트
        this.chatRoomList = new ArrayList();

        // 바텀 메뉴
        bthome = findViewById(R.id.message_tohome);
        btissue = findViewById(R.id.message_toissue);
        btmessage = findViewById(R.id.message_tomessage);
        btprofile = findViewById(R.id.message_toprofile);
        btwallet = findViewById(R.id.message_towallet);

        // 채팅방 추가 버튼
        btnChatRoomAdd = findViewById(R.id.message_chat_room_add);

        // 바텀 메뉴 - 스택 X 액티비티 이동 (TODO 바텀 내비게이션으로 변경하는 작업)
        bottomNavi();
        // 클릭 리스너 모음 - 스택 O
        clickListeners();
        // 내비 터치
        NaviTouch();

        // 뷰모델 생성
        chattingRoomViewModel = new ViewModelProvider(this).get(ChattingRoomViewModel.class);

        // 뷰모델 변경 시 리사이클러뷰에 변경된 데이터 세팅
        observe();

        // 채팅서비스와 연결이 돼있다면
        if(ChatService.IS_BOUND_MAIN_ACTIVITY){
            // 리사이클러뷰 데이터 가져오기
            loadrecycler();
        }
    }

    // 바텀 메뉴 클릭
    private void bottomNavi() {
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.message_tohome:
                        Intent mIntent1 = new Intent(getApplicationContext(), MM_Home.class);
                        mIntent1.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(mIntent1);
                        finish();
                        break;

                    case R.id.message_toissue:
                        Intent mIntent2 = new Intent(getApplicationContext(), MM_Issue.class);
                        mIntent2.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(mIntent2);
                        finish();
                        break;

                    case R.id.message_tomessage:
                        break;

                    case R.id.message_toprofile:
                        Intent mIntent3 = new Intent(getApplicationContext(), MM_Profile.class);
                        mIntent3.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(mIntent3);
                        finish();
                        break;

                    case R.id.message_towallet:
                        Intent mIntent4 = new Intent(getApplicationContext(), MM_Wallet.class);
                        mIntent4.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(mIntent4);
                        finish();
                        break;

                    default:
                        break;
                }
            }
        };

        bthome.setOnClickListener(clickListener);
        btissue.setOnClickListener(clickListener);
        btwallet.setOnClickListener(clickListener);
        btmessage.setOnClickListener(clickListener);
        btprofile.setOnClickListener(clickListener);

    }

    // 내비 터치치
    private void NaviTouch() {

        // 내비뷰 메뉴 레이아웃에 직접 구현
//       CircleImageView myAccount;
//       LinearLayout myActivity, myList, myCommunity;
//       TextView settingOption, info, logout;
        myAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent3 = new Intent(getApplicationContext(), MM_Profile.class);
                mIntent3.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(mIntent3);
                finish();
            }
        });
        myActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "TODO 보상 체계 구현 (with 지갑)", Toast.LENGTH_SHORT).show();
            }
        });
        /*myList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  Toast.makeText(getApplicationContext(), "겉멋", Toast.LENGTH_SHORT).show();
            }
        });*/
        myCommunity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(getApplicationContext(), Community_Home_Feeds.class);
                startActivity(mIntent);
            }
        });
        settingOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingIntent = new Intent(getApplicationContext(), SS_Setting.class);
                startActivity(settingIntent);
            }
        });
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "고객센터", Toast.LENGTH_SHORT).show();
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toLogin = new Intent(getApplicationContext(), LL_Login.class);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                startActivity(toLogin);
                finish();
                Toast.makeText(getApplicationContext(), "로그아웃", Toast.LENGTH_SHORT).show();
            }
        });


    }

    // 클릭 이벤트 모음
    private void clickListeners() {

        // 좌측 상단 메뉴 버튼
        sidemenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        // DrawerLayer (사이드 메뉴) 내부 카테고리 클릭 = 별로인듯... 그냥 참고용으로 쓰기 (메뉴 대신 헤더 xml 에서 전부 완성 시킴)
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
//                    case R.id.nav_camera:
//                        item.setChecked(true);
//                        Toast.makeText(getApplicationContext(), "ㅇㅇ",Toast.LENGTH_SHORT).show();
//                        drawerLayout.closeDrawers();
//                        return true;

                }
                return false;
            }
        });

        // 리사이클러뷰 새로고침 인식
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
//                        loadrecycler();
                        Toast.makeText(getApplicationContext(), "TODO 새로고침", Toast.LENGTH_SHORT).show();
                        /* 업데이트가 끝났음을 알림 */
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });

        // 채팅방 추가 버튼
        btnChatRoomAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toChatMemberSelect = new Intent(getApplicationContext(), ChatMemberSelect.class);

                startActResultForChatMemberSelect.launch(toChatMemberSelect);
            }
        });
    }

    // 채팅서비스와 연결한다.
    public void connectToService(){
        // 서비스 커넥션 생성
        conn = new ServiceConnection() {
            // 서비스와 연결된 경우 호출
            // 우리는 메신저로 서비스와 통신하기 때문에 클라이언트를 대표하는 날것의 IBinder 객체가 있다.
            public void onServiceConnected(ComponentName className, IBinder service) {
                // 서비스와 연결된 메신저 객체 생성
                mServiceMessenger = new Messenger(service);
                Log.e("채팅서비스와 연결 - mServiceMessenger 생성", mServiceMessenger.toString());

                Log.e("채팅서비스와 연결 - ChatService.IS_BOUND_MAIN_ACTIVITY true로 변경", "" + ChatService.IS_BOUND_MAIN_ACTIVITY);

                try {
                    // 서비스에게 보낼 메시지를 생성한다.
                    // 파라미터: 핸들러, msg.what에 들어갈 int값
                    Message msg = Message.obtain(null, ChatService.CONNECT_HOME_ACT);
                    Log.e("채팅서비스와 연결 - 서비스로 보낼 메시지 객체 생성", "" + msg);

                    // 서비스에 userId 보내기
                    Bundle bundle = msg.getData();
                    bundle.putString("userId", userId);

                    // 메시지의 송신자를 넣어준다.
                    msg.replyTo = mActivityMessenger;
                    Log.e("채팅서비스와 연결 - 메시지를 처리할 메신저 객체 넣어주기 msg.replyTo = mActivityMessenger", "" + mActivityMessenger);

                    // 서비스에 연결됐다는 메시지를 전송한다.
                    mServiceMessenger.send(msg);
                    Log.e("채팅서비스와 연결 - 서비스에 메시지 전송", "" + msg);

                    // 리사이클러뷰 데이터 가져오기
                    loadrecycler();

                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            // 예상치 못하게 연결이 종료되는 경우 호출되는 콜백 함수
            public void onServiceDisconnected(ComponentName className) {
                // 서비스의 메시지를 받는 메신저를 null로 변경
                mServiceMessenger = null;
                // 연결이 종료되었으므로 연결여부를 false로 봐꿔준다.
                ChatService.IS_BOUND_MAIN_ACTIVITY = false;
            }
        };

        // 서비스시작! - 서비스에 바인드 한다.
        // Intent와 ServiceConnection 객체를 파라미터로 넣는다.
        Intent intent1 = new Intent(getApplicationContext(), ChatService.class);

        startService(intent1);
        Log.e("MemberHomeAct에서 Socket 연결 0. startService", "true");
        getApplicationContext().bindService(intent1, conn, Context.BIND_AUTO_CREATE);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                // 왼쪽 상단 버튼 눌렀을 때
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    //뒤로가기 했을 때
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            toast = Toast.makeText(this, "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
            toast.show();
            return;
        } else if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            finish();
            toast.cancel();
        } else {
            super.onBackPressed();
        }
    }

    // 액티비티 종료 시, 애니메이션 효과 없애기
    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    // ==============================================================================================

    // 데이터 http 요청
    private void loadrecycler() {
        // 채팅방 리스트 데이터 가져오기
        fillList();

        // 리사이클러뷰 초기세팅(아답터는 데이터 가져올 때마다 할 것이므로 여기서 해주지 않음)
        setUpRecyclerView();
    }

    // 서버에서 채팅방 리스트 정보 가져오기
    private void fillList() {
        ApiInterface apiClient = ApiClient.getApiClient(MM_Message.this).create(ApiInterface.class);
        Call<ArrayList<Chat_Room_Info>> call = apiClient.selectChatRoomListUsingUserId(userId);
        call.enqueue(new Callback<ArrayList<Chat_Room_Info>>()
        {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(@NonNull Call<ArrayList<Chat_Room_Info>> call, @NonNull Response<ArrayList<Chat_Room_Info>> response)
            {
                if (response.isSuccessful() && response.body() != null)
                {
                    // 채팅방 리스트 - latestMsg에 들어있는 문자열은 마지막 채팅 메시지를 바이너리 형태로 저장한 것이다. 따라서 이것을 Chat_Item 형태로 변경해줘야 한다.
                    ArrayList<Chat_Room_Info> chatRoomList = response.body();
                    Log.e("채팅방 리스트 조회 성공: ", "1111");

                    //
                    for(int i = 0; i < chatRoomList.size(); i++) {
                        // 채팅방 정보
                        Chat_Room_Info chatRoomInfo = chatRoomList.get(i);

                        // 최신 메시지
                        String latestMsg = chatRoomInfo.getLatestMsg();
                        
                        // 채팅 메시지세팅
                        chatRoomList.get(i).setLatestMsg(latestMsg);
                        Log.e("채팅 텍스트로 latestMsg 세팅(chatRoomId - " + chatRoomInfo.getChatRoomId() + "): ", latestMsg);

                        try {
                            ChatService.mqttClient.subscribe("/topics/" + chatRoomInfo.getChatRoomId(),2);
                        } catch (MqttException e) {
                            e.printStackTrace();
                        }
                    }

                    // 뷰모델에 채팅방 데이터리스트 넘겨주기(채팅방 리스트는 얘가 관리)
                    chattingRoomViewModel.getChatRoomList().setValue(chatRoomList);

                    chatRoomIdMap = new HashMap<>();
                    
                    // chatRoomIdMap에 모든 채팅방 아이디와 마지막 메시지 아이디 넣어서 관리 => 채팅방 아이디, 마지막 메시지 아이디
                    for(Chat_Room_Info chatRoomInfo : chatRoomList){
                        chatRoomIdMap.put(chatRoomInfo.getChatRoomId(), chatRoomInfo.getLatestMsgId());
                    }
                } else {
                    Log.e("채팅방 리스트 조회 성공 했지만 데이터 없음: ", "1111");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<Chat_Room_Info>> call, @NonNull Throwable t)
            {
                Log.e("채팅방 리스트 조회 실패: ", "1111");
            }
        });
    }

    private void setUpRecyclerView() {
        // 어댑터에 데이터가 crud될 때마다 리사이클러뷰의 크기를 계산하고 이때 자원을 소모한다.
        // 그런데 채팅리스트는 데이터 갯수가 변경된다고 해도 리사이클러뷰의 크기는 항상 그대로 유지되도 상관없다.
        // 따라서 변경시마다 재계산할 필요가 없으므로 앱에게 리사이클러뷰의 크기는 항상 동일하다고 알려주는 것이 자원소모를
        // 하지 않을 수 있어서 효율적이다. 따라서 이 값을 true로 설정한다
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(layoutManager);

        // 리사이클러뷰의 위치를 저장하기 위한 값 => 화면전환 등의 상황에서 리사이클러뷰 스크롤의 위치를 저장해야 할 경우 사용할 수 있음!
        //recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();

        //위치 유지
        //recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
    }

    // 결과값을 가지고 돌아오는 액티비티를 설정하기 휘한 함수
    public void setUpStartActForResult(){
        // 채팅방 만들기
        startActResultForChattingRoom = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override public void onActivityResult(ActivityResult result) {
                        Log.d("df","11");
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            /*// 채팅 참여자 리스트
                            Chat_Room_Info chatRoomInfo = (Chat_Room_Info) result.getData().getSerializableExtra("chatRoomInfo");

                            // viewModel에 채팅방 정보를 업데이트 해준다!
                            // 기존의 채팅방정보 리스트
                            ArrayList<Chat_Room_Info> chatRoomList = chattingRoomViewModel.getChatRoomList().getValue();
                            chatRoomList.add(0,chatRoomInfo);

                            chattingRoomViewModel.getChatRoomList().setValue(chatRoomList);

                            // 채팅방아이디를 관리하는 맵에도 데이터를 넣어준다!
                            chatRoomIdMap.put(chatRoomInfo.getChatRoomId(), chatRoomInfo.getLatestMsgId());*/
                        }
                    }
                });

        // 채팅방 멤버 선택 화면
        startActResultForChatMemberSelect = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // 새로운 채팅방 여부 - 채팅방 리스트 화면에서 넘어온 경우 값이 없으므로 false로 세팅
                            boolean isNew = result.getData().getBooleanExtra("isNew", false);

                            // 메시지 전송됐는지 여부 - 채팅방 리스트 화면에서 넘어온 경우 값이 없으므로 true로 세팅
                            boolean isMsgTransfered = result.getData().getBooleanExtra("isMsgTransfered", true);

                            // 채팅 참여자 리스트
                            ArrayList<OtherUserInfo> chatMemberList = (ArrayList<OtherUserInfo>) result.getData().getSerializableExtra("chatMemberList");

                            // chatRoomId 가져오기
                            String chatRoomId = result.getData().getStringExtra("chatRoomId");

                            // 채팅참여자 FCM 토큰리스트
                            Chat_Member_FCM_Sub chatMemberFcmSub = (Chat_Member_FCM_Sub) result.getData().getSerializableExtra("chatMemberFcmTokenList");

                            // 채팅방 생성 혹은 파괴 시 사용하는 객체
                            Chat_Room_Cre_Or_Del chatRoomCreOrDel = (Chat_Room_Cre_Or_Del) result.getData().getSerializableExtra("chatRoomCreOrDel");

                            // 채팅방 액티비티로 이동
                            Intent mIntent = new Intent(getApplicationContext(), ChattingRoom.class);
                            mIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

                            // 채팅방 아이디
                            mIntent.putExtra("chatRoomId",chatRoomId);
                            Log.d("chatRoomId",chatRoomId);

                            // 채팅방 명
                            mIntent.putExtra("chatRoomName",chatRoomCreOrDel.getChatRoomNameCreator());
                            Log.d("chatRoomName",chatRoomCreOrDel.getChatRoomNameCreator());

                            // 새로 생성된 채팅방
                            mIntent.putExtra("isNew",isNew);
                            Log.d("chatRoomId","" + isNew);

                            // 메시지 전송여부 - 메시지가 전송되지 않고 채팅방이 제거되면 MQTT와 FCM 구독을 해지해야 함
                            mIntent.putExtra("isMsgTransfered",isMsgTransfered);
                            Log.d("isMsgTransfered","" + isMsgTransfered);

                            // 채팅멤버 리스트
                            mIntent.putExtra("chatMemberList", chatMemberList);
                            Log.d("chatMemberList","" + chatMemberList.size());

                            // FCM 토큰리스트
                            mIntent.putExtra("chatMemberFcmTokenList", chatMemberFcmSub);
                            //Log.d("chatMemberFcmTokenList", chatMemberFcmSub.toString());

                            // 채팅방 생성 혹은 파괴 객체정보
                            mIntent.putExtra("chatRoomCreOrDel", chatRoomCreOrDel);
                            //Log.d("chatRoomCreOrDel",chatRoomCreOrDel.toString());

                            // 채팅방 화면으로 이동!
                            startActResultForChattingRoom.launch(mIntent);
                        }
                    }
                });
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int position = -1;
        Chat_Room_Info chatRoomInfo;

        try {
            position = adapter.getPosition();
            chatRoomInfo = adapter.getItem(position);
        } catch (Exception e) {
            Log.d("에러", e.getLocalizedMessage(), e);
            return super.onContextItemSelected(item);
        }
        switch (item.getItemId()) {
            case R.id.action_chat_room_delete:
                // 기존의 채팅방정보 리스트
                ArrayList<Chat_Room_Info> chatRoomList = chattingRoomViewModel.getChatRoomList().getValue();
                chatRoomList.remove(position);

                chattingRoomViewModel.getChatRoomList().setValue(chatRoomList);

                // 채팅방아이디를 관리하는 맵도 삭제!
                chatRoomIdMap.remove(chatRoomInfo.getChatRoomId());

                String chatRoomId = chatRoomInfo.getChatRoomId();

                // 채팅방 나가기
                ApiInterface apiClient = ApiClient.getApiClient(MM_Message.this).create(ApiInterface.class);
                Call<String> call = apiClient.deleteChatParticipant(UserInfo.user_id, chatRoomId);
                call.enqueue(new Callback<String>()
                {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response)
                    {
                        if (response.isSuccessful() && response.body() != null)
                        {
                            // 채팅방 아이디
                            String msg = response.body();

                            Log.e("채팅방 정보 삭제 성공  ", chatRoomId);

                            // 채팅방 unsubscribe
                            Chat_Item chat_item = new Chat_Item(chatRoomId, userId, ("!!!!!!" + UserInfo.user_nick + "님이 나갔습니다."),null, GetDate.getDateWithYMDAndWeekDay(), GetDate.getAmPmTime(), UserInfo.user_nick, 0);

                            try {
                                String exitMsg = chatUtil.chatItemToString(chat_item);
                                chatUtil.publishChatMsg(exitMsg,chatRoomId,ChatService.mqttClient);
                                ChatService.mqttClient.unsubscribe("/topics/" + chatRoomId);
                            } catch (MqttException e) {
                                e.printStackTrace();
                            }

                            // FCM서버에게 구독 해지요청
                            FirebaseMessaging.getInstance().unsubscribeFromTopic("/topics/" + chatRoomId)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            String msg = "UnSubscribed";
                                            if (!task.isSuccessful()) {
                                                msg = "UnSubscribe failed";
                                            }
                                            Log.d("FCM에게 구독해지 요청!", msg);
                                        }
                                    });

                            // 채팅방 사용자수 맵 업데이트
                            chatUtil.updateUserCountMap(chatRoomId,2, 0,userId);
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
                break;
        }
        return super.onContextItemSelected(item);
    }
    
    // 뷰모델 observe
    public void observe(){
        Log.d("여기 들어옴?", "!1");

        // 가져온 채팅방리스트정보 observe
        chattingRoomViewModel.getChatRoomList().observe(this, chatRoomList -> {
            adapter = new Messages_Adapter(this, chatRoomList);
            Log.d("여기 들어옴?", "22");

            recyclerView.setAdapter(adapter);

        });
    }
}
