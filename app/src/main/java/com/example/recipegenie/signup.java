package com.example.recipegenie;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class signup extends AppCompatActivity {

    private EditText nameEditText, emailEditText, passwordEditText, confirmEditText;
    private Button signupButton;
    private FirebaseAuth mAuth;
    private TextView loginTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Bind views
        nameEditText = findViewById(R.id.username);
        emailEditText = findViewById(R.id.signupemail);
        passwordEditText = findViewById(R.id.signupPassword);
        signupButton = findViewById(R.id.signup);
        confirmEditText = findViewById(R.id.confirmPassword);
        loginTextView = findViewById(R.id.login);

        // Set up onClick listener for the sign-up button
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameEditText.getText().toString();
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                String confirmPassword = confirmEditText.getText().toString().trim();

                // Validate user input
                if (TextUtils.isEmpty(name)) {
                    nameEditText.setError("Name is required");
                    return;
                }
                if (TextUtils.isEmpty(email)) {
                    emailEditText.setError("Email is required");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    passwordEditText.setError("Password is required");
                    return;
                }
                if (password.length() < 6) {
                    passwordEditText.setError("Password must be at least 6 characters");
                    return;
                }
                if (TextUtils.isEmpty(confirmPassword)) {
                    confirmEditText.setError("Password is required");
                    return;
                }
                if (!password.equals(confirmPassword)) {
                    confirmEditText.setError("Password do not match");
                    return;
                }

                // Create new user with email and password
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(signup.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign up success
                                    Toast.makeText(signup.this, "Sign up successful", Toast.LENGTH_SHORT).show();
                                } else {
                                    // Sign up failed, display a message to the user
                                    Toast.makeText(signup.this, "Sign up failed: " + task.getException().getMessage(),
                                            Toast.LENGTH_LONG).show();
                                    Log.e("SignUp", "Failed to sign up user", task.getException());
                                }
                            }
                        });
            }
        });

        loginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to ForgotPassword activity
                Intent intent = new Intent(signup.this, Login.class);
                startActivity(intent);
            }
        });
    }
}
