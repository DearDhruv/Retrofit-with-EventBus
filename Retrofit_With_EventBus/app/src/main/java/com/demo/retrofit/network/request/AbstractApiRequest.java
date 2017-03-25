package com.demo.retrofit.network.request;


import com.demo.retrofit.network.APIService;

/**
 * Base class for the API requests. Provides functionality for cancelling APIService requests.
 */
public abstract class AbstractApiRequest {
    /**
     * The endpoint for executing the calls.
     */
    protected final APIService apiService;

    /**
     * Identifies the request.
     */
    protected final String tag;

    /**
     * Initialize the request with the passed values.
     *
     * @param APIService The {@link APIService} used for executing the calls.
     * @param tag        Identifies the request.
     */
    protected AbstractApiRequest(APIService APIService, String tag) {
        this.apiService = APIService;
        this.tag = tag;
    }

    /**
     * Cancels the running request. The response will still be delivered but will be ignored. The
     * implementation should call invalidate on the callback which is used for the request.
     */
    public abstract void cancel();

    /**
     * Check for active internet connection
     *
     * @return boolean
     */
    public abstract boolean isInternetActive();

    public abstract void execute();

}
