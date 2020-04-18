package com.unitn.disi.lpsmt.happypuppy;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Thread welcomeThread = new Thread() {
            @Override
            public void run() {
                try {
                    super.run();
                    sleep(3000);  //Delay of 3 seconds
                } catch (Exception e) {

                } finally {
                    Intent intent = new Intent(MainActivity.this, SignIn.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
        welcomeThread.start();
    }

}
