package com.mainnet.bubbly.tabFragments;


import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.mainnet.bubbly.MM_Wallet;
import com.mainnet.bubbly.R;
import com.mainnet.bubbly.controller.TransactionHistory_Adapter;
import com.mainnet.bubbly.model.TransactionHistory_Item;
import com.mainnet.bubbly.retrofit.ApiClient;
import com.mainnet.bubbly.retrofit.ApiInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WalletFragment_Tab1_Activity extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;

    View v;
    LinearLayoutManager linearLayoutManager;
    private RecyclerView recyclerView;
    private ArrayList<TransactionHistory_Item> list = new ArrayList<>();
    private TransactionHistory_Adapter adapter;
    String uid;
    private String nextToken = ""; //api에 20개씩 나누어서 요청하는 토큰
    private String address;

    public WalletFragment_Tab1_Activity() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static WalletFragment_Tab1_Activity newInstance(String param1, String param2) {
        WalletFragment_Tab1_Activity fragment = new WalletFragment_Tab1_Activity();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        uid = ((MM_Wallet)getActivity()).getUid();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        // 레이아웃
        v = inflater.inflate(R.layout.fragment_profile_tab1, container, false);
        recyclerView = v.findViewById(R.id.tab_recyclerview);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new TransactionHistory_Adapter(getActivity().getApplicationContext(), this.list);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        clickListeners();

//        selectHistory();

        return v;
    }

    private void selectHistory(){
        String base_url = "https://testnet-algorand.api.purestake.io/idx2/";
        String token = "4LS0jVPkU61EBPpW2Ml3A2iaEcEfXK92aCDSzXXr";
        ApiInterface api = ApiClient.getApiClientWithUrlInput(base_url).create(ApiInterface.class);
        Call<String> call = api.transactionHistory(token,address,20,nextToken);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                System.out.println("거래기록"+response.message()+response.errorBody()+response.code());
                System.out.println(response.headers()+""+response.raw());
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body());
                        nextToken = jsonObject.getString("next-token");
                        JSONArray jsonArray = new JSONArray("transactions");
                        for(int i = 0; i<jsonArray.length(); i++){
                         JSONObject tx = jsonArray.getJSONObject(i);
                         String txId = tx.getString("id");
                         String sender = tx.getString("sender");
                         long roundTime = tx.getLong("round-time");
                         //epoch time to date
                         SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
                         Date date = new Date();
                         date.setTime(roundTime);
                         String roundTimeToDate = sdf.format(date);
                         int fee = tx.getInt("fee");
                         String txnTypeToString = "";
                         String txType = tx.getString("tx-type");
                         switch (txType){
                             case "pay":
                                 txnTypeToString = "Payment";
                                 break;
                             case "keyreg":
                                 txnTypeToString = "Key Registration";
                                 break;
                             case "acfg":
                                 txnTypeToString = "Asset Configuration";
                                 break;
                             case "axfer":
                                 txnTypeToString = "Asset Transfer";
                                 break;
                             case "afrz":
                                 txnTypeToString = "Asset Freeze";
                                 break;
                             case "appl":
                                 txnTypeToString = "Application Call";
                                 break;
                         }
                         int amount;
                         String receiver = null;
                         int assetId;
                         if(txType.equals("pay")){
                             JSONObject txn = tx.getJSONObject("payment-transaction");
                             amount = txn.getInt("amount");
                             receiver = txn.getString("receiver");
                             assetId = 0;
                         }else if(txType.equals("axfer")){
                             JSONObject axtx = tx.getJSONObject("asset-transfer-transaction");
                             amount = axtx.getInt("amount");
                             receiver = axtx.getString("receiver");
                             assetId = axtx.getInt("asset-id");
                         }else{
                             amount = 0;
                             receiver = "";
                             assetId = 0;
                         }
                         list.add(new TransactionHistory_Item(
                                 txnTypeToString,
                                 txId,
                                 sender,
                                 roundTimeToDate,
                                 ""+fee,
                                 ""+amount,
                                 receiver,
                                 ""+assetId));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("거래기록 가져오기 실패", t.getMessage());
            }
        });
    }


    private void clickListeners() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1)) {
                    selectHistory();
                }
            }
        });

    }



    @Override
    public void onResume() {
        super.onResume();
    }


}
