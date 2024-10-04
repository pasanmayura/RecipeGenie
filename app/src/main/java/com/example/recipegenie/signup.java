package com.example.recipegenie;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.recipegenie.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class signup extends AppCompatActivity {

    private FirebaseAuth auth; // for Firebase authentication
    private DatabaseReference databaseReference; // for Realtime Database connection
    private GoogleSignInOptions gso;
    private EditText user_name, signup_Email, signup_Password, confirm_Password;
    private String userID;
    private GoogleSignInOptions googleSigninOption; // Google sign-in option
    private GoogleSignInClient googleSigninClient; // Google sign-in client
    private int RC_SIGN_IN = 1; // request code for Google sign-in

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

        // Initialize Firebase Auth and Realtime Database
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users"); // Reference to "users" node

        // Find input fields by ID
        user_name = findViewById(R.id.username);
        signup_Email = findViewById(R.id.signupemail);
        signup_Password = findViewById(R.id.signupPassword);
        confirm_Password = findViewById(R.id.confirmPassword);
        googleSigninOption = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        googleSigninClient = GoogleSignIn.getClient(this, googleSigninOption);
    }

    // navigate to login
    public void logIn(View view) {
        startActivity(new Intent(this, Login.class));
    }

    // function onclick on signup button
    public void Signup(View view) {

        // get inputs and convert to string
        String password = signup_Password.getText().toString().trim();
        String email = signup_Email.getText().toString().trim();
        String rePassword = confirm_Password.getText().toString().trim();
        String uname = user_name.getText().toString().trim();

        // validation for inputs
        if (email.isEmpty()) {
            signup_Email.setError("Email cannot be empty");
        } else if (uname.isEmpty()) {
            user_name.setError("Name cannot be empty");
        } else if (password.isEmpty()) {
            signup_Password.setError("Password cannot be empty");
        } else if (!(passwordisvalidation(password))) {
            signup_Password.setError("Password must contain both letters and numbers and minimum 6 characters");
        } else if (rePassword.isEmpty()) {
            confirm_Password.setError("Confirm password cannot be empty");
        } else if (!password.equals(rePassword)) {
            confirm_Password.setError("Password doesn't match");
        } else {
            // create user in Firebase (authentication)
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(signup.this, "Authentication successful", Toast.LENGTH_SHORT).show(); // alert to user
                        userID = auth.getCurrentUser().getUid(); // get the user id of created user

                        // create user document in Realtime Database
                        Map<String, Object> user = new HashMap<>();
                        user.put("userName", uname);
                        user.put("userEmail", email);
                        databaseReference.child(userID).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(signup.this, "Signup successful", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(signup.this, Home.class));
                            }

                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(signup.this, "Signup failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    } else {
                        // alert when user creation failed
                        Toast.makeText(signup.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    // password validation method
    public boolean passwordisvalidation(String password) {
        // Check for minimum length of 6 characters
        if (password.length() < 6) {
            return false;
        }
        // Check for at least one letter (either case)
        if (!password.matches(".*[A-Za-z].*")) {
            return false;
        }
        // Check for at least one digit
        if (!password.matches(".*\\d.*")) {
            return false;
        }

        return true;
    }

    // Initiate Google sign-in process
    public void googlesignin(View view) {
        // Get the sign-in intent from the GoogleSignInClient
        Intent signInIntent = googleSigninClient.getSignInIntent();
        // Start the sign-in activity, waiting for a result
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    // Handle the result of the Google sign-in process
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            // Retrieve the GoogleSignInAccount from the sign-in result intent
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            handleSignInResult(task);
        }
    }

    // Process the result of the Google sign-in attempt
    private void handleSignInResult(Task<GoogleSignInAccount> completetask) {
        try {
            // Get the signed-in account from the task
            GoogleSignInAccount acc = completetask.getResult(ApiException.class);
            Toast.makeText(signup.this, "Sign-in Successful", Toast.LENGTH_SHORT).show();
            // Authenticate with Firebase using the Google account
            FirebaseGoogleAuth(acc);
        } catch (ApiException e) {
            Toast.makeText(signup.this, "Authentication Unsuccessful", Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(null);
        }
    }

    // Authenticate with Firebase using the Google account credentials
    private void FirebaseGoogleAuth(GoogleSignInAccount acct) {
        // Create an Authentication Credential
        AuthCredential authCredential = GoogleAuthProvider.getCredential(acct != null ? acct.getIdToken() : null, null);
        // Sign in to Firebase with the Google credentials
        auth.signInWithCredential(authCredential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(signup.this, "Authentication Successful", Toast.LENGTH_SHORT).show();
                            // Get the currently signed-in user
                            FirebaseUser user = auth.getCurrentUser();
                            // Navigate to the main login screen
                            startActivity(new Intent(signup.this, Home.class));
                        } else {
                            // Show an error message if authentication failed
                            Toast.makeText(signup.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
