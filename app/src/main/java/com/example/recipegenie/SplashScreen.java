// IM/2021/009 - Y.A.D.S.C.Basnayake
package com.example.recipegenie;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Handler;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import android.content.SharedPreferences;

public class SplashScreen extends AppCompatActivity {
    // IM/2021/009 - Y.A.D.S.C.Basnayake



    private static int SPLASH_TIME_OUT = 1500; // 1.5 seconds
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        // Delay for SPLASH_TIME_OUT milliseconds
        new Handler().postDelayed(() -> {

            // Get SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences("MyAppPreferences", MODE_PRIVATE);
            boolean isFirstTime = sharedPreferences.getBoolean("isFirstTime", true);

            // If it's the first time, show Get Started screen
            if (isFirstTime) {
                Intent intent = new Intent(SplashScreen.this, GetStarted.class); // Create GetStarted activity
                startActivity(intent);

                // Update the preference to indicate first time is over
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isFirstTime", false);
                editor.apply();

            } else {
                // Check if the user is already logged in
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                if (currentUser != null) {
                    Intent intent = new Intent(SplashScreen.this, Home.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(SplashScreen.this, Login.class);
                    startActivity(intent);
                }
            }

            // Close the splash activity
            finish();

        }, SPLASH_TIME_OUT);
    }
}
// IM/2021/009 - Y.A.D.S.C.Basnayake