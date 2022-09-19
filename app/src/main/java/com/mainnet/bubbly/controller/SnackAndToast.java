package com.mainnet.bubbly.controller;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.mainnet.bubbly.R;
import com.google.android.material.snackbar.Snackbar;

public class SnackAndToast {

    public void createToast(Context context, String msg){
//        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
//        View layout = inflater.inflate(R.layout.custom_toast, v, false);
//        TextView tv_msg = (TextView) layout.findViewById(R.id.tv_msg);
//        tv_msg.setText(msg);

        View layout = LayoutInflater.from(context).inflate(R.layout.custom_toast, null);
        TextView tv_msg = (TextView) layout.findViewById(R.id.tv_msg);
        tv_msg.setText(msg);
        Toast toast = new Toast(context);
        toast.setView(layout);
        toast.setGravity(Gravity.TOP, 0, 160);
        toast.setDuration(Toast.LENGTH_LONG);
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


    public void createSnack(Context context, View view, String msg){
        Snackbar snack = Snackbar.make(view.findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG);
        snack.setBackgroundTint(ContextCompat.getColor(context, R.color.black));
        snack.setActionTextColor(ContextCompat.getColor(context, R.color.white));
        View snackView = snack.getView();
        FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)snackView.getLayoutParams();
        params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        params.width = FrameLayout.LayoutParams.WRAP_CONTENT;
        snackView.setLayoutParams(params);
        snack.show();
    }

}
