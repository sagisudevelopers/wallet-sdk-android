package com.sagisu.vault.ui.businessprofile;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.sagisu.vault.R;
import com.sagisu.vault.databinding.DirectorDetailsSheetBinding;
import com.sagisu.vault.models.Business;
import com.sagisu.vault.models.WaitList;
import com.sagisu.vault.network.VaultAPIError;
import com.sagisu.vault.network.VaultResult;
import com.sagisu.vault.repository.NetworkRepository;
import com.sagisu.vault.ui.home.SuccessBottomDialogFragment;
import com.sagisu.vault.utils.FeatureNameDescriptor;
import com.sagisu.vault.utils.ProgressShimmer;
import com.sagisu.vault.utils.Util;

public class DirectorDetailsSheet extends BottomSheetDialogFragment {
    DirectorDetailsSheetBinding binding;
    IDirectorSheetNavigator navigator;
    private Business.Director director;

    public interface IDirectorSheetNavigator {
        void onAddClick(Business.Director director);
    }

    public DirectorDetailsSheet(IDirectorSheetNavigator navigator) {
        this.navigator = navigator;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.director_details_sheet, container,
                false);
        director = new Business.Director();
        binding = DataBindingUtil.bind(view);
        binding.setModel(director);
        binding.addDirector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate(director)) {
                    navigator.onAddClick(director);
                    dismiss();
                }
            }
        });

        return binding.getRoot();

    }

    public boolean validate(Business.Director director) {
        boolean flag = true;
        if (director.getName() == null || director.getName().isEmpty()) {
            binding.directorName.setError(getString(R.string.required_field_error));
            binding.directorName.setErrorEnabled(true);
            flag = false;
        }

        if (director.getPhone() == null || director.getPhone().isEmpty()) {
            binding.directorPhone.setError(getString(R.string.required_field_error));
            binding.directorPhone.setErrorEnabled(true);
            flag = false;
        }

        return flag;
    }

    public void loading(String text, boolean loading) {
        ProgressShimmer.shimmerLoading(binding.waitlistLoading, binding.viewWaitlist, text, loading);
    }
}
