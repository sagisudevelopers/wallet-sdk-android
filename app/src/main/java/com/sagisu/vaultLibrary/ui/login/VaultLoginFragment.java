package com.sagisu.vaultLibrary.ui.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.sagisu.vaultLibrary.R;
import com.sagisu.vaultLibrary.ui.OTP.IOtp;
import com.sagisu.vaultLibrary.ui.OTP.Otp;
import com.sagisu.vaultLibrary.ui.OTP.OtpView;
import com.sagisu.vaultLibrary.ui.VaultMainActivity;
import com.sagisu.vaultLibrary.ui.login.fragments.ForgotPasswordPage1Fragment;
import com.sagisu.vaultLibrary.ui.login.fragments.LoginFormError;
import com.sagisu.vaultLibrary.ui.login.fragments.User;
import com.sagisu.vaultLibrary.ui.login.fragments.VaultLoginPage1Fragment;
import com.sagisu.vaultLibrary.ui.login.fragments.VaultLoginPage2Fragment;
import com.sagisu.vaultLibrary.ui.login.fragments.VaultLoginPage3Fragment;
import com.sagisu.vaultLibrary.ui.login.fragments.VaultLoginViewModel;
import com.sagisu.vaultLibrary.ui.login.fragments.VaultSignupPage1Fragment;
import com.sagisu.vaultLibrary.utils.AppManager;
import com.sagisu.vaultLibrary.utils.OtpTypeDescriptor;
import com.sagisu.vaultLibrary.utils.ProgressShimmer;
import com.sagisu.vaultLibrary.utils.SharedPref;
import com.sagisu.vaultLibrary.utils.Util;

import java.util.Objects;


public class VaultLoginFragment extends Fragment implements IOtp {

    public static final String USER_ID = "UserId";
    public static final String EXTERNAL_APP = "externalApp";
    public static final String BUNDLE_PHONE = "phone";
    public static final String BUNDLE_PASSWORD = "password";

    private VaultLoginViewModel mViewModel;
    private OtpView otpView;
    Bundle intent;
    private IVaultLoginListener listener;
    View rootView;

    public interface IVaultLoginListener {
        Activity getActivityReference();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = (IVaultLoginListener) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_vault_login, container, false);
        //Add an Activity instance to the stack of AppManager
        //FirebaseApp.initializeApp(this);
        AppManager.getAppManager().addActivity(listener.getActivityReference());
        mViewModel = new ViewModelProvider(getActivity()).get(VaultLoginViewModel.class);
        intent = getArguments();
        if (intent.getBoolean(EXTERNAL_APP, false))
            mViewModel.init(VaultLoginViewModel.ViewType.AUTO_LOGIN_EXTERNAL_APP);
        else {
            mViewModel.init(VaultLoginViewModel.ViewType.LoginOrSignup);
            mViewModel.consumer.getValue().setPhone(intent.getString(BUNDLE_PHONE));
        }
        getFcmToken();
        attachObserver();
        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Remove the Activity instance from the stack of AppManager
        AppManager.getAppManager().finishActivity(listener.getActivityReference());
    }


    private void attachObserver() {
        mViewModel.toastMsg.observe(getActivity(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s != null) {
                    mViewModel.toastMsg.setValue(null);
                    Util.showSnackBar(s, listener.getActivityReference());
                    //Toast.makeText(LoginActivity.this, s, Toast.LENGTH_SHORT).show();
                }
            }
        });
        mViewModel.pagePos.observe(getActivity(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                switch (integer) {
                    case 1:
                        loadFragment(new VaultLoginPage1Fragment());
                        break;
                    case 2:
                        loadFragment(new VaultLoginPage2Fragment());
                        break;
                    /*case 3:
                        loadFragment(new LoginPage3Fragment());
                        break;*/
                }
            }
        });

        mViewModel.page.observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String pageType) {
                switch (pageType) {
                    case VaultLoginViewModel.PageType
                            .Login:
                        loadFragment(new VaultLoginPage1Fragment());
                        break;
                    case VaultLoginViewModel.PageType
                            .Otp:
                        loadFragment(new VaultLoginPage2Fragment());
                        break;
                    case VaultLoginViewModel.PageType
                            .SignUp:
                        loadFragment(new VaultSignupPage1Fragment());
                        break;
                    case VaultLoginViewModel.PageType
                            .Password:
                        loadFragment(new VaultLoginPage3Fragment());
                        break;
                    case VaultLoginViewModel.PageType
                            .ForgotPasswordPhone:
                        loadFragment(new ForgotPasswordPage1Fragment());
                        break;
                    case VaultLoginViewModel.PageType
                            .AUTO_LOGIN_EXTERNAL_APP:
                        mViewModel.autoLoginExternalApp(intent.getString(BUNDLE_PHONE), intent.getString(BUNDLE_PASSWORD));
                        break;
                }
            }
        });

        mViewModel.loginStatus.observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                switch (s) {
                    case "DoLogin":
                        //mViewModel.login(CountryDetails.getInstance().getMobileCode(LoginActivity.this), getOsVersion(), getVersionNumber());
                        break;
                    case "LoggedIn":
                        new SharedPref().setToken(mViewModel.getLoginRes().getToken());
                        new SharedPref().setUser(mViewModel.getLoginRes().getUser());
                        goNext();
                        break;
                    case "SkipLogin":
                        //new SharedPref(getApplicationContext()).setRefreshTokenToSharedPref(mViewModel.agentLoggedIn.getRefreshToken());
                        //new SharedPref(getApplicationContext()).setValueToSharedPref(BundleKeyDescriptor.STRIPE_CUSTOMER_ID,mViewModel.agentLoggedIn.stripeCustomerId);
                        goNext();
                        break;
                    case "SignedUp":
                        //mViewModel.setPage(VaultLoginViewModel.PageType.Login);
                        if (Objects.equals(mViewModel.viewType.getValue(), VaultLoginViewModel.ViewType.AUTO_LOGIN_EXTERNAL_APP))
                            mViewModel.login();
                        else
                            mViewModel.setViewType(VaultLoginViewModel.ViewType.Login);
                        break;

                }
            }
        });

        mViewModel.otpStatus.observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                switch (s) {
                    case OtpTypeDescriptor.GENERATE:
                        generateOtp();
                        break;
                    case OtpTypeDescriptor.VERIFY:
                        verifyOtp();
                        break;
                    case OtpTypeDescriptor.RESEND:
                        resendOtp();
                        break;
                }
            }
        });

        mViewModel.getLoadingObservable().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String loadingText) {
                if (loadingText == null)
                    loading(null, false);
                else loading(loadingText, true);
            }
        });

        mViewModel.getFormError().observe(getViewLifecycleOwner(), new Observer<LoginFormError>() {
            @Override
            public void onChanged(LoginFormError loginFormError) {
                if (loginFormError.getToastMsg() != null)
                    Util.showSnackBar(loginFormError.getToastMsg(), listener.getActivityReference());
                //Toast.makeText(LoginActivity.this, loginFormError.getToastMsg(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void generateOtp() {
        //mViewModel.login();
        User agent = mViewModel.consumer.getValue();
        Otp otp = new Otp(agent.getPhone(), agent.getName(), "", "", "+91");
        otpView = new OtpView(getActivity(), this, otp);
        otpView.generateOtp();
    }

    private void verifyOtp() {
        otpView.verifyOtp(mViewModel.otpNumber.getValue());
    }

    private void resendOtp() {
        otpView.resendOtp();
    }

    private void loadFragment(Fragment fragment) {
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.login_frame, fragment).commit();
    }

    private String getVersionNumber() {
        PackageInfo pInfo = null;
        try {
            pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            String version = pInfo.versionName;
            return version;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void goNext() {
       /* if(getIntent().getBooleanExtra(ReserveActivity.FORCE_LOGIN_FOR_BOOKING,false))
            goToBooking();
        else goToDashboard();*/
        goToDashboard();
    }

    private void goToDashboard() {
        Intent intent = new Intent(getActivity(), VaultMainActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    private void getFcmToken() {
        /*FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("Fcm token", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();
                        mViewModel.setFcmToken(token);
                    }
                });*/
    }

    @Override
    public void verifiedNumber(@Nullable String token) {
        mViewModel.otpNumber.setValue(null);
        if (mViewModel.viewType.getValue().equals(VaultLoginViewModel.ViewType.Login) || mViewModel.viewType.getValue().equals(VaultLoginViewModel.ViewType.AUTO_LOGIN_EXTERNAL_APP))
            mViewModel.loginStatus.setValue("LoggedIn");
        else if (mViewModel.viewType.getValue().equals(VaultLoginViewModel.ViewType.SignUp) || mViewModel.viewType.getValue().equals(VaultLoginViewModel.ViewType.ForgotPassword))
            mViewModel.setPage(VaultLoginViewModel.PageType.Password);
    }

    @Override
    public void generateOtpSuccess() {
        mViewModel.onNextClicked();
        //mViewModel.setPage(LoginViewModel.PageType.Otp);
    }

    @Override
    public void loading(String text, boolean loading) {
        // findViewById(R.id.login_loading).setVisibility(loading ? View.VISIBLE : View.GONE);
        // findViewById(R.id.login_frame).setVisibility(loading ? View.GONE : View.VISIBLE);
        ProgressShimmer.shimmerLoading(rootView.findViewById(R.id.login_loading), rootView.findViewById(R.id.login_frame), text, loading);
    }
}