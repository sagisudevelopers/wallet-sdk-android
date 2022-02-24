package com.sagisu.vault.ui.businessprofile;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.sagisu.vault.R;
import com.sagisu.vault.databinding.AddDaoFragmentBinding;
import com.sagisu.vault.utils.BusinessTypeDescriptor;
import com.sagisu.vault.utils.ProgressShimmer;

public class AddDaoFragment extends BottomSheetDialogFragment {

    private AddBusinessViewModel mViewModel;
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