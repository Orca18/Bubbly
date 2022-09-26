package com.mainnet.bubbly.controller;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mainnet.bubbly.R;
import com.mainnet.bubbly.model.NFTSell_Item;
import com.mainnet.bubbly.model.NFT_Item;
import com.mainnet.bubbly.model.UserInfo;
import com.mainnet.bubbly.retrofit.ApiClient;
import com.mainnet.bubbly.retrofit.ApiInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NFT_ss_Adapter extends RecyclerView.Adapter<NFT_ss_Adapter.ViewHolder> {

    private Context context;
    private ArrayList<NFTSell_Item> lists;
    private Activity activity;
    private SnackAndToast toast;
    private ViewGroup v;

    public NFT_ss_Adapter(Context context, ArrayList<NFTSell_Item> lists, Activity activity, ViewGroup v) {
        this.context = context;
        this.lists = lists;
        this.activity = activity;
        this.v = v;
    }

    @NonNull
    @Override
    public NFT_ss_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_nft_ss_profile, parent, false);
        return new NFT_ss_Adapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        //이미 판매중인 nft목록 가져오기 (만약 판매중이라면 판매가 표시하기 위함)
        ApiInterface api = ApiClient.getApiClient(context).create(ApiInterface.class);
        Call<List<NFTSell_Item>> call = api.selectSelledNftListUsingSellerId(UserInfo.user_id);
        call.enqueue(new Callback<List<NFTSell_Item>>() {
            @Override
            public void onResponse(Call<List<NFTSell_Item>> call, Response<List<NFTSell_Item>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<NFTSell_Item> responseResult = response.body();
                    lists.get(position).setIsSell("n");
                    for(int i=0; i<responseResult.size(); i++){
                        String element = responseResult.get(i).getNft_id();
                        System.out.println("nft 판매 목록"+element);
                        if (element.equals(lists.get(position).getNft_id())) {
                            lists.get(position).setIsSell("y");
                            lists.get(position).setApp_id(responseResult.get(i).getApp_id());
                            lists.get(position).setSell_price(responseResult.get(i).getSell_price());
                            lists.get(position).setSeller_id(responseResult.get(i).getSeller_id());
                            break;
                        } else {
                            lists.get(position).setIsSell("n");
                        }
                    }
                    if(lists.get(position).getIsSell().equals("y")){
                        holder.tv_forSale.setVisibility(View.VISIBLE);
                        holder.ll_sell.setVisibility(View.VISIBLE);
                    }else{
                        holder.tv_forSale.setVisibility(View.GONE);
                        holder.ll_sell.setVisibility(View.GONE);
                    }
                }
            }
            @Override
            public void onFailure(Call<List<NFTSell_Item>> call, Throwable t) {
                Log.e("nft 판매 목록 가져오기 실패", t.getMessage());
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

        holder.bt_sell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity).setTitle("NFT 구매");
                builder.setMessage("판매 금액만큼 버블과 거래 수수료로 약 0.001 nova가 차감됩니다. 구매하시겠습니까?");
                builder.setCancelable(false);
                builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ApiInterface api = ApiClient.getApiClient(context).create(ApiInterface.class);
                        System.out.println(UserInfo.mnemonic+"nftid"+lists.get(position).getNft_id()+UserInfo.user_id);
                        Call<String> call = api.nftBuy(lists.get(position).getNovaland_account_addr(),UserInfo.mnemonic,lists.get(position).getNft_id(),lists.get(position).getApp_id(),lists.get(position).getSell_price(),UserInfo.user_id);
                        call.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    new SnackAndToast().createToast(context,"NFT 구매가 완료되었습니다.");

                                }
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                Log.e("nft 구매 실패", t.getMessage());
                                new SnackAndToast().createToast(context,"NFT 구매에 실패하였습니다.");
                            }
                        });

                        //블록체인에서 nft 관련 트랜잭션이 느린 관계로 모든 response를 수신하기 전 timeout되는 문제가 있음.
                        //따라서 response 수신과 무관하게 토스트를 띄운다.
                        Toast.makeText(context, "NFT구매 신청 완료. 블록체인 반영까지 3~5분이 소요될 수 있습니다.",Toast.LENGTH_SHORT).show();

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


    }


    @Override
    public int getItemCount() {
        return lists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout ll_background, ll_sell;
        ImageView iv_image;
        Button bt_sell;
        TextView tv_price, tv_forSale;

        public ViewHolder(@NonNull View view) {
            super(view);
            ll_background = view.findViewById(R.id.ll_item_nft_ss_profile_background);
            ll_sell = view.findViewById(R.id.ll_sell);
            iv_image = view.findViewById(R.id.iv_nft_itemNFT_ssProfile);
            bt_sell  = view.findViewById(R.id.bt_buy_itemNFT_ssProfile);
            tv_price = view.findViewById(R.id.tv_price_itemNFT_ssProfile);
            tv_forSale = view.findViewById(R.id.item_nft_ss_profile_sale_yn);
        }
    }

}
