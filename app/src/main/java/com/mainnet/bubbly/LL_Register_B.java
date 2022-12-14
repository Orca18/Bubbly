package com.mainnet.bubbly;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.mainnet.bubbly.R;
import com.mainnet.bubbly.model.AccessAndRefreshToken;
import com.mainnet.bubbly.model.UserInfo;
import com.mainnet.bubbly.retrofit.ApiClient;
import com.mainnet.bubbly.retrofit.ApiInterface;
import com.mainnet.bubbly.retrofit.user_Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.List;
import java.util.regex.Pattern;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LL_Register_B extends AppCompatActivity {

    // 뒤로가기 시간
    private long backKeyPressedTime = 0;
    private Toast toast;

    LinearLayout done;
    androidx.appcompat.widget.Toolbar toolbar;

    EditText et_id, et_password, et_password_check, et_nick;
    TextView tv_id_check, tv_pw_check, tv_done;

    ProgressBar progressBar_register;
    TextView register_text;

    int id_check, pw_check, nick_check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_b);

        toolbar = findViewById(R.id.toolbar_registerB);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        // 뒤로가기 버튼, 디폴트로 true 만 해도 백버튼이 생김
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        et_id = findViewById(R.id.et_id);
        tv_id_check = findViewById(R.id.tv_id_check);
        et_password = findViewById(R.id.et_password);
        et_password_check = findViewById(R.id.et_password_check);
        tv_pw_check = findViewById(R.id.tv_pw_check);
        et_nick = findViewById(R.id.et_nick);
        done = findViewById(R.id.done);
        tv_done = findViewById(R.id.tv_done);
        progressBar_register = findViewById(R.id.progressBar_register);
        register_text = findViewById(R.id.register_text);

        Intent mIntent = getIntent();
        String user_email = mIntent.getStringExtra("user_email");
        String user_phone = mIntent.getStringExtra("user_phone");

        // 회원가입 (회원정보 저장)
        done.setEnabled(false);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tv_id_check.getText().toString().equals("사용가능한 아이디입니다.") && tv_pw_check.getText().toString().equals("비밀번호가 일치합니다.")){
                    progressBar_register.setVisibility(View.VISIBLE);
                    register_text.setVisibility(View.VISIBLE);

                    // 화면터치 금지
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                    //클라이언트 비밀번호 암호화
                    String pw =  et_password.getText().toString();
                    String encryptedPW = encryption(pw);
                    ApiInterface createUserInfo_api = ApiClient.getApiClient(LL_Register_B.this).create(ApiInterface.class);
                    Call<String> call = createUserInfo_api.createUserInfo(et_id.getText().toString(), encryptedPW, user_email, user_phone, et_nick.getText().toString());
                    call.enqueue(new Callback<String>()
                    {
                        @Override
                        public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response)
                        {
                            if (response.isSuccessful() && response.body() != null)
                            {
                                Log.e("희원가입 성공", response.body().toString());
                                if(response.body().toString().equals("success")){
                                    //Toast.makeText(getApplicationContext(), "희원가입 성공",Toast.LENGTH_SHORT).show();
                                    //현 시점에서 userID 알수 없어, 로그인해서 userID 가져오기
                                    ApiInterface login_api = ApiClient.getApiClient(LL_Register_B.this).create(ApiInterface.class);
                                    Call<String> call_login = login_api.login(et_id.getText().toString(), encryptedPW);
                                    call_login.enqueue(new Callback<String>()
                                    {
                                        @Override
                                        public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response)
                                        {
                                            if (response.isSuccessful() && response.body() != null)
                                            {
                                                Log.e("로그인 데이터", response.body().toString());
                                                if(response.body().toString().equals("fail")){
                                                    progressBar_register.setVisibility(View.GONE);
                                                    register_text.setVisibility(View.GONE);
                                                    Toast.makeText(getApplicationContext(), "로그인 실패",Toast.LENGTH_SHORT).show();
                                                }
                                                else{
//                                                    Toast.makeText(getApplicationContext(), "로그인 성공",Toast.LENGTH_SHORT).show();
                                                    //수신한 데이터를 json으로 파싱한다.
                                                    JSONObject json = null;
                                                    try {
                                                        //수신한 토큰을 static으로 저장한다.
                                                        json = new JSONObject(response.body().toString());
                                                        AccessAndRefreshToken.accessToken = json.getString("accessToken");
                                                        AccessAndRefreshToken.refreshToken = json.getString("refreshToken");
                                                        int user_id = json.getInt("userId");
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

                                                                //블록체인 계정생성 요청
                                                                ApiInterface api = ApiClient.getApiClient(LL_Register_B.this).create(ApiInterface.class);
                                                                Call<String> call_mnemonic = api.createAddrToBlockchain(UserInfo.user_id);
                                                                call_mnemonic.enqueue(new Callback<String>()
                                                                {
                                                                    @Override
                                                                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response)
                                                                    {
                                                                        if (response.isSuccessful() && response.body() != null)
                                                                        {
                                                                            if(!response.body().equals("fail")){
                                                                                Log.e("성공 : 니모닉  - ", response.body().toString());
                                                                                String mnemonic = "";
                                                                                String address = "";
                                                                                try {
                                                                                    JSONObject jsonObject = new JSONObject(response.body().toString());
                                                                                    mnemonic = jsonObject.getString("mnemonic");
                                                                                    address = jsonObject.getString("addr");
                                                                                } catch (JSONException e) {
                                                                                    e.printStackTrace();
                                                                                }
                                                                                MasterKey masterkey = null;
                                                                                try {
                                                                                    masterkey = new MasterKey.Builder(getApplicationContext(), MasterKey.DEFAULT_MASTER_KEY_ALIAS)
                                                                                            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                                                                                            .build(); //암호화 키 생성
                                                                                    SharedPreferences sharedPreferences = EncryptedSharedPreferences
                                                                                            .create(getApplicationContext(),
                                                                                                    "account",
                                                                                                    masterkey,
                                                                                                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV, //key(name, 이경우 mnemonic) 암호화 방식
                                                                                                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM); //value 암호화 방식 선택

                                                                                    SharedPreferences.Editor spfEditor = sharedPreferences.edit();
                                                                                    spfEditor.putString("address", address);
                                                                                    spfEditor.putString("mnemonic", mnemonic);
                                                                                    spfEditor.commit();
                                                                                } catch (GeneralSecurityException e) {
                                                                                    e.printStackTrace();
                                                                                } catch (IOException e) {
                                                                                    e.printStackTrace();
                                                                                }

                                                                                // 화면터치 금지해제
                                                                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                                                                progressBar_register.setVisibility(View.GONE);
                                                                                register_text.setVisibility(View.GONE);

                                                                                //인텐트 전달
                                                                                Intent mIntent = new Intent(getApplicationContext(), LL_Register_C.class);
                                                                                mIntent.putExtra("address",address);
                                                                                mIntent.putExtra("mnemonic",mnemonic);
                                                                                startActivity(mIntent);

                                                                            }else{
                                                                                // 화면터치 금지해제
                                                                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                                                                progressBar_register.setVisibility(View.GONE);
                                                                                register_text.setVisibility(View.GONE);
                                                                                Toast.makeText(getApplicationContext(), "블록체인 계정 생성에 실패하였습니다.",Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        }
                                                                    }
                                                                    @Override
                                                                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t)
                                                                    {
                                                                        // 화면터치 금지해제
                                                                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                                                        progressBar_register.setVisibility(View.GONE);
                                                                        register_text.setVisibility(View.GONE);
                                                                        Log.e("니모닉 생성 에러", t.getMessage());
                                                                    }
                                                                });

                                                            }
                                                            @Override
                                                            public void onFailure(@NonNull Call<List<user_Response>> call, @NonNull Throwable t)
                                                            {
                                                                // 화면터치 금지해제
                                                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                                                progressBar_register.setVisibility(View.GONE);
                                                                register_text.setVisibility(View.GONE);
                                                                Log.e("에러", t.getMessage());
                                                            }
                                                        });
                                                    } catch (JSONException e) {
                                                        // 화면터치 금지해제
                                                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                                        progressBar_register.setVisibility(View.GONE);
                                                        register_text.setVisibility(View.GONE);
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }
                                        }
                                        @Override
                                        public void onFailure(@NonNull Call<String> call, @NonNull Throwable t)
                                        {
                                            // 화면터치 금지해제
                                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                            progressBar_register.setVisibility(View.GONE);
                                            register_text.setVisibility(View.GONE);
                                            Log.e("로그인 에러", t.getMessage());
                                        }
                                    });
                                }
                                else{
                                    Toast.makeText(getApplicationContext(), "희원가입 실패",Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<String> call, @NonNull Throwable t)
                        {
                            // 화면터치 금지해제
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                            progressBar_register.setVisibility(View.GONE);
                            register_text.setVisibility(View.GONE);
                            Log.e("희원가입 에러", t.getMessage());
                        }
                    });
                }
            }
        });

        // 아이디 중복체크
        et_id.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(et_id.length() == 0){
                    tv_id_check.setText("");
                    id_check = 0;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                tv_done.setTextColor(Color.parseColor("#737373"));
                done.setEnabled(false);
                done.setBackgroundColor(Color.parseColor("#eeeeee"));

                if(et_id.length() > 0){
                    //정규식 확인(영문 대소문자/숫자만 가능)
                    String idRex = "^([A-Za-z0-9]*)$";  //영숫자만 가능, 띄어쓰기 불가
                    Pattern pattern = Pattern.compile(idRex);
                    if(pattern.matcher(et_id.getText().toString()).matches()){
                        ApiInterface selectIsExistingId_api = ApiClient.getApiClient(LL_Register_B.this).create(ApiInterface.class);
                        Call<String> call = selectIsExistingId_api.selectIsExistingId(et_id.getText().toString());
                        call.enqueue(new Callback<String>()
                        {
                            @Override
                            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response)
                            {
                                if (response.isSuccessful() && response.body() != null)
                                {
                                    Log.e("아이디 중복 체크 성공", response.body().toString());
                                    if(response.body().toString().equals("exist")){ // 아이디 중복
                                        id_check = 0;
                                        tv_id_check.setText("중복된 아이디입니다.");
                                        tv_id_check.setTextColor(Color.parseColor("#FF0000"));
                                    }
                                    else if(response.body().toString().equals("not exist")){ // 아이디 중복x 사용가능
                                        id_check = 1;
                                        tv_id_check.setText("사용가능한 아이디입니다.");
                                        tv_id_check.setTextColor(Color.parseColor("#002AFF"));
                                        if(id_check == 1 && pw_check == 1 && nick_check == 1){
                                            InputMethodManager keyboard = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                                            keyboard.hideSoftInputFromWindow(et_id.getWindowToken(), 0);
                                            tv_done.setTextColor(Color.parseColor("#FFFFFF")); // 아이디,비밀번호 정상 입력시 다음 텍스트 색상 흰색
                                            done.setEnabled(true); // 인증완료 시 다음 버튼 활성화
                                            done.setBackgroundColor(Color.parseColor("#FF000000")); // 아이디,비밀번호 정상 시 다음 버튼 색상 검정색
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t)
                            {
                                Log.e("아이디 중복 체크 에러", t.getMessage());
                            }
                        });
                    }else{
                        Toast.makeText(getApplicationContext(), "아이디 규칮에 맞지 않습니다.",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // pw
        et_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                et_password_check.setText(null); // 비밀번호를 다시입력시, 비밀번호 확인란 입력값 초기화
                tv_pw_check.setText("");
                pw_check = 0;
            }
            @Override
            public void afterTextChanged(Editable s) {
                //비밀번호 정규식 확인
                String pwRex = "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@$!%*#?&]).{8,16}.$";
                Pattern pattern = Pattern.compile(pwRex);
                if(pattern.matcher(et_password.getText().toString()).matches()){
                    tv_pw_check.setText("비밀번호 규칮에 맞습니다.");
                    tv_pw_check.setTextColor(Color.parseColor("#002AFF"));
                    et_password_check.setEnabled(true);
                }else{
                    tv_pw_check.setText("비밀번호 규칮에 맞지 않습니다.");
                    tv_pw_check.setTextColor(Color.parseColor("#FF0000"));
                }
            }
        });

        // pw 확인
        et_password_check.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                tv_done.setTextColor(Color.parseColor("#737373"));
                done.setEnabled(false);
                done.setBackgroundColor(Color.parseColor("#eeeeee"));

                if(et_password.getText().toString().equals(et_password_check.getText().toString())){
                    tv_pw_check.setText("비밀번호가 일치합니다.");
                    tv_pw_check.setTextColor(Color.parseColor("#002AFF"));
                    pw_check = 1;
                    if(id_check == 1 && pw_check == 1 && nick_check ==1){
                        InputMethodManager keyboard = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        keyboard.hideSoftInputFromWindow(et_password_check.getWindowToken(), 0);
                        tv_done.setTextColor(Color.parseColor("#FFFFFF")); // 아이디,비밀번호 정상 입력시 다음 텍스트 색상 흰색
                        done.setEnabled(true); // 인증완료 시 다음 버튼 활성화
                        done.setBackgroundColor(Color.parseColor("#FF000000")); // 아이디,비밀번호 정상 시 다음 버튼 색상 검정색
                    }
                }
                else{
                    pw_check = 0;
                    tv_pw_check.setText("비밀번호가 일치하지 않습니다.");
                    tv_pw_check.setTextColor(Color.parseColor("#FF0000"));
                }
            }
        });


        // nick
        et_nick.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                nick_check = 0;
            }
            @Override
            public void afterTextChanged(Editable s) {
                tv_done.setTextColor(Color.parseColor("#737373"));
                done.setEnabled(false);
                done.setBackgroundColor(Color.parseColor("#eeeeee"));
                nick_check = 1;
                if(id_check == 1 && pw_check == 1 && nick_check ==1){
                    tv_done.setTextColor(Color.parseColor("#FFFFFF")); // 아이디,비밀번호 정상 입력시 다음 텍스트 색상 흰색
                    done.setEnabled(true); // 인증완료 시 다음 버튼 활성화
                    done.setBackgroundColor(Color.parseColor("#FF000000")); // 아이디,비밀번호 정상 시 다음 버튼 색상 검정색
                }
            }
        });
    } // onCreate 닫는곳

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
                    backKeyPressedTime = System.currentTimeMillis();
                    toast = Toast.makeText(this, "인증 다시 받아야되는데, 진짜 종료??", Toast.LENGTH_SHORT);
                    toast.show();
                } else if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
                    finish();
                    toast.cancel();
                }
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }




    //뒤로가기 했을 때
    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            toast = Toast.makeText(this, "인증 다시 받아야되는데, 진짜 종료??", Toast.LENGTH_SHORT);
            toast.show();
            return;
        } else if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            finish();
            toast.cancel();
        } else {
            super.onBackPressed();
        }
    }


    private String encryption(String str) {
        String result = "";
        byte[] ivBytes = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
        String secretKey = "Novarand";
        byte[] plaintext = new byte[0];
        try {
            plaintext = str.getBytes("UTF-8");
            AlgorithmParameterSpec ivSpec = new IvParameterSpec(ivBytes);
            SecretKeySpec newKey = new SecretKeySpec(secretKey.getBytes("UTF-8"), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, newKey, ivSpec);
            result = Base64.encodeToString(cipher.doFinal(plaintext), 0);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return result;
    }

}