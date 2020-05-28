package com.unitn.disi.lpsmt.happypuppy;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.unitn.disi.lpsmt.happypuppy.api.AuthManager;
import com.unitn.disi.lpsmt.happypuppy.ui.HomePage;
import com.unitn.disi.lpsmt.happypuppy.ui.auth.SignIn;

public class Launcher extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launcher_activity);

        new Thread() {
            @Override
            public void run() {
                try {
                    super.run();
                    sleep(2000); //Delay of 3 seconds
                } catch (Exception ignored) {
                } finally {
                    Intent intent = AuthManager.getInstance().isAuth()
                            ? new Intent(Launcher.this, HomePage.class)
                            : new Intent(Launcher.this, SignIn.class);
                    startActivity(intent);
                    finish();
                }
            }
        }.start();
    }
}
