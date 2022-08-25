package com.example.bubbly.controller;

import android.app.Activity;
import android.content.Context;
import android.renderscript.ScriptGroup;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bubbly.R;

public class Custom_Toast {

    public void createToast(Context context, ViewGroup v, String msg){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.custom_toast, v, false);
        TextView tv_msg = (TextView) layout.findViewById(R.id.tv_msg);
        tv_msg.setText(msg);

        Toast toast = new Toast(context);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }


}
