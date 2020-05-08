package com.unitn.disi.lpsmt.happypuppy.ui.profile.user;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.FileUtils;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.unitn.disi.lpsmt.happypuppy.Launcher;
import com.unitn.disi.lpsmt.happypuppy.R;
import com.unitn.disi.lpsmt.happypuppy.api.API;
import com.unitn.disi.lpsmt.happypuppy.api.AuthManager;
import com.unitn.disi.lpsmt.happypuppy.api.entity.User;
import com.unitn.disi.lpsmt.happypuppy.api.service.UserService;
import com.unitn.disi.lpsmt.happypuppy.ui.profile.puppy.RegisterPuppy;
import com.unitn.disi.lpsmt.happypuppy.util.ImageUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import org.apache.http.HttpStatus;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class ProfileUser extends AppCompatActivity {
    private static final int REQUEST_CODE = 6384;

    Button buttonBack;
    TextView usernameTop;
    ImageView buttonSignOut;
    ImageView userAvatar;
    Button changeAvatar;
    TextView numberFriends;
    TextView numberPuppies;
    LinearLayout buttonsUser;
    LinearLayout buttonsVisit;
    Button button1;
    Button button2;

    User userLogged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_user_activity);


        buttonBack = findViewById(R.id.profile_user_button_back);
        usernameTop = findViewById(R.id.profile_user_username);
        buttonSignOut = findViewById(R.id.profile_user_sign_out);
        userAvatar = findViewById(R.id.profile_user_profile_image);
        changeAvatar = findViewById(R.id.profile_user_button_image);
        numberFriends = findViewById(R.id.profile_user_number_friends);
        numberPuppies = findViewById(R.id.profile_user_number_puppies);

        buttonsUser = findViewById(R.id.profile_user_buttons_profile);
        buttonsVisit = findViewById(R.id.profile_user_buttons_visit);

        UUID uuid = UUID.fromString(getIntent().getStringExtra("uuid_user"));

        if (AuthManager.getInstance().isCurrentAuthUser(uuid)) {
            buttonsUser.setVisibility(View.VISIBLE);
            buttonsVisit.setVisibility(View.GONE);
            changeAvatar.setVisibility(View.VISIBLE);

            button1 = findViewById(R.id.profile_user_button_edit_profile);
            button2 = findViewById(R.id.profile_user_button_add_puppy);

            changeAvatar.setOnClickListener(this::showFileChooser);

            button1.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), EditUser.class);
                intent.putExtra("uuid_user", AuthManager.getInstance().getAuthUserId().toString());
                startActivity(intent);
            });
            button2.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), RegisterPuppy.class);
                intent.putExtra("uuid_user", AuthManager.getInstance().getAuthUserId().toString());
                startActivity(intent);
            });
        } else {
            buttonsUser.setVisibility(View.GONE);
            buttonsVisit.setVisibility(View.VISIBLE);
            changeAvatar.setVisibility(View.GONE);

            button1 = findViewById(R.id.profile_user_button_friendship);
            button2 = findViewById(R.id.profile_user_button_view_puppies);

            button1.setOnClickListener(v -> {

            });
            button2.setOnClickListener(v -> {

            });
        }

        Call<API.Response<User>> call = API.getInstance().getClient().create(UserService.class).findById(uuid);
        call.enqueue(new Callback<API.Response<User>>() {
            @Override
            public void onResponse(@NotNull Call<API.Response<User>> call, @NotNull Response<API.Response<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    userLogged = response.body().data;
                    new ImageUtil.DownloadImage(avatar -> {
                        if (avatar == null) return;
                        userAvatar.setImageBitmap(avatar);
                    }).execute(userLogged.avatar);
                    usernameTop.setText(userLogged.username);

                } else if (response.errorBody() != null) {
                    switch (response.code()) {
                        case HttpStatus.SC_NOT_FOUND: {
                            new com.unitn.disi.lpsmt.happypuppy.ui.components.Toast(getApplicationContext(),
                                    getWindow().getDecorView().findViewById(android.R.id.content),
                                    getResources().getString(R.string.user_not_found));
                            break;
                        }
                        default:
                            new com.unitn.disi.lpsmt.happypuppy.ui.components.Toast(getApplicationContext(),
                                    getWindow().getDecorView().findViewById(android.R.id.content),
                                    getResources().getString(R.string.internal_server_error));
                            break;
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<API.Response<User>> call, @NotNull Throwable t) {
                new com.unitn.disi.lpsmt.happypuppy.ui.components.Toast(getApplicationContext(),
                        getWindow().getDecorView().findViewById(android.R.id.content),
                        getResources().getString(R.string.no_internet));
            }
        });

        buttonBack.setOnClickListener(v -> {
            finish();
        });
        buttonSignOut.setOnClickListener(v -> {
            AlertDialog alertDialog = new AlertDialog.Builder(ProfileUser.this).create();
            alertDialog.setTitle(getResources().getString(R.string.sign_out));
            alertDialog.setMessage(getResources().getString(R.string.sign_out_confirm));
            alertDialog.setIcon(R.drawable.ic_sign_out);
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getResources().getString(R.string.confirm),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            AuthManager.getInstance().clearToken();
                            Intent intent = new Intent(v.getContext(), Launcher.class);
                            startActivity(intent);
                            finish();
                        }
                    });
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getResources().getString(R.string.dismiss),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog.dismiss();
                        }
                    });
            alertDialog.show();
        });
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
}
