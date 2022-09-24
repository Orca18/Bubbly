package com.mainnet.bubbly;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.mainnet.bubbly.model.UserInfo;
import com.mainnet.bubbly.retrofit.ApiClient;
import com.mainnet.bubbly.retrofit.ApiInterface;

import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SS_Setting_MyAccount_ChangeID extends AppCompatActivity {
    Toolbar toolbar;
    LinearLayout bt_changeID;
    EditText et_id;
    TextView tv_id_check,tv_changeID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ss_setting_myaccount_changeid);
        toolbar = findViewById(R.id.toolbar_setting_myAccount_changeID);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        bt_changeID = findViewById(R.id.bt_change_setting_confirmID);
        et_id = findViewById(R.id.et_id_setting_changeID);
        tv_id_check = findViewById(R.id.tv_id_check_setting_changeID);
        tv_changeID = findViewById(R.id.tv_change_setting_confirmPW);
        bt_changeID.setEnabled(false);

        //어이디 변경 버튼
        bt_changeID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApiInterface api = ApiClient.getApiClient(SS_Setting_MyAccount_ChangeID.this).create(ApiInterface.class);
                Call<String> call = api.changeLoginId(et_id.getText().toString(),UserInfo.user_id);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            if(response.body().toString().equals("fail")){
                                Toast.makeText(getApplicationContext(), "변경에 실패했습니다.",Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(getApplicationContext(), "성공적으로 변경했습니다.",Toast.LENGTH_SHORT).show();
                                UserInfo.login_id = et_id.getText().toString();
                                Intent mIntent = new Intent(getApplicationContext(), SS_Setting_MyAccount.class);
                                startActivity(mIntent);
                            }
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                        Log.e("아이디 변경 에러", t.getMessage());
                    }
                });
            }
        });


        // 변경 id입력
        et_id.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tv_id_check.setText("");
            }
            @Override
            public void afterTextChanged(Editable s) {
                //아이디 정규식 확인
                String rex = "^([A-Za-z0-9]*)$";
                Pattern pattern = Pattern.compile(rex);
                if(pattern.matcher(et_id.getText().toString()).matches()&&!et_id.getText().toString().equals("")){
                    tv_id_check.setText("아이디 규칮에 맞습니다.");
                    //아이디 중복 체크
                    ApiInterface selectIsExistingId_api = ApiClient.getApiClient(SS_Setting_MyAccount_ChangeID.this).create(ApiInterface.class);
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
                                    tv_id_check.setText("중복된 아이디입니다.");
                                    tv_id_check.setTextColor(Color.parseColor("#FF0000"));
                                }
                                else if(response.body().toString().equals("not exist")){ // 아이디 중복x 사용가능
                                    tv_id_check.setText("사용가능한 아이디입니다.");
                                    tv_id_check.setTextColor(Color.parseColor("#002AFF"));
                                    InputMethodManager keyboard = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                                    keyboard.hideSoftInputFromWindow(et_id.getWindowToken(), 0);
                                    tv_id_check.setTextColor(Color.parseColor("#002AFF"));
                                    bt_changeID.setEnabled(true);
                                    bt_changeID.setBackgroundColor(Color.parseColor("#FF000000"));
                                    tv_changeID.setTextColor(Color.parseColor("#FFFFFF"));
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
                    tv_id_check.setText("아이디 규칮에 맞지 않습니다.");
                    tv_id_check.setTextColor(Color.parseColor("#FF0000"));
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

}
