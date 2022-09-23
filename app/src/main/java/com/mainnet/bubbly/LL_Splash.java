package com.mainnet.bubbly;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.mainnet.bubbly.R;
import com.mainnet.bubbly.config.Config;
import com.mainnet.bubbly.model.AccessAndRefreshToken;
import com.mainnet.bubbly.model.UserInfo;
import com.mainnet.bubbly.retrofit.ApiClient;
import com.mainnet.bubbly.retrofit.ApiInterface;
import com.mainnet.bubbly.retrofit.user_Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LL_Splash extends AppCompatActivity {
    androidx.appcompat.widget.Toolbar toolbar;

    ImageView appname, splashimg;

    String deep_data;
    String deep_type, deep_id;

    LottieAnimationView lottieAnimationView;

    Handler handler = new Handler();






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        // 설정파일 가져오기
        try {
            new Config(getApplicationContext()).getConfigData();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // 툴바
        toolbar = findViewById(R.id.splash_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        lottieAnimationView = findViewById(R.id.lottie_voltz);

        lottieAnimationView.animate().setStartDelay(0);


        Uri uri = this.getIntent().getData();

        if (uri != null) {
            deep_data = uri.toString();
            checkDeep();
        }

        // 딥링크를 받지 않는거라면...
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
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
                Log.d("자동 로그인 확인", "쉐어드프리퍼런스 id:" + id + "pw:" + pw);
                if (id.equals("") & pw.equals("")) {
                    //만약 쉐어드프리퍼런스에 저장된 사용자 정보가 없으면 로그인 페이지로 이동
                    startActivity(new Intent(LL_Splash.this, LL_Login.class));
                    overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                    finish();
                } else {
                    //만약 쉐어드프리퍼런스에 저장된 사용자 정보기 있으면 login api 요청 후 Home으로 이동
                    ApiInterface login_api = ApiClient.getApiClient(LL_Splash.this).create(ApiInterface.class);
                    Call<String> call = login_api.login(id, pw);
                    call.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                            Log.d("디버그태그", "바디:" + response.body());
                            Log.d("디버그태그", "메시지:" + response.message());
                            if (response.isSuccessful() && response.body() != null) {
                                Log.e("로그인 데이터", response.body().toString());
                                if (response.body().toString().equals("fail")) {
                                    Toast.makeText(getApplicationContext(), "로그인 실패", Toast.LENGTH_SHORT).show();
                                } else if(response.body().toString().equals("stop")){
                                    Toast.makeText(getApplicationContext(), "정지된 사용자입니다.",Toast.LENGTH_SHORT).show();
                                } else {
//                                    Toast.makeText(getApplicationContext(), "로그인 성공", Toast.LENGTH_SHORT).show();
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
                                            Map<String, ?> all = sharedPreferences.getAll();
                                            System.out.println("모든값" + all.toString());
                                            System.out.println("모든값" + all.values());
                                            String address = sharedPreferences.getString("address", "");
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
                                                if(response.body().toString().equals("fail")||response.body().toString().equals("stop")){
                                                    //만약 로그인 실패시 로그인 페이지로 이동한다.
                                                    Toast.makeText(getApplicationContext(), "로그인 실패",Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(LL_Splash.this, LL_Login.class));
                                                    overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                                                    finish();
                                                }
                                                else {
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
                                                }
                                            }

                                            @Override
                                            public void onFailure(@NonNull Call<List<user_Response>> call, @NonNull Throwable t) {
                                                Log.e("에러", t.getMessage());
                                                //만약 로그인 실패시 로그인 페이지로 이동한다.
                                                Toast.makeText(getApplicationContext(), "로그인 실패",Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(LL_Splash.this, LL_Login.class));
                                                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                                                finish();
                                            }
                                        });
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    //기존에 남아있던 민규님 코드. 이제 static으로 변환했으므로 불필요하나, 최종 수정단계에서 한꺼번에 수정할 예정.
                                    String[] split = response.body().toString().split(":");
                                    String splitId = split[3];
                                    splitId = splitId.replace("}", "");
                                    Log.e("userID", splitId);
                                    SharedPreferences preferences = getSharedPreferences("novarand", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putString("user_id", splitId);
                                    editor.commit();
//                                    startActivity(new Intent(LL_Splash.this, MM_Home.class));


                                    Intent intent = new Intent(LL_Splash.this, MainActivity.class);

                                    intent.putExtra("deep_type", "" + deep_type);
                                    intent.putExtra("deep_id", "" + deep_id);

                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                                    finishAffinity();

                                    // 만약 딥링크로 들어왔다면, 즉시 핸들러 종료하기
                                      if (uri != null) {
                                        handler.removeCallbacksAndMessages(0);
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
            }
        }, 2300);

    }

    private void checkDeep() {
        // 딥링크 (type & id 파싱)
//        Intent intent = getIntent();
        deep_data = deep_data.replace("bubbly://deep/", "");
        String[] typeid = deep_data.split("_");
        deep_type = typeid[0]; // 타입 - 프로필, 커뮤니티, 게시물...
        deep_id = typeid[1]; // 아이디
        Log.d("딥링크 테스트:", "딥링크 테스트:" + deep_type + "_" + deep_id);
    }
}