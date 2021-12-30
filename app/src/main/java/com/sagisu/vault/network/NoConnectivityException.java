package com.sagisu.vault.network;

import java.io.IOException;

/**
 * Created by Anusha on 26-02-2018.
 */

public class NoConnectivityException extends IOException {

    @Override
    public String getMessage() {
        return "No connectivity exception";
    }

}
