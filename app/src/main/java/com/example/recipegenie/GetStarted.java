package com.example.recipegenie;
// IM/2021/009 - Y.A.D.S.C.Basnayake

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class GetStarted extends Activity {
    private Button buttonGet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_started); // Set your activity's layout

        // Initialize the button inside onCreate
        buttonGet = findViewById(R.id.buttonGet);

        // Set OnClickListener for the button
        buttonGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to Forgot_Password activity
                Intent intent = new Intent(GetStarted.this, Login.class);
                startActivity(intent);
            }
        });
    }
}
// IM/2021/009 - Y.A.D.S.C.Basnayake
