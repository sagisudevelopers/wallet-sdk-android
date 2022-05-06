package com.sagisu.vaultLibrary.ui.kyc;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.sagisu.vaultLibrary.R;
import com.sagisu.vaultLibrary.databinding.DvSsnInfoBinding;


public class SSNInfoFragment extends Fragment {

    private DocumentVerificationViewModel mViewModel;
    private DvSsnInfoBinding binding;

    public static SSNInfoFragment newInstance() {
        return new SSNInfoFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dv_ssn_fragment, container, false);
        binding = DataBindingUtil.bind(view);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(getActivity()).get(DocumentVerificationViewModel.class);
        binding.setViewModel(mViewModel);
        binding.setLifecycleOwner(this);

        mViewModel.getFormError().observe(getViewLifecycleOwner(), new Observer<KycFormError>() {
            @Override
            public void onChanged(KycFormError kycFirstname) {
                binding.kycSsn.setError(kycFirstname.getSsnError() == null ? null : getString(kycFirstname.getSsnError()));
                binding.kycSsn.setErrorEnabled(kycFirstname.getSsnError() != null);
            }
        });
    }

}