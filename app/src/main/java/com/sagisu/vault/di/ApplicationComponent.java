package com.sagisu.vault.di;

import android.content.Context;

import com.sagisu.vault.network.ConnectivityInterceptor;
import com.sagisu.vault.repository.TransactionPagingSource;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {
        ApplicationModule.class
})
public interface ApplicationComponent {

    @ApplicationContext
    Context getContext();

    void inject(ConnectivityInterceptor connectivityInterceptor);
    void inject(TransactionPagingSource transactionPagingSource);

   /* // Utility Module
    Commons getCommonUtils();*/
}
