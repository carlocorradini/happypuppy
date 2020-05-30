package com.unitn.disi.lpsmt.happypuppy.util;

import android.os.AsyncTask;
import android.util.Log;

import com.unitn.disi.lpsmt.happypuppy.api.API;
import com.unitn.disi.lpsmt.happypuppy.api.entity.User;
import com.unitn.disi.lpsmt.happypuppy.api.entity.UserFriend;
import com.unitn.disi.lpsmt.happypuppy.api.service.UserFriendService;
import com.unitn.disi.lpsmt.happypuppy.api.service.UserService;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import retrofit2.Response;

/**
 * {@link InfoCardView} utility class
 *
 * @author Anthony Farina
 */
public final class CardsUtil {
    /**
     * {@link Log} TAG of this class
     */
    private static final String TAG = CardsUtil.class.getName();

    /**
     * Download generice user possible friend {@link User} within an {@link AsyncTask} in background.
     * This class must be constructed with a {@link OnTaskCompleted<User> listener} function when
     * the {@link User} has been downloaded.
     * The downloaded {@link User} can be null if an error occurs.
     *
     * @author Anthony Farina
     */
    public static final class DownloadCardUser extends AsyncTask<UUID, Void, User> {
        /**
         * {@link Log} TAG of this class
         */
        private static final String TAG = DownloadCardUser.class.getName();

        /**
         * Callback listener called when the download operation has finished
         */
        private OnTaskCompleted<User> listener;

        /**
         * Construct a {@link DownloadCardUser} class with a finish callback {@link OnTaskCompleted<User> listener}
         *
         * @param listener The callback listener called when the {@link User} has been download
         */
        public DownloadCardUser(OnTaskCompleted<User> listener) {
            this.listener = listener;
        }

        @Override
        protected User doInBackground(UUID... uuids) {
            User user = null;

            try {
                Response<API.Response<User>> response = API.getInstance().getClient().create(UserService.class).findById(uuids[0]).execute();
                if (response.isSuccessful() && response.body() != null) {
                    user = response.body().data;
                    Log.i(TAG, "Successfully downloaded possible Friend");
                } else {
                    Log.e(TAG, "Unable to download this possible Friend due to failure response " + response.code() + "received");
                }
            } catch (IOException e) {
                Log.e(TAG, "Unable to download this possible Friend due to " + e.getMessage(), e);
            }

            return user;
        }

        @Override
        protected void onPostExecute(User user) {
            listener.onTaskCompleted(user);
        }
    }

    /**
     * Download generice user possible friend {@link User} within an {@link AsyncTask} in background.
     * This class must be constructed with a {@link OnTaskCompleted<User> listener} function when
     * the {@link User} has been downloaded.
     * The downloaded {@link User} can be null if an error occurs.
     *
     * @author Anthony Farina
     */
    public static final class DownloadFriendRequests extends AsyncTask<Void, Void, List<UserFriend>> {
        /**
         * {@link Log} TAG of this class
         */
        private static final String TAG = DownloadFriendRequests.class.getName();

        /**
         * Callback listener called when the download operation has finished
         */
        private OnTaskCompleted<List<UserFriend>> listener;

        /**
         * Construct a {@link DownloadFriendRequests} class with a finish callback {@link OnTaskCompleted<List<UserFriend>> listener}
         *
         * @param listener The callback listener called when the {@link List<UserFriend>} has been download
         */
        public DownloadFriendRequests(OnTaskCompleted<List<UserFriend>> listener) {
            this.listener = listener;
        }

        @Override
        protected List<UserFriend> doInBackground(Void... voids) {
            List<UserFriend> users = null;

            try {
                Response<API.Response<List<UserFriend>>> response = API.getInstance().getClient().create(UserFriendService.class).find().execute();
                if (response.isSuccessful() && response.body() != null) {
                    users = response.body().data;
                    Log.i(TAG, "Successfully downloaded possible Friends");
                } else {
                    Log.e(TAG, "Unable to download all possible Friends due to failure response " + response.code() + "received");
                }
            } catch (IOException e) {
                Log.e(TAG, "Unable to download all possible Friends due to " + e.getMessage(), e);
            }

            return users;
        }

        /**
         *
         * @param friend friend
         */
        @Override
        protected void onPostExecute(List<UserFriend> friend) {
            listener.onTaskCompleted(friend);
        }
    }
}
