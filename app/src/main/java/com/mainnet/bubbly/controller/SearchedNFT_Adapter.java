package com.mainnet.bubbly.controller;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mainnet.bubbly.R;
import com.mainnet.bubbly.SS_Profile;
import com.mainnet.bubbly.config.Config;
import com.mainnet.bubbly.kim_util_test.Kim_DateUtil;
import com.mainnet.bubbly.model.NFTSearched_Item;
import com.mainnet.bubbly.model.NFTSell_Item;
import com.mainnet.bubbly.model.UserInfo;
import com.mainnet.bubbly.retrofit.ApiClient;
import com.mainnet.bubbly.retrofit.ApiInterface;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchedNFT_Adapter extends RecyclerView.Adapter<SearchedNFT_Adapter.ViewHolder> {

    private Context context;
    private ArrayList<NFTSearched_Item> lists;
    private Activity activity;
    private ViewGroup v;

    public SearchedNFT_Adapter(Context context, ArrayList<NFTSearched_Item> lists, Activity activity, ViewGroup v) {
        this.context = context;
        this.lists = lists;
        this.activity = activity;
        this.v = v;
    }

    @NonNull
    @Override
    public SearchedNFT_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_nft_sr_com, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.bt_buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity).setTitle("NFT ??????");
                builder.setMessage("?????? ???????????? ????????? ?????? ???????????? ??? 0.001 nova??? ???????????????. ?????????????????????????");
                builder.setCancelable(false);
                builder.setPositiveButton("???", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ApiInterface api = ApiClient.getApiClient(context).create(ApiInterface.class);
                        System.out.println(UserInfo.mnemonic+"nftid"+lists.get(position).getNft_id()+UserInfo.user_id);
                        Call<String> call = api.nftBuy(lists.get(position).getNovaland_account_addr(),UserInfo.mnemonic,lists.get(position).getNft_id(),lists.get(position).getApp_id(),lists.get(position).getSell_price(),UserInfo.user_id);
                        call.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    new SnackAndToast().createToast(context,"NFT ????????? ?????????????????????.");

                                }
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                Log.e("nft ?????? ??????", t.getMessage());
                                new SnackAndToast().createToast(context,"NFT ????????? ?????????????????????.");
                            }
                        });

                        //?????????????????? nft ?????? ??????????????? ?????? ????????? ?????? response??? ???????????? ??? timeout?????? ????????? ??????.
                        //????????? response ????????? ???????????? ???????????? ?????????.
                        Toast.makeText(context, "NFT?????? ?????? ??????. ???????????? ???????????? 3~5?????? ????????? ??? ????????????.",Toast.LENGTH_SHORT).show();

                    }
                });
                builder.setNegativeButton("?????????", new DialogInterface.OnClickListener() {
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

        holder.tv_user_nick.setText(lists.get(position).getUserName());
        holder.feed_basic_userID.setText(lists.get(position).getLoginId());
        String user_img = lists.get(position).getProfileImageURL();
        System.out.println(user_img);
        if(user_img!=null&&!user_img.equals("")){
            Glide.with(context)
                    .load(Config.cloudfront_addr+user_img)
                    .into(holder.iv_user_image);

        }else{
            Glide.with(context)
                    .load(R.drawable.blank_profile)
                    .into(holder.iv_user_image);
        }
        holder.iv_user_image.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(context, SS_Profile.class);
                mIntent.putExtra("user_id",lists.get(position).getHolder_id());
                context.startActivity(mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });


        if(lists.get(position).getIsSell().equals("y")){
            holder.tv_forSale.setVisibility(View.VISIBLE);
            holder.ll_sell.setVisibility(View.VISIBLE);
        }else{
            holder.tv_forSale.setVisibility(View.GONE);
            holder.ll_sell.setVisibility(View.GONE);
        }

        String a = null;
        try {
            Log.d("???????????????", "???????????????:" + lists.get(position).getCreation_time());
            a = Kim_DateUtil.beforeTime(getDate(lists.get(position).getCreation_time()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // SNS ?????? ??????
        holder.tv_time.setText(a);
        String ipfsUrl = lists.get(position).getFile_save_url();
        System.out.println(ipfsUrl);
        if(ipfsUrl!=null&&!ipfsUrl.equals("")){
            Glide.with(context)
                    .load(ipfsUrl)
                    .into(holder.iv_image);

        }else{
            Glide.with(context)
                    .load(R.drawable.no_img_box)
                    .into(holder.iv_user_image);
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
        LinearLayout ll_background, ll_sell;
        TextView tv_user_nick, feed_basic_userID, tv_time, tv_forSale;
        ImageView iv_user_image, iv_image;
        Button bt_buy;
        TextView tv_price;

        public ViewHolder(@NonNull View view) {
            super(view);
            ll_background = view.findViewById(R.id.ll_nftSrCom_background);
            ll_sell = view.findViewById(R.id.ll_sell);
            tv_user_nick = view.findViewById(R.id.tv_user_nick_nftSrCom);
            feed_basic_userID = view.findViewById(R.id.feed_basic_userID_nftSrCom);
            iv_user_image = view.findViewById(R.id.iv_user_image_nftSrCom);
            tv_time = view.findViewById(R.id.tv_time_nftSrCom);
            iv_image = view.findViewById(R.id.iv_nft_nftSrCom);
            bt_buy  = view.findViewById(R.id.bt_buy_nftSrCom);
            tv_price = view.findViewById(R.id.tv_price_nftSrCom);
            tv_forSale = view.findViewById(R.id.item_nftSrCom_sale_yn);
        }
    }

    public static Date getDate(String from) throws ParseException {
        // "yyyy-MM-dd HH:mm:ss"
        Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(from);
        return date;
    }

}
