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
import android.widget.LinearLayout;
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
        holder.tv_type.setText(lists.get(position).getType());
        holder.tv_id.setText(lists.get(position).getId());
        holder.tv_assetId.setText(lists.get(position).getAssetId());
        holder.tv_sender.setText(lists.get(position).getSender());
        holder.tv_receiver.setText(lists.get(position).getReceiver());
        holder.tv_date.setText(lists.get(position).getTxRoundTimeToDate());
        holder.tv_fee.setText(lists.get(position).getFee());
        holder.tv_amount.setText(lists.get(position).getAmount());
        //페이먼트에서 0인 경우는 정상적으로 0을 보낸 경우이고, 아닌 경우는 코드에서 임의로 0할당했으므로 안보이게 한다.
        if(lists.get(position).getAssetId().equals("0")){
            if(!lists.get(position).getType().equals("Payment")||!lists.get(position).getType().equals("Asset Transfer")){
                holder.ll_assetId.setVisibility(View.GONE);
            }
        }
        if(lists.get(position).getReceiver().equals("")){
            if(!lists.get(position).getType().equals("Payment")||!lists.get(position).getType().equals("Asset Transfer")){
                holder.ll_receiver.setVisibility(View.GONE);
            }
        }
        if(lists.get(position).getAmount().equals("0")){
            if(!lists.get(position).getType().equals("Payment")||!lists.get(position).getType().equals("Asset Transfer")){
                holder.ll_amount.setVisibility(View.GONE);
            }
        }



    }


    @Override
    public int getItemCount() {
        return lists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_icon;
        TextView tv_id, tv_type, tv_assetId, tv_date, tv_sender, tv_receiver, tv_amount,tv_fee;
        LinearLayout ll_assetId, ll_receiver, ll_amount;
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
            ll_assetId = view.findViewById(R.id.ll_assetId_txHistory);
            ll_receiver = view.findViewById(R.id.ll_receiver_txHistory);
            ll_amount = view.findViewById(R.id.ll_amount_txHistory);
        }
    }

}
