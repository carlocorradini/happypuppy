package com.unitn.disi.lpsmt.happypuppy.util;

import android.os.AsyncTask;

/**
 * Listener used within {@link AsyncTask} when {@link AsyncTask#onPostExecute(T)} is called.
 *
 * @param <T> Type of the completed {@link AsyncTask} parameter
 * @author Carlo Corradini
 */
public interface OnTaskCompleted<T> {
    /**
     * Listener callback when {@link AsyncTask#onPostExecute(T)} is called.
     *
     * @param result The result of the {@link AsyncTask#onPostExecute(T)}
     */
    void onTaskCompleted(T result);
}
