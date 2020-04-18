package com.unitn.disi.lpsmt.happypuppy;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class RegisterPuppy extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private static final int PICK_IMAGE = 1;
    private RadioButton genderMale;
    private RadioButton genderFemale;
    private EditText kindAnimal;
    private EditText raceAnimal;
    private EditText puppyName;
    private Spinner sizePuppy;
    private Spinner unitWeightPuppy;
    private EditText weightPuppy;

    private TextView date;
    private Button personality;
    private Button confirm;

    /* Checklist objects for puppy Personality */
    String[] listItems;
    boolean[] checkedItems;
    ArrayList<Integer> puppyPersonality = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_register_puppy);

        /* Input fields */
        genderFemale = findViewById(R.id.register_puppy_input_gender_female);
        genderMale = findViewById(R.id.register_puppy_input_gender_male);
        kindAnimal = findViewById(R.id.register_puppy_input_kind_animal);
        raceAnimal = findViewById(R.id.register_puppy_input_race);
        puppyName = findViewById(R.id.register_puppy_input_name_puppy);
        sizePuppy = findViewById(R.id.register_puppy_input_size);
        unitWeightPuppy = findViewById(R.id.register_puppy_input_spinner_weight);
        weightPuppy = findViewById(R.id.register_puppy_input_weight_puppy);
        date = findViewById(R.id.register_puppy_input_age);

        personality = findViewById(R.id.register_puppy_button_input_personality);
        confirm = findViewById(R.id.register_puppy_button_confirm);

        /* Image input for puppy */
        /*Button imgPuppy = findViewById(R.id.register_puppy_profile_image);
        imgPuppy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });*/

        /* Reset hint for optional fields */
        date.setHint(date.getHint()+" "+getString(R.string.optional_field));


        /* Spinner for Puppy's size */
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
        R.array.puppy_size, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sizePuppy.setAdapter(adapter);

        /* Spinner for Puppy's weight unit */
        ArrayAdapter<CharSequence> adapterUnit = ArrayAdapter.createFromResource(this,
                R.array.puppy_unit_weight, android.R.layout.simple_spinner_item);
        adapterUnit.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unitWeightPuppy.setAdapter(adapterUnit);

        /* Button listeners */
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), HomePage.class); /* TODO: layout confirm created puppy */
                startActivity(intent);
            }
        });

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

        /* Get checklist for dialog */
        personality = findViewById(R.id.register_puppy_button_input_personality);
        listItems = getResources().getStringArray(R.array.puppy_size);
        checkedItems = new boolean[listItems.length];


        /* Birth date of puppy */
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

   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == PICK_IMAGE && data != null && data.getData() != null) {
            Uri _uri = data.getData();

            //User had pick an image.
            Cursor cursor = getContentResolver().query(_uri, new String[] { android.provider.MediaStore.Images.ImageColumns.DATA }, null, null, null);
            cursor.moveToFirst();

            //Link to the image
            final String imageFilePath = cursor.getString(0);
            cursor.close();
        }
        super.onActivityResult(requestCode, resultCode, data);
    } */
}
