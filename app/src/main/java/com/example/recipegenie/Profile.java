package com.example.recipegenie;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Profile extends AppCompatActivity {

    // Firebase authentication instance to get the current user and handle authentication actions
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    //declare the main buttons in profile
    private Button editProfileButton;
    private Button changePasswordButton;
    private Button logoutButton;
    private Button deleteAccountButton;

    //declare the dialogs for delete and logout
    Dialog dialog_profile_delete, dialog_logout;
    //declare the buttons in dialog boxes
    Button no_button_delete,yes_button_delete, no_button_logout, yes_button_logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile); //set the layout

        //initialize the main 4 buttons in the profile
        editProfileButton = findViewById(R.id.edit_profile);
        changePasswordButton = findViewById(R.id.change_password);
        logoutButton = findViewById(R.id.logout);
        deleteAccountButton= findViewById(R.id.delete_account);

        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the edit profile activity
                Intent intent = new Intent(Profile.this, EditProfile.class);
                startActivity(intent);
            }
        });

        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the change password activity
                Intent intent = new Intent(Profile.this, ChangePassword.class);
                startActivity(intent);
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //shows the logout confirmation dialog box
                dialog_logout.show();

            }
        });

        deleteAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //shows the delete account confirmation dialog box
                dialog_profile_delete.show();
            }
        });

        // Code for the logout confirmation dialog box
        dialog_logout = new Dialog(Profile.this);
        dialog_logout.setContentView(R.layout.activity_logout); //set layout
        dialog_logout.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT); //set size
        dialog_logout.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialogbox_bg)); //set background
        dialog_logout.setCancelable(false); // Prevent the dialog from being dismissed without action

        //initialize the yes, No buttons in logout box
        yes_button_logout = dialog_logout.findViewById(R.id.yes_button_logout);
        no_button_logout  = dialog_logout.findViewById(R.id.no_button_logout);

        no_button_logout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                // Close the logout dialog
                dialog_logout.dismiss();
            }
        });

        yes_button_logout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mAuth.signOut(); //logout from the app
                //Navigate to the login page
                Intent intent = new Intent(Profile.this,Login.class);
                startActivity(intent);
                finish(); // Finish the Profile activity
                Toast.makeText(Profile.this, "Logout Successful!", Toast.LENGTH_SHORT).show();
                dialog_logout.dismiss(); // Close the logout dialog
            }
        });

        /*yes_button_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_logout.show();
            }
        });*/

        //code for profile delete confirmation dialog box
        dialog_profile_delete = new Dialog(Profile.this);
        dialog_profile_delete.setContentView(R.layout.activity_delete_profile); //set layout
        dialog_profile_delete.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT); //set the size
        dialog_profile_delete.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialogbox_bg)); //set the background
        dialog_profile_delete.setCancelable(false); // Prevent the dialog from being dismissed without action

        //initialize the yes, No buttons in delete account dialog box
        yes_button_delete = dialog_profile_delete.findViewById(R.id.yes_button_delete);
        no_button_delete  = dialog_profile_delete.findViewById(R.id.no_button_delete);

        no_button_delete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                dialog_profile_delete.dismiss(); //close delete acc dialog
            }
        });

        //
        yes_button_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user != null) {
                    // check whether user is logged in
                    user.delete().addOnCompleteListener(new OnCompleteListener<Void>() { //delete the logged-in user account
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {//handle the completion of the delete task.
                            if (task.isSuccessful()) { //Checks if the account deletion was successful.
                                Toast.makeText(Profile.this, "Profile deleted Successfully!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Profile.this, GetStarted.class); // navigate to the get start pg
                                startActivity(intent);
                                finish();  //finish the profile activity
                                dialog_profile_delete.dismiss(); //close delete acc dialog
                            } else {
                                Toast.makeText(Profile.this, "Profile deletion failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {
                    //if user is not logged in, then shows an error msg
                    Toast.makeText(Profile.this, "No user is currently signed in.", Toast.LENGTH_SHORT).show();
                }
            }
        });


      /*  yes_button_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_profile_delete.show();
           }
        });*/
        //IM/2021/064

    }
}