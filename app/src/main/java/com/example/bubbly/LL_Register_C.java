package com.example.bubbly;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bubbly.retrofit.ApiClient;
import com.example.bubbly.retrofit.ApiInterface;

import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LL_Register_C extends AppCompatActivity {

    // 뒤로가기 시간
    private long backKeyPressedTime = 0;
    private Toast toast;

    LinearLayout bt_go_login;
    androidx.appcompat.widget.Toolbar toolbar;

    Button bt_copy_address,bt_copy_mnemonic, bt_share_wallet;
    TextView tv_address,tv_mnemonic;
    ClipboardManager clipboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_c);

        toolbar = findViewById(R.id.toolbar_registerC);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        // 뒤로가기 버튼, 디폴트로 true 만 해도 백버튼이 생김
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //리소스 할당
        bt_copy_address = findViewById(R.id.bt_copy_address_register);
        tv_address = findViewById(R.id.tv_address_register);
        bt_copy_mnemonic = findViewById(R.id.bt_copy_mnemonic_register);
        tv_mnemonic = findViewById(R.id.tv_mnemonic_register);
        bt_share_wallet = findViewById(R.id.bt_shar_wallet_register);
        bt_go_login = findViewById(R.id.bt_go_login_register);


        //니모닉 가져오기
        Intent mIntent = getIntent();
        String address = mIntent.getStringExtra("address");
        String mnemonic = mIntent.getStringExtra("mnemonic");

        //데이터 표시
        tv_address.setText(address);
        tv_mnemonic.setText(mnemonic);

        // 로그인 페이지 이동
        bt_go_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(getApplicationContext(), LL_Login.class);
                startActivity(mIntent);
                finish();
            }
        });

        //주소 카피
        bt_copy_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("address", address);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getApplicationContext(), "클립보드에 복사되었습니다.",Toast.LENGTH_SHORT).show();
            }
        });


        //니모닉 카피
        bt_copy_mnemonic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("mnemonic", mnemonic);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getApplicationContext(), "클립보드에 복사되었습니다.",Toast.LENGTH_SHORT).show();
            }
        });

        //지갑연결
        bt_share_wallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String unicornWalletPackageName = "com.example.unicornWallet";
                if(isAppInstalled(unicornWalletPackageName)){
                    Intent intent = new Intent();
                    intent.setComponent(new ComponentName(unicornWalletPackageName,unicornWalletPackageName+".MainActivity"));
                    intent.putExtra("address",address);
                    intent.putExtra("mnemonic",mnemonic);
                    startActivity(intent);
                }
                else{
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    intent.setData(Uri.parse("market://details?id="+unicornWalletPackageName));
                    startActivity(intent);
                }
            }
        });

    } // onCreate 닫는곳



    private boolean isAppInstalled(String packageName) {
        PackageManager pm = getPackageManager();
        boolean installed = false;
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            installed = false;
        }
        return installed;
    }

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

}