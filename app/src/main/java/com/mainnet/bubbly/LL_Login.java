package com.mainnet.bubbly;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.List;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

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

        bt_login = findViewById(R.id.bt_login); //????????? ??????
        tv_register = findViewById(R.id.tv_register); //???????????? ??????
        et_login_id = findViewById(R.id.et_login_id);
        et_login_pw = findViewById(R.id.et_login_pw);
        reset_password = findViewById(R.id.reset_password); //?????? ??????

        preferences = getSharedPreferences("novarand",MODE_PRIVATE);
        editor = preferences.edit();

        editor.remove("user_id");
        editor.commit();

        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //id or pw??? ??????/null/""??? ?????? ????????? ???????????? ????????? ?????????.
                if(et_login_id.getText().toString().equals("")||et_login_id.getText().toString().equals(null)){
                    Toast.makeText(getApplicationContext(), "???????????? ??????????????????.",Toast.LENGTH_SHORT).show();
                }else if(et_login_pw.getText().toString().equals("")||et_login_pw.getText().toString().equals(null)){
                    Toast.makeText(getApplicationContext(), "??????????????? ??????????????????.",Toast.LENGTH_SHORT).show();
                }else{
                    //???????????? ?????????
                    String pw =  et_login_pw.getText().toString();
                    String encryptedPW = encryption(pw);
                    //?????? ????????? ?????? ?????? ????????? ???????????? ????????????.
                    ApiInterface login_api = ApiClient.getApiClient(getApplicationContext()).create(ApiInterface.class);
                    Call<String> call = login_api.login(et_login_id.getText().toString(),encryptedPW);
                    call.enqueue(new Callback<String>()
                    {
                        @Override
                        public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response)
                        {
                            if (response.isSuccessful() && response.body() != null)
                            {
                                Log.e("????????? ?????????", response.body().toString());
                                Log.d("502 ?????????", "onResponse: "+response.body());
                                Log.d("502 ?????????", "onResponse: "+response.message());
                                if(response.body().toString().equals("fail")){
                                    Toast.makeText(getApplicationContext(), "????????? ??????",Toast.LENGTH_SHORT).show();
                                }
                                else if(response.body().toString().equals("stop")){
                                    Toast.makeText(getApplicationContext(), "????????? ??????????????????.",Toast.LENGTH_SHORT).show();
                                }
                                else{
//                                    Toast.makeText(getApplicationContext(), "????????? ??????",Toast.LENGTH_SHORT).show();

                                    //??????????????? : ??????????????????????????? ????????????.
                                    MasterKey masterKey = null;
                                    try {
                                        masterKey = new MasterKey.Builder(getApplicationContext(), MasterKey.DEFAULT_MASTER_KEY_ALIAS)
                                                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                                                .build(); //????????? ??? ??????
                                        SharedPreferences sharedPreferences = EncryptedSharedPreferences
                                                .create(getApplicationContext(),
                                                        "account",
                                                        masterKey,
                                                        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV, //key(name, ????????? mnemonic) ????????? ??????
                                                        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM); //value ????????? ?????? ??????

                                        SharedPreferences.Editor spfEditor = sharedPreferences.edit();
                                        spfEditor.putString("id", et_login_id.getText().toString());
                                        spfEditor.putString("pw", encryptedPW);
                                        spfEditor.commit();
                                    } catch (GeneralSecurityException e) {
                                        e.printStackTrace();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    //????????? ???????????? json?????? ????????????.
                                    JSONObject json = null;
                                    try {
                                        //????????? ????????? static?????? ????????????.
                                        json = new JSONObject(response.body().toString());
                                        AccessAndRefreshToken.accessToken = json.getString("accessToken");
                                        AccessAndRefreshToken.refreshToken = json.getString("refreshToken");
                                        int user_id = json.getInt("userId");

                                        //????????? ????????? ??????????????? ???????????? ????????? ????????? ???????????? (???????????? ?????? ????????? ???????????? ?????? ?????? ?????? ?????? ??????)
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
                                            Map<String, ?> all = sharedPreferences.getAll();
                                            System.out.println(all.values());
                                            UserInfo.user_addr = sharedPreferences.getString("address","");
                                            UserInfo.mnemonic = sharedPreferences.getString("mnemonic","");
                                        } catch (GeneralSecurityException e) {
                                            e.printStackTrace();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }


                                        //??????????????? ????????????.
                                        Call<List<user_Response>> call_userInfo = login_api.selectUserInfo(""+user_id);
                                        call_userInfo.enqueue(new Callback<List<user_Response>>()
                                        {
                                            @Override
                                            public void onResponse(@NonNull Call<List<user_Response>> call, @NonNull Response<List<user_Response>> response)
                                            {
                                                System.out.println(response.body());
                                                //????????? ??????????????? ??????????????? ????????????.
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
                                                    UserInfo.profile_file_name = Config.cloudfront_addr+responseResult.get(0).getProfile_file_name();
                                                }
                                            }
                                            @Override
                                            public void onFailure(@NonNull Call<List<user_Response>> call, @NonNull Throwable t)
                                            {
                                                Log.e("??????", t.getMessage());
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

                                    //????????? ???????????? ????????? ??????. ?????? static?????? ?????????????????? ???????????????, ?????? ?????????????????? ???????????? ????????? ??????.
                                    String[] split = response.body().toString().split(":");
                                    String splitId = split[3];
                                    splitId = splitId.replace("}", "");
                                    Log.e("userID", splitId);
                                    editor.putString("user_id",splitId);
                                    editor.commit();

//                                    startActivity(new Intent(LL_Login.this, MM_Home.class));



                                    Intent intent = new Intent(LL_Login.this, MainActivity.class);

                                    intent.putExtra("deep_type", "");
                                    intent.putExtra("deep_id", "");

                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                                    finishAffinity();

                                }
                            }
                        }
                        @Override
                        public void onFailure(@NonNull Call<String> call, @NonNull Throwable t)
                        {
                            Log.e("????????? ??????", t.getMessage());
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