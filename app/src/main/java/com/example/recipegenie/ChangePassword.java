package com.example.recipegenie; //IM/2021/064

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePassword extends AppCompatActivity {

    // Firebase authentication instance to get the current user
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    //declare the change password button
    private Button ChangePasswordButton;

    //declare the buttons in dialog boxes
    private EditText currentPassword, newPassword;

    private ImageView backIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_password); // Set the layout for the ChangePassword activity

        //initialize EditText fields for current and new password inputs and button
        currentPassword = findViewById(R.id.editCurrentPassword);
        newPassword = findViewById(R.id.editEmail); //in XML editEmail should be changed to a suitable name
        ChangePasswordButton = findViewById(R.id.change_password_button);

        ChangePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the text input from the newPassword and currentPassword fields
                String NewPassword = newPassword.getText().toString().trim();
                String CurrentPassword = currentPassword.getText().toString().trim();

                if(TextUtils.isEmpty(CurrentPassword)){
                    currentPassword.setError("Current Password is required!"); // Validation checks for current password input
                    return;
                }

                // Validation checks for new password input
                if(TextUtils.isEmpty(NewPassword)){
                    newPassword.setError("New Password is required!");
                    return;
                }

                if(NewPassword.length() <6){
                    newPassword.setError("Password should have at least 6 characters");
                    return;
                }

                // If user is logged in and email is available, proceed with authentication
                if(user != null && user.getEmail() != null) {
                    // Create an AuthCredential object with the current email and password
                    AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), CurrentPassword);

                    // Re-authenticate the user with the provided credentials
                    user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // If re-authentication succeeds, proceed to update the user's password
                                user.updatePassword(NewPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(ChangePassword.this, "Password updated successfully!", Toast.LENGTH_LONG).show();
                                            currentPassword.getText().clear(); //clear the editText boxes
                                            newPassword.getText().clear();
                                        } else {
                                            Toast.makeText(ChangePassword.this, " Failed to update the password!", Toast.LENGTH_LONG).show();
                                            newPassword.getText().clear();
                                        }
                                    }
                                });
                            } else {
                                // Notify the user if the current password is incorrect
                                Toast.makeText(ChangePassword.this, "Current password is incorrect!", Toast.LENGTH_LONG).show();
                                currentPassword.getText().clear();
                                newPassword.getText().clear();
                            }
                        }
                    });
                }
            }
        });

        backIcon = findViewById(R.id.backIcon);

        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the edit profile activity
                Intent intent = new Intent(ChangePassword.this, Profile.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // Clears previous activities
                startActivity(intent);
                finish();            }
        });


        TextView UsernameTextView = findViewById(R.id.profile_name);
        UserDataFetch.fetchUsername(UsernameTextView);
    }
} //IM-2021-064