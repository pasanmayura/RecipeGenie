package com.example.recipegenie;
// IM/2021/009 - Y.A.D.S.C.Basnayake

import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserDataFetch {

    public static void fetchUsername(final TextView textView) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

            userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Users user = snapshot.getValue(Users.class);
                        if (user != null) {
                            String username = user.getUserName();
                            textView.setText(username); // Displaying directly for simplicity
                        }
                    } else {
                        textView.setText("User not found");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("UserDataFetch", "Database error: " + error.getMessage());
                }
            });
        } else {
            textView.setText("User not logged in");
        }
    }
}
// IM/2021/009 - Y.A.D.S.C.Basnayake
