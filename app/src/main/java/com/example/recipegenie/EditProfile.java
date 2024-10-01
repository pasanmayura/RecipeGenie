package com.example.recipegenie;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;


public class EditProfile extends AppCompatActivity {

    private Users currentUserModel;

    private ImageView backIcon;
    private TextInputEditText emailTextInputEditText, nameTextInputEditText;
    private Button save_button;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Initialize views
        backIcon = findViewById(R.id.backIcon);
        emailTextInputEditText = findViewById(R.id.editEmail);
        nameTextInputEditText = findViewById(R.id.editName);
        save_button = findViewById(R.id.save_button);

        // Fetch current user data from Firebase
        getUserData();

        // Set up Save button click listener to update the username
        save_button.setOnClickListener(v -> updateBtnClick());

        // Back button functionality
        backIcon.setOnClickListener(v -> {
            Intent intent = new Intent(EditProfile.this, Profile.class);
            startActivity(intent);
        });
    }

    // Method to handle the save button click and update the username
    void updateBtnClick() {
        String newUserName = Objects.requireNonNull(nameTextInputEditText.getText()).toString();

        // Update only if the username is valid (non-empty)
        if (!newUserName.isEmpty()) {
            currentUserModel.setUserName(newUserName);
            updateToFirebase();
        } else {
            Toast.makeText(this, "Please enter a valid username", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to update the user data in Firebase Realtime Database
    void updateToFirebase() {
        FirebaseUserDetails.currentUserDetails().setValue(currentUserModel)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Updated successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(EditProfile.this, Profile.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // Clears previous activities
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(this, "Update Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Method to fetch the current user data from Firebase
    void getUserData() {
        FirebaseUserDetails.currentUserDetails().get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                currentUserModel = task.getResult().getValue(Users.class);

                // Set email as non-editable and pre-fill fields
                emailTextInputEditText.setText(Objects.requireNonNull(currentUserModel).getUserEmail());
                emailTextInputEditText.setEnabled(false);  // Make email non-editable
                nameTextInputEditText.setText(currentUserModel.getUserName());
            } else {
                Toast.makeText(this, "Failed to fetch user data", Toast.LENGTH_SHORT).show();
            }
        });

        TextView UsernameTextView = findViewById(R.id.profile_name);
        UserDataFetch.fetchUsername(UsernameTextView);
    }
}
