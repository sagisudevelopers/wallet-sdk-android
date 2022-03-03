package com.sagisu.vault.ui.businessprofile;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.sagisu.vault.R;
import com.sagisu.vault.databinding.EnrollToBusinessProfileBinding;
import com.sagisu.vault.models.Business;
import com.sagisu.vault.models.MyBusinessVault;
import com.sagisu.vault.ui.transactions.TransactionListAdapter;
import com.sagisu.vault.ui.transactions.TransactionLoadStateAdapter;
import com.sagisu.vault.utils.ViewAnimation;

import java.util.List;

import autodispose2.AutoDispose;
import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider;

public class EnrollToBusinessProfile extends Fragment implements IBusinessClickListener, View.OnClickListener {

    private EnrollToBusinessProfileViewModel mViewModel;
    private BusinessProfileViewModel businessProfileViewModel;
    private EnrollToBusinessProfileBinding binding;
    private AllBusinessListAdapter adapter;
    private IBusinessNavigator navigator;

    private boolean extendFab = true;

    public static EnrollToBusinessProfile newInstance() {
        return new EnrollToBusinessProfile();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof IBusinessNavigator) {
            navigator = (IBusinessNavigator) context;
            navigator.setActionBarTittle(getResources().getString(R.string.business_profile_enroll_tittle));
        } else {
            try {
                throw new Exception("Please implement IBusinessNavigator");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.enroll_to_business_profile_fragment, container, false);
        binding = DataBindingUtil.bind(rootView);

        //Listen for text change on search view
        // Add Text Change Listener to EditText
        binding.businessNameSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (adapter == null) return;
                mViewModel.getAllBusiness(s.toString());
                initRecyclerView();//TODO : Replace with better approach
                //adapter.notifyDataSetChanged();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        /*binding.fabMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.fabMenu.setExpanded(extendFab);
                extendFab = !extendFab;
            }
        });*/

        binding.addBusiness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Route to adding new business fragment
                navigator.loadFragment(new AddBusinessFragment());
            }
        });

        binding.addDao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Route to adding new business fragment
                AddDaoFragment sheet =
                        new AddDaoFragment();
                sheet.show(getActivity().getSupportFragmentManager(),
                        "add_dao");
            }
        });


        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(EnrollToBusinessProfileViewModel.class);
        mViewModel.getAllBusiness("");
        initRecyclerView();

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
                if (adapter != null) {
                    adapter.setMyBusinessVaultList(myBusinessVaults);
                }
            }
        });
    }

    private void initRecyclerView() {
        binding.lstAllBusiness.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new AllBusinessListAdapter(getActivity(), this);
        adapter.withLoadStateFooter(
                new TransactionLoadStateAdapter(this));
        binding.lstAllBusiness.setAdapter(adapter);

        mViewModel.getFlowable()
                // Using AutoDispose to handle subscription lifecycle.
                // See: https://github.com/uber/AutoDispose
                .to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(pagingData -> adapter.submitData(getLifecycle(), pagingData));

        mViewModel.getJoinBusinessSuccess().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
                getActivity().onBackPressed();
            }
        });
    }

    private void joinConfirmationPopup(Business business) {
        new MaterialAlertDialogBuilder(getActivity())
                .setTitle("Join Business")
                .setMessage("Are you sure you want to join " + business.getName() + " ?")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setPositiveButton("Join", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mViewModel.joinBusiness(business.get_id());
                    }
                }).show();

    }

    @Override
    public void itemClick(View view, Business business) {
        if (view.getId() == R.id.join_business) joinConfirmationPopup(business);

    }

    @Override
    public void onClick(View view) {

    }
}