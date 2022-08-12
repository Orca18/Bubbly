package com.example.bubbly.controller;
import android.widget.Toast;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.bubbly.R;
import com.example.bubbly.model.Ranking_Item;

import java.util.ArrayList;

public class Ranking_Adapter extends BaseAdapter {

    Context mContext = null;
    LayoutInflater mLayoutInflater = null;
    ArrayList<Ranking_Item> rankingList;

    public Ranking_Adapter(Context context, ArrayList<Ranking_Item> data) {
        mContext = context;
        rankingList = data;
        mLayoutInflater = LayoutInflater.from(mContext);
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
                Toast.makeText(mContext.getApplicationContext(),"TODO 해당 키워드를 검색한 결과 페이지 : "+ s_rank + "위",Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}