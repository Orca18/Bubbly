package com.example.bubbly.Kim_Bottom;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.bubbly.ChatMemberSelect;
import com.example.bubbly.ChattingRoom;
import com.example.bubbly.MainActivity;
import com.example.bubbly.R;
import com.example.bubbly.chatting.service.ChatService;
import com.example.bubbly.chatting.util.ChatUtil;
import com.example.bubbly.chatting.util.GetDate;
import com.example.bubbly.chatting.viewmodel.ChattingRoomViewModel;
import com.example.bubbly.controller.Messages_Adapter;
import com.example.bubbly.model.Chat_Item;
import com.example.bubbly.model.Chat_Member_FCM_Sub;
import com.example.bubbly.model.Chat_Room_Cre_Or_Del;
import com.example.bubbly.model.Chat_Room_Info;
import com.example.bubbly.model.OtherUserInfo;
import com.example.bubbly.model.UserInfo;
import com.example.bubbly.retrofit.ApiClient;
import com.example.bubbly.retrofit.ApiInterface;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/** 채팅 프래그먼트*/
public class Bottom3_Fragment extends Fragment {

    // ★프래그먼트 View그룹
    private ViewGroup view;
    // 툴바 (& 버튼)
    androidx.appcompat.widget.Toolbar toolbar;
    ImageView sidemenu, sidemenubtnChatRoomAdd, btnChatOption;
    // 새로고침, 프로그레스바
    SwipeRefreshLayout swipeRefreshLayout;
    ProgressBar progressBar;
    // 리사이클러 뷰
    private Messages_Adapter adapter;
    RecyclerView recyclerView;
    private Parcelable recyclerViewState;
    // 유저 정보
    SharedPreferences preferences;
    String userId;
    // 채팅방 추가 버튼
    ImageView btnChatRoomAdd;

    // ChatMemberSelect화면으로 이동해서 채팅방 생성 시 다 시돌아와 채팅방을 생성하기 위한 객체
    private ActivityResultLauncher<Intent> startActResultForChatMemberSelect;

    // 채팅방 멤버 선택화면에서 다시 이 액트로 돌아온 후 채팅방 화면을 생성하기 위한 객체
    private ActivityResultLauncher<Intent> startActResultForChattingRoom;

    private String divisionVal = "@@@@@";

    // 채팅방리스트를 관리할 뷰모델
    private ChattingRoomViewModel chattingRoomViewModel;

    // 채팅유틸
    private ChatUtil chatUtil;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        // 결과값을 가지고 있는 인텐트 설정
        setUpStartActForResult();
        super.onCreate(savedInstanceState);

        chattingRoomViewModel = new ViewModelProvider(requireActivity()).get(ChattingRoomViewModel.class);
        chatUtil = new ChatUtil(requireActivity());

        // 채팅방리스트 변화할 때마다 업데이트 해줌
        observe();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 결과값을 가지고 있는 인텐트 설정 setUpStartActForrResult();
        view = (ViewGroup) inflater.inflate(R.layout.bottom3_frame, container, false);

        // 리소스 ID 선언
        initiallize();
        clickListeners();

        return view;
    }



    // 리소스 아이디 선언
    private void initiallize() {
        // 툴바
        toolbar = view.findViewById(R.id.message_toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("");

        // 사이드바 메뉴를 열기위한 변수
        sidemenu = view.findViewById(R.id.message_sidemenu);
        // 채팅방 추가 버튼
        sidemenubtnChatRoomAdd= view.findViewById(R.id.message_chat_room_add);
        
        // 리사이클러뷰를 새로고침할 수 있게하기 위한 레이아웃
        swipeRefreshLayout = view.findViewById(R.id.message_refresh);
        // 채팅방리스트를 저장하기 위한 리사이클러뷰
        recyclerView = view.findViewById(R.id.message_recyclerView);

        // 채팅방 추가 버튼
        btnChatRoomAdd = view.findViewById(R.id.message_chat_room_add);

        preferences = getActivity().getSharedPreferences("novarand",MODE_PRIVATE);
        userId = preferences.getString("user_id", "");

        // 리사이클러뷰 초기화
        setUpRecyclerView();
    }

    private void clickListeners() {
        // 좌측 상단 메뉴 버튼
        sidemenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.drawerLayout.openDrawer(GravityCompat.START);
            }
        });


        // 리사이클러뷰 새로고침 인식
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Toast.makeText(getContext(), "TODO 새로고침", Toast.LENGTH_SHORT).show();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });

        // 채팅방 추가 버튼
        btnChatRoomAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toChatMemberSelect = new Intent(requireActivity(), ChatMemberSelect.class);

                startActResultForChatMemberSelect.launch(toChatMemberSelect);
            }
        });
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
                            Intent mIntent = new Intent(requireActivity(), ChattingRoom.class);
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

    // 뷰모델 observe
    public void observe(){
        Log.d("여기 들어옴?", "!1");

        // 가져온 채팅방리스트정보 observe
        chattingRoomViewModel.getChatRoomList().observe(this, chatRoomList -> {
            adapter = new Messages_Adapter(requireActivity(), chatRoomList);
            Log.d("여기 들어옴?", "22");

            recyclerView.setAdapter(adapter);
        });
    }

    private void setUpRecyclerView() {
        // 어댑터에 데이터가 crud될 때마다 리사이클러뷰의 크기를 계산하고 이때 자원을 소모한다.
        // 그런데 채팅리스트는 데이터 갯수가 변경된다고 해도 리사이클러뷰의 크기는 항상 그대로 유지되도 상관없다.
        // 따라서 변경시마다 재계산할 필요가 없으므로 앱에게 리사이클러뷰의 크기는 항상 동일하다고 알려주는 것이 자원소모를
        // 하지 않을 수 있어서 효율적이다. 따라서 이 값을 true로 설정한다
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireActivity());

        recyclerView.setLayoutManager(layoutManager);

        // 리사이클러뷰의 위치를 저장하기 위한 값 => 화면전환 등의 상황에서 리사이클러뷰 스크롤의 위치를 저장해야 할 경우 사용할 수 있음!
        //recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();

        //위치 유지
        //recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
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
                //chatRoomIdMap.remove(chatRoomInfo.getChatRoomId());

                String chatRoomId = chatRoomInfo.getChatRoomId();

                // 채팅방 나가기
                ApiInterface apiClient = ApiClient.getApiClient(requireActivity()).create(ApiInterface.class);
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
                                chatUtil.publishChatMsg(exitMsg,chatRoomId, ChatService.mqttClient);
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
}
