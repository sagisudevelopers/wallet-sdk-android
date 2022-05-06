package com.sagisu.vaultLibrary.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import androidx.multidex.MultiDexApplication;
/*
import com.sagisu.vault.di.ApplicationComponent;
import com.sagisu.vault.di.ApplicationModule;*/
/*import com.sagisu.vault.di.DaggerApplicationComponent;*/

import java.text.SimpleDateFormat;
import java.util.Date;

public class Globals extends MultiDexApplication {

   /* private static ApplicationComponent appComponent;

    public static ApplicationComponent getAppComponent() {
        return appComponent;
    }*/

    public static Toast toast;


    @Override
    public void onCreate() {
        super.onCreate();
       // createDaggerInjections();
       /* FirebaseApp.initializeApp(getApplicationContext());
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);*/
    }


    /*private void createDaggerInjections() {
        appComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();
    }*/

    /**
     * Return date in specified format.
     *
     * @param milliSeconds Date in milliseconds
     * @param dateFormat   Date format
     * @return String representing date in specified format
     */
    public static String Epoch2DateString(long milliSeconds, String dateFormat) {
        Date updatedate = new Date(milliSeconds * 1000);
        SimpleDateFormat format = new SimpleDateFormat(dateFormat);
        return format.format(updatedate);
    }

    /**
     * Return country name in CountryNameDescriptor format.
     *
     * @param phoneNumber mobile number with country code
     * @return String representing country name in specified format
     */
    public static String getCountryName(String phoneNumber) {
        if (phoneNumber.startsWith("+1")) {
            return CountryNameDescriptor.US;
        } else return CountryNameDescriptor.IN;
    }


    public static boolean isOnline() {
        Context context = AppManager.getAppManager().currentActivity();
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        }

        return connected;
    }

    /*public static Context getContext() {
        return appComponent.getContext();
    }*/
}
