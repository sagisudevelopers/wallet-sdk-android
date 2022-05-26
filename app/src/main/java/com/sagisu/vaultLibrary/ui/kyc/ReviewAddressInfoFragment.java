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
import com.sagisu.vaultLibrary.databinding.DvAddressInfoBinding;


public class ReviewAddressInfoFragment extends Fragment {

    private DocumentVerificationViewModel mViewModel;
    private DvAddressInfoBinding binding;

    public static ReviewAddressInfoFragment newInstance() {
        return new ReviewAddressInfoFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dv_address_info_fragment, container, false);
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
                binding.kycCountry.setError(kycFirstname.getCountryError() == null ? null : getString(kycFirstname.getCountryError()));
                binding.kycCountry.setErrorEnabled(kycFirstname.getCountryError() != null);

                binding.kycStreet.setError(kycFirstname.getStreetError() == null ? null : getString(kycFirstname.getStreetError()));
                binding.kycStreet.setErrorEnabled(kycFirstname.getStreetError() != null);

                binding.kycAppNo.setError(kycFirstname.getAppNoError() == null ? null : getString(kycFirstname.getAppNoError()));
                binding.kycAppNo.setErrorEnabled(kycFirstname.getAppNoError() != null);

                binding.kycAddress.setError(kycFirstname.getAddressError() == null ? null : getString(kycFirstname.getAddressError()));
                binding.kycAddress.setErrorEnabled(kycFirstname.getAddressError() != null);

                binding.kycState.setError(kycFirstname.getStateError() == null ? null : getString(kycFirstname.getStateError()));
                binding.kycState.setErrorEnabled(kycFirstname.getStateError() != null);

                binding.kycPincode.setError(kycFirstname.getPinCodeError() == null ? null : getString(kycFirstname.getPinCodeError()));
                binding.kycPincode.setErrorEnabled(kycFirstname.getPinCodeError() != null);

            }
        });
    }

}