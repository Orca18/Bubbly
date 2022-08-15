package com.example.bubbly.chatting.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.widget.Toast;


import com.example.bubbly.chatting.util.ChatUtil;
import com.example.bubbly.model.Chat_Item;

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
    // 채팅상대 초대
    public static final int INVITE = 1;
    // 채팅방 나가기
    public static final int EXIT = 2;
    // 텍스트 전송
    public static final int SEND_TEXT = 3;
    // 이미지 전송
    public static final int SEND_IMAGE = 4;
    // 읽지 않은 사람수 업데이트
    public static final int NOT_READ_USER_UPDATE = 5;
    // 홈 액티비티와 연결
    public static final int CONNECT_HOME_ACT = 6;
    // 채팅 액티비티와 연결
    public static final int CONNECT_CHAT_ACT = 7;
    // 채팅 액티비티 - 서버 연결 해제
    public static final int DISCONNECTED_CHAT_ACT = 8;
    // 서버로부터 메시지 받음
    public static final int MSG_RECEIVE_FROM_SERVER = 10;
    // 메시지의 마지막 인덱스 보내기
    public static final int SEND_LAST_MSG = 11;
    // 알림으로 채팅방 전송 => 채팅방리스트의 안읽은 메시지 없애주기위해 필요
    public static final int NOTIFICATION = 12;

    // 채팅서버 주소
    private String ServerIP = "tcp://43.200.189.111:1883";

    // 채팅서버(메시지 브로커)와 연결할 MQTT 클라이언트 객체
    private MqttClient mqttClient = null;
    // 채팅 관련 기능을 수행하는 util 객체
    private ChatUtil chatUtil;

    // 채팅방 아이디
    private String chatRoomId;

    // 액티비티가 보낸 메시지를 처리하는 메신저
    // 직접 처리하는 것은 Incoming Handler이며 액티비티와 핸들어의 연결을 위해 사용한다.
    private Messenger mServiceMessenger;

    // ChatAct의 활성화 여부 => 채팅방이 열려있는지!
    private boolean isChatActivityActive = false;

    // 바인딩된 액티비티들과 통신하기위해 필요한 메신저들을 저장하는 리스트
    private ArrayList<Messenger> mActivityMessengerList = new ArrayList<>();

    // 사용자 아이디
    private String userId;

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

                    Chat_Item exitMsgInfo = new Chat_Item(chatRoomId,userId,exitMsg,null,"");

                    // 서버에 publish
                    chatUtil.publishChatMsg(exitMsgInfo, mqttClient);

                    // 해당 채팅방 구독 해제
                    try {
                        mqttClient.unsubscribe(chatRoomId);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }

                    break;
                // 문자열 전송
                case SEND_TEXT:
                    Chat_Item sendMsg = (Chat_Item) msg.getData().getSerializable("sendMsg");
                    Log.e("텍스트 메시지 송신", "3. 서비스로 메시지 전송 -> " + "서비스에서 서버로 메시지 전송: " + "1. 수신한 메시지: " + sendMsg);

                    // 서버에 publish
                    chatUtil.publishChatMsg(sendMsg, mqttClient);
                    break;
                // 정적파일 전송
                case SEND_IMAGE:

                    // 레트로핏
                    // ChattingService service = retrofit.create(ChattingService.class);

                    // http request 객체 생성
                    //Call<ArrayList<String>> call = service.insertFileList(userIdReq, chatRoomIdReq, list);

                    System.out.println("transferFileToServer들어옴");

                    //new InsertFileInfo().execute(call);

                    break;
                // 홈 액티비티와 연결
                case CONNECT_HOME_ACT:
                    // fcm토큰 가져오기
                    FCMService.getToken();

                    // 채팅방 관련 기능을 가지고 있는 util
                    chatUtil = new ChatUtil();

                    userId = msg.getData().getString("userId");

                    // 브로커 서버에 연결
                    mqttClient = chatUtil.getMqttClient(userId, ServerIP);

                    // 클라이언트가 처리할 수 있는 콜백 등록!!
                    mqttClient.setCallback(new MqttCallback() {
                        @Override
                        public void connectionLost(Throwable throwable) {
                            Log.d("MQTTService", "Connection Lost");
                        }

                        // 메시지 도착 시 연결되어있는 액티비티들에게 메시지를 전송한다.
                        @Override
                        public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                            Log.d("MQTTService", "Message Arrived : " + mqttMessage.toString());

                            ByteArrayInputStream bis = new ByteArrayInputStream(mqttMessage.getPayload());
                            ObjectInput in = new ObjectInputStream(bis);
                            Chat_Item chat_item = (Chat_Item)in.readObject();

                            Message msg = Message.obtain(null, ChatService.MSG_RECEIVE_FROM_SERVER);
                            Bundle bundle = msg.getData();
                            bundle.putSerializable("message",chat_item);

                            // 채팅 액티비티가 활성화 되어있고 활성화된 채팅방에 메시지가 도착했다면
                            if(isChatActivityActive && chat_item.getChatRoomId().equals(chatRoomId)){
                                Log.e("액티비티로 메시지 전송: ", " 6. 채팅방이 활성화되어있고 동일한 채팅방에서 메시지 전송 시 ChatAct로 전송 시작");
                                Message tempMsg = new Message();
                                tempMsg.copyFrom(msg);
                                mActivityMessengerList.get(1).send(tempMsg);
                            } else { // 채팅 액티비티가 비활성화라면 채팅리스트로 메시지 전송
                                Message tempMsg = new Message();
                                tempMsg.copyFrom(msg);
                                mActivityMessengerList.get(0).send(tempMsg);
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
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }

                    // 액티비티와 연결된 경우 그것과 통신할 수 있는 메신저를 저장한다.
                    mActivityMessengerList.add(msg.replyTo);

                    Log.e("Broker서버와 연결 완료","완료!");

                    break;
                // 채팅 액티비티와 연결
                case CONNECT_CHAT_ACT:
                    // 액티비티와 연결된 경우 그것과 통신할 수 있는 메신저를 저장한다.
                    mActivityMessengerList.add(msg.replyTo);
                    Log.e("ChatAct와 소켓 연결 12. 메신저리스트에 새로운 메신저 추가 ","" + mActivityMessengerList.size());

                    isChatActivityActive = true;

                    chatRoomId = msg.getData().getString("chatRoomId");
                    userId = msg.getData().getString("userId");

                    // 채팅방 클릭 시 구독
                    chatUtil.enterChatRoom(mqttClient, chatRoomId);

                    break;
                // 채팅 액티비티 - 서버 연결 해제
                case DISCONNECTED_CHAT_ACT:
                    // 채팅 액티비티의 활성화여부 = false 를 넣어줌
                    isChatActivityActive = false;
                    Log.e("채팅방 나갈 때 서비스와의 연결 제거 4-1. isChatActivityActive false로 변경", "" + isChatActivityActive);

                    String chatRoomId = msg.getData().getString("chatRoomId");
                    Log.e("채팅방 나갈 때 서비스와의 연결 제거 4-2. chatRoomId", "" + chatRoomId);

                    // 메신저 제거!
                    mActivityMessengerList.remove(1);
                    Log.e("채팅방 나갈 때 서비스와의 연결 제거 4-3. 메신저 제거!", "" + mActivityMessengerList.size());

                    break;
            }
        }
    }

    public ChatService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        Toast.makeText(getApplicationContext(), "binding", Toast.LENGTH_SHORT).show();

        // 메신저의 매개변수로 핸들러를 받는 구나
        mServiceMessenger = new Messenger(new IncomingHandler()); // 매신저의 매개변수로 핸들러
        return mServiceMessenger.getBinder();
    }
}