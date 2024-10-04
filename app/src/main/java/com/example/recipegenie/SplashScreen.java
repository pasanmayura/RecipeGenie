//IM/2021/009
package com.example.recipegenie;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Handler;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreen extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 1500; // 3 seconds
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
                    // User is logged in, redirect to home screen
                    Intent intent = new Intent(SplashScreen.this, Home.class); // Change HomeActivity to your home screen class
                    startActivity(intent);
                } else {
                    // User is not logged in, show the get started/login screen
                    Intent intent = new Intent(SplashScreen.this, Login.class);
                    startActivity(intent);
                }

                // Close the splash activity so it won't show again on back press
                finish();

            }
        }, SPLASH_TIME_OUT);
    }
}
//IM/2021/009