package com.unitn.disi.lpsmt.happypuppy.ui.profile.puppy;

import android.content.Intent;
import android.database.Cursor;
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
import androidx.appcompat.app.AppCompatActivity;

import com.unitn.disi.lpsmt.happypuppy.R;
import com.unitn.disi.lpsmt.happypuppy.api.API;
import com.unitn.disi.lpsmt.happypuppy.api.AuthManager;
import com.unitn.disi.lpsmt.happypuppy.api.entity.Puppy;
import com.unitn.disi.lpsmt.happypuppy.api.service.PuppyService;
import com.unitn.disi.lpsmt.happypuppy.helper.ErrorHelper;

import org.apache.http.HttpStatus;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfilePuppy extends AppCompatActivity {
    /**
     * {@link Log} TAG of this class
     */
    private static final String TAG = ProfilePuppy.class.getName();
    private static final int REQUEST_CODE = 6384;

    Button buttonBack;
    TextView nameTop;
    ImageView puppyAvatar;
    Button changeAvatar;
    TextView numberFriends;
    TextView usernameOwner;
    LinearLayout buttonsUser;
    LinearLayout buttonsVisit;

    UUID thisPuppyOwner;
    Puppy thisPuppy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_puppy_activity);

        buttonBack = findViewById(R.id.profile_puppy_button_back);
        nameTop = findViewById(R.id.profile_puppy_name);
        changeAvatar = findViewById(R.id.profile_puppy_button_image);
        puppyAvatar = findViewById(R.id.profile_puppy_profile_image);
        numberFriends = findViewById(R.id.profile_puppy_number_friends);
        usernameOwner = findViewById(R.id.profile_puppy_user_owner);

        buttonsUser = findViewById(R.id.profile_puppy_buttons_profile);
        buttonsVisit = findViewById(R.id.profile_puppy_buttons_visit);

        Long id_puppy = Long.parseLong(Objects.requireNonNull(getIntent().getStringExtra("id_puppy")));

        Call<API.Response<Puppy>> call = API.getInstance().getClient().create(PuppyService.class).findById(id_puppy);

        call.enqueue(new Callback<API.Response<Puppy>>() {
            @Override
            public void onResponse(@NotNull Call<API.Response<Puppy>> call, @NotNull Response<API.Response<Puppy>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    thisPuppy = response.body().data;
                    thisPuppyOwner = response.body().data.user;
                } else if (response.code() == HttpStatus.SC_NOT_FOUND) {
                    Log.i(TAG, "Didn't found this puppy ");
                } else {
                    Log.i(TAG, "Unknown error due to " + response.code());
                }
            }

            @Override
            public void onFailure(@NotNull Call<API.Response<Puppy>> call, @NotNull Throwable t) {
                ErrorHelper.showFailureError(getBaseContext(), ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0), t);
            }
        });

        if (AuthManager.getInstance().getAuthUserId() == thisPuppyOwner) {
            buttonsUser.setVisibility(View.VISIBLE);
            buttonsVisit.setVisibility(View.GONE);
            changeAvatar.setVisibility(View.VISIBLE);

            changeAvatar.setOnClickListener(this::showFileChooser);
        } else {
            buttonsUser.setVisibility(View.GONE);
            buttonsVisit.setVisibility(View.VISIBLE);
            changeAvatar.setVisibility(View.GONE);
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
}
