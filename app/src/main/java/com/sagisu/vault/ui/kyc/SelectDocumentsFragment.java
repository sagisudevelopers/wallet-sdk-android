package com.sagisu.vault.ui.kyc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.jumio.MobileSDK;
import com.jumio.core.enums.JumioDataCenter;
import com.jumio.core.exceptions.MissingPermissionException;
import com.jumio.core.exceptions.PlatformNotSupportedException;
import com.jumio.nv.NetverifyDeallocationCallback;
import com.jumio.nv.NetverifySDK;
import com.sagisu.vault.R;
import com.sagisu.vault.databinding.DocumentVerificationFragmentBinding;
import com.sagisu.vault.network.ApiClient;


public class SelectDocumentsFragment extends Fragment implements NetverifyDeallocationCallback {
    private final static String TAG = "JumioSDK_Netverify";
    private DocumentVerificationViewModel mViewModel;
    private DocumentVerificationFragmentBinding binding;

    public static final JumioDataCenter KEY_DATACENTER = JumioDataCenter.US;

    private String apiToken = ApiClient.JUMIO_KEY_API_TOKEN;
    private String apiSecret = ApiClient.JUMIO_KEY_API_SECRET;
    private JumioDataCenter dataCenter = KEY_DATACENTER;

    private NetverifySDK netverifySDK;

    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // features requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                }
            });

    ActivityResultLauncher<Intent> netVerifyActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Intent data = result.getData();
                    if (data == null)
                        return;

                    int resultCode = result.getResultCode();
                    if (resultCode == Activity.RESULT_CANCELED) return;

                    String scanReference = data.getStringExtra(NetverifySDK.EXTRA_SCAN_REFERENCE);
                    if (scanReference != null && !scanReference.isEmpty())
                        mViewModel.postKycScanId(scanReference);
                   /* String authResult = data.getStringExtra(NetverifySDK.EXTRA_AUTHENTICATION_RESULT);

                    if (result.getResultCode() == Activity.RESULT_OK) {
                        //Handle the success case and retrieve data
                        NetverifyDocumentData documentData = data.getParcelableExtra(NetverifySDK.EXTRA_SCAN_DATA);
                        NetverifyMrzData mrzData = documentData != null ? documentData.getMrzData() : null;


                    } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                        //Handle the error cases as described in our documentation: https://github.com/Jumio/mobile-sdk-android/blob/master/docs/integration_faq.md#managing-errors
                        String errorMessage = data.getStringExtra(NetverifySDK.EXTRA_ERROR_MESSAGE);
                        String errorCode = data.getStringExtra(NetverifySDK.EXTRA_ERROR_CODE);
                    }*/

                    //At this point, the SDK is not needed anymore. It is highly advisable to call destroy(), so that
                    //internal resources can be freed.
                    if (netverifySDK != null) {
                        netverifySDK.destroy();
                        netverifySDK.checkDeallocation(SelectDocumentsFragment.this);
                        netverifySDK = null;
                    }
                }
            });


    public static SelectDocumentsFragment newInstance() {
        return new SelectDocumentsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.document_verification_fragment, container, false);
        binding = DataBindingUtil.bind(rootView);
        binding.btnScanDocuments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                init();
            }
        });

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goBackHome();
            }
        });
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        // init();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(getActivity()).get(DocumentVerificationViewModel.class);
        mViewModel.init();
        binding.setViewModel(mViewModel);
        binding.setLifecycleOwner(this);
    }

    public void init() {
        //Since the NetverifySDK is a singleton internally, a new instance is not
        //created here.
        initializeNetverifySDK();

        if (getActivity() != null) {
            if (checkPermissions()) {
                startNetVerifySdk();
            }
        }
    }

    public void goBackHome() {
        getActivity().finish();
    }

    /**
     * Check and request missing permissions for the SDK.
     */
    public boolean checkPermissions() {
        if (!MobileSDK.hasAllRequiredPermissions(getActivity())) {
            //Acquire missing permissions.
            String[] mp = MobileSDK.getMissingPermissions(getActivity());
            for (String m :
                    mp) {
                requestPermissionLauncher.launch(m);
            }
            //The result is received in onRequestPermissionsResult.
            return false;
        } else {
            return true;
        }
    }

    private void startNetVerifySdk() {
        try {
            if (netverifySDK != null) {
                netVerifyActivityResultLauncher.launch(netverifySDK.getIntent());
                //startActivityForResult(netverifySDK.getIntent(), NetverifySDK.REQUEST_CODE);
            }
        } catch (MissingPermissionException e) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void initializeNetverifySDK() {
        try {
            // You can get the current SDK version using the method below.
//			NetverifySDK.getSDKVersion();

            // Call the method isSupportedPlatform to check if the device is supported.
            if (!NetverifySDK.isSupportedPlatform(getActivity()))
                Log.w(TAG, "Device not supported");

            // Applications implementing the SDK shall not run on rooted devices. Use either the below
            // method or a self-devised check to prevent usage of SDK scanning functionality on rooted
            // devices.
            if (NetverifySDK.isRooted(getActivity()))
                Log.w(TAG, "Device is rooted");

            // To create an instance of the SDK, perform the following call as soon as your activity is initialized.
            // Make sure that your merchant API token and API secret are correct and specify an instance
            // of your activity. If your merchant account is created in the EU data center, use
            // JumioDataCenter.EU instead.
            netverifySDK = NetverifySDK.create(getActivity(), apiToken, apiSecret, dataCenter);

            // Use the following method to create an instance of the SDK, using offline fastfill scanning.
//			try {
//				netverifySDK = NetverifySDK.create(getActivity(), "YOUROFFLINETOKEN", "YOURPREFERREDCOUNTRY");
//			} catch (SDKExpiredException e) {
//				e.printStackTrace();
//				Toast.makeText(getActivity().getApplicationContext(), "The offline SDK is expired", Toast.LENGTH_LONG).show();
//				return;
//			}

            // Enable ID verification to receive a verification status and verified data positions (see Callback chapter).
            // Note: Not possible for accounts configured as Fastfill only.
            netverifySDK.setEnableVerification(true);

            // You can specify issuing country (ISO 3166-1 alpha-3 country code) and/or ID types and/or document variant to skip
            // their selection during the scanning process.
            // Use the following method to convert ISO 3166-1 alpha-2 into alpha-3 country code.
//			String alpha3 = IsoCountryConverter.convertToAlpha3("AT");
            netverifySDK.setPreselectedCountry("US");
//			ArrayList<NVDocumentType> documentTypes = new ArrayList<>();
//			documentTypes.add(NVDocumentType.PASSPORT);
//			netverifySDK.setPreselectedDocumentTypes(documentTypes);
//			netverifySDK.setPreselectedDocumentVariant(NVDocumentVariant.PLASTIC);

            // The customer internal reference allows you to identify the scan (max. 100 characters).
            // Note: Must not contain sensitive data like PII (Personally Identifiable Information) or account login.
//			netverifySDK.setCustomerInternalReference("YOURSCANREFERENCE");

            // Use the following property to identify the scan in your reports (max. 100 characters).
//			netverifySDK.setReportingCriteria("YOURREPORTINGCRITERIA");

            // You can also set a user reference (max. 100 characters).
            // Note: The user reference should not contain sensitive data like PII (Personally Identifiable Information) or account login.
//			netverifySDK.setUserReference("USERREFERENCE");

            // Callback URL for the confirmation after the verification is completed. This setting overrides your Jumio merchant settings.
            netverifySDK.setCallbackUrl(ApiClient.URL + "auth/jumioCallback");

            // You can disable Identity Verification during the ID verification for a specific transaction.
            netverifySDK.setEnableIdentityVerification(true);

            // Use the following method to set the default camera position.
//			netverifySDK.setCameraPosition(JumioCameraPosition.FRONT);

            // Use the following method to only support IDs where data can be extracted on mobile only.
//			netverifySDK.setDataExtractionOnMobileOnly(true);

            // Use the following method to explicitly send debug-info to Jumio. (default: false)
            // Only set this property to true if you are asked by our Jumio support personnel.
//			netverifySDK.sendDebugInfoToJumio(true);

            // Use the following method to override the SDK theme that is defined in the Manifest with a custom Theme at runtime
//			netverifySDK.setCustomTheme(R.style.YOURCUSTOMTHEMEID);

            // Set watchlist screening on transaction level. Enable to override the default search, or disable watchlist screening for this transaction.
//			netverifySDK.setWatchlistScreening(NVWatchlistScreening.ENABLED);

            // Search profile for watchlist screening.
//			netverifySDK.setWatchlistSearchProfile("YOURPROFILENAME");

            // Use the following method to initialize the SDK before displaying it
//			netverifySDK.initiate(new NetverifyInitiateCallback() {
//				@Override
//				public void onNetverifyInitiateSuccess() {
//				}
//				@Override
//				public void onNetverifyInitiateError(String errorCode, String errorMessage, boolean retryPossible) {
//				}
//			});

        } catch (PlatformNotSupportedException | NullPointerException e) {
            Log.e(TAG, "Error in initializeNetverifySDK: ", e);
            if (getActivity() != null) {
                Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
            netverifySDK = null;
        }
    }

    @Override
    public void onNetverifyDeallocated() {

    }
}