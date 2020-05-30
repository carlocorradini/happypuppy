package com.unitn.disi.lpsmt.happypuppy.ui.components.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.unitn.disi.lpsmt.happypuppy.R;
import com.unitn.disi.lpsmt.happypuppy.api.API;
import com.unitn.disi.lpsmt.happypuppy.api.entity.AnimalBreed;
import com.unitn.disi.lpsmt.happypuppy.api.entity.AnimalSpecie;
import com.unitn.disi.lpsmt.happypuppy.api.entity.Puppy;
import com.unitn.disi.lpsmt.happypuppy.api.service.AnimalBreedService;
import com.unitn.disi.lpsmt.happypuppy.api.service.PuppyService;
import com.unitn.disi.lpsmt.happypuppy.helper.ErrorHelper;
import com.unitn.disi.lpsmt.happypuppy.ui.components.Toasty;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Animal Breeds Dialog
 *
 * @author Carlo Corradini
 */
public final class AnimalBreedsDialog extends AppCompatDialogFragment {

    /**
     * Find all {@link AnimalBreed Animal Breeds} independent from any {@link AnimalSpecie}
     */
    public static final long FIND_ALL_ANIMAL_BREEDS = 0;

    /**
     * The {@link Puppy} used to load the checked {@link AnimalBreed} when calling loadAnimalBreeds
     */
    private final Puppy puppy;
    /**
     * Current {@link AnimalSpecie}
     */
    private long animalSpecie;
    /**
     * Flag if the animalSpecie has been changed
     */
    private boolean animalSpecieHasChanged;
    /**
     * Current {@link View}
     */
    private View view;
    /**
     * Current selected {@link AnimalBreed Animal Breeds}
     */
    private List<AnimalBreed> currentAnimalBreeds = new ArrayList<>();
    /**
     * Old selected {@link AnimalBreed Animal Breeds} if the {@link AnimalBreedsDialog} was close and the positive button has been clicked
     */
    private List<AnimalBreed> savedAnimalBreeds = new ArrayList<>();
    /**
     * {@link List} of available {@link AnimalBreed Animal Breeds}
     * The {@link List} is populated only once calling the {@link AnimalBreedService} find method
     */
    private List<AnimalBreed> animalBreeds = new ArrayList<>();
    /**
     * {@link CheckBox Animal Breeds CheckBoxes} {@link LinearLayout container}
     */
    private LinearLayout animalBreedsContainer;
    /**
     * Data container
     */
    private LinearLayout dataContainer;
    /**
     * {@link ProgressBar} loader
     */
    private ProgressBar loader;
    /**
     * Listener when positive button has been clicked with the selected {@link AnimalBreed Animal Breeds}
     */
    private OnDialogSelectionInterface<List<AnimalBreed>> listener = null;

    /**
     * Construct an {@link AnimalBreedsDialog} filtering with the given animalSpecie
     *
     * @param animalSpecie The animalSpecie used to filter when find method is called
     */
    public AnimalBreedsDialog(long animalSpecie) {
        if (animalSpecie <= FIND_ALL_ANIMAL_BREEDS) animalSpecie = FIND_ALL_ANIMAL_BREEDS;
        this.puppy = null;
        this.animalSpecie = animalSpecie;
    }

    /**
     * Construct an {@link AnimalBreedsDialog} filtering with the given puppy
     *
     * @param puppy The {@link Puppy} used to filter when find method is called
     */
    public AnimalBreedsDialog(Puppy puppy) {
        this.puppy = puppy;
        this.animalSpecie = FIND_ALL_ANIMAL_BREEDS;
    }

    /**
     * Construct an {@link AnimalBreedsDialog} without filtering.
     * FIND_ALL_ANIMAL_BREEDS is used.
     */
    public AnimalBreedsDialog() {
        this(FIND_ALL_ANIMAL_BREEDS);
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        setRetainInstance(true);
        setCancelable(true);

        view = requireActivity().getLayoutInflater().inflate(R.layout.fragment_dialog_animal_breeds, null);

        EditText inputSearch = view.findViewById(R.id.fragment_dialog_animal_breeds_input_search);
        animalBreedsContainer = view.findViewById(R.id.fragment_dialog_animal_breeds_container);
        dataContainer = view.findViewById(R.id.fragment_dialog_animal_breeds_data_container);
        loader = view.findViewById(R.id.fragment_dialog_animal_breeds_loader);

        loader.setVisibility(View.VISIBLE);
        dataContainer.setVisibility(View.GONE);

        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                for (int i = 0; i < animalBreedsContainer.getChildCount(); i++) {
                    View v = animalBreedsContainer.getChildAt(i);
                    CheckBox animalBreedCheckBox = v instanceof CheckBox ? (CheckBox) v : null;

                    if (animalBreedCheckBox != null
                            && animalBreedCheckBox.getText().toString().toLowerCase().contains(s.toString().toLowerCase())) {
                        animalBreedCheckBox.setVisibility(View.VISIBLE);
                    } else if (animalBreedCheckBox != null) {
                        animalBreedCheckBox.setVisibility(View.GONE);
                    }
                }
            }
        });

        loadAnimalBreeds();

        return new AlertDialog.Builder(requireActivity())
                .setView(view)
                .setTitle(R.string.puppy_race)
                .setNegativeButton(R.string.dismiss, (dialog, which) -> dialog.dismiss())
                .setPositiveButton(R.string.confirm, (dialog, which) -> {
                    savedAnimalBreeds = new ArrayList<>(currentAnimalBreeds);
                    if (!savedAnimalBreeds.isEmpty() && listener != null)
                        listener.onDialogSelection(savedAnimalBreeds);
                    else if(listener != null){
                        listener.onDialogSelection(savedAnimalBreeds);
                    }
                })
                .create();
    }

    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance())
            getDialog().setDismissMessage(null);
        super.onDestroyView();
    }

    /**
     * Load all available {@link AnimalBreed Animal Breeds} calling the {@link AnimalBreedService} find method
     * If the animalBreeds list is not empty no call will be generated
     */
    private void loadAnimalBreeds() {
        if (!animalBreeds.isEmpty() && !animalSpecieHasChanged) showAnimalBreeds();
        else {
            Call<API.Response<List<AnimalBreed>>> call = API.getInstance().getClient().create(AnimalBreedService.class).find();
            if (animalSpecie != FIND_ALL_ANIMAL_BREEDS)
                call = API.getInstance().getClient().create(AnimalBreedService.class).findByAnimalSpecie(animalSpecie);

            call.enqueue(new Callback<API.Response<List<AnimalBreed>>>() {
                @Override
                public void onResponse(@NotNull Call<API.Response<List<AnimalBreed>>> call, @NotNull Response<API.Response<List<AnimalBreed>>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        animalBreeds = response.body().data;
                        savedAnimalBreeds.clear();
                        animalSpecieHasChanged = false;

                        if (puppy == null) {
                            showAnimalBreeds();
                        } else {
                            API.getInstance().getClient().create(PuppyService.class).findById(puppy.id).enqueue(new Callback<API.Response<Puppy>>() {
                                @Override
                                public void onResponse(@NotNull Call<API.Response<Puppy>> call, @NotNull Response<API.Response<Puppy>> response) {
                                    if (response.isSuccessful() && response.body() != null) {
                                        Puppy foundPuppy = response.body().data;
                                        AnimalBreed fake = new AnimalBreed();
                                        int index;

                                        animalBreeds.removeIf(animalBreed -> !animalBreed.specie.equals(foundPuppy.specie));
                                        System.out.println("TEST: " + animalBreeds.size());
                                        for (Long animalBreedId : foundPuppy.breeds) {
                                            fake.id = animalBreedId;
                                            if ((index = animalBreeds.indexOf(fake)) != -1)
                                                savedAnimalBreeds.add(animalBreeds.get(index));
                                        }
                                    } else {
                                        new Toasty(getContext(), view, R.string.error_unknown).show();
                                    }
                                    showAnimalBreeds();
                                }

                                @Override
                                public void onFailure(@NotNull Call<API.Response<Puppy>> call, @NotNull Throwable t) {
                                    ErrorHelper.showFailureError(getContext(), view, t);
                                }
                            });
                        }
                    } else {
                        new Toasty(getContext(), view, R.string.error_unknown).show();
                    }
                }

                @Override
                public void onFailure(@NotNull Call<API.Response<List<AnimalBreed>>> call, @NotNull Throwable t) {
                    ErrorHelper.showFailureError(getContext(), view, t);
                }
            });
        }
    }

    /**
     * Show the available animal breeds in the {@link List} of animalBreeds into the animalBreedsContainer
     * After hide the loader and show the animalBreedsContainer
     */
    private void showAnimalBreeds() {
        for (int i = 0; i < animalBreeds.size(); ++i) {
            CheckBox animalPersonalityCheckBox = new CheckBox(getContext());
            String breed = getResources().getStringArray(R.array.animal_breeds)[Integer.parseInt(animalBreeds.get(i).id.toString())-1];
            animalPersonalityCheckBox.setText(breed);
            animalPersonalityCheckBox.setTag(i);
            animalPersonalityCheckBox.setOnCheckedChangeListener((personalityCheckbox, isChecked) -> {
                int index = (int) personalityCheckbox.getTag();
                if (index >= animalBreeds.size()) return;
                if (isChecked && !currentAnimalBreeds.contains(animalBreeds.get(index)))
                    currentAnimalBreeds.add(animalBreeds.get(index));
                else
                    currentAnimalBreeds.remove(animalBreeds.get(index));
            });
            animalBreedsContainer.addView(animalPersonalityCheckBox);
            if (savedAnimalBreeds.contains(animalBreeds.get(i)))
                animalPersonalityCheckBox.setChecked(true);
        }

        currentAnimalBreeds = new ArrayList<>(savedAnimalBreeds);

        loader.setVisibility(View.GONE);
        dataContainer.setVisibility(View.VISIBLE);
    }

    /**
     * Return the current {@link AnimalSpecie}
     *
     * @return Current {@link AnimalSpecie}
     */
    public long getAnimalSpecie() {
        return animalSpecie;
    }

    /**
     * Set the current {@link AnimalSpecie} if no puppy was given
     *
     * @param animalSpecie The new {@link AnimalSpecie} to set to
     */
    public void setAnimalSpecie(long animalSpecie) {
        if (this.animalSpecie == animalSpecie || this.puppy != null) return;
        this.animalSpecie = animalSpecie;
        this.animalSpecieHasChanged = true;
    }

    /**
     * Set the listener called when the positive button has been clicked with the selected {@link AnimalBreed Animal Breeds}
     *
     * @param listener Listener when positive button has been clicked with the selected {@link AnimalBreed Animal Breeds}
     */
    public void setOnDialogSelectionListener(OnDialogSelectionInterface<List<AnimalBreed>> listener) {
        this.listener = listener;
    }
}

