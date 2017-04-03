package com.demo.retrofit.network.request;

import android.content.Context;

import com.demo.retrofit.R;
import com.demo.retrofit.RetroFitApp;
import com.demo.retrofit.network.APIService;
import com.demo.retrofit.network.response.AbstractApiResponse;
import com.demo.retrofit.network.response.ApiCallback;
import com.demo.retrofit.utils.Helper;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by deardhruv on 26/03/17.
 */
public class UploadImageRequest extends AbstractApiRequest {

    static final String LOGTAG = UploadImageRequest.class.getSimpleName();
    Context mContext;
    private String imageFile;

    /**
     * + The callback used for this request. Declared globally for cancellation. See {@link
     * #cancel()}.
     */
    ApiCallback<AbstractApiResponse> callback;

    /**
     * Initialize the request with the passed values.
     *
     * @param APIService
     * @param tag
     * @param imageFile
     */
    public UploadImageRequest(APIService APIService,
                              String tag,
                              String imageFile) {
        super(APIService, tag);
        this.imageFile = imageFile;
        this.mContext = RetroFitApp.getAppContext();
    }

    public void execute() {
        callback = new ApiCallback<>(tag);
        if (!isInternetActive()) {
            callback.postUnexpectedError(mContext.getString(R.string.error_no_internet));
            return;
        }

        MediaType mediaType = MediaType.parse("multipart/form-data");
        MultipartBody.Part body;
        RequestBody requestFile;
        File file = new File(imageFile);

        requestFile = RequestBody.create(mediaType, file);
        body = MultipartBody.Part.createFormData("file",
                file.getName(),
                requestFile);

        apiService.uploadFile("", "", body).enqueue(callback);
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
