package com.unitn.disi.lpsmt.happypuppy;

import android.app.Activity;

import android.os.Bundle;

import android.util.SparseBooleanArray;

import android.view.View;

import android.widget.ArrayAdapter;

import android.widget.Button;

import android.widget.EditText;
import android.widget.ListView;

import android.widget.Toast;



public class AndroidListViewActivity extends Activity {
    ListView myList;
    EditText getChoice;
    String[] listContent = {"January","February","March", "April","May", "June","July","August","September","October","November","December"};

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_multiple_choice);
        myList = findViewById(R.id.custom_view_list_personalities);
        getChoice = findViewById(R.id.custom_view_name_personality);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice, listContent);
        myList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        myList.setAdapter(adapter);
        getChoice.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String selected = "";
                int cntChoice = myList.getCount();
                SparseBooleanArray sparseBooleanArray = myList.getCheckedItemPositions();
                for(int i = 0; i < cntChoice; i++){
                    if(sparseBooleanArray.get(i)) {
                        selected += myList.getItemAtPosition(i).toString() + "\n";
                    }
                }
                Toast.makeText(AndroidListViewActivity.this,
                        selected,
                        Toast.LENGTH_LONG).show();
            }});
    }
}