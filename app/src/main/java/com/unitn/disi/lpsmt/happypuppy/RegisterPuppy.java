package com.unitn.disi.lpsmt.happypuppy;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.FileUtils;
import android.provider.OpenableColumns;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.unitn.disi.lpsmt.happypuppy.ui.components.DatePicker;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class RegisterPuppy extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private static final int FILE_SELECT_CODE = 0;
    private static final String TAG = "RegisterActivity";
    private static final int REQUEST_CODE = 6384; //onActivityResult request

    private RadioButton genderMale;
    private RadioButton genderFemale;
    private EditText kindAnimal;
    private EditText raceAnimal;
    private EditText puppyName;
    private Spinner sizePuppy;
    private Spinner unitWeightPuppy;
    private EditText weightPuppy;
    private ImageView avatarPuppy;

    private TextView date;
    private Button personality;
    private Button confirm;
    private Button imgPuppy;

    /* Checklist objects for puppy Personality */
    String[] listItems;
    boolean[] checkedItems;
    ArrayList<Integer> puppyPersonality = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_puppy_activity);

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
        avatarPuppy = findViewById(R.id.register_puppy_profile_image);

        personality = findViewById(R.id.register_puppy_button_input_personality);
        confirm = findViewById(R.id.register_puppy_button_confirm);

        /* Image input for puppy */
        imgPuppy = findViewById(R.id.register_puppy_button_image);

        imgPuppy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser(v);
            }
        });

        /* Reset hint for optional fields */
        raceAnimal.setHint(raceAnimal.getHint()+" "+getString(R.string.optional_field));
        weightPuppy.setHint(weightPuppy.getHint()+" "+getString(R.string.optional_field));
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
                LayoutInflater inflater = getLayoutInflater();
                pBuilder.setTitle(getResources().getString(R.string.puppy_behaviour));
                // Set an EditText view to get user input

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
                pBuilder.setView(inflater.inflate(R.layout.custom_multiple_choice_fragment, null));
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
                DialogFragment datePicker = new DatePicker();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });
        date.setInputType(InputType.TYPE_NULL);
    }

    /* Date selected */
    @Override
    public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        String puppyAge = DateFormat.getDateInstance(DateFormat.MEDIUM).format(c.getTime());
        date.setText(puppyAge);
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
        super.onActivityResult(requestCode,resultCode,data);

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
