package com.sagisu.vaultLibrary.ui.contacts;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.sagisu.vaultLibrary.databinding.ContactsFragmentBinding;
import com.sagisu.vaultLibrary.ui.ContactTransferActivity;

import java.util.List;

public class ContactsFragment extends Fragment implements ContactsAdapter.IContactsClickListener {

    private ContactsViewModel mViewModel;
    private ContactsFragmentBinding binding;
    private ContactsAdapter adapter;

    public static ContactsFragment newInstance() {
        return new ContactsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.contacts_fragment, container, false);
        binding = DataBindingUtil.bind(rootView);

        //Listen for text change on search view
        // Add Text Change Listener to EditText
        binding.contactListSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (adapter == null) return;
                // Call back the Adapter with current character to Filter
                adapter.getFilter().filter(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(getActivity()).get(ContactsViewModel.class);

        /* data is set once the contacts are fetched from device in Activity class
         * Observe variable and display contacts in listview on any changes
         */
        mViewModel.getContactInfoList().observe(getViewLifecycleOwner(), new Observer<List<ContactsInfo>>() {
            @Override
            public void onChanged(List<ContactsInfo> contactsInfos) {
                adapter = new ContactsAdapter(getActivity(), R.layout.contacts_item, contactsInfos, ContactsFragment.this);
                binding.lstContacts.setAdapter(adapter);
            }
        });
    }

    @Override
    public void onContactClick(ContactsInfo contactsInfo) {
        Intent intent = new Intent(getActivity(), ContactTransferActivity.class);
        intent.putExtra(ContactTransferActivity.CONTACT_INFO, contactsInfo);
        startActivity(intent);
    }
}