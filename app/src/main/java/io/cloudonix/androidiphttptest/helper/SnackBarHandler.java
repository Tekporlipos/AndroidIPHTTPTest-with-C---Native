package io.cloudonix.androidiphttptest.helper;

import android.content.Context;

import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;

public class SnackBarHandler {

    public static void showSnackBar(String message, int colorResourceId, Context context) {
        int backgroundColor = ContextCompat.getColor(context, colorResourceId);
        Snackbar.make(((android.app.Activity) context).findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(backgroundColor)
                .show();
    }
}
