package com.sagisu.vault.network;

import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.HttpException;

/**
 * A generic class that holds a result success w/ data or an error exception.
 */
public class VaultResult<T> {
    // hide the private constructor to limit subclass types (Success, Error)
    private VaultResult() {
    }

    @Override
    public String toString() {
        if (this instanceof VaultResult.Success) {
            Success success = (Success) this;
            return "Success[data=" + success.getData().toString() + "]";
        } else if (this instanceof VaultResult.Error) {
            Error error = (Error) this;
            return "Error[exception=" + error.getError().toString() + "]";
        }
        return "";
    }

    // Success sub-class
    public final static class Success<T> extends VaultResult {
        private T data;
        private String msg;

        public Success(T data, String msg) {
            this.data = data;
            this.msg = msg;
        }

        public Success(T data) {
            this.data = data;
        }

        public T getData() {
            return this.data;
        }

        public String getMsg() {
            return msg;
        }
    }

    // Error sub-class
    public final static class Error extends VaultResult {
        private VaultAPIError error;


        public Error(HttpException errResponse) {
            Converter<ResponseBody, VaultAPIError> converter =
                    VaultApiClient.getRetrofit().responseBodyConverter(VaultAPIError.class, new Annotation[0]);

            try {
                error = converter.convert(errResponse.response().errorBody());
            } catch (Exception e) {
                error = new VaultAPIError();
            }

        }

        public Error(ResponseBody errResponse) {
            Converter<ResponseBody, VaultAPIError> converter =
                    VaultApiClient.getRetrofit().responseBodyConverter(VaultAPIError.class, new Annotation[0]);

            try {
                error = converter.convert(errResponse);
            } catch (Exception e) {
                error = new VaultAPIError();
            }

        }


        public Error(VaultAPIError errResponse) {
            this.error = errResponse;
        }

        public VaultAPIError getError() {
            return this.error;
        }
    }
}
