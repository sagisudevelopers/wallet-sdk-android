package com.sagisu.vault.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.plaid.link.OpenPlaidLink;
import com.plaid.link.configuration.LinkTokenConfiguration;
import com.plaid.link.result.LinkError;
import com.plaid.link.result.LinkExit;
import com.plaid.link.result.LinkSuccess;
import com.plaid.link.result.LinkSuccessMetadata;
import com.sagisu.vault.network.VaultAPIError;
import com.sagisu.vault.network.VaultResult;
import com.sagisu.vault.repository.NetworkRepository;
import com.sagisu.vault.utils.Util;

public class PlaidLinkActivity extends AppCompatActivity {

    public static final String BUNDLE_PUBLIC_TOKEN = "public_token";
    public static final String BUNDLE_INSTITUTION_NAME = "institution_name";
    public static final String BUNDLE_ERROR_MESSAGE = "error_msg";

    private final ActivityResultLauncher<LinkTokenConfiguration> linkAccountToPlaid = registerForActivityResult(new OpenPlaidLink(),
            result -> {
                if (result instanceof LinkSuccess) {
                    /* handle LinkSuccess */
                    LinkSuccess linkSuccess = (LinkSuccess) result;
                    String publicToken = linkSuccess.getPublicToken();
                    LinkSuccessMetadata metadata = linkSuccess.getMetadata();
                    /*List<Account> accountList = new ArrayList<>();
                    for (LinkAccount account : metadata.getAccounts()) {
                        Account account1 = new Account(account.getId(),account.getName());
                        String accountId = account.getId();
                        String accountName = account.getName();
                        String accountMask = account.getMask();
                        LinkAccountSubtype accountSubtype = account.getSubtype();
                        accountList.add(account1);
                    }
                    String institutionId = metadata.getInstitution().getId();*/
                    String institutionName = metadata.getInstitution().getName();
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra(BUNDLE_PUBLIC_TOKEN, publicToken);
                    returnIntent.putExtra(BUNDLE_INSTITUTION_NAME, institutionName);
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                } else if (result instanceof LinkExit) {
                    /* handle LinkExit */
                    LinkExit linkExit = ((LinkExit) result);
                    LinkError error = linkExit.getError();
                    String errorMessage = "Cancelled";
                    if (error != null) {
                        errorMessage = error.getErrorMessage();
                    }
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra(BUNDLE_ERROR_MESSAGE, errorMessage);
                    setResult(Activity.RESULT_CANCELED, returnIntent);
                    finish();
                }
            }
    );

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Redirecting to plaid");
        NetworkRepository.getInstance().createLinkToken().observe(this, new Observer<VaultResult<String>>() {
            @Override
            public void onChanged(VaultResult<String> tokenResult) {
                if (tokenResult instanceof VaultResult.Success) {
                    String linkToken = ((VaultResult.Success<String>) tokenResult).getData();
                    if (linkToken == null) return;
                    LinkTokenConfiguration linkTokenConfiguration = new LinkTokenConfiguration.Builder()
                            .token(linkToken)
                            .build();
                    linkAccountToPlaid.launch(linkTokenConfiguration);
                } else if (tokenResult instanceof VaultResult.Error) {
                    VaultAPIError vaultApiError = ((VaultResult.Error) tokenResult).getError();
                    Util.showSnackBar(vaultApiError.message(),PlaidLinkActivity.this);
                    //Toast.makeText(PlaidLinkActivity.this, apiError.message(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
