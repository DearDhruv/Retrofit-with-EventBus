package com.demo.retrofit;

import android.app.Application;
import android.content.Context;

import com.demo.retrofit.network.ApiClient;

/**
 * Application class
 * Initialization of {@link ApiClient} etc.
 */
public class RetroFitApp extends Application {

    private final String TAG = "RetroFitApp";
    private ApiClient mApiClient;
    private static Context mContext = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();

        initApiClient();
    }

    public ApiClient getApiClient() {
        return mApiClient;
    }

    /**
     * Initialize the {@link ApiClient}
     */
    private void initApiClient() {
        mApiClient = new ApiClient(this);
    }

    public RetroFitApp getApp() {
        return this;
    }

    public static Context getAppContext() {
        return mContext;
    }


}
