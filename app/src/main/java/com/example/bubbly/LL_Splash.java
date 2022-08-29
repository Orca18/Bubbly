package com.example.bubbly;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.bubbly.model.AccessAndRefreshToken;
import com.example.bubbly.model.UserInfo;
import com.example.bubbly.retrofit.ApiClient;
import com.example.bubbly.retrofit.ApiInterface;
import com.example.bubbly.retrofit.user_Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LL_Splash extends AppCompatActivity {
    androidx.appcompat.widget.Toolbar toolbar;

    ImageView appname,splashimg;

    LottieAnimationView lottieAnimationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);


        // 툴바
        toolbar = findViewById(R.id.splash_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        lottieAnimationView = findViewById(R.id.lottie_voltz);

        lottieAnimationView.animate().setStartDelay(0);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
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
                    startActivity(new Intent(LL_Splash.this, LL_Login.class));
                    overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                    finish();
                }else{
                    //만약 쉐어드프리퍼런스에 저장된 사용자 정보기 있으면 login api 요청 후 Home으로 이동
                    ApiInterface login_api = ApiClient.getApiClient().create(ApiInterface.class);
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
                                                    UserInfo.profile_file_name = "https://d2gf68dbj51k8e.cloudfront.net/"+responseResult.get(0).getProfile_file_name();
                                                }
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
                                    //기존에 남아있던 민규님 코드. 이제 static으로 변환했으므로 불필요하나, 최종 수정단계에서 한꺼번에 수정할 예정.
                                    String[] split = response.body().toString().split(":");
                                    String splitId = split[3];
                                    splitId = splitId.replace("}", "");
                                    Log.e("userID", splitId);
                                    SharedPreferences preferences = getSharedPreferences("novarand",MODE_PRIVATE);
                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putString("user_id",splitId);
                                    editor.commit();
                                    startActivity(new Intent(LL_Splash.this, MM_Home.class));
                                    overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                                    finish();
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
            }
        },2300);
    }

}