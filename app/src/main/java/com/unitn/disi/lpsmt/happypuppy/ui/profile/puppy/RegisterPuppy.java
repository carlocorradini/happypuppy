package com.unitn.disi.lpsmt.happypuppy.ui.profile.puppy;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.FileUtils;
import android.provider.OpenableColumns;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.unitn.disi.lpsmt.happypuppy.api.API;
import com.unitn.disi.lpsmt.happypuppy.api.entity.Puppy;
import com.unitn.disi.lpsmt.happypuppy.R;
import com.unitn.disi.lpsmt.happypuppy.api.entity.error.ConflictError;
import com.unitn.disi.lpsmt.happypuppy.api.service.PuppyService;
import com.unitn.disi.lpsmt.happypuppy.helper.ErrorHelper;
import com.unitn.disi.lpsmt.happypuppy.ui.components.Toasty;
import com.unitn.disi.lpsmt.happypuppy.ui.components.dialog.AnimalBreedsDialog;
import com.unitn.disi.lpsmt.happypuppy.ui.components.dialog.AnimalPersonalitiesDialog;
import com.unitn.disi.lpsmt.happypuppy.ui.components.dialog.AnimalSpecieDialog;

import org.apache.http.HttpStatus;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterPuppy extends AppCompatActivity {
    private static final int REQUEST_CODE = 6384; //onActivityResult request

    private LinearLayout loader;
    private LinearLayout root;
    private RadioButton genderMale;
    private RadioButton genderFemale;
    private EditText kindAnimal;
    private Button raceAnimal;
    private EditText puppyName;
    //private Spinner sizePuppy;
    private Spinner unitWeightPuppy;
    private EditText weightPuppy;
    private ImageView avatarPuppy;
    private TextView date;
    private Button personality;
    private Button confirm;
    private Button imgPuppy;
    private Button buttonBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_puppy_activity);

        final Puppy puppy = new Puppy();

        root = findViewById(R.id.register_puppy_root_view);
        loader = findViewById(R.id.register_puppy_view_loader);
        /* Input fields */
        genderFemale = findViewById(R.id.register_puppy_input_gender_female);
        genderMale = findViewById(R.id.register_puppy_input_gender_male);
        kindAnimal = findViewById(R.id.register_puppy_input_kind_animal);
        kindAnimal.setInputType(InputType.TYPE_NULL);
        AnimalSpecieDialog animalSpecieDialog = new AnimalSpecieDialog();
        raceAnimal = findViewById(R.id.register_puppy_input_race);
        raceAnimal.setInputType(InputType.TYPE_NULL);
        AnimalBreedsDialog animalBreedsDialog = new AnimalBreedsDialog();
        puppyName = findViewById(R.id.register_puppy_input_name_puppy);
        //sizePuppy = findViewById(R.id.register_puppy_input_size);
        unitWeightPuppy = findViewById(R.id.register_puppy_input_spinner_weight);
        weightPuppy = findViewById(R.id.register_puppy_input_weight_puppy);
        date = findViewById(R.id.register_puppy_input_age);
        date.setInputType(InputType.TYPE_NULL);
        avatarPuppy = findViewById(R.id.register_puppy_profile_image);
        personality = findViewById(R.id.register_puppy_button_input_personality);
        personality.setInputType(InputType.TYPE_NULL);
        AnimalPersonalitiesDialog animalPersonalitiesDialog = new AnimalPersonalitiesDialog();
        confirm = findViewById(R.id.register_puppy_button_confirm);
        buttonBack = findViewById(R.id.register_puppy_button_back);

        /* Image input for puppy */
        imgPuppy = findViewById(R.id.register_puppy_button_image);

        imgPuppy.setOnClickListener(this::showFileChooser);
        buttonBack.setOnClickListener(v -> finish());

        /* Reset hint for optional fields */
        raceAnimal.setHint(raceAnimal.getHint() + " " + getString(R.string.optional_field));
        weightPuppy.setHint(weightPuppy.getHint() + " " + getString(R.string.optional_field));
        date.setHint(date.getHint() + " " + getString(R.string.optional_field));

        // Spinner for Puppy's size
        /*ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.puppy_size, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sizePuppy.setAdapter(adapter); */

        // Spinner for Puppy's weight unit
        ArrayAdapter<CharSequence> adapterUnit = ArrayAdapter.createFromResource(this,
                R.array.puppy_unit_weight, android.R.layout.simple_spinner_item);
        adapterUnit.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unitWeightPuppy.setAdapter(adapterUnit);


        // Animal Specie
        kindAnimal.setOnClickListener(v -> {
            if (!animalSpecieDialog.isAdded())
                animalSpecieDialog.show(getSupportFragmentManager(), AnimalSpecieDialog.class.getName());
        });
        animalSpecieDialog.setOnDialogSelectionListener(animalSpecie -> {
            if (animalSpecie == null) return;
            puppy.specie = animalSpecie.id;
            animalBreedsDialog.setAnimalSpecie(animalSpecie.id);
            raceAnimal.setEnabled(true);
            kindAnimal.setText(animalSpecie.name);
        });

        // Animal Breeds
        raceAnimal.setOnClickListener(v -> {
            if (!animalBreedsDialog.isAdded())
                animalBreedsDialog.show(getSupportFragmentManager(), AnimalBreedsDialog.class.getName());
        });
        animalBreedsDialog.setOnDialogSelectionListener(animalBreeds -> {
            if (animalBreeds == null) return;
            if (puppy.breeds == null) puppy.breeds = new ArrayList<>();
            puppy.breeds.clear();
            animalBreeds.forEach(animalBreed -> puppy.breeds.add(animalBreed.id));
        });

        // Animal Personalities
        personality.setOnClickListener(v -> {
            if (!animalPersonalitiesDialog.isAdded())
                animalPersonalitiesDialog.show(getSupportFragmentManager(), AnimalPersonalitiesDialog.class.getName());
        });
        animalPersonalitiesDialog.setOnDialogSelectionListener(animalPersonalities -> {
            if (animalPersonalities == null) return;
            if (puppy.personalities == null) puppy.personalities = new ArrayList<>();
            puppy.personalities.clear();
            animalPersonalities.forEach(animalPersonality -> puppy.personalities.add(animalPersonality.id));
        });

        // Birth date of puppy
        date.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(v.getContext(), (view, year, month, dayOfMonth) -> {
                Calendar dateOfBirth = Calendar.getInstance();
                dateOfBirth.set(year, month, dayOfMonth);
                System.out.println(dateOfBirth.toString());
                puppy.dateOfBirth = LocalDate.of(year, month + 1, dayOfMonth);
                date.setText(DateFormat.getDateInstance(DateFormat.MEDIUM).format(dateOfBirth.getTime()));
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
            datePickerDialog.show();
        });

        confirm.setOnClickListener(v -> {
            puppy.name = puppyName.getText().toString();
            if (genderFemale.isChecked())
                puppy.gender = Puppy.Gender.FEMALE;
            else if (genderMale.isChecked())
                puppy.gender = Puppy.Gender.MALE;

            puppy.weight = Long.parseLong(weightPuppy.getText().toString());
            // Validation
            if (validatePuppy(v, puppy)) {
                registerPuppy(v, puppy);
            }
        });
    }

    public boolean validatePuppy(final View v, final Puppy puppy) {
        if (puppy.name.isEmpty()) {
            new Toasty(getBaseContext(), v, R.string.insert_name);
            return false;
        }
        if (kindAnimal.getText().toString().isEmpty()) {
            new Toasty(getBaseContext(), v, R.string.insert_specie);
            return false;
        }
        if (!genderFemale.isChecked() && !genderMale.isChecked()) {
            new Toasty(getBaseContext(), v, R.string.select_gender);
            return false;
        }
        if(unitWeightPuppy.getSelectedItemPosition() == 0)
            if(puppy.weight != null)
                puppy.weight = puppy.weight*1000;
        return true;
    }

    public void registerPuppy(final View v, final Puppy puppy) {
        for (int i = 0; i < root.getChildCount(); i++) {
            View child = root.getChildAt(i);
            child.setEnabled(false);
            child.setClickable(false);
        }
        loader.setVisibility(View.VISIBLE);

        Call<API.Response<Long>> call = API.getInstance().getClient().create(PuppyService.class).create(puppy);
        call.enqueue(new Callback<API.Response<Long>>() {
            @Override
            public void onResponse(@NotNull Call<API.Response<Long>> call, @NotNull Response<API.Response<Long>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Intent intent = new Intent(v.getContext(), ProfilePuppy.class);
                    intent.putExtra("id_puppy", response.body().data.toString());
                    startActivity(intent);
                    finish();
                } else if (response.errorBody() != null) {
                    switch (response.code()) {
                        case HttpStatus.SC_CONFLICT: {
                            StringBuilder conflicts = new StringBuilder();
                            API.Response<List<ConflictError>> error = API.ErrorConverter.convert(response.errorBody(), API.ErrorConverter.TYPE_CONFLICT_LIST);
                            for (int i = 0; i < error.data.size(); i++) {
                                conflicts.append(error.data.get(i).property);
                                if (i != error.data.size() - 1)
                                    conflicts.append(", ");
                            }
                            System.out.println("INFO: " + conflicts);
                            String message = getResources().getString(R.string.conflicts_on) + conflicts;
                            new Toasty(getBaseContext(), v, message);
                            break;
                        }
                        default: {
                            new Toasty(getBaseContext(), v, R.string.internal_server_error);
                            break;
                        }
                    }
                } else {
                    new Toasty(getApplicationContext(), v, R.string.error_unknown);
                }
            }

            @Override
            public void onFailure(@NotNull Call<API.Response<Long>> call, @NotNull Throwable t) {
                ErrorHelper.showFailureError(getBaseContext(), v, t);
                loader.setVisibility(View.GONE);
                for (int i = 0; i < root.getChildCount(); i++) {
                    View child = root.getChildAt(i);
                    child.setEnabled(true);
                    child.setClickable(true);
                }
            }
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
