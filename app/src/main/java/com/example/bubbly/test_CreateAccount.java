package com.example.bubbly;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.v2.client.common.AlgodClient;
import com.example.bubbly.retrofit.ApiClient;
import com.example.bubbly.retrofit.ApiInterface;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/** 여기 파트는 깊게 안보셔도 됩니다. */
public class test_CreateAccount extends AppCompatActivity {

//    String ALGOD_API_ADDR = "http://hackathon.algodev.network";
//    Integer ALGOD_PORT = 9100;
//    String ALGOD_API_TOKEN = "ef920e2e7e002953f4b29a8af720efe8e4ecc75ff102b165e0472834b25832c1";

    // 데브넷으로 했다가, 테스트넷으로 변경
    String ALGOD_API_ADDR = "http://testnet.algorand.network";
    Integer ALGOD_PORT = 4001;
    String ALGOD_API_TOKEN = "ef920e2e7e002953f4b29a8af720efe8e4ecc75ff102b165e0472834b25832c1";

    AlgodClient client;

    TextView tv_mnemonic;

    SharedPreferences preferences;
    String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_create_account);

        tv_mnemonic = findViewById(R.id.createAccount_mnemonic25words);

        // TODO  Security 알아보기
        Security.removeProvider("BC");
        Security.insertProviderAt(new BouncyCastleProvider(), 0);
        String providerName = "BC";

        if (Security.getProvider(providerName) == null) {
            Log.d("algoDebug", providerName + " provider not installed");
        } else {
            Log.d("algoDebug", providerName + " is installed.");
        }


        createNewAccount();

    }


    //Method that creates a new account without using an existing mnemonic String
    //기존 니모닉 문자열을 사용하지 않고 새 계정을 생성하는 메소드
    public void createNewAccount() {
        preferences = getSharedPreferences("novarand",MODE_PRIVATE);
        user_id = preferences.getString("user_id", ""); // 로그인한 user_id값
        ApiInterface createAddrToBlockchain_api = ApiClient.getApiClient(this).create(ApiInterface.class);
        Call<String> call = createAddrToBlockchain_api.createAddrToBlockchain(user_id);
        call.enqueue(new Callback<String>()
        {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response)
            {
                if (response.isSuccessful() && response.body() != null)
                {
                    Log.e("성공 : 니모닉  - ", response.body().toString());
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t)
            {
                Log.e("에러", t.getMessage());
            }
        });







        ///////////////////////////////////
        Account myAccount1 = null;

        try {
            myAccount1 = new Account();
            Log.d("algoDebugNew", " algod account address: " + myAccount1.getAddress());
            Log.d("algoDebugNew", " algod account MNEMONIC: " + myAccount1.toMnemonic());
            // 계정의 지갑 생성 여부 => 기기 변경 시에도, 니모닉 25자를 작성해줘야 된다. (복구, 추가 모드는 추가 제공 예정)
            SharedPreferences.Editor mEditor;
            SharedPreferences mSharedPreferences = getSharedPreferences("Account", MODE_PRIVATE);
            mEditor = mSharedPreferences.edit();
            mEditor.putString("Mnemonic", "" + myAccount1.toMnemonic());
            mEditor.putString("Address", "" + myAccount1.getAddress());
            mEditor.apply();

            tv_mnemonic.setText("[내 주소]\n"+myAccount1.getAddress()+"\n\n[니모닉 25단어]\n"+myAccount1.toMnemonic());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            Log.d("algoDebug", " Eror while creating new account " + e);
        }


    }


    /** */
    /**
     * ~~~~~~~~~~~~~~~~~~~~~ 아래는 나중에 사용할 수 도 있는 코드들 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    //Method that restores an account from  an existing Mnemonic String
    //기존 Mnemonic String에서 계정을 복원하는 메소드
    public static Account createAccountWithMnemonic(String mnemonic) {
        Account myAccount1 = null;
        try {
            myAccount1 = new Account(mnemonic);
            Log.d("algoDebug", " algod account address: " + myAccount1.getAddress());
            Log.d("algoDebug", " algod account MNEMONIC: " + myAccount1.toMnemonic());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            Log.d("algoDebug", " Eror while creating new account " + e);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return myAccount1;
    }

    //테스트
}