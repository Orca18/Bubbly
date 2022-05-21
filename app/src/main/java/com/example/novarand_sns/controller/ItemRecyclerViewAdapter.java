package com.example.novarand_sns.controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.novarand_sns.R;
import com.example.novarand_sns.model.Item;

import java.util.ArrayList;

public class ItemRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    int resource;
    ArrayList<Item> list;

    public ItemRecyclerViewAdapter(Context context, int resource, ArrayList<Item> list) {
        this.context = context;
        this.resource = resource;
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(resource, parent, false);
        ItemViewHolder holder = new ItemViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        ((ItemViewHolder)holder).imageImageView.setImageResource(list.get(position).getImage());
        ((ItemViewHolder)holder).titleTextView.setText(list.get(position).getTitle());
        ((ItemViewHolder)holder).dateTextView.setText(list.get(position).getDate());

        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = ((ItemViewHolder)holder).titleTextView.getText().toString();
                Toast.makeText(v.getContext(), title, Toast.LENGTH_SHORT).show();
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                remove(holder.getAdapterPosition());
                return true;
            }
        });
    }

    public void remove(int position) {
        try {
            list.remove(position);
            notifyItemRemoved(position);
        } catch (IndexOutOfBoundsException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return (null != list ? list.size() : 0);
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView imageImageView;
        TextView titleTextView;
        TextView dateTextView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            this.imageImageView = itemView.findViewById(R.id.imageImageView);
            this.titleTextView = itemView.findViewById(R.id.titleTextView);
            this.dateTextView = itemView.findViewById(R.id.dateTextView);
        }
    }
}