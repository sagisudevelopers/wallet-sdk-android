package com.sagisu.vaultLibrary.ui.businessprofile;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.sagisu.vaultLibrary.R;
import com.sagisu.vaultLibrary.databinding.AddDaoFragmentBinding;
import com.sagisu.vaultLibrary.models.MyBusinessVault;
import com.sagisu.vaultLibrary.utils.BusinessTypeDescriptor;
import com.sagisu.vaultLibrary.utils.ProgressShimmer;

import java.util.List;

public class AddDaoFragment extends BottomSheetDialogFragment {

    private AddBusinessViewModel mViewModel;
    private BusinessProfileViewModel businessProfileViewModel;
    private AddDaoFragmentBinding binding;

    public static AddDaoFragment newInstance() {
        return new AddDaoFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_dao_fragment, container, false);
        binding = DataBindingUtil.bind(view);
        mViewModel = new ViewModelProvider(this).get(AddBusinessViewModel.class);
        mViewModel.init(BusinessTypeDescriptor.DAO);
        binding.setViewModel(mViewModel);
        binding.setLifecycleOwner(this);

        mViewModel.getPostBusinessSuccess().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String msg) {
                //GO to the previous fragment
                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });

        mViewModel.getErrorBean().observe(getViewLifecycleOwner(), new Observer<BusinessErrorBean>() {
            @Override
            public void onChanged(BusinessErrorBean businessErrorBean) {
                binding.daoName.setError(businessErrorBean.getNameError() == null ? null : getString(businessErrorBean.getNameError()));
                binding.daoName.setErrorEnabled(businessErrorBean.getNameError() != null);
            }
        });

        mViewModel.getLoadingObservable().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s == null) loading(null, false);
                else loading(s, true);
            }
        });


        businessProfileViewModel = new ViewModelProvider(getActivity()).get(BusinessProfileViewModel.class);
        businessProfileViewModel.getBusinessList().observe(getViewLifecycleOwner(), new Observer<List<MyBusinessVault>>() {
            @Override
            public void onChanged(List<MyBusinessVault> myBusinessVaults) {
                binding.setEnableDefault(!myBusinessVaults.isEmpty());
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    public void loading(String text, boolean loading) {
        ProgressShimmer.shimmerLoading(binding.daoLoading, binding.viewDao, text, loading);
    }


}