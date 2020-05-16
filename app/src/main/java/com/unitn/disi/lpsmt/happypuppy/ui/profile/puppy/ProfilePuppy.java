package com.unitn.disi.lpsmt.happypuppy.ui.profile.puppy;

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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.squareup.picasso.Picasso;
import com.unitn.disi.lpsmt.happypuppy.R;
import com.unitn.disi.lpsmt.happypuppy.api.API;
import com.unitn.disi.lpsmt.happypuppy.api.AuthManager;
import com.unitn.disi.lpsmt.happypuppy.api.entity.Puppy;
import com.unitn.disi.lpsmt.happypuppy.api.entity.User;
import com.unitn.disi.lpsmt.happypuppy.api.service.PuppyService;
import com.unitn.disi.lpsmt.happypuppy.helper.ErrorHelper;
import com.unitn.disi.lpsmt.happypuppy.ui.HomePage;
import com.unitn.disi.lpsmt.happypuppy.ui.profile.user.ProfileUser;
import com.unitn.disi.lpsmt.happypuppy.util.ImageUtil;

import org.apache.commons.lang3.tuple.Pair;
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
    /**
     * {@link User} {@link Marker} size
     */
    private static final Pair<Integer, Integer> USER_MARKER_SIZE = Pair.of(128, 128);
    private static final int REQUEST_CODE = 6384;

    Button buttonBack;
    TextView nameTop;
    ImageView puppyAvatar;
    Button changeAvatar;
    TextView numberPersonalities;
    TextView usernameOwner;
    LinearLayout buttonsUser;
    LinearLayout buttonsVisit;
    SwipeRefreshLayout refreshPuppy;

    TextView specie;
    TextView races;
    TextView dateOfBirth;
    TextView gender;
    TextView weight;
    TextView personalities;

    Button button1;
    Button button2;

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
        numberPersonalities = findViewById(R.id.profile_puppy_number_personalities);
        usernameOwner = findViewById(R.id.profile_puppy_user_owner);

        specie = findViewById(R.id.profile_puppy_info_specie);
        races = findViewById(R.id.profile_puppy_info_races);
        dateOfBirth = findViewById(R.id.profile_puppy_info_dateOfBirth);
        gender = findViewById(R.id.profile_puppy_info_gender);
        weight = findViewById(R.id.profile_puppy_info_weight);
        personalities = findViewById(R.id.profile_puppy_info_personality);

        buttonsUser = findViewById(R.id.profile_puppy_buttons_profile);
        buttonsVisit = findViewById(R.id.profile_puppy_buttons_visit);
        refreshPuppy = findViewById(R.id.profile_puppy_swipe_refresh);

        Long id_puppy = Long.parseLong(Objects.requireNonNull(getIntent().getStringExtra("id_puppy")));

        downloadPuppy(id_puppy);

        refreshPuppy.setOnRefreshListener(() -> {
            refreshPuppy.setRefreshing(false);
            downloadPuppy(id_puppy);
        });

        buttonBack.setOnClickListener(v -> {
            finish();
        });
    }
    private void downloadPuppy(Long id_puppy){
        Call<API.Response<Puppy>> call = API.getInstance().getClient().create(PuppyService.class).findById(id_puppy);

        call.enqueue(new Callback<API.Response<Puppy>>() {
            @Override
            public void onResponse(@NotNull Call<API.Response<Puppy>> call, @NotNull Response<API.Response<Puppy>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    thisPuppy = response.body().data;
                    thisPuppyOwner = response.body().data.user;
                    loadDataPuppy();

                    if (AuthManager.getInstance().getAuthUserId().equals(thisPuppyOwner)) {
                        buttonsUser.setVisibility(View.VISIBLE);
                        buttonsVisit.setVisibility(View.GONE);
                        changeAvatar.setVisibility(View.VISIBLE);

                        loadButtonsAuth();
                    } else {
                        buttonsUser.setVisibility(View.GONE);
                        buttonsVisit.setVisibility(View.VISIBLE);
                        changeAvatar.setVisibility(View.GONE);

                        loadButtonsVisit();
                    }
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
    }

    public void loadDataPuppy(){
        nameTop.setText(thisPuppy.name);

        numberPersonalities.setText(String.valueOf(thisPuppy.personalities.size()));
        Picasso.get().load(String.valueOf(thisPuppy.avatar)).into(puppyAvatar);

        if(thisPuppy.gender == Puppy.Gender.MALE)
            gender.setText(getString(R.string.male));
        else
            gender.setText(getString(R.string.female));
        String specie = String.valueOf(thisPuppy.specie);
        this.specie.setText(specie);

        if(thisPuppy.weight != null) {
            if(thisPuppy.weight > 1000) {
                String weight = Long.toString(thisPuppy.weight/1000);
                this.weight.setText(weight.concat(" Kg"));
            }else{
                String weight = Long.toString(thisPuppy.weight);
                this.weight.setText(weight.concat(" G"));
            }
        }
        if(thisPuppy.dateOfBirth != null){
            dateOfBirth.setText(thisPuppy.dateOfBirth.toString());
        }
    }

    public void loadButtonsVisit(){
        button1 = findViewById(R.id.profile_puppy_button_friendship);
        button2 = findViewById(R.id.profile_puppy_button_view_owner);
        button1.setOnClickListener(v -> {

        });

        button2.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ProfileUser.class);
            intent.putExtra("uuid_user", thisPuppyOwner.toString());
            startActivity(intent);
        });
    }
    public void loadButtonsAuth(){
        button1 = findViewById(R.id.profile_puppy_button_edit_puppy);
        button2 = findViewById(R.id.profile_puppy_button_remove_puppy);

        button1.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), EditPuppy.class);
            intent.putExtra("id_puppy", thisPuppy.id.toString());
            startActivity(intent);
        });

        button2.setOnClickListener(v -> {
            AlertDialog alertDialog = new AlertDialog.Builder(ProfilePuppy.this).create();
            alertDialog.setTitle(getResources().getString(R.string.remove_puppy));
            alertDialog.setMessage(getResources().getString(R.string.remove_puppy_confirm));
            alertDialog.setIcon(R.drawable.ic_sign_out);
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getResources().getString(R.string.confirm),
                    (dialog, which) -> {
                        Call<API.Response> call = API.getInstance().getClient().create(PuppyService.class).delete(thisPuppy.id);
                        call.enqueue(new Callback<API.Response>() {
                            @Override
                            public void onResponse(@NotNull Call<API.Response> call, @NotNull Response<API.Response> response) {
                                if(response.isSuccessful()){
                                    Intent intent = new Intent(v.getContext(), HomePage.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }

                            @Override
                            public void onFailure(@NotNull Call<API.Response> call, @NotNull Throwable t) {
                                Log.i(TAG, "Didn't found puppy");
                            }
                        });
                    });
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getResources().getString(R.string.dismiss),
                    (dialog, which) -> alertDialog.dismiss());
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
