package com.mainnet.bubbly.kim_util_test;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mainnet.bubbly.Community_MainPage;
import com.mainnet.bubbly.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//                 final BottomSheetFragment bottomSheetFragment = new BottomSheetFragment(getApplicationContext());
// 활용하고 싶으면 onClick 에 오른쪽 코드=>                 bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());

public class BottomSheetFragment extends BottomSheetDialogFragment {
    Context context;

    String owner_id, user_id, contents;

    Kim_ApiInterface api;


    public BottomSheetFragment(Context context, String owner_id, String user_id, String contents) {
        this.context = context;
        this.owner_id = owner_id;
        this.user_id = user_id;
        this.contents = contents;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.bottom_sheet_dialog_layout, container, false);
        api = Kim_ApiClient.getApiClient(context).create(Kim_ApiInterface.class);

        LinearLayout main = view.findViewById(R.id.sheet_no);
        main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Call<String> call = api.report("0", owner_id, user_id, contents);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Toast.makeText(context,"신고 완료",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                        Log.e("게시글 생성 에러", t.getMessage());
                    }
                });
                dismiss();
            }
        });


        return view;
    }
}
