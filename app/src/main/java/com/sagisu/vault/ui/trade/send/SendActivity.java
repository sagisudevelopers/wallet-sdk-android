package com.sagisu.vault.ui.trade.send;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.mlkit.md.LiveBarcodeScanningActivity;
import com.sagisu.vault.R;
import com.sagisu.vault.models.Coins;
import com.sagisu.vault.models.ValidateAddressResponse;
import com.sagisu.vault.ui.ReceiveCryptoActivity;
import com.sagisu.vault.ui.home.TradeCoinDialogFragment;
import com.sagisu.vault.ui.trade.SelectCoinsFragment;
import com.sagisu.vault.utils.AppManager;
import com.sagisu.vault.utils.Globals;
import com.sagisu.vault.utils.ProgressShimmer;
import com.sagisu.vault.utils.SharedPref;
import com.sagisu.vault.utils.Util;

public class SendActivity extends AppCompatActivity implements SelectCoinsFragment.ICoinSelectionListener, TradeCoinDialogFragment.ITradeCoinListener {

    private static final int PERMISSIONS_REQUEST_CAMERA = 589;
    private SendCoinsViewModel mViewModel;

    ActivityResultLauncher<Intent> plaidActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        if (data != null) {
                            String qrData = data.getStringExtra("data");
                            //Toast.makeText(SendActivity.this, qrData, Toast.LENGTH_LONG).show();
                            /*String[] splitQrData = qrData.split("\\|");
                            if (!mViewModel.matchAsset(splitQrData[1])) {
                                Toast.makeText(SendActivity.this, "Token mismatch - Please select the same token as of the receiver", Toast.LENGTH_LONG).show();
                                return;
                            }
                            mViewModel.setReceiverVaultId(splitQrData[0]);
                            mViewModel.setAssetId(splitQrData[1]);*/
                            mViewModel.setQrData(qrData);
                            //mViewModel.setViewType(SendCoinsViewModel.SendViewType.ENTER_AMOUNT);
                            //mViewModel.nextPage();
/*                            //fragment.setPublicToken(publicToken);
                            mViewModel.exchangeForAccessToken(publicToken, institutionName);*/
                        }
                    } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                        // There are no request codes
                        //mViewModel.goBack();

                    }
                }
            });


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStackImmediate();
        } else finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Remove the Activity instance from the stack of AppManager
        AppManager.getAppManager().finishActivity(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Coins coins = intent.getParcelableExtra(ReceiveCryptoActivity.BUNDLE_COIN_SELECTED);
        if (coins != null) {
            mViewModel.setNeedCoinSelection(false);
            onCoinSelected(coins);
        } else mViewModel.setViewType(SendCoinsViewModel.SendViewType.SELECT_COIN);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);
        AppManager.getAppManager().addActivity(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mViewModel = new ViewModelProvider(this).get(SendCoinsViewModel.class);
        onNewIntent(getIntent());
       /*mViewModel.getPageNo().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                mViewModel.setPages(integer);
            }
        });*/

        mViewModel.getViewType().observe(this, new Observer<SendCoinsViewModel.SendViewType>() {
            @Override
            public void onChanged(SendCoinsViewModel.SendViewType sendViewType) {
                switch (sendViewType) {
                    case DESTROY:
                        finish();
                        break;
                    case SELECT_COIN:
                        if (savedInstanceState == null) {
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.container_send, SelectCoinsFragment.newInstance((Util.TradeTypes) getIntent().getSerializableExtra(SelectCoinsFragment.BUNDLE_TRADE_TYPE), getIntent().getParcelableArrayListExtra(SelectCoinsFragment.BUNDLE_COINS_KEY)), "Select Token")
                                    .commit();
                        }
                        setActionBarTitle("Select token");
                        break;
                    case ENTER_ADDRESS:
                        if (savedInstanceState == null) {
                            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                            if (mViewModel.getNeedCoinSelection().getValue())
                                transaction.addToBackStack(null);
                            transaction
                                    .replace(R.id.container_send, SendCryptoAddressFragment.newInstance(), "Enter Address")
                                    .commit();
                        }
                        setActionBarTitle("Send ".concat(mViewModel.getCoins().getValue().getName()));
                        break;
                    case SCAN_QR:
                        requestCameraPermission();
                        //launchBarcodeScan();
                        //setActionBarTitle("Scan QR code");
                        break;
                    case ENTER_AMOUNT:
                        TradeCoinDialogFragment bottomSheetFragment = new TradeCoinDialogFragment();
                        Bundle bundle = new Bundle();
                        bundle.putParcelable(TradeCoinDialogFragment.COINS_KEY, mViewModel.getCoins().getValue());
                        bundle.putSerializable(TradeCoinDialogFragment.TRADE_TYPE_KEY, Util.TradeTypes.SEND);
                        bottomSheetFragment.setArguments(bundle);
                        bottomSheetFragment.show(getSupportFragmentManager(),
                                "trade_coin_dialog");
                        break;
                }
            }
        });

        mViewModel.getSendResponse().observe(this, new Observer<SendCryptoResponse>() {
            @Override
            public void onChanged(SendCryptoResponse sendCryptoResponse) {
                if (sendCryptoResponse != null) {
                    Toast.makeText(SendActivity.this, "Transaction initiated", Toast.LENGTH_LONG).show();
                    new SharedPref(Globals.getContext()).setCryptoBalanceUpdated(false);
                    finish();
                }
            }
        });

        mViewModel.getAddressValidationResponse().observe(this
                , new Observer<ValidateAddressResponse>() {
                    @Override
                    public void onChanged(ValidateAddressResponse validateAddressResponse) {
                        if (validateAddressResponse == null) return;

                        mViewModel.setViewType(SendCoinsViewModel.SendViewType.ENTER_AMOUNT);
                    }
                });

        mViewModel.getLoadingObservable().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String txt) {
                if (txt == null) loading(null, false);
                else loading(txt, true);
            }
        });

        mViewModel.getToastMsg().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s != null)
                    Toast.makeText(SendActivity.this, s, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void launchBarcodeScan() {
        Intent plaidLinkIntent = new Intent(SendActivity.this, LiveBarcodeScanningActivity.class);
        plaidActivityResultLauncher.launch(plaidLinkIntent);
    }

    public void requestCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        android.Manifest.permission.CAMERA)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Camera access needed");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setMessage("Please enable access to camera.");
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @TargetApi(Build.VERSION_CODES.M)
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            requestPermissions(
                                    new String[]
                                            {android.Manifest.permission.CAMERA}
                                    , PERMISSIONS_REQUEST_CAMERA);
                        }
                    });
                    builder.show();
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{android.Manifest.permission.CAMERA},
                            PERMISSIONS_REQUEST_CAMERA);
                }
            } else {
                launchBarcodeScan();
            }
        } else {
            launchBarcodeScan();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CAMERA: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    launchBarcodeScan();
                } else {
                    Util.showSnackBar("You have disabled a contacts permission", this);
                    //Toast.makeText(this, "You have disabled a contacts permission", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    @Override
    public void onCoinSelected(Coins coins) {
        if (coins.getBalance() == 0) {
            Util.showSnackBar("Insufficient token to send");
            return;
        }
        mViewModel.setCoins(coins);
        mViewModel.setViewType(SendCoinsViewModel.SendViewType.ENTER_ADDRESS);
        //mViewModel.nextPage();
    }

    @Override
    public void OnTradeAction(String amount, String currencyCode) {
        mViewModel.actionSend(amount, currencyCode);
    }

    public void setActionBarTitle(String title) {
       /* getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.vault_logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);*/
        getSupportActionBar().setTitle(title);
    }

    public void loading(String text, boolean loading) {
        ProgressShimmer.shimmerLoading(findViewById(R.id.loading_send), findViewById(R.id.container_send), text, loading);
    }
}