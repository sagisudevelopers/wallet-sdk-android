package com.sagisu.vault.ui.contacts;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.sagisu.vault.R;
import com.sagisu.vault.TransactionDetailsActivity;
import com.sagisu.vault.databinding.ContactTransferFragmentBiding;
import com.sagisu.vault.models.MessageBean;
import com.sagisu.vault.models.Transaction;
import com.sagisu.vault.utils.SharedPref;
import com.sagisu.vault.utils.Util;

import java.util.List;

public class ContactTransferFragment extends Fragment implements ConfirmationBottomDialogFragment.IConfirmDialogListener, MessageAdapter.IMessageClickListener {

    private ContactTransferViewModel mViewModel;
    private PubnubViewModel mPNViewModel;
    private ContactTransferFragmentBiding biding;
    private MessageAdapter messageAdapter;

    public static ContactTransferFragment newInstance() {
        return new ContactTransferFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.contact_transfer_fragment, container, false);
        biding = DataBindingUtil.bind(rootView);

        //Chat send key listener
        biding.chatBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    mPNViewModel.sendMessage(mViewModel.getChatMessage().getValue(), null);
                    handled = true;
                }
                return handled;
            }
        });

        biding.backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        return biding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(getActivity()).get(ContactTransferViewModel.class);
        mPNViewModel = new ViewModelProvider(getActivity()).get(PubnubViewModel.class);
        biding.setViewModel(mViewModel);
        biding.setLifecycleOwner(this);

        mPNViewModel.setUser(new SharedPref(getActivity().getApplicationContext()).getUser());
        mPNViewModel.setChannelName(mViewModel.getContactInfo().get().getPhoneNumber());
        mPNViewModel.setRecipientName(mViewModel.getContactInfo().get().getDisplayName());
        mPNViewModel.initPubNubClient();
        mPNViewModel.getmMessageBeanList().observe(getViewLifecycleOwner(), new Observer<List<MessageBean>>() {
            @Override
            public void onChanged(List<MessageBean> messageBeans) {
                if (messageAdapter == null)
                    initRecyclerView(messageBeans);
                else {
                    messageAdapter.notifyItemRangeChanged(messageBeans.size(), 1);
                    biding.messageList.scrollToPosition(messageBeans.size() - 1);
                }
            }
        });

        mPNViewModel.getClearChatBox().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) biding.chatBox.setText("");
            }
        });
        mViewModel.getToastMsg().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s == null) return;
                //Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
                Util.showSnackBar(s, getActivity());
            }
        });
        mViewModel.getContactTransferResponse().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s == null) return;

                mPNViewModel.sendMessage("$".concat(mViewModel.getChatMessage().getValue()), s);
                mViewModel.setContactTransferResponse(null);
                //routeToTransactionDetails(s);
            }
        });

        mViewModel.getShowConfirmationPopup().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean == null) return;
                if (aBoolean) {
                    showConfirmPopup();
                    mViewModel.setShowConfirmationPopup(false);
                }
            }
        });
    }

    private void showConfirmPopup() {
        ConfirmationBottomDialogFragment fragment =
                ConfirmationBottomDialogFragment.newInstance(this, mViewModel.getChatMessage().getValue());
        fragment.show(getActivity().getSupportFragmentManager(),
                "confirm_popup");
    }

    private void initRecyclerView(List<MessageBean> mMessageBeanList) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setStackFromEnd(true);
        biding.messageList.setLayoutManager(linearLayoutManager);
        messageAdapter = new MessageAdapter(getActivity(), mMessageBeanList, this);
        biding.messageList.setAdapter(messageAdapter);
    }

    @Override
    public void onConfirm() {
        mViewModel.doTransfer();
    }

    @Override
    public void onMessageClicked(MessageBean messageBean) {
        if (!messageBean.isBeSelf()) return;

        routeToTransactionDetails(messageBean.getPaymentId());
    }

    private void routeToTransactionDetails(String paymentId) {
        mViewModel.getTransaction(paymentId).observe(getViewLifecycleOwner(), new Observer<Transaction>() {
            @Override
            public void onChanged(Transaction transaction) {
                Intent intent = new Intent(getActivity(), TransactionDetailsActivity.class);
                intent.putExtra(TransactionDetailsActivity.BUNDLE_TRANSACTION, transaction);
                startActivity(intent);
            }
        });
    }
}