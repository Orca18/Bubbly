package com.mainnet.bubbly.chatting.service;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

import com.mainnet.bubbly.chatting.util.ChatUtil;
import com.mainnet.bubbly.chatting.util.GetDate;
import com.mainnet.bubbly.config.Config;
import com.mainnet.bubbly.model.Chat_Item;
import com.mainnet.bubbly.model.Chat_Room_Cre_Or_Del;
import com.mainnet.bubbly.model.UserInfo;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.ByteArrayInputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.ArrayList;

/**
* 애플리케이션 시작 시 실행되는 서비스
 * 메시지 브로커(채팅서버)와 연결되고 채팅관련 처리를 담당한다.
* */
public class ChatService extends Service {
    // 채팅방 나가기
    public static final int EXIT = 2;
    // 홈 액티비티와 연결
    public static final int CONNECT_HOME_ACT = 6;
    // 채팅 액티비티와 연결
    public static final int CONNECT_CHAT_ACT = 7;
    // 채팅 액티비티 - 서버 연결 해제
    public static final int DISCONNECTED_CHAT_ACT = 8;
    // 서버로부터 메시지 받음
    public static final int MSG_RECEIVE_FROM_SERVER = 10;
    // 서비스와 재연결하기(noti클릭시)
    public static final int RE_CONNECT_HOME_ACT = 11;
    // ChatService 종료
    public static final int DISCONNECTED_HOME_ACT = 12;

    // 서비스와 연결됐는지 여부
    public static boolean IS_BOUND_MAIN_ACTIVITY = false;
    public static boolean IS_BOUND_CHATTING_ROOM = false;

    // 채팅서버 주소
    private String ServerIP = Config.mqtt_server;

    // 채팅서버(메시지 브로커)와 연결할 MQTT 클라이언트 객체
    public static MqttClient mqttClient = null;
    // 채팅 관련 기능을 수행하는 util 객체
    private ChatUtil chatUtil;

    // 채팅방 아이디
    private String chatRoomId;

    //private Context context;

    // 액티비티가 보낸 메시지를 처리하는 메신저
    // 직접 처리하는 것은 Incoming Handler이며 액티비티와 핸들어의 연결을 위해 사용한다.
    private Messenger mServiceMessenger;

    // ChattingRoom의 활성화 여부 => 채팅방이 열려있는지!
    private boolean isChattingRoomivityActive = false;

    // 바인딩된 액티비티들과 통신하기위해 필요한 메신저들을 저장하는 리스트
    private ArrayList<Messenger> mActivityMessengerList = new ArrayList<>();

    // 사용자 아이디
    private String userId;

    // Chat_Item <-> String으로 변경시 사용할 구분값
    private String divisionVal = "@@@@@";

    // int형의 변수가 사용할 기본값
    private int defaultInt = 9999999;

    // 액티비티 -> 서비스에 데이터를 전송할 때 이것을 처리하는 핸들러
    // 액티비티에서 메시지 종류에 따라 msg.what 값에 따라 다른 처리를 할 수 있다.
    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                // 채팅방 나가기
                case EXIT:
                    // 서버에 사용자가 나갔음을 알려라!
                    String exitMsg = userId + "님이 나갔습니다.";

                    Chat_Item exitMsgInfo = new Chat_Item(chatRoomId,userId,exitMsg,null, GetDate.getDateWithYMDAndWeekDay(),GetDate.getAmPmTime(),"",0);

                    // 해당 채팅방 구독 해제
                    try {
                        mqttClient.unsubscribe(chatRoomId);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }

                    break;
                // 홈 액티비티와 연결
                case CONNECT_HOME_ACT:
                    IS_BOUND_MAIN_ACTIVITY = true;

                    // 채팅방 관련 기능을 가지고 있는 util
                    chatUtil = new ChatUtil(getApplicationContext());

                    userId = msg.getData().getString("userId");

                    // 브로커 서버에 연결
                    mqttClient = chatUtil.getMqttClient(userId, ServerIP);

                    // 클라이언트가 처리할 수 있는 콜백 등록!!
                    mqttClient.setCallback(new MqttCallback() {
                        @Override
                        public void connectionLost(Throwable throwable) {
                            Log.d("MQTTService", "Connection Lost");
                            Log.d("커넥션 사라짐", throwable.getStackTrace().toString());
                            Log.d("커넥션 사라짐", throwable.getCause().toString());
                        }

                        // 메시지 도착 시 연결되어있는 액티비티들에게 메시지를 전송한다.
                        @Override
                        public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                            Log.d("MQTTService", "Message Arrived : " + mqttMessage.toString());

                            // 채팅 메시지
                            if(mqttMessage.toString().contains(divisionVal)){
                                String chatItemStr = mqttMessage.toString();
                                Log.d("채팅메시지", chatItemStr);

                                Chat_Item chatItem = chatUtil.stringToChatItem(chatItemStr);

                                Log.d("채팅메시지 문자열 chatItem으로 변황", chatItem.toString());

                                // 서버에서 받은 메시지 중 id가 기본값이 아닌것만 즉, db에 저장된 메시지만 사용자에게 보낸다!
                                if(chatItem.getChatId() != defaultInt){
                                    Message msg = Message.obtain(null, ChatService.MSG_RECEIVE_FROM_SERVER);
                                    Bundle bundle = msg.getData();
                                    bundle.putSerializable("message",chatItem);

                                    Log.d("메시지 아이디가 있는 메시지", chatItem.toString());

                                    // 채팅방리스트엔 무조건 메시지 전송
                                    Message tempMsg = new Message();
                                    tempMsg.copyFrom(msg);

                                    // 내가 작성한 메시지가 아니라면
                                    Log.d("리스트로 채팅 메시지 전달","111");
                                    mActivityMessengerList.get(0).send(tempMsg);

                                    // 채팅방이 활성화 되어있고 메시지가 해당 채팅방의 메시지라면
                                    if(isChattingRoomivityActive && chatItem.getChatRoomId().equals(chatRoomId)){
                                        // 활성화돼있는 채팅방에서 채팅, 이미지, 동영상 메시지 받았을 때 SP의 마지막 메시지아이디를 업데이트 해준다.
                                        if(chatItem.getChatType() < 3){ // 0: 텍스트, 1: 이미지, 2: 동영상
                                            saveLatestChatIdToSharedPreference(chatItem);
                                        }

                                        Log.e("액티비티로 메시지 전송: ", " 6. 채팅방이 활성화되어있고 동일한 채팅방에서 메시지 전송 시 ChattingRoom로 전송 시작");

                                        Message tempMsg2 = new Message();
                                        tempMsg2.copyFrom(msg);
                                        mActivityMessengerList.get(1).send(tempMsg2);
                                    }
                                } else if(chatItem.getChatType() == 3 ) { // 안읽은 메시지수 업데이트하기 위해 채팅방에 포함된 모두에게 메시지 전송!
                                    Message msg2 = Message.obtain(null, ChatService.MSG_RECEIVE_FROM_SERVER);
                                    Bundle bundle = msg2.getData();
                                    bundle.putSerializable("message",chatItem);

                                    Log.e("액티비티로 메시지 전송 - 안읽은 사람수 ", chatItem.toString());

                                    // 채팅방이 활성화돼있고 해당 채팅방의 메시지라면!
                                    Log.d("isChattingRoomivityActive: " , "" + isChattingRoomivityActive);
                                    Log.d("chatItem.getChatRoomId(): " + "" , chatItem.getChatRoomId());
                                    //Log.d("chatRoomId: " + "" , chatRoomId);

                                    if (isChattingRoomivityActive && chatItem.getChatRoomId().equals(chatRoomId)) {
                                        Log.e("액티비티로 메시지 전송 완료!!", chatItem.toString());
                                        Message tempMsg3 = new Message();
                                        tempMsg3.copyFrom(msg2);
                                        mActivityMessengerList.get(1).send(tempMsg3);
                                    }
                                }
                            } else  { // 채팅방 생성 혹은 파괴 메시지
                                ByteArrayInputStream bis = new ByteArrayInputStream(mqttMessage.getPayload());
                                ObjectInput in = new ObjectInputStream(bis);
                                Object obj = in.readObject();

                                Chat_Room_Cre_Or_Del chatRoomCreOrDel = (Chat_Room_Cre_Or_Del)obj;
                                Log.d("채팅방 생성 혹은 파괴메시지 받음: ", chatRoomCreOrDel.toString());

                                // chatRoomId가 -1줄어들어서 온다 왜지?
                                chatRoomCreOrDel.setChatRoomId(chatRoomCreOrDel.getChatRoomId());

                                // 내가 채팅방리스트에 포함되어 있다면 채팅방 구독
                                ArrayList<String> chatMemberList = chatRoomCreOrDel.getChatRoomMemberList();
                                for(String userId : chatMemberList){
                                    // 내가 채팅방에 포함되어있다면 브로커에게 구독 요청!

                                    if(userId.equals(UserInfo.user_id)){
                                        if(chatRoomCreOrDel.getMsgType() == 0){ // 채팅방 구독
                                            mqttClient.subscribe("/topics/" + chatRoomCreOrDel.getChatRoomId(),2);
                                        } else { // 채팅방 구독 해지
                                            mqttClient.unsubscribe("/topics/" + chatRoomCreOrDel.getChatRoomId());
                                        }
                                        break;
                                    }
                                }
                            }
                        }

                        @Override
                        public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                            Log.d("MQTTService", "Delivery Complete");
                        }
                    });

                    try {
                        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
                        mqttConnectOptions.setCleanSession(true);

                        mqttClient.connect(mqttConnectOptions);
                        mqttClient.subscribe("/charRoomCreAndDel");
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }

                    // 액티비티와 연결된 경우 그것과 통신할 수 있는 메신저를 저장한다.
                    mActivityMessengerList.add(msg.replyTo);

                    Log.e("Broker서버와 연결 완료","완료!");

                    break;
                // 채팅 액티비티와 연결
                case CONNECT_CHAT_ACT:
                    IS_BOUND_CHATTING_ROOM = true;
                    // 액티비티와 연결된 경우 그것과 통신할 수 있는 메신저를 저장한다.
                    mActivityMessengerList.add(msg.replyTo);
                    Log.e("ChattingRoom과 연결 메신저리스트에 새로운 메신저 추가 ","" + mActivityMessengerList.size());

                    isChattingRoomivityActive = true;

                    chatRoomId = msg.getData().getString("chatRoomId");
                    userId = msg.getData().getString("userId");
                    boolean isNew = msg.getData().getBoolean("isNew");
                    int lastReadMsgId = msg.getData().getInt("lastReadMsgId");


                    // 마지막으로 읽은 메시지id가 있다면
                    /*if(lastReadMsgId != 99999999){
                        // 브로커에게 마지막으로 읽은 메시지의 id를 전달한다 => 이 메시지 다음 id부터 안읽은 사용자수 -1 을 해주기 위해서
                        sendLastReadIdToBroker(lastReadMsgId);
                    } else {
                        // 마디막으로 읽은 메시지가 없다면 채팅방을 새로 만들거나 기존에 만들어진 채팅방에 처음 들어올 때이다.
                        // 채팅방 생성시는 업데이트할 필요가 없고 기존에 있던 채팅방에 처음들어왔을 때만 업데이트 해주면 된다.
                        if(!isNew) {
                            // 모든 메시지 업데이트 (서버에서 lastIdx + 1부터 업데이트 하기 때문에 -1을 보낸다!)
                            sendLastReadIdToBroker(-1);
                        }
                    }*/


                    break;
                // 채팅 액티비티 - 서버 연결 해제
                case DISCONNECTED_CHAT_ACT:
                    // 채팅 액티비티의 활성화여부 = false 를 넣어줌
                    isChattingRoomivityActive = false;
                    Log.e("채팅방 나갈 때 서비스와의 연결 제거 4-1. isChattingRoomivityActive false로 변경", "" + isChattingRoomivityActive);

                    String chatRoomId = msg.getData().getString("chatRoomId");
                    Log.e("채팅방 나갈 때 서비스와의 연결 제거 4-2. chatRoomId", "" + chatRoomId);

                    // 메신저 제거!
                    mActivityMessengerList.remove(1);
                    Log.e("채팅방 나갈 때 서비스와의 연결 제거 4-3. 메신저 제거!", "" + mActivityMessengerList.size());

                    break;
                case DISCONNECTED_HOME_ACT:
                    IS_BOUND_MAIN_ACTIVITY = false;
                    stopSelf();
                    Log.d("서비스 종료","11");
                    break;
                case RE_CONNECT_HOME_ACT:
                    // 기존에 연결되어있던 메인 액트의 메신저 제거
                    mActivityMessengerList.remove(0);
                    // 새로운 액트의 메신저와 연결
                    mActivityMessengerList.add(msg.replyTo);
            }
        }
    }

    public ChatService() {
        //this.context = getApplicationContext();
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

    /** 채팅방에 입장했을 때 다른 채팅방의 안읽은사용자수 -- 해주기위해 브로커에게 채팅방 id와 마지막 인덱스를 보내준다.
     */
    /*public void sendLastReadIdToBroker(int lastIdx){
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
    }*/


    @Override
    public IBinder onBind(Intent intent) {
        //Toast.makeText(getApplicationContext(), "binding", Toast.LENGTH_SHORT).show();

        // 메신저의 매개변수로 핸들러를 받는 구나
        mServiceMessenger = new Messenger(new IncomingHandler()); // 매신저의 매개변수로 핸들러
        return mServiceMessenger.getBinder();
    }
}