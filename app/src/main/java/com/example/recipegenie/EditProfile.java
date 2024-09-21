package com.example.recipegenie;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class EditProfile extends AppCompatActivity {

    private ImageView backIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.getstarted), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        backIcon = findViewById(R.id.backIcon);

        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the edit profile activity
                Intent intent = new Intent(EditProfile.this, Profile.class);
                startActivity(intent);
            }
        });

        TextView UsernameTextView = findViewById(R.id.profile_name);
        UserDataFetch.fetchUsername(UsernameTextView);
    }
}