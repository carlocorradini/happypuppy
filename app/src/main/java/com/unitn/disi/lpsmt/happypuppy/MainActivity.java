package com.unitn.disi.lpsmt.happypuppy;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.unitn.disi.lpsmt.happypuppy.api.AuthManager;
import com.unitn.disi.lpsmt.happypuppy.auth.SignIn;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AuthManager.getInstance().clearToken(); /* TODO: remove this line: only for testing */

        Thread welcomeThread = new Thread() {
            @Override
            public void run() {
                try {
                    super.run();
                    sleep(3000); //Delay of 3 seconds
                } catch (Exception ignored) {
                } finally {
                    Intent intent = AuthManager.getInstance().isAuth()
                            ? new Intent(MainActivity.this, HomePage.class)
                            : new Intent(MainActivity.this, SignIn.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
        welcomeThread.start();
    }
}
