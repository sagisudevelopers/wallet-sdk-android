package com.sagisu.vaultLibrary.network;

/*import com.sagisu.vault.di.ApplicationContext;*/
import com.sagisu.vaultLibrary.utils.Globals;
import com.sagisu.vaultLibrary.utils.SharedPref;

import java.io.IOException;

        import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Anusha on 26-02-2018.
 */

public class VaultConnectivityInterceptor implements Interceptor {

    /*@Inject
    @ApplicationContext
    Context context;*/
    Globals globals;

    public VaultConnectivityInterceptor() {
        globals = new Globals();
        //Globals.getAppComponent().inject(this);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        if (!globals.isOnline()) {
            throw new VaultNoConnectivityException();
        }

        Request originalRequest = chain.request();
        Request authorizedRequest;
        String authorizationToken = new SharedPref().getToken();

        if (authorizationToken != null) {
            authorizedRequest = originalRequest.newBuilder()
                    .header("authorization", authorizationToken)
                    .build();
        } else {
            authorizedRequest = originalRequest.newBuilder()
                    .build();
        }
        return chain.proceed(authorizedRequest);
    }

}
