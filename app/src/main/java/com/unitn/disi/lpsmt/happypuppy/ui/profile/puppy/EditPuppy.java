package com.unitn.disi.lpsmt.happypuppy.ui.profile.puppy;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.unitn.disi.lpsmt.happypuppy.R;
import com.unitn.disi.lpsmt.happypuppy.api.API;
import com.unitn.disi.lpsmt.happypuppy.api.entity.Puppy;
import com.unitn.disi.lpsmt.happypuppy.api.service.PuppyService;
import com.unitn.disi.lpsmt.happypuppy.helper.ErrorHelper;
import com.unitn.disi.lpsmt.happypuppy.ui.components.Toasty;
import com.unitn.disi.lpsmt.happypuppy.ui.components.ToastyConfirm;
import com.unitn.disi.lpsmt.happypuppy.ui.components.dialog.AnimalBreedsDialog;
import com.unitn.disi.lpsmt.happypuppy.ui.components.dialog.AnimalPersonalitiesDialog;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.HttpStatus;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.net.URI;
import java.text.DateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditPuppy extends AppCompatActivity {
    /**
     * {@link Log} TAG of this class
     */
    private static final String TAG = EditPuppy.class.getName();
    /**
     * {@link User} avatar image max size in KB
     */
    private static final int AVATAR_MAX_SIZE_KB = 2048;
    /**
     * {@link User} avatar image max dimension in pixel
     */
    private static final Pair<Integer, Integer> AVATAR_DIMENSIONS = Pair.of(512, 512);

    private ImageView imageAvatar;
    private ImageView inputAvatar;
    private File fileAvatar = null;
    EditText namePuppy;
    Button puppyRaces;
    EditText puppyAge;
    Button personalities;
    Button saveChanges;
    Button buttonBack;
    EditText puppyWeight;
    LinearLayout loader;
    LinearLayout root;
    Spinner spinnerWeight;

    Puppy puppy;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_puppy_edit_activity);

        long id_puppy = Long.parseLong(Objects.requireNonNull(getIntent().getStringExtra("id_puppy")));
        Call<API.Response<Puppy>> call = API.getInstance().getClient().create(PuppyService.class).findById(id_puppy);

        call.enqueue(new Callback<API.Response<Puppy>>() {
            @Override
            public void onResponse(@NotNull Call<API.Response<Puppy>> call, @NotNull Response<API.Response<Puppy>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    puppy = response.body().data;
                    namePuppy.setText(puppy.name);
                    if (puppy.weight != null) {
                        if (puppy.weight > 1000) {
                            String weight = Long.toString(puppy.weight / 1000);
                            puppyWeight.setText(weight);
                        } else {
                            String weight = Long.toString(puppy.weight);
                            puppyWeight.setText(weight);
                            spinnerWeight.setSelection(1);
                        }
                    }
                    if (puppy.dateOfBirth != null)
                        puppyAge.setText(puppy.dateOfBirth.toString());
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

        root = findViewById(R.id.profile_puppy_edit_root_view);
        loader = findViewById(R.id.profile_puppy_edit_view_loader);
        imageAvatar = findViewById(R.id.profile_puppy_edit_avatar_image);
        inputAvatar = findViewById(R.id.profile_puppy_edit_avatar_input);
        namePuppy = findViewById(R.id.profile_puppy_edit_input_name_puppy);
        spinnerWeight = findViewById(R.id.profile_puppy_edit_input_spinner_weight);
        puppyRaces = findViewById(R.id.profile_puppy_edit_input_race);
        puppyRaces.setInputType(InputType.TYPE_NULL);
        AnimalBreedsDialog animalBreedsDialog = new AnimalBreedsDialog(id_puppy);
        puppyAge = findViewById(R.id.profile_puppy_edit_input_age);
        puppyWeight = findViewById(R.id.profile_puppy_edit_input_weight_puppy);
        buttonBack = findViewById(R.id.profile_puppy_edit_button_back);
        saveChanges = findViewById(R.id.profile_puppy_edit_button_confirm);
        personalities = findViewById(R.id.profile_puppy_edit_button_input_personality);
        personalities.setInputType(InputType.TYPE_NULL);
        AnimalPersonalitiesDialog animalPersonalitiesDialog = new AnimalPersonalitiesDialog(id_puppy);

        // Avatar image picker
        inputAvatar.setOnClickListener(v -> ImagePicker
                .Companion
                .with(this)
                .cropSquare()
                .compress(AVATAR_MAX_SIZE_KB)
                .maxResultSize(AVATAR_DIMENSIONS.getLeft(), AVATAR_DIMENSIONS.getRight())
                .start());

        // Spinner for Puppy's weight unit
        ArrayAdapter<CharSequence> adapterUnit = ArrayAdapter.createFromResource(this,
                R.array.puppy_unit_weight, android.R.layout.simple_spinner_item);
        adapterUnit.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerWeight.setAdapter(adapterUnit);

        // Animal Breeds
        puppyRaces.setOnClickListener(v -> {
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
        personalities.setOnClickListener(v -> {
            if (!animalPersonalitiesDialog.isAdded())
                animalPersonalitiesDialog.show(getSupportFragmentManager(), AnimalPersonalitiesDialog.class.getName());
        });
        animalPersonalitiesDialog.setOnDialogSelectionListener(animalPersonalities -> {
            if (animalPersonalities == null) return;
            if (puppy.personalities == null) puppy.personalities = new ArrayList<>();
            puppy.personalities.clear();
            animalPersonalities.forEach(animalPersonality -> this.puppy.personalities.add(animalPersonality.id));
        });

        // Birth date of puppy
        puppyAge.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(v.getContext(), (view, year, month, dayOfMonth) -> {
                Calendar dateOfBirth = Calendar.getInstance();
                dateOfBirth.set(year, month, dayOfMonth);
                System.out.println(dateOfBirth.toString());
                this.puppy.dateOfBirth = LocalDate.of(year, month + 1, dayOfMonth);
                puppyAge.setText(DateFormat.getDateInstance(DateFormat.MEDIUM).format(dateOfBirth.getTime()));
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
            datePickerDialog.show();
        });


        saveChanges.setOnClickListener(v -> {
            Puppy puppy = new Puppy();
            if (!namePuppy.getText().toString().isEmpty()) {
                puppy.name = namePuppy.getText().toString();
            }
            //puppy.personalities = this.puppy.personalities;
            //puppy.breeds = this.puppy.breeds;
            puppy.dateOfBirth = this.puppy.dateOfBirth;
            if (!puppyWeight.getText().toString().isEmpty())
                puppy.weight = Long.parseLong(puppyWeight.getText().toString());
            else
                puppy.weight = 0L;
            if (spinnerWeight.getSelectedItemPosition() == 0 && puppy.weight != 0L)
                puppy.weight = puppy.weight * 1000;
            // Validation
            updatePuppy(v, puppy);
        });

        buttonBack.setOnClickListener(v -> finish());
    }

    private void updatePuppy(View v, Puppy puppy) {
        for (int i = 0; i < root.getChildCount(); i++) {
            View child = root.getChildAt(i);
            child.setEnabled(false);
            child.setClickable(false);
        }
        loader.setVisibility(View.VISIBLE);
        Long puppyId = this.puppy.id;

        Call<API.Response> call = API.getInstance().getClient().create(PuppyService.class).update(puppyId, puppy);
        call.enqueue(new Callback<API.Response>() {
            @Override
            public void onResponse(@NotNull Call<API.Response> call, @NotNull Response<API.Response> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (fileAvatar != null) {
                        updatePuppyAvatar(puppyId, fileAvatar);
                    } else {
                        new ToastyConfirm(getApplicationContext(), v, R.string.changes_applied);
                    }
                } else {
                    new Toasty(getBaseContext(), v, R.string.error_unknown);
                }
                if (fileAvatar == null) {
                    for (int i = 0; i < root.getChildCount(); i++) {
                        View child = root.getChildAt(i);
                        child.setEnabled(true);
                        child.setClickable(true);
                    }
                    loader.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NotNull Call<API.Response> call, @NotNull Throwable t) {
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
     * This method can be called only from updatePuppyData.
     *
     * @param id     {@link Puppy} {@link Long id}
     * @param avatar The avatar {@link File} to change to
     */
    private void updatePuppyAvatar(final Long id, final File avatar) {
        if (id == null || avatar == null) return;
        MultipartBody.Part avatarPart = MultipartBody.Part.createFormData("image", fileAvatar.getName(), RequestBody.create(avatar, MediaType.parse("image/*")));

        API.getInstance().getClient().create(PuppyService.class).updateAvatar(id, avatarPart).enqueue(new Callback<API.Response<URI>>() {
            @Override
            public void onResponse(@NotNull Call<API.Response<URI>> call, @NotNull Response<API.Response<URI>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    new ToastyConfirm(getBaseContext(), root, R.string.changes_applied);
                    fileAvatar = null;
                } else {
                    new Toasty(getBaseContext(), root, R.string.error_unknown);
                }

                for (int i = 0; i < root.getChildCount(); i++) {
                    View child = root.getChildAt(i);
                    child.setEnabled(true);
                    child.setClickable(true);
                }
                loader.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NotNull Call<API.Response<URI>> call, @NotNull Throwable t) {
                Log.e(TAG, "Unable to update Puppy avatar due to " + t.getMessage(), t);
                ErrorHelper.showFailureError(getBaseContext(), root, t);
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
            new Toasty(getBaseContext(), root, ImagePicker.Companion.getError(data));
        } else {
            Log.i(TAG, "Image picker task cancelled");
        }
    }
}
