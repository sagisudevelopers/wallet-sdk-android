package com.sagisu.vaultLibrary.ui.businessrequests;

import android.content.Context;
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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.sagisu.vaultLibrary.R;
import com.sagisu.vaultLibrary.databinding.BusinessRequestFragmentBinding;
import com.sagisu.vaultLibrary.databinding.EnrollToBusinessProfileBinding;
import com.sagisu.vaultLibrary.models.BusinessRequest;
import com.sagisu.vaultLibrary.ui.businessprofile.IBusinessNavigator;
import com.sagisu.vaultLibrary.ui.transactions.TransactionLoadStateAdapter;

import autodispose2.AutoDispose;
import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider;

public class BusinessRequestsFragment extends Fragment implements AllBusinessRequestsAdapter.IBusinessRequestClickListener, View.OnClickListener {

    private BusinessRequestsViewModel mViewModel;
    private BusinessRequestFragmentBinding binding;
    private AllBusinessRequestsAdapter adapter;
    private IBusinessNavigator navigator;

    private boolean extendFab = true;

    public static BusinessRequestsFragment newInstance() {
        return new BusinessRequestsFragment();
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
        View rootView = inflater.inflate(R.layout.business_request_fragment, container, false);
        binding = DataBindingUtil.bind(rootView);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(getActivity()).get(BusinessRequestsViewModel.class);

        /*mViewModel.getLoadingObservable().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                navigator.loading(s);
            }
        });*/

        mViewModel.getBusinessId().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s != null) {
                    mViewModel.fetchAllBusinessRequests();
                    if (adapter == null) initRecyclerView();
                    else adapter.notifyDataSetChanged();
                }
            }
        });

        /*mViewModel.getResponse().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s != null) {
                    mViewModel.fetchAllBusinessRequests();
                    initRecyclerView();
                }
            }
        });*/
    }

    private void initRecyclerView() {
        binding.lstAllBusiness.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new AllBusinessRequestsAdapter(getActivity(), this);
        adapter.withLoadStateFooter(
                new TransactionLoadStateAdapter(this));
        binding.lstAllBusiness.setAdapter(adapter);

        mViewModel.getBusinessRequestList()
                // Using AutoDispose to handle subscription lifecycle.
                // See: https://github.com/uber/AutoDispose
                .to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(pagingData -> adapter.submitData(getLifecycle(), pagingData));
    }

    @Override
    public void itemClick(View view, BusinessRequest businessRequest) {
        if (view.getId() == R.id.approve_business || view.getId() == R.id.approve_user) {
            mViewModel.setBusinessRequest(businessRequest);
            mViewModel.approve(businessRequest.get_id());
        }
    }

    @Override
    public void onClick(View view) {

    }
}