package com.example.recipegenie;

import android.app.Activity;
import android.content.Intent;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.view.MenuItem;
import android.view.View;

public class NavBar {

    public static void setupBottomNavigation(final Activity activity, BottomNavigationView bottomNavigationView, int selectedItemId, FloatingActionButton fab) {

        // Set the selected item in the BottomNavigationView
        bottomNavigationView.setSelectedItemId(selectedItemId);

        // Handle navigation item selection for BottomNavigationView
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                // Using if-else to handle navigation
                if (itemId == R.id.home) {
                    if (!(activity instanceof Home)) {
                        activity.startActivity(new Intent(activity, Home.class));
                    }
                    return true;
                } else if (itemId == R.id.search) {
                    if (!(activity instanceof Search)) {
                        activity.startActivity(new Intent(activity, Search.class));
                    }
                    return true;
                } else if (itemId == R.id.bookmark) {
                    if (!(activity instanceof SavedRecipe)) {
                        activity.startActivity(new Intent(activity, SavedRecipe.class));
                    }
                    return true;
                } else if (itemId == R.id.profile) {
                    if (!(activity instanceof Profile)) {
                        activity.startActivity(new Intent(activity, Profile.class));
                    }
                    return true;
                }

                return false;
            }
        });

        // Handle navigation for FloatingActionButton
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to AddRecipe when FAB is clicked
                if (!(activity instanceof AddRecipe)) {
                    activity.startActivity(new Intent(activity, AddRecipe.class));
                }
            }
        });
    }
}
