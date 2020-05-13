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
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.unitn.disi.lpsmt.happypuppy.R;
import com.unitn.disi.lpsmt.happypuppy.api.API;
import com.unitn.disi.lpsmt.happypuppy.api.entity.AnimalSpecie;
import com.unitn.disi.lpsmt.happypuppy.api.service.AnimalSpecieService;
import com.unitn.disi.lpsmt.happypuppy.helper.ErrorHelper;
import com.unitn.disi.lpsmt.happypuppy.ui.components.Toasty;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Animal Specie Dialog component
 *
 * @author Carlo Corradini
 */
public final class AnimalSpecieDialog extends AppCompatDialogFragment {

    /**
     * Current {@link View}
     */
    private View view;
    /**
     * Current selected {@link AnimalSpecie}
     */
    private AnimalSpecie currentAnimalSpecie = null;
    /**
     * Old selected {@link AnimalSpecie Animal Specie} if the {@link AnimalSpecie} was close and the positive button has been clicked
     */
    private AnimalSpecie savedAnimalSpecie = null;
    /**
     * {@link List} of available {@link AnimalSpecie Animal Specie}
     * The {@link List} is populated only once calling the {@link AnimalSpecieService} find method
     */
    private List<AnimalSpecie> animalSpecies = new ArrayList<>();
    /**
     * {@link RadioButton Animal Species Radio Button} {@link RadioGroup container}
     */
    private RadioGroup animalSpeciesContainer;
    /**
     * Data container
     */
    private LinearLayout dataContainer;
    /**
     * {@link ProgressBar} loader
     */
    private ProgressBar loader;
    /**
     * Listener when positive button has been clicked with the selected {@link AnimalSpecie}
     */
    private OnDialogSelectionInterface<AnimalSpecie> listener = null;

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        setRetainInstance(true);
        setCancelable(true);

        view = requireActivity().getLayoutInflater().inflate(R.layout.fragment_dialog_animal_specie, null);

        EditText inputSearch = view.findViewById(R.id.fragment_dialog_animal_specie_input_search);
        animalSpeciesContainer = view.findViewById(R.id.fragment_dialog_animal_specie_container);
        dataContainer = view.findViewById(R.id.fragment_dialog_animal_specie_data_container);
        loader = view.findViewById(R.id.fragment_dialog_animal_specie_loader);

        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                for (int i = 0; i < animalSpeciesContainer.getChildCount(); i++) {
                    View v = animalSpeciesContainer.getChildAt(i);
                    RadioButton carRadioButton = v instanceof RadioButton ? (RadioButton) v : null;

                    if (carRadioButton != null
                            && carRadioButton.getText().toString().toLowerCase().contains(s.toString().toLowerCase())) {
                        carRadioButton.setVisibility(View.VISIBLE);
                    } else if (carRadioButton != null) {
                        carRadioButton.setVisibility(View.GONE);
                    }
                }
            }
        });
        animalSpeciesContainer.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton carRadioButton = group.findViewById(checkedId);
            int index = (int) carRadioButton.getTag();
            currentAnimalSpecie = index >= animalSpecies.size() ? null : animalSpecies.get(index);
        });

        loadCars();

        return new AlertDialog.Builder(requireActivity())
                .setView(view)
                .setTitle(R.string.animal_specie)
                .setNegativeButton(R.string.dismiss, (dialog, which) -> dialog.dismiss())
                .setPositiveButton(R.string.confirm, (dialog, which) -> {
                    savedAnimalSpecie = currentAnimalSpecie;
                    if (currentAnimalSpecie != null && listener != null)
                        listener.onDialogSelection(currentAnimalSpecie);
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
     * Load all available {@link AnimalSpecie Animal Species} calling the {@link AnimalSpecieService} find method
     * If the animal species list is not empty no call will be generated
     */
    private void loadCars() {
        if (!animalSpecies.isEmpty()) showAnimalSpecies();
        else
            API.getInstance().getClient().create(AnimalSpecieService.class).find().enqueue(new Callback<API.Response<List<AnimalSpecie>>>() {
                @Override
                public void onResponse(@NotNull Call<API.Response<List<AnimalSpecie>>> call, @NotNull Response<API.Response<List<AnimalSpecie>>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        animalSpecies = response.body().data;
                        showAnimalSpecies();
                    } else {
                        new Toasty(requireContext(), view, R.string.error_unknown);
                    }
                }

                @Override
                public void onFailure(@NotNull Call<API.Response<List<AnimalSpecie>>> call, @NotNull Throwable t) {
                    ErrorHelper.showFailureError(getContext(), view, t);
                }
            });
    }

    /**
     * Show the available animal species in the {@link List} of animal species into the animalSpeciesContainer
     * After hide the loader and show the animalSpeciesContainer
     */
    private void showAnimalSpecies() {
        for (int i = 0; i < animalSpecies.size(); ++i) {
            RadioButton carRadioButton = new RadioButton(getContext());
            carRadioButton.setText(animalSpecies.get(i).name);
            carRadioButton.setTag(i);
            animalSpeciesContainer.addView(carRadioButton);
            if (savedAnimalSpecie != null && savedAnimalSpecie.equals(animalSpecies.get(i)))
                carRadioButton.setChecked(true);
        }
        loader.setVisibility(View.GONE);
        dataContainer.setVisibility(View.VISIBLE);
    }

    /**
     * Set the listener called when the positive button has been clicked with the selected {@link AnimalSpecie}
     *
     * @param listener Listener when positive button has been clicked with the selected {@link AnimalSpecie}
     */
    public void setOnDialogSelectionListener(OnDialogSelectionInterface<AnimalSpecie> listener) {
        this.listener = listener;
    }
}
