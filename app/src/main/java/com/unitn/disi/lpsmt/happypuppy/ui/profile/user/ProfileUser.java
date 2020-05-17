package com.unitn.disi.lpsmt.happypuppy.ui.profile.user;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.FileUtils;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Picasso;
import com.unitn.disi.lpsmt.happypuppy.Launcher;
import com.unitn.disi.lpsmt.happypuppy.R;
import com.unitn.disi.lpsmt.happypuppy.api.API;
import com.unitn.disi.lpsmt.happypuppy.api.AuthManager;
import com.unitn.disi.lpsmt.happypuppy.api.entity.User;
import com.unitn.disi.lpsmt.happypuppy.api.entity.UserFriend;
import com.unitn.disi.lpsmt.happypuppy.api.service.UserFriendService;
import com.unitn.disi.lpsmt.happypuppy.api.service.UserService;
import com.unitn.disi.lpsmt.happypuppy.helper.ErrorHelper;
import com.unitn.disi.lpsmt.happypuppy.ui.profile.puppy.RegisterPuppy;
import com.unitn.disi.lpsmt.happypuppy.util.UserUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.HttpStatus;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class ProfileUser extends AppCompatActivity {
    private static final int REQUEST_CODE = 6384;

    /**
     * {@link User} {@link Marker} size
     */
    private static final Pair<Integer, Integer> USER_MARKER_SIZE = Pair.of(128, 128);
    /**
     * {@link Log} TAG of this class
     */
    private static final String TAG = ProfileUser.class.getName();

    Button buttonBack;
    TextView usernameTop;
    ImageView buttonSignOut;
    ImageView userAvatarView;
    Bitmap userAvatar;
    TextView numberFriends;
    TextView numberPuppies;
    LinearLayout buttonsUser;
    LinearLayout buttonsVisit;
    Button button1;
    Button button2;

    TextView fullName;
    TextView gender;
    TextView birthDate;

    User user;
    UUID uuid;
    UserFriend friendship;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_user_activity);


        buttonBack = findViewById(R.id.profile_user_button_back);
        usernameTop = findViewById(R.id.profile_user_username);
        buttonSignOut = findViewById(R.id.profile_user_sign_out);
        userAvatarView = findViewById(R.id.profile_user_profile_image);
        numberFriends = findViewById(R.id.profile_user_number_friends);
        numberPuppies = findViewById(R.id.profile_user_number_puppies);

        fullName = findViewById(R.id.profile_user_info_full_name);
        gender = findViewById(R.id.profile_user_info_gender);
        birthDate = findViewById(R.id.profile_user_info_dateOfBirth);

        buttonsUser = findViewById(R.id.profile_user_buttons_profile);
        buttonsVisit = findViewById(R.id.profile_user_buttons_visit);

        uuid = UUID.fromString(getIntent().getStringExtra("uuid_user"));
        friendship = new UserFriend();

        if (AuthManager.getInstance().isCurrentAuthUser(uuid)) {

            loadData();

            buttonsUser.setVisibility(View.VISIBLE);
            buttonsVisit.setVisibility(View.GONE);

            button1 = findViewById(R.id.profile_user_button_edit_profile);
            button2 = findViewById(R.id.profile_user_button_add_puppy);

            button1.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), EditUser.class);
                startActivity(intent);
            });
            button2.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), RegisterPuppy.class);
                intent.putExtra("uuid_user", AuthManager.getInstance().getAuthUserId().toString());
                startActivity(intent);
            });
        } else {
            //Load data of visited user
            Call<API.Response<User>> call = API.getInstance().getClient().create(UserService.class).findById(uuid);
            call.enqueue(new Callback<API.Response<User>>() {
                @Override
                public void onResponse(@NotNull Call<API.Response<User>> call, @NotNull Response<API.Response<User>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        user = response.body().data;
                        Picasso.get().load(String.valueOf(user.avatar)).into(userAvatarView);
                        numberPuppies.setText(String.valueOf(user.puppies.size()));
                        if (user.friends != null)
                            numberFriends.setText(String.valueOf(user.friends));
                        else
                            numberFriends.setText("0");
                        if(user.gender != null && user.gender == User.Gender.MALE) {
                            gender.setText(getResources().getString(R.string.male));
                            gender.setVisibility(View.VISIBLE);
                        }
                        else if( user.gender != null && user.gender == User.Gender.FEMALE) {
                            gender.setText(getResources().getString(R.string.female));
                            gender.setVisibility(View.VISIBLE);
                        }
                        if(user.name != null && user.surname != null)
                            fullName.setText(user.name.concat(" ").concat(user.surname));
                        if(user.dateOfBirth != null)
                            birthDate.setText(user.dateOfBirth.toString());
                        usernameTop.setText(user.username);
                        Log.e(TAG, "Successfully downloaded current visited User");
                    } else {
                        Log.e(TAG, "Unable to download current visited User due to failure response " + response.code() + "received");
                    }
                }

                @Override
                public void onFailure(@NotNull Call<API.Response<User>> call, @NotNull Throwable t) {
                    ErrorHelper.showFailureError(getBaseContext(), ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0), t);
                }
            });

            buttonsUser.setVisibility(View.GONE);
            buttonsVisit.setVisibility(View.VISIBLE);
            buttonSignOut.setVisibility(View.GONE);

            button1 = findViewById(R.id.profile_user_button_friendship);
            button2 = findViewById(R.id.profile_user_button_view_puppies);

            viewFriendship();
        }

        buttonBack.setOnClickListener(v -> {
            finish();
        });
        buttonSignOut.setOnClickListener(v -> {
            AlertDialog alertDialog = new AlertDialog.Builder(ProfileUser.this).create();
            alertDialog.setTitle(getResources().getString(R.string.sign_out));
            alertDialog.setMessage(getResources().getString(R.string.sign_out_confirm));
            alertDialog.setIcon(R.drawable.ic_sign_out);
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getResources().getString(R.string.confirm),
                    (dialog, which) -> {
                        AuthManager.getInstance().clearToken();
                        Intent intent = new Intent(v.getContext(), Launcher.class);
                        startActivity(intent);
                        finish();
                    });
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getResources().getString(R.string.dismiss),
                    (dialog, which) -> alertDialog.dismiss());
            alertDialog.show();
        });
    }

    /**
     * Load/Download useful data used in the current {@link ActivityCompat activity} and {@link GoogleMap map}.
     * It can be called once.
     */
    private void loadData() {
        if (user != null && userAvatar != null) return;

        new UserUtil.DownloadAuthUser(user -> {
            if(user == null) return;
            this.user = user;
            Picasso.get().load(String.valueOf(this.user.avatar)).into(this.userAvatarView);
            this.numberPuppies.setText(String.valueOf(this.user.puppies.size()));

            this.numberFriends.setText(String.valueOf(this.user.friends));
            if(user.gender == User.Gender.MALE) {
                gender.setText(getResources().getString(R.string.male));
            }
            else if(user.gender == User.Gender.FEMALE) {
                gender.setText(getResources().getString(R.string.female));
            }
            if(user.name != null && user.surname != null){
                fullName.setText(user.name.concat(" ").concat(user.surname));
            }
            if(user.dateOfBirth != null) {
                birthDate.setText(user.dateOfBirth.toString());
            }
            this.usernameTop.setText(this.user.username);
        }).execute();
    }

    public void viewFriendship() {
        Call<API.Response<UserFriend>> call = API.getInstance().getClient().create(UserFriendService.class).findById(uuid);
        call.enqueue(new Callback<API.Response<UserFriend>>() {
            @Override
            public void onResponse(@NotNull Call<API.Response<UserFriend>> call, @NotNull Response<API.Response<UserFriend>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    friendship = response.body().data;
                } else if (response.code() == HttpStatus.SC_NOT_FOUND) {
                    Log.i(TAG, "Didn't found a friendship");
                    friendship.type = null;
                }
                setButtons(friendship);
            }

            @Override
            public void onFailure(@NotNull Call<API.Response<UserFriend>> call, @NotNull Throwable t) {
                ErrorHelper.showFailureError(getBaseContext(), ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0), t);
            }
        });
    }

    public void addFriend() {
        friendship.friend = uuid;
        Call<API.Response<UUID>> call = API.getInstance().getClient().create(UserFriendService.class).create(friendship);
        call.enqueue(new Callback<API.Response<UUID>>() {
            @Override
            public void onResponse(@NotNull Call<API.Response<UUID>> call, @NotNull Response<API.Response<UUID>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    viewFriendship();
                    reload();
                }
            }

            @Override
            public void onFailure(@NotNull Call<API.Response<UUID>> call, @NotNull Throwable t) {
                Log.e(TAG, "Unable to add friend due to " + t.getMessage(), t);
            }
        });
    }
    public void acceptFriendRequest() {
        friendship.friend = uuid;
        UserFriend accepted = new UserFriend();
        accepted.type = UserFriend.Type.FRIEND;
        Call<API.Response> call = API.getInstance().getClient().create(UserFriendService.class).update(friendship.friend, accepted);
        call.enqueue(new Callback<API.Response>() {
            @Override
            public void onResponse(@NotNull Call<API.Response> call, @NotNull Response<API.Response> response) {
                if (response.isSuccessful() && response.body() != null) {
                    viewFriendship();
                    reload();
                }
            }

            @Override
            public void onFailure(@NotNull Call<API.Response> call, @NotNull Throwable t) {
                Log.e(TAG, "Unable to add friend due to " + t.getMessage(), t);
            }
        });
    }

    public void removeFriend() {
        AlertDialog alertDialog = new AlertDialog.Builder(ProfileUser.this).create();
        alertDialog.setTitle(getResources().getString(R.string.remove_friend));
        alertDialog.setMessage(getResources().getString(R.string.remove_friend) + "?");
        alertDialog.setIcon(R.drawable.ic_add_friend);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getResources().getString(R.string.confirm),
                (dialog, which) -> {
                    Call<API.Response> delete = API.getInstance().getClient().create(UserFriendService.class).delete(uuid);
                    delete.enqueue(new Callback<API.Response>() {
                        @Override
                        public void onResponse(@NotNull Call<API.Response> call, @NotNull Response<API.Response> response) {
                            if (response.isSuccessful()) {
                                viewFriendship();
                                reload();
                            }
                        }

                        @Override
                        public void onFailure(@NotNull Call<API.Response> call, @NotNull Throwable t) {
                            ErrorHelper.showFailureError(getBaseContext(), ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0), t);
                        }
                    });
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getResources().getString(R.string.dismiss),
                (dialog, which) -> alertDialog.dismiss());
        alertDialog.show();
    }

    public void cancelFriendRequest() {
        AlertDialog alertDialog = new AlertDialog.Builder(ProfileUser.this).create();
        alertDialog.setTitle(getResources().getString(R.string.remove_request_friend));
        alertDialog.setMessage(getResources().getString(R.string.remove_request_friend) + "?");
        alertDialog.setIcon(R.drawable.ic_add_friend);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getResources().getString(R.string.confirm),
                (dialog, which) -> {
                    Call<API.Response> delete = API.getInstance().getClient().create(UserFriendService.class).delete(uuid);
                    delete.enqueue(new Callback<API.Response>() {
                        @Override
                        public void onResponse(@NotNull Call<API.Response> call, @NotNull Response<API.Response> response) {
                            if (response.isSuccessful()) {
                                viewFriendship();
                                reload();
                            }
                        }

                        @Override
                        public void onFailure(@NotNull Call<API.Response> call, @NotNull Throwable t) {
                            ErrorHelper.showFailureError(getBaseContext(), ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0), t);
                        }
                    });
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getResources().getString(R.string.dismiss),
                (dialog, which) -> alertDialog.dismiss());
        alertDialog.show();
    }

    public void setButtons(UserFriend friendship) {
        if (friendship != null && friendship.type != null) {
            switch (friendship.type) {
                case FRIEND:
                    Log.i(TAG, "We are friends");
                    button1.setText(getResources().getString(R.string.remove_friend));
                    button1.setVisibility(View.VISIBLE);
                    button2.setVisibility(View.VISIBLE);
                    button1.setOnClickListener(v -> {
                        removeFriend();
                    });
                    button2.setOnClickListener(v -> {
                        /* View friend's puppies */
                    });
                    break;
                case BLOCKED:
                    finish();
                    Log.i(TAG, "You want to avoid me");
                    break;
                case FRIEND_REQUEST:
                    Log.i(TAG, "I'm thinking about");
                    button2.setText(getResources().getString(R.string.remove_request));
                    button2.setVisibility(View.VISIBLE);
                    button1.setText(getResources().getString(R.string.accept_request));
                    button1.setVisibility(View.VISIBLE);
                    button1.setOnClickListener(v -> {
                        acceptFriendRequest();
                    });
                    button2.setOnClickListener(v -> {
                        removeFriend();
                    });
                    break;
                case WAITING_ACCEPTANCE:
                    Log.i(TAG, "I'm waiting for you");
                    button2.setVisibility(View.GONE);
                    button1.setText(getResources().getString(R.string.remove_request_friend));
                    button1.setTextAppearance(R.style.Button_Colored_Tertiary);
                    button1.setVisibility(View.VISIBLE);
                    button1.setOnClickListener(v -> {
                        cancelFriendRequest();
                    });
                    break;
                default:
                    button1.setVisibility(View.VISIBLE);
                    button2.setVisibility(View.GONE);
                    button1.setOnClickListener(v -> {
                        addFriend();
                    });
                    break;
            }
        } else {
            button1.setVisibility(View.VISIBLE);
            button2.setVisibility(View.GONE);
            button1.setOnClickListener(v -> {
                addFriend();
            });
        }
    }

    public void reload() {
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }
}
