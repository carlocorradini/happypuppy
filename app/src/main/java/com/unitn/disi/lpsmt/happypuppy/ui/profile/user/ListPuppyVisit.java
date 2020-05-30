package com.unitn.disi.lpsmt.happypuppy.ui.profile.user;

import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.unitn.disi.lpsmt.happypuppy.R;
import com.unitn.disi.lpsmt.happypuppy.ui.profile.puppy.ListPuppy;

import java.util.UUID;

/**
 * ListPuppyVisit class
 * @author Anthony Farina
 */
public class ListPuppyVisit extends AppCompatActivity {
    UUID user;
    Button buttonBack;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_user_list_puppies_visit_activity);

        buttonBack = findViewById(R.id.profile_user_list_puppies_button_back);
        user = UUID.fromString(getIntent().getStringExtra("uuid_user"));

        getSupportFragmentManager().beginTransaction().replace(R.id.profile_user_list_puppies_container_fragment, new ListPuppy(user)).commit();

        buttonBack.setOnClickListener(v -> {
            finish();
        });
    }
}
