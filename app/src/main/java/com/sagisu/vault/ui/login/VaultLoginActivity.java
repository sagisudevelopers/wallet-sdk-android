package com.sagisu.vault.ui.login;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.sagisu.vault.R;
import com.sagisu.vault.ui.VaultMainActivity;
import com.sagisu.vault.ui.OTP.IOtp;
import com.sagisu.vault.ui.OTP.Otp;
import com.sagisu.vault.ui.OTP.OtpView;
import com.sagisu.vault.ui.login.fragments.ForgotPasswordPage1Fragment;
import com.sagisu.vault.ui.login.fragments.LoginFormError;
import com.sagisu.vault.ui.login.fragments.VaultLoginPage1Fragment;
import com.sagisu.vault.ui.login.fragments.VaultLoginPage2Fragment;
import com.sagisu.vault.ui.login.fragments.VaultLoginPage3Fragment;
import com.sagisu.vault.ui.login.fragments.VaultLoginViewModel;
import com.sagisu.vault.ui.login.fragments.VaultSignupPage1Fragment;
import com.sagisu.vault.ui.login.fragments.User;
import com.sagisu.vault.utils.AppManager;
import com.sagisu.vault.utils.OtpTypeDescriptor;
import com.sagisu.vault.utils.ProgressShimmer;
import com.sagisu.vault.utils.SharedPref;
import com.sagisu.vault.utils.Util;

import java.util.Objects;


public class VaultLoginActivity extends AppCompatActivity implements IOtp {

    public static final String USER_ID = "UserId";
    public static final String EXTERNAL_APP = "externalApp";
    public static final String BUNDLE_PHONE = "phone";
    public static final String BUNDLE_PASSWORD = "password";

    private VaultLoginViewModel mViewModel;
    private OtpView otpView;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vault_login);
        //Add an Activity instance to the stack of AppManager
        //FirebaseApp.initializeApp(this);
        AppManager.getAppManager().addActivity(this);
        mViewModel = new ViewModelProvider(this).get(VaultLoginViewModel.class);
        intent = getIntent().getParcelableExtra(Intent.EXTRA_INTENT);
        if (intent != null && intent.getBooleanExtra(EXTERNAL_APP, false))
            mViewModel.init(VaultLoginViewModel.ViewType.AUTO_LOGIN_EXTERNAL_APP);
        else {
            mViewModel.init(VaultLoginViewModel.ViewType.Login);
            mViewModel.consumer.getValue().setPhone(intent != null ? intent.getStringExtra(BUNDLE_PHONE) : null);
        }
        getFcmToken();
        attachObserver();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Remove the Activity instance from the stack of AppManager
        AppManager.getAppManager().finishActivity(this);
    }


    private void attachObserver() {
        mViewModel.toastMsg.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s != null) {
                    mViewModel.toastMsg.setValue(null);
                    Util.showSnackBar(s, VaultLoginActivity.this);
                    //Toast.makeText(LoginActivity.this, s, Toast.LENGTH_SHORT).show();
                }
            }
        });
        mViewModel.pagePos.observe(this, new Observer<Integer>() {
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

        mViewModel.page.observe(this, new Observer<String>() {
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
                        mViewModel.autoLoginExternalApp(intent.getStringExtra(BUNDLE_PHONE), intent.getStringExtra(BUNDLE_PASSWORD));
                        break;
                }
            }
        });

        mViewModel.loginStatus.observe(this, new Observer<String>() {
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

        mViewModel.otpStatus.observe(this, new Observer<String>() {
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

        mViewModel.getLoadingObservable().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String loadingText) {
                if (loadingText == null)
                    loading(null, false);
                else loading(loadingText, true);
            }
        });

        mViewModel.getFormError().observe(this, new Observer<LoginFormError>() {
            @Override
            public void onChanged(LoginFormError loginFormError) {
                if (loginFormError.getToastMsg() != null)
                    Util.showSnackBar(loginFormError.getToastMsg(), VaultLoginActivity.this);
                //Toast.makeText(LoginActivity.this, loginFormError.getToastMsg(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void generateOtp() {
        //mViewModel.login();
        User agent = mViewModel.consumer.getValue();
        Otp otp = new Otp(agent.getPhone(), agent.getName(), "", "", "+91");
        otpView = new OtpView(this, this, otp);
        otpView.generateOtp();
    }

    private void verifyOtp() {
        otpView.verifyOtp(mViewModel.otpNumber.getValue());
    }

    private void resendOtp() {
        otpView.resendOtp();
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.login_frame, fragment).commit();
    }

    private String getVersionNumber() {
        PackageInfo pInfo = null;
        try {
            pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
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
        /*Intent intent = new Intent(VaultLoginActivity.this, VaultMainActivity.class);
        startActivity(intent);*/ //TODO : Do a routing if it is from normal way
        finish();
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
        ProgressShimmer.shimmerLoading(findViewById(R.id.login_loading), findViewById(R.id.login_frame), text, loading);
    }
}