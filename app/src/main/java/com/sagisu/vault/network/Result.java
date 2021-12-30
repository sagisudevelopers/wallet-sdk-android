package com.sagisu.vault.network;

import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.HttpException;

/**
 * A generic class that holds a result success w/ data or an error exception.
 */
public class Result<T> {
    // hide the private constructor to limit subclass types (Success, Error)
    private Result() {
    }

    @Override
    public String toString() {
        if (this instanceof Result.Success) {
            Success success = (Success) this;
            return "Success[data=" + success.getData().toString() + "]";
        } else if (this instanceof Result.Error) {
            Error error = (Error) this;
            return "Error[exception=" + error.getError().toString() + "]";
        }
        return "";
    }

    // Success sub-class
    public final static class Success<T> extends Result {
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
    public final static class Error extends Result {
        private APIError error;


        public Error(HttpException errResponse) {
            Converter<ResponseBody, APIError> converter =
                    ApiClient.getRetrofit().responseBodyConverter(APIError.class, new Annotation[0]);

            try {
                error = converter.convert(errResponse.response().errorBody());
            } catch (Exception e) {
                error = new APIError();
            }

        }

        public Error(ResponseBody errResponse) {
            Converter<ResponseBody, APIError> converter =
                    ApiClient.getRetrofit().responseBodyConverter(APIError.class, new Annotation[0]);

            try {
                error = converter.convert(errResponse);
            } catch (Exception e) {
                error = new APIError();
            }

        }


        public Error(APIError errResponse) {
            this.error = errResponse;
        }

        public APIError getError() {
            return this.error;
        }
    }
}
