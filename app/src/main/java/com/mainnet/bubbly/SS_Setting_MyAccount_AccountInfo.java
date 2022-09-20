package com.mainnet.bubbly;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.mainnet.bubbly.R;
import com.mainnet.bubbly.model.UserInfo;

public class SS_Setting_MyAccount_AccountInfo extends AppCompatActivity {

    Toolbar toolbar;
    TextView tv_id, tv_phone, tv_email, tv_address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ss_setting_myaccount_accountinfo);

        // (기본) 리소스 ID 선언
        initiallize();

        tv_id.setText("@"+UserInfo.login_id);
        tv_phone.setText(UserInfo.phone_num);
        tv_email.setText(UserInfo.email_addr);
        tv_address.setText(UserInfo.user_addr);

        //블록체인 계정 정보 요청
//        ApiInterface api = ApiClient.getApiClient().create(ApiInterface.class);
//        Call<String> call_addr = api.selectAddrUsingUserId(UserInfo.user_id);
//        call_addr.enqueue(new Callback<String>()
//        {
//            @Override
//            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response)
//            {
//                if (response.isSuccessful() && response.body() != null)
//                {
//                    System.out.println(response.body());
//                    try {
//                        JSONObject jsonObject = new JSONObject(response.body());
//                        String address = jsonObject.getString("address");
//
//                        //화면에 개인 정보 표시하기
//                        tv_id.setText("@"+UserInfo.login_id);
//                        tv_phone.setText(UserInfo.phone_num);
//                        tv_email.setText(UserInfo.email_addr);
//                        tv_address.setText(UserInfo.user_addr);
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//            @Override
//            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t)
//            {
//                Log.e("계정 정보 수신 에러", t.getMessage());
//            }
//        });


    }

    // 리소스 아이디 선언
    private void initiallize() {
        // 툴바
        toolbar = findViewById(R.id.toolbar_setting_myAccount_accountInfo);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tv_id = findViewById(R.id.tv_id_settingMyAccount);
        tv_phone = findViewById(R.id.tv_phone_settingMyAccount);
        tv_email = findViewById(R.id.tv_email_settingMyAccount);
        tv_address = findViewById(R.id.tv_addr_settingMyAccount);

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
