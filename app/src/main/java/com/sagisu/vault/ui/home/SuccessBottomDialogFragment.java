package com.sagisu.vault.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.sagisu.vault.R;
import com.sagisu.vault.models.WaitList;

public class SuccessBottomDialogFragment extends BottomSheetDialogFragment {

    private WaitList waitList;

    public static SuccessBottomDialogFragment newInstance(WaitList waitList) {
        return new SuccessBottomDialogFragment(waitList);
    }

    SuccessBottomDialogFragment(WaitList waitList) {
        this.waitList = waitList;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.success_bottom_sheet, container,
                false);
        view.findViewById(R.id.home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        // get the views and attach the listener

        return view;

    }
}
