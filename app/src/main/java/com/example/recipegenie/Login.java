// IM/2021/020 - M.A.P.M Karunathilaka

package com.example.recipegenie;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
    private Button buttonRegister, buttonLogin;
    private TextView forgotPassword;
    private EditText editTextEmail, editTextPassword;
    private FirebaseAuth mAuth;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        buttonRegister = findViewById(R.id.buttonRegister);
        forgotPassword = findViewById(R.id.forgotPasswordText);
        buttonLogin = findViewById(R.id.buttonLogin);
        editTextEmail = findViewById(R.id.emailEdit);
        editTextPassword = findViewById(R.id.passwordEditText1);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                // Validate user input
                if (TextUtils.isEmpty(email)) {
                    editTextEmail.setError("Email is required");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    editTextPassword.setError("Password is required");
                    return;
                }
                if (password.length() < 6) {
                    editTextPassword.setError("Password must be at least 6 characters");
                    return;
                }

                // Sign in the user using Firebase Authentication
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(Login.this, task -> {
                            if (task.isSuccessful()) {
                                // Sign-in success, navigate to Home
                                FirebaseUser user = mAuth.getCurrentUser();
                                Toast.makeText(Login.this, "Login successful", Toast.LENGTH_SHORT).show();

                                // Redirect to Home
                                Intent intent = new Intent(Login.this, Home.class);
                                startActivity(intent);
                                finish();
                            } else {
                                // If sign-in fails, display a message to the user
                                Toast.makeText(Login.this, "Authentication failed: " + task.getException().getMessage(),
                                        Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });


        // Set OnClickListener for the button
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to Forgot_Password activity
                Intent intent = new Intent(Login.this, signup.class);
                startActivity(intent);
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to ForgotPassword activity
                Intent intent = new Intent(Login.this, Forgot_Password.class);
                startActivity(intent);
            }
        });

    }
}
// IM/2021/020 - M.A.P.M Karunathilaka


