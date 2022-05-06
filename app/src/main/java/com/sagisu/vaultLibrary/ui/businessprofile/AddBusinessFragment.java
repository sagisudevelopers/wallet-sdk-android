package com.sagisu.vaultLibrary.ui.businessprofile;

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

import com.google.android.material.chip.Chip;
import com.sagisu.vaultLibrary.R;
import com.sagisu.vaultLibrary.databinding.AddBusinessBinding;
import com.sagisu.vaultLibrary.models.Business;
import com.sagisu.vaultLibrary.models.MyBusinessVault;
import com.sagisu.vaultLibrary.utils.BusinessTypeDescriptor;

import java.util.List;

public class AddBusinessFragment extends Fragment implements DirectorDetailsSheet.IDirectorSheetNavigator {

    private AddBusinessViewModel mViewModel;
    private BusinessProfileViewModel businessProfileViewModel;
    private AddBusinessBinding binding;
    private IBusinessNavigator navigator;

    public static AddBusinessFragment newInstance() {
        return new AddBusinessFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof IBusinessNavigator) {
            navigator = (IBusinessNavigator) context;
        } else {
            try {
                throw new Exception("Please implement IBusinessNavigator");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        navigator.setActionBarTittle(getResources().getString(R.string.business_profile_add_tittle));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_business_fragment, container, false);
        binding = DataBindingUtil.bind(view);

        binding.addDirector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Load sheet for taking director details
                showAddDirectorSheet();
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(AddBusinessViewModel.class);
        mViewModel.init(BusinessTypeDescriptor.BUSINESS);
        binding.setViewModel(mViewModel);
        binding.setLifecycleOwner(this);

        mViewModel.getDirectorsList().observe(getViewLifecycleOwner(), new Observer<List<Business.Director>>() {
            @Override
            public void onChanged(List<Business.Director> directors) {

            }
        });

        mViewModel.getErrorBean().observe(getViewLifecycleOwner(), new Observer<BusinessErrorBean>() {
            @Override
            public void onChanged(BusinessErrorBean businessErrorBean) {
                binding.addBusinessName.setError(businessErrorBean.getNameError() == null ? null : getString(businessErrorBean.getNameError()));
                binding.addBusinessName.setErrorEnabled(businessErrorBean.getNameError() != null);

                binding.businessEinNumber.setError(businessErrorBean.getEinError() == null ? null : getString(businessErrorBean.getEinError()));
                binding.businessEinNumber.setErrorEnabled(businessErrorBean.getEinError() != null);

                binding.corporationType.setError(businessErrorBean.getCorporationTypeError() == null ? null : getString(businessErrorBean.getCorporationTypeError()));
                binding.corporationType.setErrorEnabled(businessErrorBean.getCorporationTypeError() != null);

                binding.department.setError(businessErrorBean.getDepartmentError() == null ? null : getString(businessErrorBean.getDepartmentError()));
                binding.department.setErrorEnabled(businessErrorBean.getDepartmentError() != null);
            }
        });

        mViewModel.getPostBusinessSuccess().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String msg) {
                //GO to the previous fragment
                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                getActivity().onBackPressed();
            }
        });

        mViewModel.getLoadingObservable().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                navigator.loading(s);
            }
        });


        businessProfileViewModel = new ViewModelProvider(getActivity()).get(BusinessProfileViewModel.class);
        businessProfileViewModel.getBusinessList().observe(getViewLifecycleOwner(), new Observer<List<MyBusinessVault>>() {
            @Override
            public void onChanged(List<MyBusinessVault> myBusinessVaults) {
                binding.setEnableDefault(!myBusinessVaults.isEmpty());
            }
        });

    }

    private void showAddDirectorSheet() {
        DirectorDetailsSheet sheet =
                new DirectorDetailsSheet(this);
        sheet.show(getActivity().getSupportFragmentManager(),
                "add_director");
    }

    private void addDirectorView(Business.Director director) {
        Chip chip = new Chip(getActivity());
        chip.setText(director.getName());
        chip.setChipBackgroundColorResource(R.color.blue_200);
        chip.setCloseIconVisible(true);
        //chip.setCloseIconTint(getResources().getColorStateList(R.color.text_500));
        //chip.setTextColor(getResources().getColor(R.color.text_500));
        //chip.setTextAppearance(R.style.ChipTextAppearance);

        chip.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewModel.removeDirectors(director);
                binding.directorGrp.removeView(chip);
            }
        });
        binding.directorGrp.addView(chip);
    }

    @Override
    public void onAddClick(Business.Director director) {
        mViewModel.addDirectors(director);
        addDirectorView(director);
    }
}