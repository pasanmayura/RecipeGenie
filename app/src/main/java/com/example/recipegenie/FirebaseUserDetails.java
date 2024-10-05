// IM/2021/020 - M.A.P.M Karunathilaka

package com.example.recipegenie;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class FirebaseUserDetails {
    // Reference to the current user's node in Firebase Realtime Database
    public static DatabaseReference currentUserDetails() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser().getUid();  // Get the current user's UID
        return FirebaseDatabase.getInstance().getReference("Users").child(userId);
    }
}
// IM/2021/020 - M.A.P.M Karunathilaka
