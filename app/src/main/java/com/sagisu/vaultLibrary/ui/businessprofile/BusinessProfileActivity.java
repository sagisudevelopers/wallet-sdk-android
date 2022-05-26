package com.sagisu.vaultLibrary.ui.businessprofile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import com.sagisu.vaultLibrary.R;
import com.sagisu.vaultLibrary.models.MyBusinessVault;
import com.sagisu.vaultLibrary.utils.ProgressShimmer;

import java.util.List;

public class BusinessProfileActivity extends AppCompatActivity implements IBusinessNavigator {

    private BusinessProfileViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_profile);
        mViewModel = new ViewModelProvider(this).get(BusinessProfileViewModel.class);
        mViewModel.init();
        mViewModel.getBusinessList().observe(this, new Observer<List<MyBusinessVault>>() {
            @Override
            public void onChanged(List<MyBusinessVault> myBusinessVaults) {
                if (myBusinessVaults.isEmpty())
                    getSupportFragmentManager().beginTransaction().replace(R.id.business_frame, new EnrollToBusinessProfile()).commit();
                else
                    getSupportFragmentManager().beginTransaction().replace(R.id.business_frame, new BusinessProfileSettings()).commit();

            }
        });
    }

    public void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.business_frame, fragment).addToBackStack(null).commit();
    }

    @Override
    public void setActionBarTittle(String tittle) {
        getSupportActionBar().setTitle(tittle);
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void loading(String text) {
        if (text == null) loading(null, false);
        else loading(text, true);
    }

    public void loading(String text, boolean loading) {
        ProgressShimmer.shimmerLoading(findViewById(R.id.loading_business), findViewById(R.id.business_frame), text, loading);
    }
}