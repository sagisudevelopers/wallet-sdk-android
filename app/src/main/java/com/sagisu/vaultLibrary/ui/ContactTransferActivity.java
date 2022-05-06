package com.sagisu.vaultLibrary.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.sagisu.vaultLibrary.R;
import com.sagisu.vaultLibrary.ui.contacts.ContactTransferFragment;
import com.sagisu.vaultLibrary.ui.contacts.ContactTransferViewModel;
import com.sagisu.vaultLibrary.utils.AppManager;
import com.sagisu.vaultLibrary.utils.ProgressShimmer;

public class ContactTransferActivity extends AppCompatActivity {

    public static final String CONTACT_INFO = "contact_info";
    private ContactTransferViewModel mViewModel;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Remove the Activity instance from the stack of AppManager
        AppManager.getAppManager().finishActivity(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_transfer_activity);
        //Add an Activity instance to the stack of AppManager
        AppManager.getAppManager().addActivity(this);
        if (savedInstanceState == null) {
            mViewModel = new ViewModelProvider(this).get(ContactTransferViewModel.class);
            mViewModel.setContactInfo(getIntent().getParcelableExtra(CONTACT_INFO));
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, ContactTransferFragment.newInstance())
                    .commitNow();

            mViewModel.getLoadingObservable().observe(this, new Observer<String>() {
                @Override
                public void onChanged(String loadinTxt) {
                    if (loadinTxt == null) loading(loadinTxt, false);
                    else loading(loadinTxt, true);
                }
            });
        }
    }

    public void loading(String loadingText, boolean loading) {
        /*findViewById(R.id.msg_loading).setVisibility(loading ? View.VISIBLE : View.GONE);
        findViewById(R.id.container).setVisibility(loading ? View.GONE : View.VISIBLE);*/
        ProgressShimmer.shimmerLoading(findViewById(R.id.msg_loading), findViewById(R.id.container), loadingText, loading);
    }
}