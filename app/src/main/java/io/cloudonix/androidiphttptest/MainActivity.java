package io.cloudonix.androidiphttptest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.*;


public class MainActivity extends AppCompatActivity {

    static {
        System.loadLibrary("androidiphttptest");
    }

    private TextView resultTextView;
    private ProgressBar progressBar;
    private Button startButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find references to UI elements
        resultTextView = findViewById(R.id.resultTextView);
        progressBar = findViewById(R.id.progressBar);
        startButton = findViewById(R.id.startButton);

        // Set click listener for the start button
        startButton.setOnClickListener(v -> {
            // Call method to start the task
            progressBar.setVisibility(View.VISIBLE);
            startButton.setVisibility(View.GONE);
            startTask();
        });

    }

    private void startTask() {
//        sendIPAddressToServer(ipAddressFromJNI());
    }



    /**
     * A native method that is implemented by the 'androidiphttptest' native library,
     * which is packaged with this application.
     */
    public native String ipAddressFromJNI();
}