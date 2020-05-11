package com.unitn.disi.lpsmt.happypuppy.ui.components;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.material.chip.ChipGroup;
import com.unitn.disi.lpsmt.happypuppy.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.IntToLongFunction;


public class DialogPersonalitiesPuppy extends AppCompatDialogFragment {
    private EditText puppyPers;
    private LinearLayout checkBoxes;
    private DialogAnimalPersonalitiesListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_dialog_checkbuttons_fragment, null);

        puppyPers = view.findViewById(R.id.custom_dialog_pers_text);
        checkBoxes = view.findViewById(R.id.dialog_personalities_checkboxes_view);
        String[] animalKinds = getResources().getStringArray(R.array.animal_kinds);

        final CheckBox[] listCheckBoxes = new CheckBox[animalKinds.length];

        for(int i=0; i< animalKinds.length; i++){
            listCheckBoxes[i] = new CheckBox(getContext());
            listCheckBoxes[i].setText(animalKinds[i]);
            checkBoxes.addView(listCheckBoxes[i]);
        }

        puppyPers.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                for(int i=0; i< animalKinds.length; i++){
                    if(animalKinds[i].toLowerCase().contains(puppyPers.getText().toString().toLowerCase())) {
                        listCheckBoxes[i].setVisibility(View.VISIBLE);
                    }
                    else{
                        listCheckBoxes[i].setVisibility(View.GONE);
                    }
                }
            }
        });

        builder.setView(view)
                .setTitle(getResources().getString(R.string.puppy_behaviour))
                .setNegativeButton(getResources().getString(R.string.dismiss), (dialog, which) -> {
                    dialog.dismiss();
                })
                .setPositiveButton(getResources().getString(R.string.confirm), (dialog, which) -> {
                    List<Long> indexesPersonality = new ArrayList<>();
                    for(int i=0; i< listCheckBoxes.length; i++){
                        if(listCheckBoxes[i].isChecked()){
                            Long y = (long) i + 1;
                            indexesPersonality.add(y);
                        }
                    }
                    listener.saveAnimalPersonalities(indexesPersonality);
                });
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (DialogAnimalPersonalitiesListener) context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString() + "must implement DialogAnimalListener");
        }
    }

    public interface DialogAnimalPersonalitiesListener{
        void saveAnimalPersonalities(List<Long> indexesPersonality);
    }
}
