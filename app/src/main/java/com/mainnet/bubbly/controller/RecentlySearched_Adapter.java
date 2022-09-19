package com.mainnet.bubbly.controller;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mainnet.bubbly.R;
import com.mainnet.bubbly.SS_SearchResult;

import java.util.ArrayList;

public class RecentlySearched_Adapter extends BaseAdapter {

    Context mContext = null;
    LayoutInflater mLayoutInflater = null;
    ArrayList<String> keywordList;
    Searched_Adapter_Callback callback;

    public RecentlySearched_Adapter(Context context, ArrayList<String> data, Searched_Adapter_Callback callback) {
        mContext = context;
        keywordList = data;
        mLayoutInflater = LayoutInflater.from(mContext);
        this.callback = callback;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = mLayoutInflater.inflate(R.layout.item_recently_searched_listview, null);

        LinearLayout linearLayout = view.findViewById(R.id.ll_item_recentlySearched);
        TextView keyword = view.findViewById(R.id.item_recentlySearched_keyword);

        String recentlySearched = keywordList.get(position);
        keyword.setText(recentlySearched);
        System.out.println("리센트리서치 어댑터"+recentlySearched);

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SS_SearchResult.class);
                System.out.println("인텐트"+keywordList.get(position));
                intent.putExtra("keyword", keywordList.get(position));
                mContext.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                callback.updateListRecentlySearched(keywordList.get(position));
            }
        });

        return view;
    }
}