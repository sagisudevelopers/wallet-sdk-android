package com.sagisu.vaultLibrary.ui.businessrequests;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.FirebaseApp;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.sagisu.vaultLibrary.R;
import com.sagisu.vaultLibrary.models.BusinessRequest;
import com.sagisu.vaultLibrary.ui.login.VaultLoginActivity;
import com.sagisu.vaultLibrary.utils.AppManager;
import com.sagisu.vaultLibrary.utils.SharedPref;

public class BusinessRequestsActivity extends AppCompatActivity {

    BusinessRequestsViewModel mViewModel;
    private boolean deepLinkAccessed = false;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Remove the Activity instance from the stack of AppManager
        AppManager.getAppManager().finishActivity(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_requests);
        getSupportActionBar().setTitle("Business Requests");
        AppManager.getAppManager().addActivity(this);
        mViewModel = new ViewModelProvider(this).get(BusinessRequestsViewModel.class);
        /*Business business = new SharedPref().getBusinessVaultSelected();
        if (business != null && business.get_id() != null)
            mViewModel.setBusinessId(business.get_id());*/
        mViewModel.getResponse().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                mViewModel.fetchAllBusinessRequests();
                Toast.makeText(BusinessRequestsActivity.this, s, Toast.LENGTH_SHORT).show();
                //finish();
            }
        });
        loadFragment(new BusinessRequestsFragment());
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.business_requests_frame, fragment).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (new SharedPref().getToken() == null) {
            Intent intent = new Intent(BusinessRequestsActivity.this, VaultLoginActivity.class);
            startActivity(intent);
        } else {
            FirebaseApp.initializeApp(getApplicationContext());
            onNewIntent(getIntent());
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleDeepLink();
    }

    private void handleDeepLink() {
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        if (pendingDynamicLinkData == null) return;

                        Uri deepLink = pendingDynamicLinkData.getLink();
                        String requestId = deepLink != null ? deepLink.getQueryParameter("requestId") : null;
                        if (requestId == null) return;
                        mViewModel.fetchBusinessRequests(requestId).observe(BusinessRequestsActivity.this, new Observer<BusinessRequest>() {
                            @Override
                            public void onChanged(BusinessRequest businessRequest) {
                                if (deepLinkAccessed) return;
                                mViewModel.setBusinessId(businessRequest.getBusiness().get_id());
                                if (businessRequest.getStatus().equals("Approved")) approvedPopup();
                                else confirmationPopup(businessRequest);
                                deepLinkAccessed = true;
                            }
                        });
                        //Make server call to get details of the requestId
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Business Request", "getDynamicLink:onFailure", e);
                    }
                });
    }

    private void confirmationPopup(BusinessRequest businessRequest) {
        String title, message;
        switch (businessRequest.getRequestType()) {
            case "Join User":
            default:
                title = "Request to join business ".concat(businessRequest.getBusiness().getName());
                message = businessRequest.getUser().getFullName().concat("(").concat(businessRequest.getUser().getPhone()).concat(") has requested to join your business." +
                        "As one of the director of this business please approve or reject this request");
                break;
            case "Verify Business":
                title = "Request to add business ".concat(businessRequest.getBusiness().getName());
                message = businessRequest.getUser().getFullName().concat("(").concat(businessRequest.getUser().getPhone()).concat(") has requested to add your business." +
                        "As one of the director of this business please approve or reject this request");
                break;
        }
        new MaterialAlertDialogBuilder(this)
                .setTitle(title)
                .setMessage(message)
                .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setNegativeButton("Reject", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mViewModel.reject(businessRequest.get_id());
                    }
                })
                .setPositiveButton("Approve", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mViewModel.approve(businessRequest.get_id());
                    }
                }).show();

    }

    private void approvedPopup() {
        String title, message;
        title = "Already Approved";
        message = "Request is already approved by one of the director";
        new MaterialAlertDialogBuilder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();

    }
}