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
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.unitn.disi.lpsmt.happypuppy.R;


public class DialogAnimalKind extends AppCompatDialogFragment {
    private EditText puppyRace;
    private RadioGroup races;
    private DialogAnimalKindListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_dialog_radiobuttons_fragment, null);

        puppyRace = view.findViewById(R.id.custom_dialog_specie_text);
        races = view.findViewById(R.id.custom_dialog_radio_group);
        String[] animalKinds = getResources().getStringArray(R.array.animal_kinds);

        for(int i=0; i< animalKinds.length; i++){
            RadioButton rb = new RadioButton(getContext()); // dynamically creating RadioButton and adding to RadioGroup.
            rb.setText(animalKinds[i]);
            races.addView(rb,i);
        }

        puppyRace.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                for(int i=0; i< animalKinds.length; i++){
                    if(animalKinds[i].toLowerCase().contains(puppyRace.getText().toString().toLowerCase())) {
                        races.getChildAt(i).setVisibility(View.VISIBLE);
                    }
                    else{
                        races.getChildAt(i).setVisibility(View.GONE);
                    }
                }
            }
        });

        builder.setView(view)
            .setTitle(getResources().getString(R.string.animal_specie))
            .setNegativeButton(getResources().getString(R.string.dismiss), (dialog, which) -> {
                dialog.dismiss();
            })
            .setPositiveButton(getResources().getString(R.string.confirm), (dialog, which) -> {
                if(races.getCheckedRadioButtonId() != -1) {
                    final String nameSpecie = ((RadioButton) view.findViewById(races.getCheckedRadioButtonId()))
                            .getText().toString();
                    int x = races.getCheckedRadioButtonId() % animalKinds.length;
                    Long idValue = (long) (x);
                    listener.saveAnimalKind(idValue, nameSpecie);
                }
                races.clearCheck();
            });
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (DialogAnimalKindListener) context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString() + "must implement DialogAnimalListener");
        }
    }

    public interface DialogAnimalKindListener{
        void saveAnimalKind(Long indexAnimal, String nameIndex);
    }
}
