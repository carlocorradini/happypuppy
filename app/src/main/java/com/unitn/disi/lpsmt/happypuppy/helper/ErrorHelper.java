package com.unitn.disi.lpsmt.happypuppy.helper;

import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.annotation.StringRes;

import com.unitn.disi.lpsmt.happypuppy.R;
import com.unitn.disi.lpsmt.happypuppy.ui.components.Toasty;

import java.io.IOException;

/**
 * Error helper class
 *
 * @author Carlo Corradini
 */
public final class ErrorHelper {
    /**
     * {@link Log} TAG of this class
     */
    private static final String TAG = ErrorHelper.class.getName();

    /**
     * Show common {@link retrofit2.Retrofit} failure error with {@link Toasty} message
     *
     * @param context The current {@link Context}
     * @param v       The {@link View}
     * @param t       The {@link Throwable error}
     */
    public static void showFailureError(Context context, View v, Throwable t) {
        @StringRes int stringErrorRes = R.string.error_unknown;
        if (t instanceof IOException) {
            stringErrorRes = R.string.error_connection_timeout;
        } else if (t instanceof IllegalStateException) {
            stringErrorRes = R.string.error_conversion;
        }

        Log.e(TAG, "Failure error due to " + t.getMessage(), t);

        new Toasty(context, v, stringErrorRes);
    }
}
