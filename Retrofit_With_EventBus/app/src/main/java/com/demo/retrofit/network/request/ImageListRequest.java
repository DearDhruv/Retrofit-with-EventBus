package com.demo.retrofit.network.request;

import com.demo.retrofit.R;
import com.demo.retrofit.network.ApiCallback;
import com.demo.retrofit.network.ApiService;
import com.demo.retrofit.network.response.ImageListResponse;

/**
 * Use this request to retrieve the image list via the ImageListResponse api call.
 */
public class ImageListRequest extends AbstractApiRequest {

    /**
     * The callback used for this request. Declared globally for cancellation. See {@link
     * #cancel()}.
     */
    private ApiCallback<ImageListResponse> callback;

    /**
     * See super constructor {@link AbstractApiRequest#AbstractApiRequest(ApiService, String)}.
     */
    public ImageListRequest(ApiService apiService, String tag) {
        super(apiService, tag);
    }

    /**
     * Executes the request asynchronously using the built-in mechanism of Retrofit. The api
     * response is posted on the EventBus.
     */
    public void execute() {
        callback = new ApiCallback(tag);

        if (!isInternetActive()) {
            callback.postUnexpectedError(context.getString(R.string.error_no_internet));
            return;
        }
        apiService.getImageList().enqueue(callback);
    }

    @Override
    public void cancel() {
        callback.invalidate();
    }

}