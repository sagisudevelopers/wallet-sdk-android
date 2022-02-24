package com.sagisu.vault.ui.businessprofile;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.google.android.material.radiobutton.MaterialRadioButton;
import com.sagisu.vault.R;
import com.sagisu.vault.databinding.BusinessProfileSettingsBinding;
import com.sagisu.vault.models.Business;
import com.sagisu.vault.models.MyBusinessVault;
import com.sagisu.vault.ui.home.CoinListAdapter;
import com.sagisu.vault.utils.SharedPref;

import java.util.List;

public class BusinessProfileSettings extends Fragment implements IBusinessClickListener {

    private BusinessProfileViewModel mViewModel;
    private BusinessProfileSettingsBinding binding;
    private BusinessSelectionAdapter adapter;
    private IBusinessNavigator navigator;

    public static BusinessProfileSettings newInstance() {
        return new BusinessProfileSettings();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.business_profile_settings_fragment, container, false);
        binding = DataBindingUtil.bind(rootView);

        binding.enrollBusinessBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                routeToEnrollBusiness();
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof IBusinessNavigator) {
            navigator = (IBusinessNavigator) context;
            navigator.setActionBarTittle(getResources().getString(R.string.business_profile_settings_tittle));
        } else try {
            throw new Exception("Please implement IBusinessNavigator");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(getActivity()).get(BusinessProfileViewModel.class);
        binding.setViewModel(mViewModel);
        binding.setLifecycleOwner(this);

        mViewModel.getBusinessList().observe(getViewLifecycleOwner(), new Observer<List<MyBusinessVault>>() {
            @Override
            public void onChanged(List<MyBusinessVault> businessList) {
                initRecyclerView(businessList);
            }
        });

        mViewModel.getSwitchToBusinessProfile().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (!aBoolean) {
                    new SharedPref().setBusinessVaultSelected(null);
                }
            }
        });


    }

    public void initRecyclerView(List<MyBusinessVault> businessList) {
        binding.recyclerView2.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new BusinessSelectionAdapter(getActivity(), businessList, this);
        mViewModel.getSwitchToBusinessProfile().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                adapter.setBusinessProfile(aBoolean);
                adapter.notifyDataSetChanged();
            }
        });
        binding.recyclerView2.setAdapter(adapter);
    }

    private void routeToEnrollBusiness() {
        navigator.loadFragment(new EnrollToBusinessProfile());
    }

    @Override
    public void itemClick(View view, Business transaction) {
        if (view.getId() == R.id.business_rb) {
            new SharedPref().setBusinessVaultSelected(transaction);
            getActivity().finish();
        }
    }
}