package com.mainnet.bubbly.chatting.service;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.mainnet.bubbly.model.UserInfo;
import com.mainnet.bubbly.retrofit.ChatApiClient;
import com.mainnet.bubbly.retrofit.ChatApiInterface;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FCMService extends FirebaseMessagingService{
    private static Context context;

    public FCMService() {}

    private Handler handler;    // for toast UI

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
        context = getApplicationContext();
    }

    private void _runOnUiThread(Runnable runnable) {
        handler.post(runnable);
    }

    @Override
    public void onNewToken(@NonNull String token) {
        Log.d("토큰 생성", "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        //sendRegistrationToServer(token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run()
            {
//                Toast.makeText(getApplicationContext(), "remoteMessage.getFrom()", Toast.LENGTH_LONG).show();
            }
        }, 0);

        //새로운 게시글이 있을 경우 알림수신 -> 핸들러로 메세지 보냄
        handler.sendEmptyMessage(100);

        /*_runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(getApplicationContext(),
                        "메시지 받음",
                        Toast.LENGTH_LONG).show();
            }
        });*/

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d("메시지 수신: ", "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d("데이터 페이로드", "Message data payload: " + remoteMessage.getData());

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d("notification", "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
    }

    // FCM 토큰정보 갱신하기
    public static void refreshToken(String userId){
        Log.d("리프레시 토큰", "11");

        // 토큰 정보 가져오기
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("토큰 가져오기 실패", "Fetching FCM registration token failed", task.getException());
                            return;
                        }
                        Log.d("리프레시 토큰", "22");

                        // Get new FCM registration token
                        String token = task.getResult();

                        /**
                         * 서버에 토큰 리프레시 요청
                         * */
                        // 레트로핏 구현체 가져오기
                        ChatApiInterface chatApiInterface= ChatApiClient.getApiClient(context).create(ChatApiInterface.class);
                        Call<String> call = chatApiInterface.refreshToken(token,userId);
                        call.enqueue(new Callback<String>()
                        {
                            @Override
                            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response)
                            {
                                if (response.isSuccessful() && response.body() != null)
                                {
                                    Log.e("토큰 갱신 완료!", response.body());

                                    // 토큰을 새로 저장했다면!
                                    if(!response.body().equals("success") && !response.body().equals("fail")){
                                        UserInfo.token = response.body();
                                        Log.e("새로운 토큰 저장완료!", response.body());

                                    }
                                } else {
                                    Log.e("토큰 갱신 완료!","지만 데이터 없음");
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t)
                            {
                                Log.e("토큰 갱신 에러", t.getMessage());
                            }
                        });
                    }
                });
    }

    public static void getToken(){
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("토큰 가져오기 실패", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // Log and toast
                        Log.w("가져온 토큰", token);
                    }
                });
    }

    @Override
    public void handleIntent(Intent intent) {
        super.handleIntent(intent);
    }
}