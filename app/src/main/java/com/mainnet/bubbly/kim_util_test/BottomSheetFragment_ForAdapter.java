package com.mainnet.bubbly.kim_util_test;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mainnet.bubbly.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

//                 final BottomSheetFragment bottomSheetFragment = new BottomSheetFragment(getApplicationContext());
// 활용하고 싶으면 onClick 에 오른쪽 코드=>                 bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());

public class BottomSheetFragment_ForAdapter extends BottomSheetDialogFragment {
    Context context;
    String post_id;

    public BottomSheetFragment_ForAdapter(Context context, String post_id) {
        this.context = context;
        this.post_id = post_id;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.bottom_sheet_dialog_layout, container, false);

        LinearLayout main = view.findViewById(R.id.sheet_no);
        main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "1", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });


        return view;
    }
}
