package com.sagisu.vault.ui.splashscreen;

import android.os.Handler;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SplashScreenViewModel extends ViewModel {
    private ObservableField<Boolean> displayBPText = new ObservableField<>();
    private MutableLiveData<Boolean> loadNextScreen = new MutableLiveData<>();

    private int secondsDelayed = 1;
    public void init(){
        new Handler().postDelayed(new Runnable() {
            public void run() {
              displayBPText.set(true);
              loadNextScreen();
            }
        }, secondsDelayed * 2000);
    }

    private void loadNextScreen(){
        new Handler().postDelayed(new Runnable() {
            public void run() {
                loadNextScreen.setValue(true);
            }
        }, secondsDelayed * 100);
    }

    public MutableLiveData<Boolean> getLoadNextScreen() {
        return loadNextScreen;
    }

    public ObservableField<Boolean> getDisplayBPText() {
        return displayBPText;
    }
}
