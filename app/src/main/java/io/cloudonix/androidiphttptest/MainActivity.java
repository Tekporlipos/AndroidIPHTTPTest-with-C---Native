package io.cloudonix.androidiphttptest;

import static io.cloudonix.androidiphttptest.helper.HelperFunctions.isValidIPAddress;
import static io.cloudonix.androidiphttptest.helper.SnackBarHandler.showSnackBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import io.cloudonix.androidiphttptest.helper.HttpRequestHandler;
import okhttp3.*;


public class MainActivity extends AppCompatActivity {

    static {
        System.loadLibrary("androidiphttptest");
    }
    private final String TAG = "MainActivity_HTTP_REQUEST";
    private TextView resultTextView;
    private LinearLayout progressBar;
    private Button startButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resultTextView = findViewById(R.id.resultTextView);
        progressBar = findViewById(R.id.progressBar);
        startButton = findViewById(R.id.startButton);

        startButton.setOnClickListener(v -> startTask());

    }

    private void reset(boolean state){
        runOnUiThread(()->{
            if (state){
                progressBar.setVisibility(View.VISIBLE);
                startButton.setVisibility(View.GONE);
            }else {
                progressBar.setVisibility(View.GONE);
                startButton.setVisibility(View.VISIBLE);
            }
        });
    }
    private void startTask() {
        reset(true);
        final String ip =ipAddressFromJNI();
        if (isValidIPAddress(ip)){
            sendIPAddressToServer(ip);
        }else {
            showSnackBar("Sorry!, we got an invalid IP Address.", R.color.failureColor,MainActivity.this);
        }
        Log.i(TAG, "IP Address: "+ip);
    }

    private void sendIPAddressToServer(String ipAddress) {
        HttpRequestHandler httpRequestHandler = HttpRequestHandler.getInstance();
        JSONObject jsonBody = httpRequestHandler.createJsonBody(ipAddress);
        RequestBody requestBody = httpRequestHandler.createRequestBody(jsonBody);
        Request request = httpRequestHandler.createRequest(requestBody,this);
        sendRequest(request,ipAddress);

    }

    private void sendRequest(Request request,String ipAddress) {
        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.i(TAG, Objects.requireNonNull(e.getMessage()));
                reset(false);
                showSnackBar("Failed to send IP address to server", R.color.failureColor,MainActivity.this);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                handleResponse(response,MainActivity.this,ipAddress);
            }
        });
    }

    private void handleResponse(Response response,Context context,String ipAddress) {
        try (ResponseBody responseBody = response.body()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            if (responseBody != null) {
                String jsonString = responseBody.string();
                JSONObject jsonResponse = new JSONObject(jsonString);
                boolean nat = jsonResponse.optBoolean("nat");
                if (nat) {
                    showSnackBar("Server response: NAT TRUE", R.color.successColor,context);
                } else {
                    showSnackBar("Server response: NAT FALSE", R.color.failureColor,context);
                }
                runOnUiThread(()-> {
                    resultTextView.setText(ipAddress);
                    Drawable drawable = ResourcesCompat.getDrawable(getResources(), nat?R.drawable.green_okay:R.drawable.red_not_okay, null);
                    resultTextView.setCompoundDrawablesWithIntrinsicBounds(drawable,null,null,null);
                });
            } else {
                showSnackBar("Empty response from server", R.color.failureColor,context);
            }
        } catch (IOException | JSONException e) {
            Log.i(TAG, Objects.requireNonNull(e.getMessage()));
            showSnackBar("Failed to parse server response", R.color.failureColor,context);
        }
        reset(false);
    }

    /**
     * A native method that is implemented by the 'androidiphttptest' native library,
     * which is packaged with this application.
     */
    public native String ipAddressFromJNI();
}