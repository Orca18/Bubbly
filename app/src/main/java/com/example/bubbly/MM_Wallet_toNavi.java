package com.example.bubbly;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.DecimalFormat;
import android.icu.text.SimpleDateFormat;
import android.icu.util.TimeZone;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.example.bubbly.controller.TransactionHistory_Adapter;
import com.example.bubbly.model.TransactionHistory_Item;
import com.example.bubbly.model.UserInfo;
import com.example.bubbly.retrofit.ApiClient;
import com.example.bubbly.retrofit.ApiInterface;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MM_Wallet_toNavi extends AppCompatActivity {

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
    ProgressBar progressBar;
//    NestedScrollView scrollView;

    // 탭 레이아웃
    TabLayout tabLayout;
    ViewPager2 pager2;
    TransactionHistory_Adapter adapter;
    String uid = "";
    String address;

    CircleImageView myAccount;
    LinearLayout myActivity, myList, myCommunity;
    TextView settingOption, info, logout;
    View view;

    //지갑 정보 화면
    Button bt_copy,bt_exchange;
    TextView tv_address, tv_bubbleBalance, tv_novaBalance;
    ClipboardManager clipboard;



    LinearLayoutManager linearLayoutManager;
    RecyclerView recyclerView;
    ArrayList<TransactionHistory_Item> list = new ArrayList<>();
    String nextToken = ""; //api에 20개씩 나누어서 요청하는 토큰

//    // TODO (임시)
//    TextView createAddress;
//    TextView refreshAmount;
//    TextView amount; //잔액
//    TextView send;
//
//    // TODO 알고 Amount 가져오기 (임시)
//    AlgodClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_e_wallet_include_navi);

        // 리소스 ID 선언
        initiallize();
        //리사이클러뷰 설정
        setRecyclerView();
        // 바텀 메뉴 - 스택 X 액티비티 이동 (TODO 바텀 내비게이션으로 변경하는 작업)
        bottomNavi();
        // 클릭 리스너 모음 - 스택 O
        clickListeners();
        // 내비 터치
        NaviTouch();
        //탭초기화
//        tabInit();

        //지갑정보 가져오기
        selectWalletInfo();

    }



    // ========================================================

    // 리소스 아이디 선언
    private void initiallize() {
        // 툴바
        toolbar = findViewById(R.id.wallet_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.wallet_navigation_view);
        sidemenu = findViewById(R.id.wallet_sidemenu);
//        scrollView = findViewById(R.id.text_scrollview);

        // 내비 안 메뉴
        view = navigationView.getHeaderView(0);
        myAccount = view.findViewById(R.id.navi_header_profileimg);
        myActivity = view.findViewById(R.id.navi_header_myActivity);
        myList = view.findViewById(R.id.navi_header_myList);
        myCommunity = view.findViewById(R.id.navi_header_myCommunity);
        settingOption = view.findViewById(R.id.navi_header_setting_option);
        info = view.findViewById(R.id.navi_header_info);
        logout = view.findViewById(R.id.navi_header_logout);

        // 바텀 메뉴
        bthome = findViewById(R.id.wallet_tohome);
        btissue = findViewById(R.id.wallet_toissue);
        btmessage = findViewById(R.id.wallet_tomessage);
        btprofile = findViewById(R.id.wallet_toprofile);
        btwallet = findViewById(R.id.wallet_towallet);

        //지갑 정보 보여주기
        bt_copy = findViewById(R.id.bt_copy_address_wallet);
        bt_exchange =findViewById(R.id.bt_exchange_wallet);
        tv_address = findViewById(R.id.tv_address_wallet);
        tv_bubbleBalance = findViewById(R.id.tv_bubble_wallet);
        tv_novaBalance = findViewById(R.id.tv_balance_wallet);

    }

    private void setRecyclerView(){
        recyclerView = findViewById(R.id.tab_recyclerview);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new TransactionHistory_Adapter(this, this.list);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }


    //블록체인 계정 정보 요청
    private void selectWalletInfo(){
        ApiInterface api = ApiClient.getApiClient(this).create(ApiInterface.class);
        Call<String> call = api.selectAddrUsingUserId(UserInfo.user_id);
        call.enqueue(new Callback<String>()
        {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response)
            {
                if (response.isSuccessful() && response.body() != null)
                {
                    System.out.println("블록체인 정보"+response.body());
                    try {
                        JSONObject jsonObject = new JSONObject(response.body());
                        address = jsonObject.getString("address");
                        int balance = jsonObject.getInt("amount");
                        int bubble = 0;
                        JSONArray jsonAssetArray = jsonObject.getJSONArray("assets");
                        for(int i = 0; i<jsonAssetArray.length(); i++){
                            JSONObject asset = jsonAssetArray.getJSONObject(i);
                            int assetID = asset.getInt("asset-id");
                            if(assetID==94434081){
                                bubble = asset.getInt("amount");
                            }
                        }
                        tv_address.setText(address);
                        DecimalFormat decim = new DecimalFormat("#,###");
                        tv_novaBalance.setText(decim.format(balance));
                        tv_bubbleBalance.setText(decim.format(bubble));
                        selectHistory();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t)
            {
                Log.e("계정 정보 수신 에러", t.getMessage());
            }
        });
    }


    // 바텀 메뉴 클릭
    private void bottomNavi() {
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.wallet_tohome:
                        Intent mIntent1 = new Intent(getApplicationContext(), MM_Home.class);
                        mIntent1.addFlags (Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(mIntent1);
                        finish();
                        break;

                    case R.id.wallet_toissue:
                        Intent mIntent2 = new Intent(getApplicationContext(), MM_Issue.class);
                        mIntent2.addFlags (Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(mIntent2);
                        finish();
                        break;

                    case R.id.wallet_tomessage:
                        Intent mIntent3 = new Intent(getApplicationContext(), MM_Message.class);
                        mIntent3.addFlags (Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(mIntent3);
                        finish();
                        break;

                    case R.id.wallet_toprofile:
                        Intent mIntent4 = new Intent(getApplicationContext(), MM_Profile.class);
                        mIntent4.addFlags (Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(mIntent4);
                        finish();
                        break;

                    case R.id.wallet_towallet:
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
                finish();            }
        });
        myActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "TODO 보상 체계 구현 (with 지갑)",Toast.LENGTH_SHORT).show();
            }
        });
        myList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(), "겉멋",Toast.LENGTH_SHORT).show();
            }
        });
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
                Toast.makeText(getApplicationContext(), "고객센터",Toast.LENGTH_SHORT).show();
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toLogin = new Intent(getApplicationContext(), LL_Login.class);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                startActivity(toLogin);
                finish();
                Toast.makeText(getApplicationContext(), "로그아웃",Toast.LENGTH_SHORT).show();            }
        });


//        // TODO 임시 계좌 생성 버튼
//        createAddress.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent toLogin = new Intent(getApplicationContext(), test_CreateAccount.class);
//                startActivity(toLogin);
//            }
//        });
//
//        refreshAmount.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // TODO 잔액 다시 가져오기
//                AlgoService algoService = new AlgoService(
//                        "https://node.testnet.algoexplorerapi.io/",
//                        443,
//                        null,
//                        "https://algoindexer.testnet.algoexplorerapi.io/idx2",
//                        443);
//
//                Long amountLong = algoService.getAccountAmount(address).orElse(-1L);
//
//                amount.setText(""+amountLong);
//                Log.d("algoDebugAmount", ""+algoService);
//                Log.d("algoDebugAmount", ""+amountLong);
//                Log.d("algoDebugAmount", ""+address);
//
//            }
//        });
//    private void sendAlgo() {
//        AlgoService algoService = new AlgoService("https://node.testnet.algoexplorerapi.io/",
//                443,
//                null,
//                "https://algoindexer.testnet.algoexplorerapi.io/idx2",
//                443,
//                "니모닉");
//        try {
//            String txId = algoService.sendAlgo("받는 주소", 1L, "보내는 사람의 니모닉");
//            return;
//        } catch (Exception e) {
//            return;
//        }
//    }

    }



    // 클릭 이벤트 모음
    private void clickListeners() {
        //카피버튼
        bt_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("address", address);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getApplicationContext(), "클립보드에 복사되었습니다.",Toast.LENGTH_SHORT).show();
            }
        });

        //환전버튼
        bt_exchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MM_Wallet_toNavi.this).setTitle("Bubble-Nova 환전");
                EditText input = new EditText(MM_Wallet_toNavi.this);
                input.setPaddingRelative(100,100,100,100);
                input.setBackground(null);
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                input.setSingleLine();
                input.setHint("몇 버블을 환전하시겠습니까?\n (1Bubble = 1mNova)");
                builder.setView(input);
                builder.setCancelable(false);

                builder.setPositiveButton("환전", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ApiInterface api = ApiClient.getApiClient(MM_Wallet_toNavi.this).create(ApiInterface.class);
                        Call<String> call = api.exchange(address,UserInfo.mnemonic,input.getText().toString());
                        call.enqueue(new Callback<String>()
                        {
                            @Override
                            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response)
                            {
                                if (response.isSuccessful() && response.body() != null)
                                {
                                    if(response.body().equals("success")){
                                        Toast.makeText(getApplicationContext(), "환전이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(getApplicationContext(), "환전에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                            @Override
                            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t)
                            {
                                Log.e("환전 에러", t.getMessage());
                            }
                        });
                        Toast.makeText(getApplicationContext(), "환전 요청이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
                alert.getButton(alert.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                alert.getButton(alert.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.blue));
            }
        });


        //리사이클러뷰 last 인식 => 추가 요청
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1)) {
                    selectHistory();
                }
            }
        });

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
        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.wallet_refresh);
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        selectWalletInfo();
                        swipeRefreshLayout.setRefreshing(false);
                    }});


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



    private void selectHistory(){
        String base_url = "https://testnet-algorand.api.purestake.io/idx2/";
        String token = "4LS0jVPkU61EBPpW2Ml3A2iaEcEfXK92aCDSzXXr";
        ApiInterface api = ApiClient.getApiClientWithUrlInput(base_url).create(ApiInterface.class);
        Call<String> call = api.transactionHistory(token,address,20,nextToken);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                System.out.println("거래기록"+response.body()+response.errorBody());
                System.out.println("거래기록"+response.raw()+response.headers()+response.code());
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body());
                        nextToken = jsonObject.getString("next-token");
                        JSONArray jsonArray = jsonObject.getJSONArray("transactions");
                        for(int i = 0; i<jsonArray.length(); i++){
                            JSONObject tx = jsonArray.getJSONObject(i);
                            String txId = tx.getString("id");
                            String sender = tx.getString("sender");
                            long roundTime = tx.getLong("round-time");
                            System.out.println(roundTime);
                            //system time to date
                            SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
                            Date date = new Date();
                            date.setTime(roundTime * 1000); //epoch seconds to ms
                            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                            String roundTimeToDate = sdf.format(date);
                            int fee = tx.getInt("fee");
                            String txnTypeToString = "";
                            String txType = tx.getString("tx-type");
                            switch (txType){
                                case "pay":
                                    txnTypeToString = "Payment";
                                    break;
                                case "keyreg":
                                    txnTypeToString = "Key Registration";
                                    break;
                                case "acfg":
                                    txnTypeToString = "Asset Configuration";
                                    break;
                                case "axfer":
                                    txnTypeToString = "Asset Transfer";
                                    break;
                                case "afrz":
                                    txnTypeToString = "Asset Freeze";
                                    break;
                                case "appl":
                                    txnTypeToString = "Application Call";
                                    break;
                            }
                            int amount;
                            String receiver = null;
                            int assetId;
                            if(txType.equals("pay")){
                                JSONObject txn = tx.getJSONObject("payment-transaction");
                                amount = txn.getInt("amount");
                                receiver = txn.getString("receiver");
                                assetId = 0;
                            }else if(txType.equals("axfer")){
                                JSONObject axtx = tx.getJSONObject("asset-transfer-transaction");
                                amount = axtx.getInt("amount");
                                receiver = axtx.getString("receiver");
                                assetId = axtx.getInt("asset-id");
                            }else{
                                amount = 0;
                                receiver = "";
                                assetId = 0;
                            }
                            list.add(new TransactionHistory_Item(
                                    txnTypeToString,
                                    txId,
                                    sender,
                                    roundTimeToDate,
                                    ""+fee,
                                    ""+amount,
                                    receiver,
                                    ""+assetId));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("거래기록 가져오기 실패", t.getMessage());
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        selectWalletInfo();
    }



//    private void tabInit() {
//        tabLayout = findViewById(R.id.wallet_tab_layout);
//        pager2 = findViewById(R.id.wallet_view_pager2);
//
//        FragmentManager fm = getSupportFragmentManager();
//        adapter = new WalletFragmentAdapter(fm, getLifecycle(), uid);
//        pager2.setAdapter(adapter);
//
//        tabLayout.addTab(tabLayout.newTab().setText("거래 내역"));
////        tabLayout.addTab(tabLayout.newTab().setText("광고"));
////        tabLayout.addTab(tabLayout.newTab().setText("활동 내역"));
//
//        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                pager2.setCurrentItem(tab.getPosition());
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });
//
//
//        pager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
//            @Override
//            public void onPageSelected(int position) {
//                tabLayout.selectTab(tabLayout.getTabAt(position));
//            }
//        });
//
//    }

    public String getUid(){
        return uid;
    }



}
