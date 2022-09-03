package com.example.bubbly.controller;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.content.Context.WINDOW_SERVICE;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.renderscript.ScriptGroup;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import com.example.bubbly.R;

public class Custom_Toast {

    public void createToast(Context context, ViewGroup v, String msg){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.custom_toast, v, false);
        TextView tv_msg = (TextView) layout.findViewById(R.id.tv_msg);
        tv_msg.setText(msg);

        Toast toast = new Toast(context);
        toast.setGravity(Gravity.TOP, 0, 160);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }


    public void createToastNewPost(Context context, ViewGroup v, String msg, NewPost_CustomToast_Callback callback){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.custom_toast, v, false);
        TextView tv_msg = (TextView) layout.findViewById(R.id.tv_msg);
        CardView cv_toast = (CardView) layout.findViewById(R.id.cv_customToast);
        tv_msg.setText(msg);
        tv_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.refreshLayout();
            }
        });
        Toast toast = new Toast(context);
        toast.setGravity(Gravity.TOP, 0, 160);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

}
