package com.unitn.disi.lpsmt.happypuppy;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class RegisterPuppy extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private TextView date;
    private Button personality;
    private Button imgPuppy;

    String[] listItems;
    boolean[] checkedItems;
    ArrayList<Integer> puppyPersonality = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_register_puppy);

        /* Image input for puppy */

        /* Spinner for Puppy's size */
        Spinner spinner = findViewById(R.id.register_puppy_input_size);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
        R.array.puppy_size, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        /* Get checklist for dialog */
        personality = findViewById(R.id.register_puppy_button_input_personality);
        listItems = getResources().getStringArray(R.array.puppy_size);
        checkedItems = new boolean[listItems.length];

        personality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder pBuilder = new AlertDialog.Builder(RegisterPuppy.this);
                pBuilder.setTitle(getResources().getString(R.string.puppy_behaviour));
                pBuilder.setMultiChoiceItems(listItems, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        /* Which item, isChecked */
                        if(isChecked) {
                            if(!puppyPersonality.contains(which)){
                                puppyPersonality.add(which);
                            } else{
                                puppyPersonality.remove(which);
                            }
                        }
                    }
                });
                pBuilder.setCancelable(false);
                pBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String item = "";
                         for (int i = 0; i < puppyPersonality.size(); i++){
                             item = item + listItems[puppyPersonality.get(i)];
                             if(i!= puppyPersonality.size() -1)
                                 item = item + ", ";
                         }
                    }
                });
                pBuilder.setNegativeButton(R.string.dismiss, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                pBuilder.setNeutralButton(R.string.clear_all, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (int j = 0; j < checkedItems.length; j++){
                            checkedItems[j] = false;
                            puppyPersonality.clear();
                        }
                    }
                });
                AlertDialog pDialog = pBuilder.create();
                pDialog.show();
            }
        });


        /* Birth date of puppy */
        date = findViewById(R.id.register_puppy_input_age);
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });
        date.setInputType(InputType.TYPE_NULL);
    }

    /* Date selected */
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        String puppyAge = DateFormat.getDateInstance(DateFormat.MEDIUM).format(c.getTime());
        date.setText(puppyAge);
    }
}
