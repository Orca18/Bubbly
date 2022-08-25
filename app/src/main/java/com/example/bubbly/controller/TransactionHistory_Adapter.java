package com.example.bubbly.controller;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bubbly.R;
import com.example.bubbly.model.NFTSell_Item;
import com.example.bubbly.model.TransactionHistory_Item;
import com.example.bubbly.model.UserInfo;
import com.example.bubbly.retrofit.ApiClient;
import com.example.bubbly.retrofit.ApiInterface;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransactionHistory_Adapter extends RecyclerView.Adapter<TransactionHistory_Adapter.ViewHolder> {

    private Context context;
    private ArrayList<TransactionHistory_Item> lists;

    public TransactionHistory_Adapter(Context context, ArrayList<TransactionHistory_Item> lists) {
        this.context = context;
        this.lists = lists;
    }

    @NonNull
    @Override
    public TransactionHistory_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_transaction_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
//        String ipfsUrl = lists.get(position).getFile_save_url();
//        System.out.println(ipfsUrl);
//        if(ipfsUrl!=null&&!ipfsUrl.equals("")){
//            Glide.with(context)
//                    .load(ipfsUrl)
//                    .into(holder.iv_icon);
//
//        }else{
//            //아무런 조치도하지 않는다. xml정의 따름.
//        }
        holder.tv_type.setText(lists.get(position).getType());
        holder.tv_id.setText(lists.get(position).getId());
        holder.tv_assetId.setText(lists.get(position).getAssetId());
        holder.tv_sender.setText(lists.get(position).getSender());
        holder.tv_receiver.setText(lists.get(position).getReceiver());
        holder.tv_date.setText(lists.get(position).getTxRoundTimeToDate());
        holder.tv_fee.setText(lists.get(position).getFee());
        holder.tv_amount.setText(lists.get(position).getAmount());

    }


    @Override
    public int getItemCount() {
        return lists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_icon;
        TextView tv_id, tv_type, tv_assetId, tv_date, tv_sender, tv_receiver, tv_amount,tv_fee;

        public ViewHolder(@NonNull View view) {
            super(view);
//            iv_icon = view.findViewById(R.id.iv_icon_txHistory);
            tv_id = view.findViewById(R.id.tv_txId_txHistory);
            tv_type = view.findViewById(R.id.tv_txType_txHistory);
            tv_assetId = view.findViewById(R.id.tv_assetID_txHistory);
            tv_date = view.findViewById(R.id.tv_txDate_txHistory);
            tv_sender = view.findViewById(R.id.tv_txSender_txHistory);
            tv_receiver = view.findViewById(R.id.tv_txRec_txHistory);
            tv_amount = view.findViewById(R.id.tv_amount_txHistory);
            tv_fee = view.findViewById(R.id.tv_fee_txHistory);
        }
    }

}
