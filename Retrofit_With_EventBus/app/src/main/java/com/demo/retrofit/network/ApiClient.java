package com.demo.retrofit.network;

import android.content.Context;

import com.demo.retrofit.BuildConfig;
import com.demo.retrofit.network.event.RequestFinishedEvent;
import com.demo.retrofit.network.request.AbstractApiRequest;
import com.demo.retrofit.network.request.ImageListRequest;
import com.demo.retrofit.network.request.UploadImageRequest;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Provides request functions for all api calls. This class maintains a map of running requests to
 * provide request cancellation.
 */

public class ApiClient {

    private static final String LOGTAG = ApiClient.class.getSimpleName();

    private static TimeUnit timeUnit = TimeUnit.SECONDS;
    private Retrofit retrofit;


    public Retrofit getRetrofit() {
        return retrofit;
    }

    private static int HTTP_TIMEOUT = 30; // Seconds by default

    // Cache size for the OkHttpClient
    private static final int HTTP_DISK_CACHE_SIZE = 30 * 1024 * 1024; // 30 MB

    private static final String WS_SCHEME = "http://";
    private static final String WS_PREFIX_DOMAIN = "deardhruv.com";
    private static final String WS_HOSTNAME = "/";
    private static final String WS_SUFFIX_FOLDER = "api/";

    private static String API_BASE_URL = WS_SCHEME
                                         + WS_PREFIX_DOMAIN
                                         + WS_HOSTNAME
                                         + WS_SUFFIX_FOLDER;

    /**
     * Makes the APIService calls.
     */
    private APIService mAPIService;

    /**
     * The list of running requests. Used to cancel requests.
     */
    private final Map<String, AbstractApiRequest> requests;

    private Context context;

    private static Interceptor requestInterceptor = new Interceptor() {
        @Override
        public Response intercept(Interceptor.Chain chain) throws IOException {
            // Request Intercepting...
            Request original = chain.request();
            return chain.proceed(original);
        }
    };


    public ApiClient(Context context) {

        this.context = context;
        requests = new HashMap<>();
        EventBus.getDefault().register(this);

        initAPIClient(initOkHttpBuilder(), API_BASE_URL);
    }

    private OkHttpClient.Builder initOkHttpBuilder() {
//        Install an HTTP cache in the application cache directory.
        File cacheDir = new File(context.getCacheDir(), "http");
        Cache cache = new Cache(cacheDir, HTTP_DISK_CACHE_SIZE);

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.cache(cache);
//        builder.followRedirects(false);

        return builder;
    }

    private void initAPIClient(OkHttpClient.Builder okBuilder, String url) {

        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            okBuilder.addInterceptor(loggingInterceptor).addInterceptor(requestInterceptor);
        }

        OkHttpClient httpClient = okBuilder.connectTimeout(HTTP_TIMEOUT, timeUnit)
                .writeTimeout(HTTP_TIMEOUT, timeUnit)
                .readTimeout(HTTP_TIMEOUT, timeUnit)
                .build();

        Retrofit.Builder builder = new Retrofit.Builder();

        retrofit = builder
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(url)
                .client(httpClient)
                .build();

        mAPIService = retrofit.create(APIService.class);
    }

    private void changeApiBaseUrl(String newApiBaseUrl) {
        API_BASE_URL = newApiBaseUrl;
        initAPIClient(initOkHttpBuilder(), API_BASE_URL);
    }

    public static String getBaseUrl() {
        return API_BASE_URL;
    }

    public void setHttpTimeoutInSecond(int httpTimeout) {
        HTTP_TIMEOUT = httpTimeout;
        timeUnit = TimeUnit.SECONDS;
        changeApiBaseUrl(API_BASE_URL);
    }

    public void setHttpTimeoutInMillis(int httpTimeout) {
        HTTP_TIMEOUT = httpTimeout;
        timeUnit = TimeUnit.MILLISECONDS;
        changeApiBaseUrl(API_BASE_URL);
    }

    public static int getHttpTimeout() {
        return HTTP_TIMEOUT;
    }

    // ============================================================================================
    // Request functions
    // ============================================================================================


    /**
     * Execute a request to retrieve the update message. See {@link APIService#getImageList()} for
     * parameter details.
     *
     * @param requestTag The tag for identifying the request.
     */
    public void getImageList(String requestTag) {
        ImageListRequest request = new ImageListRequest(mAPIService, requestTag);
        requests.put(requestTag, request);
        request.execute();
    }

    public void uploadImage(String requestTag, String file) {
        UploadImageRequest request = new UploadImageRequest(mAPIService, requestTag, file);
        requests.put(requestTag, request);
        request.execute();
    }


    // ============================================================================================
    // Public functions
    // ============================================================================================

    /**
     * Look up the event with the passed tag in the event list. If the request is found, cancel it
     * and remove it from the list.
     *
     * @param requestTag Identifies the request.
     * @return True if the request was cancelled, false otherwise.
     */
    public boolean cancelRequest(String requestTag) {
        System.gc();
        AbstractApiRequest request = requests.get(requestTag);

        if (request != null) {
            request.cancel();
            requests.remove(requestTag);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns true if the request with the passed tag is in the list of running requests, false
     * otherwise.
     */
    public boolean isRequestRunning(String requestTag) {
        return requests.containsKey(requestTag);
    }


    /**
     * A request has finished. Remove it from the list of running requests.
     *
     * @param event The event posted on the EventBus.
     */
    @Subscribe
    public void onEvent(RequestFinishedEvent event) {
        System.gc();
        requests.remove(event.getRequestTag());

    }
}
