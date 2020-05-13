package com.unitn.disi.lpsmt.happypuppy.util;

import android.os.AsyncTask;
import android.util.Log;

import com.unitn.disi.lpsmt.happypuppy.api.API;
import com.unitn.disi.lpsmt.happypuppy.api.entity.User;
import com.unitn.disi.lpsmt.happypuppy.api.service.UserService;

import java.io.IOException;

import retrofit2.Response;

/**
 * {@link User} utility class
 *
 * @author Carlo Corradini
 */
public final class UserUtil {
    /**
     * {@link Log} TAG of this class
     */
    private static final String TAG = UserUtil.class.getName();

    /**
     * Download current authenticated {@link User} within an {@link AsyncTask} in background.
     * This class must be constructed with a {@link OnTaskCompleted<User> listener} function when
     * the {@link User} has been downloaded.
     * The downloaded {@link User} can be null if an error occurs.
     *
     * @author Carlo Corradini
     */
    public static final class DownloadAuthUser extends AsyncTask<Void, Void, User> {
        /**
         * {@link Log} TAG of this class
         */
        private static final String TAG = DownloadAuthUser.class.getName();

        /**
         * Callback listener called when the download operation has finished
         */
        private OnTaskCompleted<User> listener;

        /**
         * Construct a {@link DownloadAuthUser} class with a finish callback {@link OnTaskCompleted<User> listener}
         *
         * @param listener The callback listener called when the {@link User} has been download
         */
        public DownloadAuthUser(OnTaskCompleted<User> listener) {
            this.listener = listener;
        }

        @Override
        protected User doInBackground(Void... voids) {
            User user = null;

            try {
                Response<API.Response<User>> response = API.getInstance().getClient().create(UserService.class).me().execute();
                if (response.isSuccessful() && response.body() != null) {
                    user = response.body().data;
                    Log.i(TAG, "Successfully downloaded current authenticated User");
                } else {
                    Log.e(TAG, "Unable to download current authenticated User due to failure response " + response.code() + "received");
                }
            } catch (IOException e) {
                Log.e(TAG, "Unable to download current authenticated User due to " + e.getMessage(), e);
            }

            return user;
        }

        @Override
        protected void onPostExecute(User user) {
            listener.onTaskCompleted(user);
        }
    }
}
