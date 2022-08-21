package com.example.bubbly.controller;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bubbly.R;
import com.example.bubbly.SS_PostDetail;
import com.example.bubbly.SS_SearchResult;
import com.example.bubbly.model.Ranking_Item;

import java.util.ArrayList;

public class RecentlySearched_Adapter extends BaseAdapter {

    Context mContext = null;
    LayoutInflater mLayoutInflater = null;
    ArrayList<String> keywordList;

    public RecentlySearched_Adapter(Context context, ArrayList<String> data) {
        mContext = context;
        keywordList = data;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return keywordList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public String getItem(int position) {
        return keywordList.get(position);
    }

    @Override
    public View getView(int position, View converView, ViewGroup parent) {
        View view = mLayoutInflater.inflate(R.layout.item_recently_searched_listview, null);

        LinearLayout linearLayout = view.findViewById(R.id.ll_item_recentlySearched);
        TextView keyword = view.findViewById(R.id.item_recentlySearched_keyword);

        String recentlySearched = keywordList.get(position);
        keyword.setText(recentlySearched);

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(mContext.getApplicationContext(),"TODO 해당 키워드를 검색한 결과 페이지 : "+ recentlySearched + "위",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(mContext, SS_SearchResult.class);
                intent.putExtra("keyword", keywordList.get(position));
                mContext.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });

        return view;
    }
}