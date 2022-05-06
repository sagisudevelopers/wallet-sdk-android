package com.sagisu.vaultLibrary.ui.businessprofile;

import androidx.appcompat.app.AlertDialog;
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
import com.sagisu.vaultLibrary.R;
import com.sagisu.vaultLibrary.databinding.EnrollToBusinessProfileBinding;
import com.sagisu.vaultLibrary.models.Business;
import com.sagisu.vaultLibrary.models.MyBusinessVault;
import com.sagisu.vaultLibrary.ui.transactions.TransactionLoadStateAdapter;

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
        navigator.setActionBarTittle(getResources().getString(R.string.business_profile_enroll_tittle));
       /* mViewModel.getAllBusiness("");
        initRecyclerView();*/
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
        final CharSequence[] charSequence = new CharSequence[]{"Use this as default business"};
        final boolean[] checkedItems = new boolean[]{true};
        final boolean[] enabledItems = new boolean[]{!businessProfileViewModel.getBusinessList().getValue().isEmpty()};

        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(getActivity())
                .setTitle("Confirm to join " + business.getName())
                //.setMessage("Are you sure you want to join " + business.getName() + " ?")
                .setMultiChoiceItems(charSequence, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        mViewModel.setDefaultBusiness(isChecked);
                    }
                })
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
                });

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.getListView().setOnHierarchyChangeListener(
                new ViewGroup.OnHierarchyChangeListener() {
                    @Override
                    public void onChildViewAdded(View parent, View child) {
                        /*CharSequence text = ((AppCompatCheckedTextView)child).getText();
                        int itemIndex = Arrays.asList(checkedItems).indexOf(text);*/
                        child.setEnabled(enabledItems[0]);
                        child.setClickable(!enabledItems[0]);
                    }

                    @Override
                    public void onChildViewRemoved(View view, View view1) {
                    }
                });

        alertDialog.show();

    }

    @Override
    public void itemClick(View view, Business business) {
        if (view.getId() == R.id.join_business) joinConfirmationPopup(business);

    }

    @Override
    public void onClick(View view) {

    }
}