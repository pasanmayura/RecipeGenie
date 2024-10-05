// IM/2021/009 - Y.A.D.S.C.Basnayake
package com.example.recipegenie;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Handler;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreen extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 1500; // 1.5 seconds
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        // Delay for SPLASH_TIME_OUT milliseconds
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                FirebaseUser currentUser = firebaseAuth.getCurrentUser();

                // Check if the user is already logged in
                if (currentUser != null) {
                    Intent intent = new Intent(SplashScreen.this, Home.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(SplashScreen.this, Login.class);
                    startActivity(intent);
                }

                // Close the splash activity
                finish();

            }
        }, SPLASH_TIME_OUT);
    }
}
// IM/2021/009 - Y.A.D.S.C.Basnayake