package com.example.bubbly;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

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
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bubbly.Kim_Bottom.Bottom1_Fragment;
import com.example.bubbly.Kim_Bottom.Bottom2_Fragment;
import com.example.bubbly.Kim_Bottom.Bottom3_Fragment;
import com.example.bubbly.Kim_Bottom.Bottom4_Fragment;
import com.example.bubbly.chatting.service.ChatService;
import com.example.bubbly.chatting.service.FCMService;
import com.example.bubbly.chatting.viewmodel.ChattingRoomViewModel;
import com.example.bubbly.config.Config;
import com.example.bubbly.model.AccessAndRefreshToken;
import com.example.bubbly.model.Chat_Item;
import com.example.bubbly.model.Chat_Room_Info;
import com.example.bubbly.model.OtherUserInfo;
import com.example.bubbly.model.UserInfo;
import com.example.bubbly.retrofit.ApiClient;
import com.example.bubbly.retrofit.ApiInterface;
import com.example.bubbly.retrofit.ChatApiClient;
import com.example.bubbly.retrofit.ChatApiInterface;
import com.example.bubbly.retrofit.user_Response;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

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
import retrofit2.http.HEAD;

public class MainActivity extends AppCompatActivity {

    // 뒤로가기 시간
    private long backKeyPressedTime = 0;
    private Toast toast;

    private FragmentManager fragmentManager = getSupportFragmentManager();
    private Bottom1_Fragment bottom1_fragment = new Bottom1_Fragment();
    private Bottom2_Fragment bottom2_fragment = new Bottom2_Fragment();
    private Bottom3_Fragment bottom3_fragment = new Bottom3_Fragment();
    private Bottom4_Fragment bottom4_fragment = new Bottom4_Fragment();

    // 딥링크 데이터
    String deep_type, deep_id;

    // 내비뷰 메뉴 레이아웃에 직접 구현
    CircleImageView myAccount;
    LinearLayout myActivity, myList, myCommunity;
    TextView settingOption, info, logout;
    View view;

    public static DrawerLayout drawerLayout;
    public static NavigationView navigationView;

    // 유저 정보
    SharedPreferences preferences;
    String userId;

    /**
     * 채팅서비스 연결 로직
     */
    // 서비스와 통신하기 위한 메신저
    private Messenger mServiceMessenger = null;

    // 서비스로 보낼 메신저
    final Messenger mActivityMessenger = new Messenger(new MainActivity.ActivityHandler());

    // 서비스의 상태에 따라 콜백 함수를 호출하는 객체.
    private ServiceConnection conn;

    // 채팅방리스트를 관리할 뷰모델
    private ChattingRoomViewModel chattingRoomViewModel;

    // 해당 사용자가 참여하고 있는 채팅방의 아이디와 사용자수를 관리하는 맵(메시지 수신 시 해당하는 채팅방이 있는지 여부를 쉽게 관리하기 위한 맵)
    private HashMap<String, Integer> chatRoomIdMap;

    // 서비스로부터 받은 메시지를 처리할 핸들러
    class ActivityHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            // msg.what에는 메시지 종류가 들어있음(서비스에서 설정)
            switch (msg.what) {
                // 메시지 수신
                case ChatService.MSG_RECEIVE_FROM_SERVER:
                    // 서비스로부터 받은 수신한 메시지 객체
                    Bundle bundle = msg.getData();
                    Chat_Item read = (Chat_Item) bundle.getSerializable("message");

                    String chatRoomId = read.getChatRoomId();

                    Log.d("브로커에게 메시지 수신 시 ChatService에서 MM_Message로 메시지 전달", read.toString());

                    /**
                     * 서비스로부터 받은 메시지 처리
                     * */
                    // 1. 해당 메시지의 chatRoomId에 해당하는 채팅방 정보가 있는지 확인
                    Integer latestMsgId = chatRoomIdMap.get(chatRoomId);

                    Log.d("MM_Message에서 관리하고 있는 채팅방인지 여부 null이면 없는 채팅방!: ", latestMsgId != null ? latestMsgId.toString() : "null");

                    // 이 메시지와 동일한 채팅방 아이디를 가지는 채팅방이 없음 => 새로 생성!
                    if (latestMsgId == null) {
                        ApiInterface apiClient = ApiClient.getApiClient(MainActivity.this).create(ApiInterface.class);
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

                                    chatRoomList.add(0, chatRoomInfo);

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

                        for (int i = 0; i < chatRoomList.size(); i++) {
                            Chat_Room_Info chatRoomInfo = chatRoomList.get(i);

                            if (chatRoomInfo.getChatRoomId().equals(chatRoomId)) {
                                chatRoomInfo.setLatestMsg(read.getChatText());
                                chatRoomInfo.setLatestMsgId(read.getChatId());

                                // 브로커로부터 받은 메시지의 시간은 yyyy년 mm월 dd일 (요일) 오전/오후 hh:mm의 형태로 보여짐
                                // db에서 조회할 땐 yyyy.MM.dd hh:mm의 형태로 될 것임 => 둘을 맞춰줘야 함!!
                                // 최신 메시지 수신 시간
                                chatRoomInfo.setLatestMsgTime(read.getChatTime());
                                // 해당 위치의 아이템 삭제
                                chatRoomList.remove(i);
                                // 가장 최근에 메시지를 받았으므로 0번쨰 인덱스로 이동!
                                chatRoomList.add(0, chatRoomInfo);

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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("리프레시 토큰", "00-4");


        // 스플래시 화면을 거치지 않고 채팅 리스트 화면으로 왔다면 설정파일의 데이터를 가져온다.
        if (Config.api_server_addr == null) {
            try {
                new Config(getApplicationContext()).getConfigData();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        initialize();


        // 딥링크로 왔다면 액티비티 띄우기
        deeplink();

        Log.d("리프레시 토큰", "00-3");

        // noti를 클릭해서 MainAct로 왔다면
        if (getIntent().getExtras().get("chatRoomId") != null || getIntent().getExtras().get("postWriterId") != null) {
            Log.d("리프레시 토큰", "00-1");

            String chatRoomId = null;
            String postWriterId = null;

            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.d("MainActivity 확인확인: ", "Key: " + key + " Value: " + value);


                if (key.contains("chatRoomId")) {
                    chatRoomId = (String) value;
                    break;
                }

                if(key.contains("postWriterId")){
                    postWriterId = (String)value;
                    break;
                }
            }

            // 서비스와 연결이 되어있다면 즉, 백그라운드에서 noti를 클릭해서 MainAct를 다시 시작했다면
            // 이전 메인 액티비티-서비스와의 연결을 끊고 새로운 메인액티비티-서비스 연결을 만들어 준다.
            if (ChatService.IS_BOUND_MAIN_ACTIVITY) {
                // 서비스가 이미 실행중이라면 다시 binde만 시켜준다!
                bindToService();
            }

            //쉐어드프리퍼런스에서 로그인정보 가져오기
            String id = "";
            String pw = "";
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
                id = sharedPreferences.getString("id", "");
                pw = sharedPreferences.getString("pw", "");
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (id.equals("") & pw.equals("")) {
                //만약 쉐어드프리퍼런스에 저장된 사용자 정보가 없으면 로그인 페이지로 이동
                startActivity(new Intent(MainActivity.this, LL_Login.class));
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                finish();
            } else {
                //만약 쉐어드프리퍼런스에 저장된 사용자 정보기 있으면 login api 요청 후 Home으로 이동
                ApiInterface login_api = ApiClient.getApiClient(MainActivity.this).create(ApiInterface.class);
                Call<String> call = login_api.login(id, pw);
                String finalChatRoomId = chatRoomId;
                String finalPostWriterId = postWriterId;
                call.enqueue(new Callback<String>()
                {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Log.e("로그인 데이터", response.body().toString());
                            if (response.body().toString().equals("fail")) {
                                Toast.makeText(getApplicationContext(), "로그인 실패", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "로그인 성공", Toast.LENGTH_SHORT).show();
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
                                        String mnemonic = sharedPreferences.getString("mnemonic", "");
                                        Log.e("니모닉", mnemonic);
                                        UserInfo.mnemonic = mnemonic;
                                    } catch (GeneralSecurityException e) {
                                        e.printStackTrace();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    //회원정보를 요청한다.
                                    Call<List<user_Response>> call_userInfo = login_api.selectUserInfo("" + user_id);
                                    call_userInfo.enqueue(new Callback<List<user_Response>>() {
                                        @Override
                                        public void onResponse(@NonNull Call<List<user_Response>> call, @NonNull Response<List<user_Response>> response) {
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
                                            if (responseResult.get(0).getProfile_file_name() != null && !responseResult.get(0).getProfile_file_name().equals("")) {
                                                UserInfo.profile_file_name = Config.cloudfront_addr + responseResult.get(0).getProfile_file_name();
                                            }

                                            // 채팅방 알림 클릭 시
                                            if (finalChatRoomId != null && !finalChatRoomId.equals("")) {
                                                // 채팅방으로 이동
                                                moveToChattingRoom(finalChatRoomId);
                                            } else if (finalPostWriterId != null && !finalPostWriterId.equals("")) {
                                                // 게시물 작성 알림 클릭해서 들어온거라면
                                                Log.d("게시물 노티 클릭해서 들어옴!", "11");
                                                moveToPostDetail(finalPostWriterId);
                                            }
                                        }

                                        @Override
                                        public void onFailure(@NonNull Call<List<user_Response>> call, @NonNull Throwable t) {
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
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                        Log.e("로그인 에러", t.getMessage());
                    }
                });
            }
        } else {
            Log.d("리프레시 토큰", "00");

            FCMService.refreshToken(userId);

        }

    }

    private void moveToPostDetail(String finalPostWriterId) {
        Intent post = new Intent(getApplicationContext(), SS_PostDetail.class);
        post.putExtra("post_id", finalPostWriterId);
        startActivity(post);
    }

    private void deeplink() {

        Bundle extras = getIntent().getExtras();
        deep_type = extras.getString("deep_type", "");
        deep_id = extras.getString("deep_id", "");


        // 딥링크가 존재
        if (deep_type != null) {
            // 타입 (커뮤니티, 게시물, 프로필) 어떤거로 이동할지 선택
            // 쿠팡 참고 결과 → 액티비티 스택 모두 제거함 ⇒ 스플래시에서 넘어올 때, 다 없앰 = 그냥 하면 됨

            switch (deep_type) {
                case "community":
                    Intent com = new Intent(getApplicationContext(), Community_MainPage.class);
                    com.putExtra("com_id", deep_id);
                    startActivity(com);
                    break;

                case "profile":
                    Intent profile = new Intent(getApplicationContext(), SS_Profile.class);
                    profile.putExtra("user_id", deep_id);
                    startActivity(profile);
                    break;

                case "post":
                    Intent post = new Intent(getApplicationContext(), SS_PostDetail.class);
                    post.putExtra("post_id", deep_id);
                    startActivity(post);
                    break;

                default:
                    break;
            }

        }

    }

    public void initialize() {
        preferences = getSharedPreferences("novarand", MODE_PRIVATE);
        userId = preferences.getString("user_id", "");

        // 채팅서비스와 연결한다.
        if (!ChatService.IS_BOUND_MAIN_ACTIVITY) {
            connectToService();
            Toast.makeText(MainActivity.this, "서비스와 연결", Toast.LENGTH_SHORT).show();
        }
        chattingRoomViewModel = new ViewModelProvider(this).get(ChattingRoomViewModel.class);

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.home_frame, bottom1_fragment).commitAllowingStateLoss();

        BottomNavigationView bottomNavigationView = findViewById(R.id.home_bottom);
        bottomNavigationView.setOnNavigationItemSelectedListener(new ItemSelecedListener2());

        navigationView = findViewById(R.id.main_navigation_view);
        drawerLayout = findViewById(R.id.drawer_layout);
        LayoutInflater.from(getApplicationContext()).inflate(R.layout.navi_header, navigationView);

        view = navigationView.inflateHeaderView(R.layout.navi_header);
        // 사이드메뉴
        // 내비 안 메뉴
        myAccount = view.findViewById(R.id.navi_header_profileimg);
        myActivity = view.findViewById(R.id.navi_header_myActivity);
        myList = view.findViewById(R.id.navi_header_myList);
        myCommunity = view.findViewById(R.id.navi_header_myCommunity);
        settingOption = view.findViewById(R.id.navi_header_setting_option);
        info = view.findViewById(R.id.navi_header_info);
        logout = view.findViewById(R.id.navi_header_logout);

        NaviTouch();
    }

    private class ItemSelecedListener2 implements BottomNavigationView.OnNavigationItemSelectedListener {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            switch (item.getItemId()) {
                case R.id.mm_home:
                    transaction.replace(R.id.home_frame, bottom1_fragment).commitAllowingStateLoss();

                    break;

                case R.id.mm_issue:
                    transaction.replace(R.id.home_frame, bottom2_fragment).commitAllowingStateLoss();
                    break;

                case R.id.mm_message:
                    transaction.replace(R.id.home_frame, bottom3_fragment).commitAllowingStateLoss();
                    break;

                case R.id.mm_profile:
                    transaction.replace(R.id.home_frame, bottom4_fragment).commitAllowingStateLoss();
                    break;
            }
            return true;
        }
    }

    // 내비 터치
    private void NaviTouch() {
        // 내비뷰 메뉴 레이아웃에 직접 구현
        // CircleImageView myAccount;
        // LinearLayout myActivity, myList, myCommunity;
        // TextView settingOption, info, logout;
        myAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawers();
                Intent mIntent3 = new Intent(getApplicationContext(), MM_Profile.class);
                mIntent3.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(mIntent3);
                finish();
            }
        });
        myActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawers();
                Intent wallet = new Intent(getApplicationContext(), MM_Wallet_toNavi.class);
                startActivity(wallet);
            }
        });
        myList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawers();
                //Toast.makeText(getApplicationContext(), "겉멋", Toast.LENGTH_SHORT).show();
            }
        });
        myCommunity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawers();
                Intent mIntent = new Intent(getApplicationContext(), Community_Home_Feeds.class);
                startActivity(mIntent);
            }
        });
        settingOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawers();
                Intent settingIntent = new Intent(getApplicationContext(), SS_Setting.class);
                startActivity(settingIntent);
            }
        });
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawers();

                Intent email = new Intent(Intent.ACTION_SEND);
                email.setType("plain/Text");
                email.putExtra(Intent.EXTRA_EMAIL, "유저 아이디: " + UserInfo.user_id);
                email.putExtra(Intent.EXTRA_SUBJECT, "<" + getString(R.string.app_name) + " 문의>");
                email.putExtra(Intent.EXTRA_TEXT, "기기명:\n안드로이드 OS:\n내용:\n");
                email.setType("message/rfc822");
                startActivity(email);

            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 로그아웃 시 api 서버의 모든 토큰정보를 지워준다.
//                ApiInterface apiClient = ApiClient.getApiClient(MainActivity.this).create(ApiInterface.class);
//                Call<String> call = apiClient.logoutFromApiServer(userId);
//                call.enqueue(new Callback<String>()
//                {
//                    @RequiresApi(api = Build.VERSION_CODES.O)
//                    @Override
//                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response)
//                    {
//                        if (response.isSuccessful() && response.body() != null)
//                        {
//                            if(response.body().equals("logout success")){
//                                // 로그아웃 시 채팅 서버의 모든 토큰정보를 지워준다.
//                                ChatApiInterface chatApiInterface = ChatApiClient.getApiClient(MainActivity.this).create(ChatApiInterface.class);
//                                Call<String> call2 = chatApiInterface.logoutFromChatServer(UserInfo.token, userId);
//                                call2.enqueue(new Callback<String>()
//                                {
//                                    @RequiresApi(api = Build.VERSION_CODES.O)
//                                    @Override
//                                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response)
//                                    {
//                                        if (response.isSuccessful() && response.body() != null)
//                                        {
//                                            if(response.body().equals("logout success")){
                drawerLayout.closeDrawers();
                Intent toLogin = new Intent(getApplicationContext(), LL_Login.class);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                startActivity(toLogin);
                finish();
                Toast.makeText(getApplicationContext(), "로그아웃", Toast.LENGTH_SHORT).show();
//                                            } else {
//                                                Log.e("채팅 서버 로그아웃 데이터 ", response.body());
//                                            }
//                                        } else {
//                                            Log.e("채팅 서버 로그아웃 성공했지만 데이터 없음 ", "1111");
//                                        }
//                                    }

//                                    @Override
//                                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t)
//                                    {
//                                        Log.e("채팅서버 로그아웃 시실패: ", "1111");
//                                    }
//                                });
//                            } else {
//                                    Log.e("api 서버 로그아웃 데이터 ", response.body());
//                            }
//                        } else {
//                            Log.e("api 서버 로그아웃 성공했지만 데이터 없음 ", "1111");
//                        }
//                    }

//                    @Override
//                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t)
//                    {
//                        Log.e("api 서버 로그아웃 시실패: ", "1111");
//                    }
//                });
            }
        });
    }

    // 채팅서비스와 연결한다.
    public void connectToService() {
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
                    loadChatList();

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

    public void bindToService() {
        // 서비스 커넥션 생성
        conn = new ServiceConnection() {
            // 서비스와 연결된 경우 호출
            // 우리는 메신저로 서비스와 통신하기 때문에 클라이언트를 대표하는 날것의 IBinder 객체가 있다.
            public void onServiceConnected(ComponentName className, IBinder service) {
                // 서비스와 연결된 메신저 객체 생성
                mServiceMessenger = new Messenger(service);
                Log.e("채팅서비스와 연결 - mServiceMessenger 생성", mServiceMessenger.toString());

                try {
                    // 서비스에게 보낼 메시지를 생성한다.
                    // 파라미터: 핸들러, msg.what에 들어갈 int값
                    Message msg = Message.obtain(null, ChatService.RE_CONNECT_HOME_ACT);
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
                    loadChatList();

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

        Log.e("MemberHomeAct에서 Socket 연결 0. startService", "true");
        getApplicationContext().bindService(intent1, conn, Context.BIND_AUTO_CREATE);
    }

    // 데이터 http 요청
    private void loadChatList() {
        // 채팅방 리스트 데이터 가져오기
        fillList();
    }

    // 서버에서 채팅방 리스트 정보 가져오기
    private void fillList() {
        ApiInterface apiClient = ApiClient.getApiClient(MainActivity.this).create(ApiInterface.class);
        Call<ArrayList<Chat_Room_Info>> call = apiClient.selectChatRoomListUsingUserId(userId);
        call.enqueue(new Callback<ArrayList<Chat_Room_Info>>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(@NonNull Call<ArrayList<Chat_Room_Info>> call, @NonNull Response<ArrayList<Chat_Room_Info>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // 채팅방 리스트 - latestMsg에 들어있는 문자열은 마지막 채팅 메시지를 바이너리 형태로 저장한 것이다. 따라서 이것을 Chat_Item 형태로 변경해줘야 한다.
                    ArrayList<Chat_Room_Info> chatRoomList = response.body();
                    Log.e("채팅방 리스트 조회 성공: ", "1111");

                    //
                    for (int i = 0; i < chatRoomList.size(); i++) {
                        // 채팅방 정보
                        Chat_Room_Info chatRoomInfo = chatRoomList.get(i);

                        // 최신 메시지
                        String latestMsg = chatRoomInfo.getLatestMsg();

                        // 채팅 메시지세팅
                        chatRoomList.get(i).setLatestMsg(latestMsg);
                        Log.e("채팅 텍스트로 latestMsg 세팅(chatRoomId - " + chatRoomInfo.getChatRoomId() + "): ", latestMsg);

                        try {
                            ChatService.mqttClient.subscribe("/topics/" + chatRoomInfo.getChatRoomId(), 2);
                        } catch (MqttException e) {
                            e.printStackTrace();
                        }
                    }

                    // 뷰모델에 채팅방 데이터리스트 넘겨주기(채팅방 리스트는 얘가 관리)
                    chattingRoomViewModel.getChatRoomList().setValue(chatRoomList);

                    chatRoomIdMap = new HashMap<>();

                    // chatRoomIdMap에 모든 채팅방 아이디와 마지막 메시지 아이디 넣어서 관리 => 채팅방 아이디, 마지막 메시지 아이디
                    for (Chat_Room_Info chatRoomInfo : chatRoomList) {
                        chatRoomIdMap.put(chatRoomInfo.getChatRoomId(), chatRoomInfo.getLatestMsgId());
                    }
                } else {
                    Log.e("채팅방 리스트 조회 성공 했지만 데이터 없음: ", "1111");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<Chat_Room_Info>> call, @NonNull Throwable t) {
                Log.e("채팅방 리스트 조회 실패: ", "1111");
            }
        });
    }

    // 채팅방으로 이동!
    public void moveToChattingRoom(String chatRoomId) {
        // 채팅방으로 이동
        ApiInterface apiClient = ApiClient.getApiClient(MainActivity.this).create(ApiInterface.class);
        Call<ArrayList<OtherUserInfo>> call = apiClient.selectChatParticipantUsingChatRoomId(chatRoomId);
        call.enqueue(new Callback<ArrayList<OtherUserInfo>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<OtherUserInfo>> call, @NonNull Response<ArrayList<OtherUserInfo>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // 채팅방 아이디
                    ArrayList<OtherUserInfo> chatMemberList = response.body();

                    // 채팅방 정보 가져오기
                    ApiInterface apiClient = ApiClient.getApiClient(MainActivity.this).create(ApiInterface.class);
                    Call<ArrayList<Chat_Room_Info>> call2 = apiClient.selectChatRoomInfo(chatRoomId);
                    call2.enqueue(new Callback<ArrayList<Chat_Room_Info>>() {
                        @Override
                        public void onResponse(@NonNull Call<ArrayList<Chat_Room_Info>> call, @NonNull Response<ArrayList<Chat_Room_Info>> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                // 채팅방 아이디
                                ArrayList<Chat_Room_Info> chatRoomInfoList = response.body();

                                Chat_Room_Info chatRoomInfo = chatRoomInfoList.get(0);

                                String chatCreator = chatRoomInfo.getChatCreatorId();

                                //TODO 인텐트 만들어주기
                                Intent intent = new Intent(MainActivity.this, ChattingRoom.class);

                                // 데이터 넣어주기
                                // 채팅방 아이디
                                intent.putExtra("chatRoomId", chatRoomId);

                                // 내가 생성자라면
                                if (chatCreator.equals(userId)) {
                                    // 채팅방 명
                                    intent.putExtra("chatRoomName", chatRoomInfo.getChatRoomNameCreator());
                                } else { // 생성자가 아니라면
                                    // 채팅방 명
                                    intent.putExtra("chatRoomName", chatRoomInfo.getChatRoomNameOther());
                                }

                                // 새로 생성된 채팅방
                                intent.putExtra("isNew", false);

                                // 메시지 전송여부 - 기존에 생성된 채팅방이기 떄문에 메시지 전송은 한번이상 이뤄짐 따라서 true!
                                intent.putExtra("isMsgTransfered", true);

                                // 채팅멤버 리스트
                                intent.putExtra("chatMemberList", chatMemberList);

                                //어답터에서 클릭 이용할 때, 아래 해줘야됨!
                                startActivity(intent);

                            } else {
                                Log.e("채팅방 정보 조회 성공 했지만 데이터 없음: ", "1111");
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<ArrayList<Chat_Room_Info>> call, @NonNull Throwable t) {
                            Log.e("채팅방 조회 실패", t.getMessage());
                        }
                    });
                } else {
                    Log.e("채팅방 멤버리스트 조회 했지만 데이터 없음: ", "1111");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<OtherUserInfo>> call, @NonNull Throwable t) {
                Log.e("채팅방 멤버리스트 조회 실패", t.getMessage());
            }
        });
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
            Message msg = Message.obtain(null, ChatService.DISCONNECTED_HOME_ACT);

            try {
                mServiceMessenger.send(msg);
                Log.e("앱 종료 했을 때 ChatService와 연결 제거 - 서비스로 메시지 전송", msg.toString());

            } catch (RemoteException e) {
                e.printStackTrace();
            }

            finish();
            toast.cancel();
        } else {
            super.onBackPressed();
        }
    }
}