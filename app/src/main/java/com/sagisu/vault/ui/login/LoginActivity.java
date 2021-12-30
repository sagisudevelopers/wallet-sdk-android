package com.sagisu.vault.ui.login;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.sagisu.vault.R;
import com.sagisu.vault.ui.MainActivity;
import com.sagisu.vault.ui.OTP.IOtp;
import com.sagisu.vault.ui.OTP.Otp;
import com.sagisu.vault.ui.OTP.OtpView;
import com.sagisu.vault.ui.login.fragments.ForgotPasswordPage1Fragment;
import com.sagisu.vault.ui.login.fragments.LoginFormError;
import com.sagisu.vault.ui.login.fragments.LoginPage1Fragment;
import com.sagisu.vault.ui.login.fragments.LoginPage2Fragment;
import com.sagisu.vault.ui.login.fragments.LoginPage3Fragment;
import com.sagisu.vault.ui.login.fragments.LoginViewModel;
import com.sagisu.vault.ui.login.fragments.SignupPage1Fragment;
import com.sagisu.vault.ui.login.fragments.User;
import com.sagisu.vault.utils.AppManager;
import com.sagisu.vault.utils.OtpTypeDescriptor;
import com.sagisu.vault.utils.ProgressShimmer;
import com.sagisu.vault.utils.SharedPref;
import com.sagisu.vault.utils.Util;


public class LoginActivity extends AppCompatActivity implements IOtp {

    public static final String USER_ID = "UserId";

    private LoginViewModel mViewModel;
    private OtpView otpView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //Add an Activity instance to the stack of AppManager
        AppManager.getAppManager().addActivity(this);
        mViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        mViewModel.init();
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
                    Util.showSnackBar(s, LoginActivity.this);
                    //Toast.makeText(LoginActivity.this, s, Toast.LENGTH_SHORT).show();
                }
            }
        });
        mViewModel.pagePos.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                switch (integer) {
                    case 1:
                        loadFragment(new LoginPage1Fragment());
                        break;
                    case 2:
                        loadFragment(new LoginPage2Fragment());
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
                    case LoginViewModel.PageType
                            .Login:
                        loadFragment(new LoginPage1Fragment());
                        break;
                    case LoginViewModel.PageType
                            .Otp:
                        loadFragment(new LoginPage2Fragment());
                        break;
                    case LoginViewModel.PageType
                            .SignUp:
                        loadFragment(new SignupPage1Fragment());
                        break;
                    case LoginViewModel.PageType
                            .Password:
                        loadFragment(new LoginPage3Fragment());
                        break;
                    case LoginViewModel.PageType
                            .ForgotPasswordPhone:
                        loadFragment(new ForgotPasswordPage1Fragment());
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
                        new SharedPref(getApplicationContext()).setToken(mViewModel.getLoginRes().getToken());
                        new SharedPref(getApplicationContext()).setUser(mViewModel.getLoginRes().getUser());
                        goNext();
                        break;
                    case "SkipLogin":
                        //new SharedPref(getApplicationContext()).setRefreshTokenToSharedPref(mViewModel.agentLoggedIn.getRefreshToken());
                        //new SharedPref(getApplicationContext()).setValueToSharedPref(BundleKeyDescriptor.STRIPE_CUSTOMER_ID,mViewModel.agentLoggedIn.stripeCustomerId);
                        goNext();
                        break;
                    case "SignedUp":
                        mViewModel.setPage(LoginViewModel.PageType.Login);
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
                    Util.showSnackBar(loginFormError.getToastMsg(), LoginActivity.this);
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
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void getFcmToken() {
        FirebaseMessaging.getInstance().getToken()
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
                });
    }

    @Override
    public void verifiedNumber(@Nullable String token) {
        mViewModel.otpNumber.setValue(null);
        if (mViewModel.viewType.getValue().equals(LoginViewModel.ViewType.Login))
            mViewModel.loginStatus.setValue("LoggedIn");
        else if (mViewModel.viewType.getValue().equals(LoginViewModel.ViewType.SignUp) || mViewModel.viewType.getValue().equals(LoginViewModel.ViewType.ForgotPassword))
            mViewModel.setPage(LoginViewModel.PageType.Password);
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