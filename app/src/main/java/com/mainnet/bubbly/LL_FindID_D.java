package com.mainnet.bubbly;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.mainnet.bubbly.R;
import com.mainnet.bubbly.retrofit.ApiClient;
import com.mainnet.bubbly.retrofit.ApiInterface;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
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

public class LL_FindID_D extends AppCompatActivity {

    Toolbar toolbar;
    LinearLayout bt_changePW_findID;
    EditText et_password_findID, et_password_check_findID;
    TextView tv_pw_check_findID, tv_changePW_findID;
    int pw_check = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ll_findid_d);
        bt_changePW_findID = findViewById(R.id.bt_changePW_findID);
        et_password_findID = findViewById(R.id.et_password_findID);
        et_password_check_findID = findViewById(R.id.et_password_check_findID);
        tv_pw_check_findID = findViewById(R.id.tv_pw_check_findID);
        tv_changePW_findID = findViewById(R.id.tv_changePW_findID);
        Intent mIntent = getIntent();
        String  user_id = mIntent.getStringExtra("user_id");

        et_password_check_findID.setEnabled(false);
        bt_changePW_findID.setEnabled(false);

        //???????????? ??????
        bt_changePW_findID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //???????????? ?????????
                String pw =  et_password_check_findID.getText().toString();
                String encryptedPW = encryption(pw);
                ApiInterface api = ApiClient.getApiClient(getApplicationContext()).create(ApiInterface.class);
                Call<String> call = api.modifyPassword(user_id, encryptedPW);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            if(response.body().toString().equals("fail")){
                                Toast.makeText(getApplicationContext(), "????????? ??????????????????.",Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(getApplicationContext(), "??????????????? ??????????????????.",Toast.LENGTH_SHORT).show();
                                Intent mIntent = new Intent(getApplicationContext(), LL_Login.class);
                                startActivity(mIntent);
                            }
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                        Log.e("????????? ???????????? ?????? ??????", t.getMessage());
                    }
                });
            }
        });


        // pw
        et_password_findID.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                et_password_check_findID.setText(null); // ??????????????? ???????????????, ???????????? ????????? ????????? ?????????
                tv_pw_check_findID.setText("");
                pw_check = 0;
            }
            @Override
            public void afterTextChanged(Editable s) {
                //???????????? ????????? ??????
                String pwRex = "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@$!%*#?&]).{8,16}.$";
                Pattern pattern = Pattern.compile(pwRex);
                if(pattern.matcher(et_password_findID.getText().toString()).matches()){
                    tv_pw_check_findID.setText("???????????? ????????? ????????????.");
                    tv_pw_check_findID.setTextColor(Color.parseColor("#002AFF"));
                    et_password_check_findID.setEnabled(true);
                }else{
                     tv_pw_check_findID.setText("???????????? ????????? ?????? ????????????.");
                    tv_pw_check_findID.setTextColor(Color.parseColor("#FF0000"));
                }
            }
        });

        // pw ??????
        et_password_check_findID.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if(et_password_check_findID.getText().toString().equals(et_password_findID.getText().toString())){
                    tv_pw_check_findID.setText("??????????????? ???????????????.");
                    tv_pw_check_findID.setTextColor(Color.parseColor("#002AFF"));
                    pw_check = 1;
                    if(pw_check == 1){
                        InputMethodManager keyboard = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        keyboard.hideSoftInputFromWindow(et_password_check_findID.getWindowToken(), 0);
                        tv_changePW_findID.setTextColor(Color.parseColor("#FFFFFF")); // ?????????,???????????? ?????? ????????? ?????? ????????? ?????? ??????
                        bt_changePW_findID.setEnabled(true); // ???????????? ??? ?????? ?????? ?????????
                        bt_changePW_findID.setBackgroundColor(Color.parseColor("#FF000000")); // ?????????,???????????? ?????? ??? ?????? ?????? ?????? ?????????
                    }
                }
                else{
                    pw_check = 0;
                    tv_pw_check_findID.setText("??????????????? ???????????? ????????????.");
                    tv_pw_check_findID.setTextColor(Color.parseColor("#FF0000"));
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
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
