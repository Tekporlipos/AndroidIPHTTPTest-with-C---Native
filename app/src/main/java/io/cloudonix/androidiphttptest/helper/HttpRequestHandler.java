package io.cloudonix.androidiphttptest.helper;


import android.content.Context;
import android.util.Log;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import io.cloudonix.androidiphttptest.R;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

public class HttpRequestHandler {
    private static HttpRequestHandler instance;

    private HttpRequestHandler() {
    }
    public static synchronized HttpRequestHandler getInstance() {
        if (instance == null) {
            instance = new HttpRequestHandler();
        }
        return instance;
    }



    public JSONObject createJsonBody(String ipAddress) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("address", ipAddress);
        } catch (JSONException e) {
            String TAG = "HttpRequestHandler_HTTP_REQUEST";
            Log.i(TAG, Objects.requireNonNull(e.getMessage()));
        }
        return jsonBody;
    }

    public RequestBody createRequestBody(JSONObject jsonBody) {
        return RequestBody.create(MediaType.parse("application/json"), jsonBody.toString());
    }

    public Request createRequest(RequestBody requestBody, Context context) {
        String url = context.getString(R.string.server_url);
        return new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
    }
}
