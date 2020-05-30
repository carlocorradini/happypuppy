package com.unitn.disi.lpsmt.happypuppy.ui.profile.puppy;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.unitn.disi.lpsmt.happypuppy.R;
import com.unitn.disi.lpsmt.happypuppy.api.API;
import com.unitn.disi.lpsmt.happypuppy.api.entity.Puppy;
import com.unitn.disi.lpsmt.happypuppy.api.entity.User;
import com.unitn.disi.lpsmt.happypuppy.api.entity.error.ConflictError;
import com.unitn.disi.lpsmt.happypuppy.api.service.PuppyService;
import com.unitn.disi.lpsmt.happypuppy.helper.ErrorHelper;
import com.unitn.disi.lpsmt.happypuppy.ui.components.Toasty;
import com.unitn.disi.lpsmt.happypuppy.ui.components.dialog.AnimalBreedsDialog;
import com.unitn.disi.lpsmt.happypuppy.ui.components.dialog.AnimalPersonalitiesDialog;
import com.unitn.disi.lpsmt.happypuppy.ui.components.dialog.AnimalSpecieDialog;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.HttpStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.net.URI;
import java.text.DateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * RegisterPuppy class
 * @author Anthony Farina
 */
public class RegisterPuppy extends AppCompatActivity {
    /**
     * {@link Log} TAG of this class
     */
    private static final String TAG = RegisterPuppy.class.getName();
    /**
     * {@link User} avatar image max size in KB
     */
    private static final int AVATAR_MAX_SIZE_KB = 2048;
    /**
     * {@link User} avatar image max dimension in pixel
     */
    private static final Pair<Integer, Integer> AVATAR_DIMENSIONS = Pair.of(512, 512);

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
    private ImageView imageAvatar;
    private ImageView inputAvatar;
    private File fileAvatar = null;
    private TextView date;
    private Button personality;
    private Button confirm;
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
        imageAvatar = findViewById(R.id.register_puppy_avatar_image);
        personality = findViewById(R.id.register_puppy_button_input_personality);
        personality.setInputType(InputType.TYPE_NULL);
        AnimalPersonalitiesDialog animalPersonalitiesDialog = new AnimalPersonalitiesDialog();
        confirm = findViewById(R.id.register_puppy_button_confirm);
        buttonBack = findViewById(R.id.register_puppy_button_back);

        /* Image input for puppy */
        inputAvatar = findViewById(R.id.register_puppy_avatar_input);
        inputAvatar.setOnClickListener(v -> ImagePicker
                .Companion
                .with(this)
                .cropSquare()
                .compress(AVATAR_MAX_SIZE_KB)
                .maxResultSize(AVATAR_DIMENSIONS.getLeft(), AVATAR_DIMENSIONS.getRight())
                .start());
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
            String specie = getResources().getStringArray(R.array.animal_kinds)[Integer.parseInt(animalSpecie.id.toString())-1];
            kindAnimal.setText(specie);
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

            if(!weightPuppy.getText().toString().isEmpty())
                puppy.weight = Long.parseLong(weightPuppy.getText().toString());
            // Validation
            if (validatePuppy(v, puppy)) {
                registerPuppy(v, puppy);
            }
        });
    }

    /**
     * Validate fields for puppy form
     * @param v view
     * @param puppy puppy
     * @return boolean
     */
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
        if (unitWeightPuppy.getSelectedItemPosition() == 0)
            if (puppy.weight != null)
                puppy.weight = puppy.weight * 1000;
        return true;
    }

    /**
     * If puppy data is valid then register with this method
     * @param v view
     * @param puppy puppy
     */
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
                    Long puppyId = response.body().data;
                    if (fileAvatar != null) {
                        updatePuppyAvatar(puppyId, fileAvatar);
                    } else {
                        Intent intent = new Intent(v.getContext(), ProfilePuppy.class);
                        intent.putExtra("id_puppy", puppyId.toString());
                        startActivity(intent);
                        finish();
                    }
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

                loader.setVisibility(View.GONE);
                for (int i = 0; i < root.getChildCount(); i++) {
                    View child = root.getChildAt(i);
                    child.setEnabled(true);
                    child.setClickable(true);
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

    /**
     * Update the current {@link Puppy} avatar image with the given avatar {@link File}.
     * This method can be called only from registerPuppy.
     *
     * @param id     The {@link Puppy} id to update
     * @param avatar The avatar {@link File} to change to
     */
    private void updatePuppyAvatar(final Long id, final File avatar) {
        if (id == null || avatar == null) return;
        MultipartBody.Part avatarPart = MultipartBody.Part.createFormData("image", fileAvatar.getName(), RequestBody.create(avatar, MediaType.parse("image/*")));

        API.getInstance().getClient().create(PuppyService.class).updateAvatar(id, avatarPart).enqueue(new Callback<API.Response<URI>>() {
            @Override
            public void onResponse(@NotNull Call<API.Response<URI>> call, @NotNull Response<API.Response<URI>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    fileAvatar = null;
                    Intent intent = new Intent(getBaseContext(), ProfilePuppy.class);
                    intent.putExtra("id_puppy", id.toString());
                    startActivity(intent);
                    finish();
                } else {
                    loader.setVisibility(View.GONE);
                    for (int i = 0; i < root.getChildCount(); i++) {
                        View child = root.getChildAt(i);
                        child.setEnabled(true);
                        child.setClickable(true);
                    }

                    new Toasty(root.getContext(), root, R.string.error_unknown);
                }
            }

            @Override
            public void onFailure(@NotNull Call<API.Response<URI>> call, @NotNull Throwable t) {
                ErrorHelper.showFailureError(root.getContext(), root, t);
                loader.setVisibility(View.GONE);
                for (int i = 0; i < root.getChildCount(); i++) {
                    View child = root.getChildAt(i);
                    child.setEnabled(true);
                    child.setClickable(true);
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri avatarUri;

        if (resultCode == Activity.RESULT_OK && data != null && (avatarUri = data.getData()) != null) {
            Log.d(TAG, "Image picked successfully");
            fileAvatar = avatarUri.getPath() != null ? new File(avatarUri.getPath()) : null;
            imageAvatar.setImageURI(avatarUri);
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Log.e(TAG, "Error picking the image due to " + ImagePicker.Companion.getError(data));
            new Toasty(this, root, ImagePicker.Companion.getError(data));
        } else {
            Log.i(TAG, "Image picker task cancelled");
        }
    }
}
