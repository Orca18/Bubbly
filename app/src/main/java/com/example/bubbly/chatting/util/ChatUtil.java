package com.example.bubbly.chatting.util;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.bubbly.model.Chat_Item;
import com.example.bubbly.model.Chat_Room_Cre_Or_Del;
import com.example.bubbly.retrofit.ChatApiClient;
import com.example.bubbly.retrofit.ChatApiInterface;
import com.example.bubbly.retrofit.chat.SyncGetMqttId;
import com.example.bubbly.retrofit.chat.SyncInsertMqttId;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
* 채팅 관련 기능을 제공하는 유틸 클래스
* */
public class ChatUtil {
    // Chat_Item <-> String으로 변경시 사용할 구분값
    private String divisionVal = "@@@@@";
    
    // int형의 변수가 사용할 기본값
    private int defaultInt = 9999999;
    
    private Context context;
    
    public ChatUtil(Context context) {
        this.context = context;
    }
    /**
    * MQTT 클라이언트 연결 => 앱이 새로 시작될 때마다 실행되야 한다.
    * */
    public MqttClient getMqttClient(String userId, String serverIp){
        // 채팅서버와 통신하기 위한 인터페이스 구현체
        ChatApiInterface chatApi = ChatApiClient.getApiClient(context).create(ChatApiInterface.class);
        // 통신 객체 -> 해당 사용자의 mqtt client 아이디를 가져온다.
        Call<String> call = chatApi.selectMqttClientUsingUserId(userId);
        String clientId = null;

        // 서버에서 mqtt_client 아이디를 가져오는 동기 함수의 쓰레드
        SyncGetMqttId t = new SyncGetMqttId(call);

        // 스레드 시작
        t.start();

        try {
            // 쓰레드에서 데이터를 조회할 때 main 스레드는 중지를 시켜야 하므로 join()사용
            t.join();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 가져온 mqttClientId 값
        String mqttClientId = t.getMqttClientId();
        Log.d("mqttClientId", mqttClientId);

        // mqttClientId가 존재한다면!
        if(!mqttClientId.equals("not exist") && !mqttClientId.equals("fail")){
            clientId = mqttClientId;
            Log.d("클라이언트 아이디11: ", clientId);
        } else {
            // 1-2. clientID가 없다면 생성
            clientId = MqttClient.generateClientId();
            Log.d("클라이언트 아이디22: ", clientId);
            // 1-3. 새로 생성한 clientID 저장
            Call<String> call2 = chatApi.insertMqttClientId(clientId, userId);

            // 서버에 mqttClientId를 저장하는 동기 함수의 쓰레드
            SyncInsertMqttId t2 = new SyncInsertMqttId(call2);
            t2.start();

            try {
                // 쓰레드에서 데이터를 저장할 때 main 스레드는 중지를 시켜야 하므로 join()사용
                t2.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        MqttClient mqttClient = null;
        try {
            // 2. MQTT 클라이언트 생성
            Log.d("클라이언트 아이디: ", clientId);
            mqttClient = new MqttClient(serverIp, clientId, null);
        } catch (MqttException e) {
            e.printStackTrace();
        }

        return mqttClient;
    }

    /**
    * 채팅방 입장: MQTT 브로커에게 구독 요청 & FCM 백엔드에게 구독 요청
    * 채팅방이 생성되거나 새로운 채팅방에 초대되었을 때 호출 됨.
    * */
    public void enterChatRoom(MqttClient mqttClient, String chatRoomId){
        try {
            // MQTT 브로커에게 구독 요청
            mqttClient.subscribe(chatRoomId,2);

            // FCM 백엔드에게 구독 요청
            subscribeReqToFCMServer(chatRoomId);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /**
     * FCM 백엔드에게 구독요청
     * */
    private void subscribeReqToFCMServer(String chatRoomId){
        String topic = "/topics/" + chatRoomId;

        FirebaseMessaging.getInstance().subscribeToTopic(topic)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Subscribed Complete";
                        if (!task.isSuccessful()) {
                            msg = "Subscribe failed";
                        }
                        Log.d("FCM서버에게 해당 채팅방 구독 요청 완료여부: ", msg);
                    }
                });
    }

    /**
     * 채팅방 나가기(구독 해제)
     * 채팅방에서 나가기를 클릭했을 때 실행됨
     * */
    public void exitChatRoom(MqttClient mqttClient, String chatRoomId){
        try {
            // MQTT 브로커에게 구독 해지 요청
            mqttClient.unsubscribe(chatRoomId);

            // FCM 백엔드에게 구독 해지 요청
            unSubscribeReqToFCMServer(chatRoomId);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /**
     * FCM 백엔드에게 구독해지 요청
     * */
    private void unSubscribeReqToFCMServer(String chatRoomId){
        String topic = "/topics/" + chatRoomId;

        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "UnSubscribed Complete";
                        if (!task.isSuccessful()) {
                            msg = "UnSubscribed failed";
                        }
                        Log.d("FCM에게 채팅방 구독해제 요청 완료여부: ", msg);
                    }
                });
    }

    /**
     * 메시지 조회
     * */
    public void retrieveChatMsgList(String chatRoomId, int pageNo){
        // 채팅서버에서 메시지 조회
    }

    /**
     * FCM 토큰 서버에게 전송 - 로그인할 때마다 전송해야 한다.
     * */
    public void sendFCMTokenToServer(String token, String user_id){
        ChatApiInterface chatApi = ChatApiClient.getApiClient(context).create(ChatApiInterface.class);
        Call<String> call = chatApi.sendTokenToServer(token, user_id);
        call.enqueue(new Callback<String>()
        {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response)
            {
                if (response.isSuccessful() && response.body() != null)
                {
                    Log.e("토큰 전송 성공", response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t)
            {
                Log.e("에러", t.getMessage());
            }
        });
    }

    /**
     *
     * */

    /**
     * 메시지 서버에게 전송(publish)
     * */
    public void publishChatMsg(String chatItemStr, String chatRoomId, MqttClient mqttClient){
        try {
            byte[] chatItemStrByteArr = chatItemStr.getBytes(StandardCharsets.UTF_8);
            Log.d("ChatUtil/publishChatMsg - 브로커에게 메시지 전송전","0000000");

            mqttClient.publish("/topics/" +chatRoomId, chatItemStrByteArr, 0, false);
            Log.d("ChatUtil/publishChatMsg - 브로커에게 메시지 전송","`111");
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /**
     * 메시지 서버에게 채팅방 생성 or 파괴 메시지 전송(publish)
     * msgType: 0이면 생성, 1이면 파괴이다.
     * */
    public void publishChatCreOrDelMsg(Chat_Room_Cre_Or_Del chatRoomCreMsg, MqttClient mqttClient){
        try {
            // 메시지 전송 시 클라이언트 아이디도 함께 보내야 한다 - 그래야 채팅방에서 내가 보낸 메시지는 받지 않을 수 있음
            // 클라아이디:메시지와 같은 형태로 보내면 될듯
            ByteArrayOutputStream boas = new ByteArrayOutputStream();
            ObjectOutputStream ois = null;
            try {
                ois = new ObjectOutputStream(boas);
                ois.writeObject(chatRoomCreMsg);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.d("채팅방 생성 혹은 삭제", "msgType 0이면 생성 1이면 삭제: " + chatRoomCreMsg.getMsgType());
            Log.d("채팅방 아이디 확인", "" + chatRoomCreMsg.getChatRoomId());

            // 서버에 메시지 전송(채팅방 아이디가 토픽이 된다!)
            mqttClient.publish("/charRoomCreAndDel", boas.toByteArray(), 2, false);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    // ChattingRoom에서 전송한 Chat_Item을 String으로 변경한다.
    public String chatItemToString(Chat_Item chatItem){
        String profileImageURL = chatItem.getProfileImageURL();
        String chatRoomId = chatItem.getChatRoomId();
        String chatUserId = chatItem.getChatUserId();
        String chatText = chatItem.getChatText();
        String chatFileUrl = chatItem.getChatFileUrl();
        String chatDate = chatItem.getChatDate();
        String chatTime = chatItem.getChatTime();
        String chatUserNickName = chatItem.getChatUserNickName();
        // 서버에서 값 측정
        int chatId = defaultInt;
        int chatType = chatItem.getChatType();
        // 서버에서 값 측정
        int notReadUserCount = defaultInt;
        
        String chatItemStr = profileImageURL + divisionVal
                           + chatRoomId + divisionVal
                           + chatUserId + divisionVal
                           + chatText + divisionVal
                           + chatFileUrl + divisionVal
                           + chatDate + divisionVal
                           + chatTime + divisionVal
                           + chatUserNickName + divisionVal
                           + chatId + divisionVal
                           + chatType + divisionVal
                           + notReadUserCount;
        Log.d("Chat_Item을 문자열로 변경한 값", chatItemStr);
        
        return chatItemStr;
    }

    // 서버에서 전송한 String을 Chat_Item을 Chat_Item으로 변경한다.
    public Chat_Item stringToChatItem(String chatItemStr){
        String[] chatItemValueArr = chatItemStr.split("@@@@@");

        String profileImageURL = chatItemValueArr[0];
        String chatRoomId = chatItemValueArr[1];
        String chatUserId = chatItemValueArr[2];
        String chatText = chatItemValueArr[3];
        String chatFileUrl = chatItemValueArr[4];
        String chatDate = chatItemValueArr[5];
        String chatTime = chatItemValueArr[6];
        String chatUserNickName = chatItemValueArr[7];
        int chatId = Integer.parseInt(chatItemValueArr[8]);
        int chatType = Integer.parseInt(chatItemValueArr[9]);
        int notReadUserCount = Integer.parseInt(chatItemValueArr[10]);

        Chat_Item chatItem = new Chat_Item(profileImageURL
                                          , chatRoomId
                                          , chatUserId
                                          , chatText
                                          , chatFileUrl
                                          , chatDate
                                          , chatTime
                                          , chatUserNickName
                                          , chatId
                                          , chatType
                                          , notReadUserCount
        );
        Log.d("문자열을 Chat_Item으로 변경한 값", chatItem.toString());

        return chatItem;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Chat_Item toChatItem(int chatId, String chatString){
        Log.d("chatData", chatString);

        byte[] chatByteArr = Base64.getDecoder().decode(chatString);

        Log.d("chatData Byte Arr", "" + chatByteArr);

        ByteArrayInputStream bis = new ByteArrayInputStream(chatByteArr);
        ObjectInput in = null;
        Chat_Item chatItem = null;

        try {
            in = new ObjectInputStream(bis);
            try {
                chatItem = (Chat_Item) in.readObject();
                // 채팅 아이디 세팅!!!!
                chatItem.setChatId(chatId);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return chatItem;
    }

    // 서버의 사용자수맵 업데이트
    public void updateUserCountMap(String chatRoomId, int updateDiv, int allUserCount, String userId){
        ChatApiInterface chatApiClient = ChatApiClient.getApiClient(context).create(ChatApiInterface.class);
        Call<String> call2 = chatApiClient.updateUserCountMap(chatRoomId, updateDiv, allUserCount, userId);
        call2.enqueue(new Callback<String>()
        {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response)
            {
                if (response.isSuccessful() && response.body() != null)
                {
                    //
                    String responseResult = response.body();
                    Log.d("채팅방 사용자수 맵 업데이트 완료",responseResult);

                } else {
                    Log.d("채팅방 사용자수 맵 업데이트 완료","지만 데이터 없음!");

                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t)
            {
                Log.e("채팅방 사용자수 맵 업데이트 실패", t.getMessage());
            }
        });
    }
}
