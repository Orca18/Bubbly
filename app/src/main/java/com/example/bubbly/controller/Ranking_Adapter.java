package com.example.bubbly.controller;
import android.content.Intent;
import android.widget.Toast;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.bubbly.R;
import com.example.bubbly.SS_SearchResult;
import com.example.bubbly.model.Ranking_Item;

import java.util.ArrayList;

public class Ranking_Adapter extends BaseAdapter {

    Context mContext = null;
    LayoutInflater mLayoutInflater = null;
    ArrayList<Ranking_Item> rankingList;
    Searched_Adapter_Callback callback;

    public Ranking_Adapter(Context context, ArrayList<Ranking_Item> data, Searched_Adapter_Callback callback) {
        mContext = context;
        rankingList = data;
        mLayoutInflater = LayoutInflater.from(mContext);
        this.callback = callback;
    }

    @Override
    public int getCount() {
        return rankingList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Ranking_Item getItem(int position) {
        return rankingList.get(position);
    }

    @Override
    public View getView(int position, View converView, ViewGroup parent) {
        View view = mLayoutInflater.inflate(R.layout.item_ranking_listview, null);

        LinearLayout linearLayout = view.findViewById(R.id.item_ranking_linear);
        TextView rank = view.findViewById(R.id.item_ranking_rank);
        TextView keyword = view.findViewById(R.id.item_ranking_keyword);
        TextView keywordcount = view.findViewById(R.id.item_ranking_keywordcount);

        String s_rank = rankingList.get(position).getRank() + "";
        String s_count = rankingList.get(position).getKeyword_count() + " 버블";

        rank.setText(s_rank);
        keyword.setText(rankingList.get(position).getKeyword());
        keywordcount.setText(s_count);

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SS_SearchResult.class);
                intent.putExtra("keyword", rankingList.get(position).getKeyword());
                mContext.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                callback.updateListRecentlySearched(rankingList.get(position).getKeyword());
            }
        });

        return view;
    }
}