package com.sagisu.vault.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.sagisu.vault.R;
import com.sagisu.vault.databinding.DocumentVerificationActivityBinding;
import com.sagisu.vault.ui.kyc.DocumentVerificationViewModel;
import com.sagisu.vault.ui.kyc.KycFormError;
import com.sagisu.vault.ui.kyc.ReviewAddressInfoFragment;
import com.sagisu.vault.ui.kyc.ReviewPersonalInfoFragment;
import com.sagisu.vault.ui.kyc.SSNInfoFragment;
import com.sagisu.vault.ui.kyc.SelectDocumentsFragment;
import com.sagisu.vault.ui.kyc.VerifyIdSuccessActivity;
import com.sagisu.vault.utils.ProgressShimmer;

public class DocumentVerificationActivity extends AppCompatActivity {

    private DocumentVerificationViewModel mViewModel;
    private DocumentVerificationActivityBinding binding;

    //Create BroadcastReceiver
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            //Get data from intent
            String scanStatus = intent.getStringExtra("scanStatus");
            if (scanStatus.equals("VERIFIED")) {
                mViewModel.setDocumentScanResult(DocumentVerificationViewModel.KycScanResult.VERIFIED);
            }
        }
    };

    private void handleBroadcast() {
        IntentFilter intentFilter = new IntentFilter("IntentFilterAction");
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Unregister BroadcastReceiver
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionBarTitle("Document Verification");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        handleBroadcast();
        binding = DataBindingUtil.setContentView(this, R.layout.document_verification_activity);
        mViewModel = new ViewModelProvider(this).get(DocumentVerificationViewModel.class);
        binding.setViewModel(mViewModel);
        binding.setLifecycleOwner(this);

        mViewModel.getPageNoObservable().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer currentPage) {
                Integer totalPages = mViewModel.getTotalPages();
                binding.docVerificationStepsIndicator.setProgress(currentPage * 100 / totalPages);
                switch (currentPage) {
                    case 1:
                        loadFragment(SelectDocumentsFragment.newInstance());
                        break;
                    case 2:
                        loadFragment(ReviewPersonalInfoFragment.newInstance());
                        break;
                    case 3:
                        loadFragment(ReviewAddressInfoFragment.newInstance());
                        break;
                    case 4:
                        loadFragment(SSNInfoFragment.newInstance());
                        break;
                    default:
                        throw new Error("No implementation for " + currentPage);
                }
            }
        });

        mViewModel.getFormError().observe(this, new Observer<KycFormError>() {
            @Override
            public void onChanged(KycFormError kycFormError) {
                if (kycFormError.getToastError() != null)
                    Toast.makeText(DocumentVerificationActivity.this, kycFormError.getToastError(), Toast.LENGTH_SHORT).show();
            }
        });

        mViewModel.getKycPostStatus().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    Intent intent = new Intent(DocumentVerificationActivity.this, VerifyIdSuccessActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        mViewModel.getLoadingObservable().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s == null) loading(null, false);
                else loading(s, true);
            }
        });
    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .commitNow();
    }

    private void loading(String text, boolean loading) {
        ProgressShimmer.shimmerLoading(findViewById(R.id.loading), findViewById(R.id.dv_container), text, loading);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}