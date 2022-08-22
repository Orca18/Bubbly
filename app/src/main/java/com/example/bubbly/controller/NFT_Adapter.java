package com.example.bubbly.controller;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bubbly.ImageView_FullScreen;
import com.example.bubbly.Post_ApplyNFT_A;
import com.example.bubbly.R;
import com.example.bubbly.SS_PostDetail;
import com.example.bubbly.SS_Profile;
import com.example.bubbly.model.NFT_Item;
import com.example.bubbly.retrofit.ApiClient;
import com.example.bubbly.retrofit.ApiInterface;
import com.example.bubbly.retrofit.post_Response;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NFT_Adapter extends RecyclerView.Adapter<NFT_Adapter.PostViewHolder> {

    private Context context;
    private ArrayList<NFT_Item> lists; //이미지 url 리스트

    public NFT_Adapter(Context context, ArrayList<NFT_Item> lists) {
        this.context = context;
        this.lists = lists;
    }

    @NonNull
    @Override
    public NFT_Adapter.PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_nft, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, @SuppressLint("RecyclerView") int position) {

        String ipfsUrl = lists.get(position).getFile_save_url();

        Glide.with(context)
                .load(ipfsUrl)
                .into(holder.iv_image);

        holder.bt_sell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(context, ImageView_FullScreen.class);
//                intent.putExtra("img_url", "https://d2gf68dbj51k8e.cloudfront.net/" + post_response.getFile_save_names());
//                context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });

    }


    @Override
    public int getItemCount() {
        return lists.size();
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_image;
        ImageButton bt_sell;

        public PostViewHolder(@NonNull View view) {
            super(view);
            iv_image = view.findViewById(R.id.iv_nft_itemNFT);
            bt_sell  = view.findViewById(R.id.bt_sell_itemNFT);

        }
    }


}
