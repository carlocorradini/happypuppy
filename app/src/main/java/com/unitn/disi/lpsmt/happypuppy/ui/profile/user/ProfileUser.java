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
import com.unitn.disi.lpsmt.happypuppy.Launcher;
import com.unitn.disi.lpsmt.happypuppy.R;
import com.unitn.disi.lpsmt.happypuppy.api.API;
import com.unitn.disi.lpsmt.happypuppy.api.AuthManager;
import com.unitn.disi.lpsmt.happypuppy.api.entity.User;
import com.unitn.disi.lpsmt.happypuppy.api.entity.UserFriend;
import com.unitn.disi.lpsmt.happypuppy.api.service.UserFriendService;
import com.unitn.disi.lpsmt.happypuppy.api.service.UserService;
import com.unitn.disi.lpsmt.happypuppy.ui.profile.puppy.RegisterPuppy;
import com.unitn.disi.lpsmt.happypuppy.util.ImageUtil;
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
    Button changeAvatar;
    TextView numberFriends;
    TextView numberPuppies;
    LinearLayout buttonsUser;
    LinearLayout buttonsVisit;
    Button button1;
    Button button2;

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
        changeAvatar = findViewById(R.id.profile_user_button_image);
        numberFriends = findViewById(R.id.profile_user_number_friends);
        numberPuppies = findViewById(R.id.profile_user_number_puppies);

        buttonsUser = findViewById(R.id.profile_user_buttons_profile);
        buttonsVisit = findViewById(R.id.profile_user_buttons_visit);

        uuid = UUID.fromString(getIntent().getStringExtra("uuid_user"));
        friendship = new UserFriend();

        if (AuthManager.getInstance().isCurrentAuthUser(uuid)) {

            loadData();

            buttonsUser.setVisibility(View.VISIBLE);
            buttonsVisit.setVisibility(View.GONE);
            changeAvatar.setVisibility(View.VISIBLE);

            button1 = findViewById(R.id.profile_user_button_edit_profile);
            button2 = findViewById(R.id.profile_user_button_add_puppy);

            changeAvatar.setOnClickListener(this::showFileChooser);

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
                        new ImageUtil.DownloadImage(avatar -> {
                            if (avatar == null) return;
                            userAvatar = Bitmap.createScaledBitmap(avatar, USER_MARKER_SIZE.getLeft(), USER_MARKER_SIZE.getRight(), false);
                            userAvatarView.setImageBitmap(avatar);
                        }).execute(user.avatar);
                        numberPuppies.setText(String.valueOf(user.puppies.size()));
                        if(user.friends != null)
                            numberFriends.setText(String.valueOf(user.friends));
                        else
                            numberFriends.setText("0");
                        usernameTop.setText(user.username);
                        Log.e(TAG, "Successfully downloaded current visited User");
                    } else {
                        Log.e(TAG, "Unable to download current visited User due to failure response " + response.code() + "received");
                    }
                }

                @Override
                public void onFailure(@NotNull Call<API.Response<User>> call, @NotNull Throwable t) {
                    Log.e(TAG, "Unable to download current visited User due to " + t.getMessage(), t);
                }
            });

            buttonsUser.setVisibility(View.GONE);
            buttonsVisit.setVisibility(View.VISIBLE);
            changeAvatar.setVisibility(View.GONE);
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
            this.user = user;
            new ImageUtil.DownloadImage(avatar -> {
                if (avatar == null) return;
                userAvatar = Bitmap.createScaledBitmap(avatar, USER_MARKER_SIZE.getLeft(), USER_MARKER_SIZE.getRight(), false);
                this.userAvatarView.setImageBitmap(avatar);
            }).execute(user.avatar);
            this.numberPuppies.setText(String.valueOf(this.user.puppies.size()));
            if(this.user.friends != null)
                this.numberFriends.setText(String.valueOf(this.user.friends));
            else
                this.numberFriends.setText("0");
            this.usernameTop.setText(this.user.username);
        }).execute();
    }

    public void viewFriendship(){
        Call<API.Response<UserFriend>> call = API.getInstance().getClient().create(UserFriendService.class).findById(uuid);
        call.enqueue(new Callback<API.Response<UserFriend>>() {
            @Override
            public void onResponse(@NotNull Call<API.Response<UserFriend>> call, @NotNull Response<API.Response<UserFriend>> response) {
                if (response.isSuccessful() && response.body() != null) {
                     friendship = response.body().data;
                }else if(response.code() == HttpStatus.SC_NOT_FOUND){
                    Log.i(TAG,"Didn't found a friendship");
                    friendship.type = null;
                }
                setButtons(friendship);
            }

            @Override
            public void onFailure(@NotNull Call<API.Response<UserFriend>> call, @NotNull Throwable t) {
                Log.e(TAG, "Unable to get the friendship " + t.getMessage(), t);
            }
        });
    }
    public void addFriend(){
        friendship.friend = uuid;
        Call<API.Response<UUID>> call = API.getInstance().getClient().create(UserFriendService.class).create(friendship);
        call.enqueue(new Callback<API.Response<UUID>>() {
            @Override
            public void onResponse(@NotNull Call<API.Response<UUID>> call, @NotNull Response<API.Response<UUID>> response) {
                if(response.isSuccessful() && response.body() != null){
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

    public void removeFriend(){
        AlertDialog alertDialog = new AlertDialog.Builder(ProfileUser.this).create();
        alertDialog.setTitle(getResources().getString(R.string.remove_friend));
        alertDialog.setMessage(getResources().getString(R.string.remove_friend)+"?");
        alertDialog.setIcon(R.drawable.ic_add_friend);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getResources().getString(R.string.confirm),
                (dialog, which) -> {
                    Call<API.Response> delete = API.getInstance().getClient().create(UserFriendService.class).delete(uuid);
                    delete.enqueue(new Callback<API.Response>() {
                        @Override
                        public void onResponse(@NotNull Call<API.Response> call, @NotNull Response<API.Response> response) {
                            if(response.isSuccessful()){
                                viewFriendship();
                                reload();
                            }
                        }

                        @Override
                        public void onFailure(@NotNull Call<API.Response> call, @NotNull Throwable t) {
                            Log.e(TAG, "Unable to remove friend due to failure response " + t.getMessage() + "received");
                        }
                    });
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getResources().getString(R.string.dismiss),
                (dialog, which) -> alertDialog.dismiss());
        alertDialog.show();
    }
    public void cancelFriendRequest(){
        AlertDialog alertDialog = new AlertDialog.Builder(ProfileUser.this).create();
        alertDialog.setTitle(getResources().getString(R.string.remove_request_friend));
        alertDialog.setMessage(getResources().getString(R.string.remove_request_friend)+"?");
        alertDialog.setIcon(R.drawable.ic_add_friend);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getResources().getString(R.string.confirm),
                (dialog, which) -> {
                    Call<API.Response> delete = API.getInstance().getClient().create(UserFriendService.class).delete(uuid);
                    delete.enqueue(new Callback<API.Response>() {
                        @Override
                        public void onResponse(@NotNull Call<API.Response> call, @NotNull Response<API.Response> response) {
                            if(response.isSuccessful()){
                                viewFriendship();
                                reload();
                            }
                        }

                        @Override
                        public void onFailure(@NotNull Call<API.Response> call, @NotNull Throwable t) {
                            Log.e(TAG, "Unable to cancel friend request due to failure response " + t.getMessage() + "received");
                        }
                    });
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getResources().getString(R.string.dismiss),
                (dialog, which) -> alertDialog.dismiss());
        alertDialog.show();
    }

    public void setButtons(UserFriend friendship){
        if(friendship != null && friendship.type != null) {
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
        }else{
            button1.setVisibility(View.VISIBLE);
            button2.setVisibility(View.GONE);
            button1.setOnClickListener(v -> {
                addFriend();
            });
        }
    }

    /* Open FileChooser Dialog */
    public void showFileChooser(View arg0) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);

        // Prende tutti i file indistintamente dalla tipologia
        intent.setType("image/*");

        // In questo modo vengono visualizzati solamente i file presenti in locale. Volendo si puo' anche integrare la ricerca su Google Drive, ma per ora la lascerei fuori.
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);

        // Volendo si possono impostare dei filtri sulle tipologie dei file. Per essere generici mantieni pure lo 0
        int REQUEST_CODE = 0;
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Ritorna se l'utente non ha selezionato nulla
        if (requestCode != REQUEST_CODE || resultCode != RESULT_OK) {
            return;
        }
        // Importa il file
        try {
            importFile(data.getData());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void importFile(Uri uri) throws IOException {
        String fileName = getFileName(uri);
        // Gestione del file temporaneo
        File tmp_file = new File(fileName);
        // File fileCopy = copyToTempFile(uri, tmp_file);
        // Done!
    }

    /**
     * Gestisce il file temporaneo
     *
     * @param uri
     * @param tempFile
     * @return il file temporaneo
     * @throws IOException se accadono errori
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private File copyToTempFile(Uri uri, File tempFile) throws IOException {
        // Ottiene l'input stream del file
        InputStream inputStream = getContentResolver().openInputStream(uri);
        OutputStream outStream = null;
        if (inputStream == null) {
            throw new IOException("Unable to obtain input stream from URI");
        }

        // Copia lo stream sul file temporaneo
        assert outStream != null;
        FileUtils.copy(inputStream, outStream);

        return tempFile;
    }

    /**
     * Ottiene il nome del file. Maggiori informazioni qui
     * https://developer.android.com/training/secure-file-sharing/retrieve-info.html#RetrieveFileInfo
     *
     * @param uri
     * @return il nome del file senza il path
     * @throws IllegalArgumentException
     */
    private String getFileName(Uri uri) throws IllegalArgumentException {
        // Ottiene il cursore della risorsa
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);

        assert cursor != null;
        if (cursor.getCount() <= 0) {
            cursor.close();
            throw new IllegalArgumentException("Can't obtain file name, cursor is empty");
        }
        cursor.moveToFirst();
        String fileName = cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME));
        cursor.close();
        return fileName;
    }

    public void reload(){
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }
}
