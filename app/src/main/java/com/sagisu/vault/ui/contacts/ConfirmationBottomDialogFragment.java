package com.sagisu.vault.ui.contacts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.sagisu.vault.R;
import com.sagisu.vault.databinding.ConfirmBottomSheetBinding;

public class ConfirmationBottomDialogFragment extends BottomSheetDialogFragment {

    private ConfirmBottomSheetBinding binding;
    private IConfirmDialogListener listener;
    private String amount;

    public interface IConfirmDialogListener{
        void onConfirm();
    }
    public static ConfirmationBottomDialogFragment newInstance(IConfirmDialogListener listener,String amount) {
        return new ConfirmationBottomDialogFragment(listener,amount);
    }

    ConfirmationBottomDialogFragment(IConfirmDialogListener listener,String amount) {
        this.listener = listener;
        this.amount = amount;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.confirm_bottom_sheet, container,
                false);
        binding = DataBindingUtil.bind(view);
        binding.setAmount(amount);
        binding.confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onConfirm();
                dismiss();
            }
        });
        // get the views and attach the listener

        return binding.getRoot();

    }
}
