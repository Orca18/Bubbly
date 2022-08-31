package com.example.bubbly;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.bubbly.model.AccessAndRefreshToken;
import com.example.bubbly.model.UserInfo;
import com.example.bubbly.retrofit.ApiClient;
import com.example.bubbly.retrofit.ApiInterface;
import com.example.bubbly.retrofit.user_Response;
import com.google.android.datatransport.runtime.firebase.transport.LogEventDropped;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LL_Login extends AppCompatActivity {

    LinearLayout bt_login;
    TextView tv_register, reset_password;
    EditText et_login_id,et_login_pw;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    String id, pw;
    Boolean sptest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        bt_login = findViewById(R.id.bt_login); //로그인 버튼
        tv_register = findViewById(R.id.tv_register); //회원가입 버튼
        et_login_id = findViewById(R.id.et_login_id);
        et_login_pw = findViewById(R.id.et_login_pw);
        reset_password = findViewById(R.id.reset_password); //계정 찾기

        preferences = getSharedPreferences("novarand",MODE_PRIVATE);
        editor = preferences.edit();

        editor.remove("user_id");
        editor.commit();

        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //id or pw가 빈칸/null/""일 경우 입력을 요청하는 토스트 띄운다.
                if(et_login_id.getText().toString().equals("")||et_login_id.getText().toString().equals(null)){
                    Toast.makeText(getApplicationContext(), "아이디를 입력해주세요.",Toast.LENGTH_SHORT).show();
                }else if(et_login_pw.getText().toString().equals("")||et_login_pw.getText().toString().equals(null)){
                    Toast.makeText(getApplicationContext(), "비밀번호를 입력해주세요.",Toast.LENGTH_SHORT).show();
                }else{
                    //둘다 빈칸이 아닐 경우 서버로 로그인을 요청한다.
                    ApiInterface login_api = ApiClient.getApiClient().create(ApiInterface.class);
                    Call<String> call = login_api.login(et_login_id.getText().toString(), et_login_pw.getText().toString());
                    call.enqueue(new Callback<String>()
                    {
                        @Override
                        public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response)
                        {
                            if (response.isSuccessful() && response.body() != null)
                            {
                                Log.e("로그인 데이터", response.body().toString());
                                Log.d("502 테스트", "onResponse: "+response.body());
                                Log.d("502 테스트", "onResponse: "+response.message());
                                if(response.body().toString().equals("fail")){
                                    Toast.makeText(getApplicationContext(), "로그인 실패",Toast.LENGTH_SHORT).show();
                                }
                                else{
//                                    Toast.makeText(getApplicationContext(), "로그인 성공",Toast.LENGTH_SHORT).show();

                                    //자동로그인 : 쉐어드프리퍼런스에 저장한다.
                                    String mnemonic = response.body().toString();
                                    MasterKey masterKey = null;
                                    try {
                                        masterKey = new MasterKey.Builder(getApplicationContext(), MasterKey.DEFAULT_MASTER_KEY_ALIAS)
                                                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                                                .build(); //암호화 키 생성
                                        SharedPreferences sharedPreferences = EncryptedSharedPreferences
                                                .create(getApplicationContext(),
                                                        "account",
                                                        masterKey,
                                                        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV, //key(name, 이경우 mnemonic) 암호화 방식
                                                        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM); //value 암호화 방식 선택

                                        SharedPreferences.Editor spfEditor = sharedPreferences.edit();
                                        spfEditor.putString("id", et_login_id.getText().toString());
                                        spfEditor.putString("pw", et_login_pw.getText().toString());
                                        spfEditor.commit();
                                    } catch (GeneralSecurityException e) {
                                        e.printStackTrace();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

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
                                            System.out.println(sharedPreferences.getString("mnemonic",""));
                                            UserInfo.user_addr = sharedPreferences.getString("address","");
                                            UserInfo.mnemonic = sharedPreferences.getString("mnemonic",""); //니모닉 앞에 file titile이 포함되어서 저장되는 문제가 있음. 추후 수정 예정.
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
//                                    catch (GeneralSecurityException e) {
//                                        e.printStackTrace();
//                                    } catch (IOException e) {
//                                        e.printStackTrace();
//                                    }

                                    //기존에 남아있던 민규님 코드. 이제 static으로 변환했으므로 불필요하나, 최종 수정단계에서 한꺼번에 수정할 예정.
                                    String[] split = response.body().toString().split(":");
                                    String splitId = split[3];
                                    splitId = splitId.replace("}", "");
                                    Log.e("userID", splitId);
                                    editor.putString("user_id",splitId);
                                    editor.commit();

                                    // TODO MM_액티비티 없애고, MainActivity로 변경
//                                    startActivity(new Intent(LL_Login.this, MM_Home.class));
                                    startActivity(new Intent(LL_Login.this, MainActivity.class));
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
        });

        tv_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LL_Login.this, LL_Register_A.class));
            }
        });

        reset_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LL_Login.this, LL_FindID_A.class));
            }
        });

    }
}