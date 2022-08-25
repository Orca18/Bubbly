package com.example.bubbly.controller;

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
import android.widget.ImageButton;
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
import com.example.bubbly.retrofit.FileUtils;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NFT_Adapter extends RecyclerView.Adapter<NFT_Adapter.ViewHolder> {

    private Context context;
    private ArrayList<NFT_Item> lists;
    private Activity activity;
    private Custom_Toast toast;
    private ViewGroup v;

    public NFT_Adapter(Context context, ArrayList<NFT_Item> lists, Activity activity, ViewGroup v) {
        this.context = context;
        this.lists = lists;
        this.activity = activity;
        this.v = v;
    }

    @NonNull
    @Override
    public NFT_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_nft, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String ipfsUrl = lists.get(position).getFile_save_url();
        System.out.println(ipfsUrl);
        if(ipfsUrl!=null&&!ipfsUrl.equals("")){
            Glide.with(context)
                    .load(ipfsUrl)
                    .into(holder.iv_image);

        }else{
            //아무런 조치도하지 않는다. xml정의 따름.
        }

        //이미 판매중인 nft목록 가져오기 (만약 판매중이라면 Cancel로 버튼 표시 변경 위함)
        ApiInterface api = ApiClient.getApiClient().create(ApiInterface.class);
//        String seller_id = "35";
        Call<List<NFTSell_Item>> call = api.selectSelledNftListUsingSellerId(UserInfo.user_id);
        call.enqueue(new Callback<List<NFTSell_Item>>() {
            @Override
            public void onResponse(Call<List<NFTSell_Item>> call, Response<List<NFTSell_Item>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<NFTSell_Item> responseResult = response.body();
                    for(int i=0; i<responseResult.size(); i++){
                        String element = responseResult.get(i).getNft_id();
                        System.out.println("nft 판매 목록"+element);
                        if (element.equals(lists.get(position).getNft_id())) {
                            lists.get(position).setAlreadySell(true);
                            lists.get(position).setApp_id(responseResult.get(i).getApp_id());
                            lists.get(position).setSell_price(responseResult.get(i).getSell_price());
                            lists.get(position).setSeller_id(responseResult.get(i).getSeller_id());
                            break;
                        } else {
                            lists.get(position).setAlreadySell(false);
                        }
                    }
                    if(lists.get(position).isAlreadySell()){
                        holder.bt_sell.setVisibility(View.GONE);
                        holder.bt_stop.setVisibility(View.VISIBLE);
                    }else{
                        holder.bt_sell.setVisibility(View.VISIBLE);
                        holder.bt_stop.setVisibility(View.GONE);
                    }
                }
            }
            @Override
            public void onFailure(Call<List<NFTSell_Item>> call, Throwable t) {
                Log.e("nft 판매 목록 가져오기 실패", t.getMessage());
            }
        });

        holder.bt_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity).setTitle("NFT 판매 취소");
                builder.setMessage("정말로 취소하시겠습니까?");
                builder.setCancelable(false);

                builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ApiInterface api = ApiClient.getApiClient().create(ApiInterface.class);
                        System.out.println(UserInfo.mnemonic+"nftid"+lists.get(position).getNft_id()+lists.get(position).getSell_price()+UserInfo.user_id);
                        Call<String> call = api.nftStopSell(UserInfo.mnemonic,lists.get(position).getNft_id(),lists.get(position).getApp_id(),lists.get(position).getSell_price());
                        call.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    new Custom_Toast().createToast(context,v,"NFT 판매가 취소되었습니다.");
                                }
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                Log.e("nft 판매 취소 실패", t.getMessage());
                                new Custom_Toast().createToast(context,v,"NFT 판매 취소에 실패하였습니다.");
                            }
                        });

                        //블록체인에서 nft 관련 트랜잭션이 느린 관계로 모든 response를 수신하기 전 timeout되는 문제가 있음.
                        //따라서 response 수신과 무관하게 토스트를 띄운다.
                        Toast.makeText(context, "NFT판매 취소 신청 완료. 몇초 뒤 NFT 목록을 확인하세요.",Toast.LENGTH_SHORT).show();

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



        holder.bt_sell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity).setTitle("NFT 판매");
                EditText input = new EditText(context);
                input.setPaddingRelative(100,100,100,100);
                input.setBackground(null);
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                input.setSingleLine();
                input.setHint("몇 버블에 판매하시겠습니까?");
                builder.setView(input);
                builder.setCancelable(false);

                builder.setPositiveButton("판매", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String amount = input.getText().toString();
                        ApiInterface api = ApiClient.getApiClient().create(ApiInterface.class);
                        System.out.println(UserInfo.mnemonic+"nftid"+lists.get(position).getNft_id()+amount+UserInfo.user_id);
                        Call<String> call = api.nftSell(UserInfo.mnemonic,lists.get(position).getNft_id(),amount,UserInfo.user_id,"");
                        call.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    new Custom_Toast().createToast(context,v,"NFT 판매가 등록되었습니다.");
                                }
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                Log.e("nft 판매 실패", t.getMessage());
                                new Custom_Toast().createToast(context,v,"NFT 판매 등록에 실패했습니다.");
                            }
                        });

                        //블록체인에서 nft 관련 트랜잭션이 느린 관계로 모든 response를 수신하기 전 timeout되는 문제가 있음.
                        //따라서 response 수신과 무관하게 토스트를 띄운다.
                        Toast.makeText(context, "NFT판매 신청 완료. 몇초 뒤 NFT 목록을 확인하세요.",Toast.LENGTH_SHORT).show();

                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
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
        ImageView iv_image;
        Button bt_sell, bt_stop;

        public ViewHolder(@NonNull View view) {
            super(view);
            iv_image = view.findViewById(R.id.iv_nft_itemNFT);
            bt_sell  = view.findViewById(R.id.bt_sell_itemNFT);
            bt_stop = view.findViewById(R.id.bt_stopSell_itemNFT);
        }
    }

}
