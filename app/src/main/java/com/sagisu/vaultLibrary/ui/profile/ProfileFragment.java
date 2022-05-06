package com.sagisu.vaultLibrary.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.sagisu.vaultLibrary.R;
import com.sagisu.vaultLibrary.databinding.ProfileFragmentBinding;
import com.sagisu.vaultLibrary.ui.businessprofile.BusinessProfileActivity;
import com.sagisu.vaultLibrary.ui.kyc.VerifyIdIntroActivity;
import com.sagisu.vaultLibrary.ui.login.VaultLoginActivity;
import com.sagisu.vaultLibrary.utils.SharedPref;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    private ProfileViewModel mViewModel;
    private ProfileFragmentBinding binding;

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.profile_fragment, container, false);
        binding = DataBindingUtil.bind(rootView);

        binding.userVerificationLayout.setOnClickListener(this);
        binding.constraintLayoutBusinessProfile.setOnClickListener(this);
        binding.logoutLayout.setOnClickListener(this);

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        binding.setModel(new SharedPref().getUser());
        binding.setLifecycleOwner(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.logout_layout) {
            logout();
        } else if (view.getId() == R.id.user_verification_layout) {
            userVerification();
        } else if (view.getId() == R.id.constraintLayout_business_profile) {
            switchToBusinessProfile();
        }

    }

    private void userVerification() {
        Intent intent = new Intent(getActivity(), VerifyIdIntroActivity.class);
        startActivity(intent);
    }

    private void switchToBusinessProfile() {
        Intent intent = new Intent(getActivity(), BusinessProfileActivity.class);
        startActivity(intent);
    }

    private void logout() {
        new SharedPref().clearSharedPref();
        Intent intent = new Intent(getActivity(), VaultLoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
}