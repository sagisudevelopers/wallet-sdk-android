package com.sagisu.vaultLibrary.network;

import java.io.IOException;

/**
 * Created by Anusha on 26-02-2018.
 */

public class VaultNoConnectivityException extends IOException {

    @Override
    public String getMessage() {
        return "No connectivity exception";
    }

}
