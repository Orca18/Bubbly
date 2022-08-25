package com.example.bubbly.controller;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bubbly.R;
import com.example.bubbly.model.NFTSell_Item;
import com.example.bubbly.model.NFT_Item;
import com.example.bubbly.model.UserInfo;
import com.example.bubbly.retrofit.ApiClient;
import com.example.bubbly.retrofit.ApiInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NFTSell_Adapter extends RecyclerView.Adapter<NFTSell_Adapter.ViewHolder> {

    private Context context;
    private ArrayList<NFTSell_Item> lists;
    private Activity activity;
    private ViewGroup v;

    public NFTSell_Adapter(Context context, ArrayList<NFTSell_Item> lists, Activity activity, ViewGroup v) {
        this.context = context;
        this.lists = lists;
        this.activity = activity;
        this.v = v;
    }

    @NonNull
    @Override
    public NFTSell_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_nft_ss_profile, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.bt_sell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity).setTitle("NFT 구매");
                builder.setMessage("판매 금액만큼 버블과 거래 수수료로 약 0.001 nova가 차감됩니다. 구매하시겠습니까?");
                builder.setCancelable(false);
                builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ApiInterface api = ApiClient.getApiClient().create(ApiInterface.class);
                        System.out.println(UserInfo.mnemonic+"nftid"+lists.get(position).getNft_id()+UserInfo.user_id);
                        Call<String> call = api.nftBuy(lists.get(position).getNovaland_account_addr(),UserInfo.mnemonic,lists.get(position).getNft_id(),lists.get(position).getApp_id(),lists.get(position).getSell_price(),UserInfo.user_id);
                        call.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    new Custom_Toast().createToast(context,v,"NFT 구매가 완료되었습니다.");

                                }
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                Log.e("nft 구매 실패", t.getMessage());
                                new Custom_Toast().createToast(context,v,"NFT 구매에 실패하였습니다.");
                            }
                        });

                        //블록체인에서 nft 관련 트랜잭션이 느린 관계로 모든 response를 수신하기 전 timeout되는 문제가 있음.
                        //따라서 response 수신과 무관하게 토스트를 띄운다.
                        Toast.makeText(context, "NFT구매 신청 완료. 몇초 뒤 NFT 목록을 확인하세요.",Toast.LENGTH_SHORT).show();

                    }
                });
                builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
                alert.getButton(alert.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context, R.color.black));
                alert.getButton(alert.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.blue));
            }
        });

        String ipfsUrl = lists.get(position).getFile_save_url();
        System.out.println(ipfsUrl);
        if(ipfsUrl!=null&&!ipfsUrl.equals("")){
            Glide.with(context)
                    .load(ipfsUrl)
                    .into(holder.iv_image);

        }else{
            //아무런 조치도하지 않는다. xml정의 따름.
        }
        if(lists.get(position).getSell_price()!=null&&!lists.get(position).getSell_price().equals("")){
            holder.tv_price.setText(lists.get(position).getSell_price()+" Bubble");
        }else{
            holder.tv_price.setText("0 Bubble");
        }

    }


    @Override
    public int getItemCount() {
        return lists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_image;
        Button bt_sell;
        TextView tv_price;

        public ViewHolder(@NonNull View view) {
            super(view);
            iv_image = view.findViewById(R.id.iv_nft_itemNFT_ssProfile);
            bt_sell  = view.findViewById(R.id.bt_buy_itemNFT_ssProfile);
            tv_price = view.findViewById(R.id.tv_price_itemNFT_ssProfile);
        }
    }

}
