package com.unitn.disi.lpsmt.happypuppy.ui.components.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.CheckBox;

import com.unitn.disi.lpsmt.happypuppy.R;
import com.unitn.disi.lpsmt.happypuppy.api.API;
import com.unitn.disi.lpsmt.happypuppy.api.entity.Puppy;
import com.unitn.disi.lpsmt.happypuppy.api.service.AnimalPersonalityService;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.unitn.disi.lpsmt.happypuppy.api.entity.AnimalPersonality;
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
 * Animal Personalities Dialog component
 *
 * @author Carlo Corradini
 */
public final class AnimalPersonalitiesDialog extends AppCompatDialogFragment {

    /**
     * The {@link Puppy} used to load the checked {@link AnimalPersonality} when calling loadAnimalPersonalities
     */
    private final Puppy puppy;
    /**
     * Current {@link View}
     */
    private View view;
    /**
     * Current selected {@link AnimalPersonality Animal Personalities}
     */
    private List<AnimalPersonality> currentAnimalPersonalities = new ArrayList<>();
    /**
     * Old selected {@link AnimalPersonality Animal Personalities} if the {@link AnimalPersonalitiesDialog} was close and the positive button has been clicked
     */
    private List<AnimalPersonality> savedAnimalPersonalities = new ArrayList<>();
    /**
     * {@link List} of available {@link AnimalPersonality Animal Personalities}
     * The {@link List} is populated only once calling the {@link AnimalPersonalityService} find method
     */
    private List<AnimalPersonality> animalPersonalities = new ArrayList<>();
    /**
     * {@link CheckBox Animal Personality CheckBoxes} {@link LinearLayout container}
     */
    private LinearLayout animalPersonalitiesContainer;
    /**
     * Data container
     */
    private LinearLayout dataContainer;
    /**
     * {@link ProgressBar} loader
     */
    private ProgressBar loader;
    /**
     * Listener when positive button has been clicked with the selected {@link AnimalPersonality Animal Personalities}
     */
    private OnDialogSelectionInterface<List<AnimalPersonality>> listener = null;

    /**
     * Construct an {@link AnimalPersonalitiesDialog} with the found {@link Puppy} {@link AnimalPersonality} checked
     *
     * @param puppy The {@link Puppy} to load personalities of
     */
    public AnimalPersonalitiesDialog(Puppy puppy) {
        this.puppy = puppy;
    }

    /**
     * Construct an {@link AnimalPersonalitiesDialog} with the found {@link Puppy} {@link AnimalPersonality} checked using the given id
     *
     * @param puppy The {@link Puppy} {@link Long id}
     */
    public AnimalPersonalitiesDialog(long puppy) {
        this(new Puppy(puppy, null, null, null, null, null, null, null, null, null, null, null));
    }

    /**
     * Construct an {@link AnimalPersonalitiesDialog} with all {@link AnimalPersonality} unchecked
     */
    public AnimalPersonalitiesDialog() {
        this(null);
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        setRetainInstance(true);
        setCancelable(true);

        view = requireActivity().getLayoutInflater().inflate(R.layout.fragment_dialog_animal_personalities, null);

        EditText inputSearch = view.findViewById(R.id.fragment_dialog_animal_personalities_input_search);
        animalPersonalitiesContainer = view.findViewById(R.id.fragment_dialog_animal_personalities_container);
        dataContainer = view.findViewById(R.id.fragment_dialog_animal_personalities_data_container);
        loader = view.findViewById(R.id.fragment_dialog_animal_personalities_loader);

        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                for (int i = 0; i < animalPersonalitiesContainer.getChildCount(); i++) {
                    View v = animalPersonalitiesContainer.getChildAt(i);
                    CheckBox animalPersonalityCheckBox = v instanceof CheckBox ? (CheckBox) v : null;

                    if (animalPersonalityCheckBox != null
                            && animalPersonalityCheckBox.getText().toString().toLowerCase().contains(s.toString().toLowerCase())) {
                        animalPersonalityCheckBox.setVisibility(View.VISIBLE);
                    } else if (animalPersonalityCheckBox != null) {
                        animalPersonalityCheckBox.setVisibility(View.GONE);
                    }
                }
            }
        });

        loadAnimalPersonalities();

        return new AlertDialog.Builder(requireActivity())
                .setView(view)
                .setTitle(R.string.puppy_behaviour)
                .setNegativeButton(R.string.dismiss, (dialog, which) -> dialog.dismiss())
                .setPositiveButton(R.string.confirm, (dialog, which) -> {
                    savedAnimalPersonalities = new ArrayList<>(currentAnimalPersonalities);
                    if (!savedAnimalPersonalities.isEmpty() && listener != null)
                        listener.onDialogSelection(savedAnimalPersonalities);
                    else if(listener != null){
                        listener.onDialogSelection(savedAnimalPersonalities);
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
     * Load all available {@link AnimalPersonality Animal Personalities} calling the {@link AnimalPersonalityService} find method
     * If the animalPersonalities list is not empty no call will be generated
     */
    private void loadAnimalPersonalities() {
        if (!animalPersonalities.isEmpty()) showAnimalPersonalities();
        else
            API.getInstance().getClient().create(AnimalPersonalityService.class).find().enqueue(new Callback<API.Response<List<AnimalPersonality>>>() {
                @Override
                public void onResponse(@NotNull Call<API.Response<List<AnimalPersonality>>> call, @NotNull Response<API.Response<List<AnimalPersonality>>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        animalPersonalities = response.body().data;
                        if (puppy == null) {
                            showAnimalPersonalities();
                        } else {
                            API.getInstance().getClient().create(PuppyService.class).findById(puppy.id).enqueue(new Callback<API.Response<Puppy>>() {
                                @Override
                                public void onResponse(@NotNull Call<API.Response<Puppy>> call, @NotNull Response<API.Response<Puppy>> response) {
                                    if (response.isSuccessful() && response.body() != null) {
                                        AnimalPersonality fake = new AnimalPersonality();
                                        int index;
                                        for (Long animalPersonalityId : response.body().data.personalities) {
                                            fake.id = animalPersonalityId;
                                            if ((index = animalPersonalities.indexOf(fake)) != -1)
                                                savedAnimalPersonalities.add(animalPersonalities.get(index));
                                        }
                                    } else {
                                        new Toasty(getContext(), view, R.string.error_unknown).show();
                                    }
                                    showAnimalPersonalities();
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
                public void onFailure(@NotNull Call<API.Response<List<AnimalPersonality>>> call, @NotNull Throwable t) {
                    ErrorHelper.showFailureError(getContext(), view, t);
                }
            });
    }

    /**
     * Show the available animal personalities in the {@link List} of animalPersonalities into the animalPersonalitiesContainer
     * After hide the loader and show the animalPersonalitiesContainer
     */
    private void showAnimalPersonalities() {
        for (int i = 0; i < animalPersonalities.size(); ++i) {
            CheckBox animalPersonalityCheckBox = new CheckBox(getContext());
            String personality = getResources().getStringArray(R.array.list_personalities)[Integer.parseInt(animalPersonalities.get(i).id.toString())-1];
            animalPersonalityCheckBox.setText(personality);
            animalPersonalityCheckBox.setTag(i);
            animalPersonalityCheckBox.setOnCheckedChangeListener((personalityCheckbox, isChecked) -> {
                int index = (int) personalityCheckbox.getTag();
                if (index >= animalPersonalities.size()) return;
                if (isChecked && !currentAnimalPersonalities.contains(animalPersonalities.get(index)))
                    currentAnimalPersonalities.add(animalPersonalities.get(index));
                else
                    currentAnimalPersonalities.remove(animalPersonalities.get(index));
            });
            animalPersonalitiesContainer.addView(animalPersonalityCheckBox);
            if (savedAnimalPersonalities.contains(animalPersonalities.get(i)))
                animalPersonalityCheckBox.setChecked(true);
        }
        currentAnimalPersonalities = new ArrayList<>(savedAnimalPersonalities);
        loader.setVisibility(View.GONE);
        dataContainer.setVisibility(View.VISIBLE);
    }

    /**
     * Set the listener called when the positive button has been clicked with the selected {@link AnimalPersonality Animal Personalities}
     *
     * @param listener Listener when positive button has been clicked with the selected {@link AnimalPersonality Animal Personalities}
     */
    public void setOnDialogSelectionListener(OnDialogSelectionInterface<List<AnimalPersonality>> listener) {
        this.listener = listener;
    }
}

