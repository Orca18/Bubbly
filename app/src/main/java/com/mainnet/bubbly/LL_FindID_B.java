package com.mainnet.bubbly;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.mainnet.bubbly.R;
import com.mainnet.bubbly.retrofit.ApiClient;
import com.mainnet.bubbly.retrofit.ApiInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LL_FindID_B extends AppCompatActivity {
    Toolbar toolbar;
    LinearLayout bt_confirm_findID;
    EditText et_authentication_phonenum_findID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ll_findid_b);
        // 툴바
        toolbar = findViewById(R.id.toolbar_findID_B);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        bt_confirm_findID = findViewById(R.id.bt_confirm_findID);
        et_authentication_phonenum_findID = findViewById(R.id.et_authentication_phonenum_findID);

        Intent mIntent = getIntent();
        String user_phone = mIntent.getStringExtra("user_phone");

        // 휴대폰 인증번호 확인
        bt_confirm_findID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApiInterface api = ApiClient.getApiClient(LL_FindID_B.this).create(ApiInterface.class);
                Call<String> call = api.verifyPhoneNumAndGetLoginId(user_phone, et_authentication_phonenum_findID.getText().toString());
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            if(response.body().toString().equals("not equal")){
                                Toast.makeText(getApplicationContext(), "입력한 코드가 발송 코드와 일치하지 않습니다.",Toast.LENGTH_SHORT).show();
                            }else{
                                try {
                                    JSONArray jsonArray = new JSONArray(response.body());
                                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                                    String login_id = jsonObject.getString("login_id");
                                    String user_id = jsonObject.getString("user_id");
                                    et_authentication_phonenum_findID.setEnabled(false); // 휴대폰 인증 전송 시 휴대폰번호 입력란 비활성화
                                    Intent mIntent = new Intent(getApplicationContext(), LL_FindID_C.class);
                                    mIntent.putExtra("login_id",login_id);
                                    mIntent.putExtra("user_id",user_id);
                                    startActivity(mIntent);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                        Log.e("휴대폰 인증번호 전송 에러", t.getMessage());
                    }
                });
            }
        });


        et_authentication_phonenum_findID.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                InputMethodManager keyboard = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                keyboard.hideSoftInputFromWindow(et_authentication_phonenum_findID.getWindowToken(), 0);
            }
        });


        //키보드 내리기
        et_authentication_phonenum_findID.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(et_authentication_phonenum_findID.getWindowToken(), 0);    //hide keyboard
                    return true;
                }
                return false;
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

}
