package com.demo.retrofit.network.request;

import android.content.Context;

import com.demo.retrofit.R;
import com.demo.retrofit.RetroFitApp;
import com.demo.retrofit.network.APIService;
import com.demo.retrofit.network.response.ApiCallback;
import com.demo.retrofit.network.response.ImageListResponse;
import com.demo.retrofit.utils.Helper;

/**
 * Use this request to retrieve the image list via the ImageListResponse api call.
 */
public class ImageListRequest extends AbstractApiRequest {
    private static final String LOGTAG = ImageListRequest.class.getSimpleName();

    private Context mContext;
    /**
     * The callback used for this request. Declared globally for cancellation. See {@link
     * #cancel()}.
     */
    private ApiCallback<ImageListResponse> callback;

    /**
     * See super constructor {@link AbstractApiRequest#AbstractApiRequest(APIService, String)}.
     */
    public ImageListRequest(APIService APIService, String tag) {
        super(APIService, tag);
        mContext = RetroFitApp.getAppContext();
    }

    /**
     * Executes the request asynchronously using the built-in mechanism of Retrofit. The api
     * response is posted on the EventBus.
     */
    public void execute() {
        callback = new ApiCallback(tag);

        if (!isInternetActive()) {
            callback.postUnexpectedError(mContext.getString(R.string.error_no_internet));
            return;
        }
        apiService.getImageList().enqueue(callback);
    }

    @Override
    public void cancel() {
        callback.invalidate();
    }

    @Override
    public boolean isInternetActive() {
        return Helper.isInternetActive(mContext);
    }
}
