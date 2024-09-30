package com.example.recipegenie;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Handler;

public class SplashScreen extends AppCompatActivity {

        private static int SPLASH_TIME_OUT = 1500; // 3 seconds

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_splash_screen);

            // Delay for SPLASH_TIME_OUT milliseconds
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Start the next activity
                    Intent i = new Intent(SplashScreen.this, GetStarted.class);
                    startActivity(i);

                    // Close the splash activity so it won't show again on back press
                    finish();
                }
            }, SPLASH_TIME_OUT);
        }
    }