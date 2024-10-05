// IM/2021/020 - M.A.P.M Karunathilaka

package com.example.recipegenie;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.widget.SearchView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Home extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecipeAdapter adapter;
    private List<Recipe> recipeList;
    private DatabaseReference databaseReference;
    private SearchView searchView;
    private ImageView profileIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        recyclerView = findViewById(R.id.recipeRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recipeList = new ArrayList<>();
        adapter = new RecipeAdapter(this, recipeList);
        recyclerView.setAdapter(adapter);

        // Set up Firebase database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Recipe");

        // Fetch data from Firebase
        fetchAllRecipes();

        // Set up button listeners for different meals
        setupMealButtons();

        // Set up Bottom Navigation
        setupBottomNavigation();

        // Setup search view
        setupSearchView();

        // Set up profile icon click listener
        setupProfileIcon();
    }

    private void fetchAllRecipes() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                recipeList.clear();  // Clear list to avoid duplication
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Recipe recipe = snapshot.getValue(Recipe.class);
                    String recipeID = snapshot.getKey();
                    if (recipe != null) {
                        recipe.setRecipeID(recipeID);  // Set the recipeID
                        recipeList.add(recipe);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Home", "Database error: " + databaseError.getMessage());
            }
        });
    }

    private void setupMealButtons() {
        Button breakfastButton = findViewById(R.id.breakfastButton);
        breakfastButton.setOnClickListener(v -> fetchRecipesByMeal("Breakfast"));

        Button lunchButton = findViewById(R.id.lunchButton);
        lunchButton.setOnClickListener(v -> fetchRecipesByMeal("Lunch"));

        Button dinnerButton = findViewById(R.id.dinnerButton);
        dinnerButton.setOnClickListener(v -> fetchRecipesByMeal("Dinner"));

        Button alldayButton = findViewById(R.id.alldayButton);
        alldayButton.setOnClickListener(v -> fetchRecipesByMeal("All Day"));
    }

    private void fetchRecipesByMeal(String mealType) {
        databaseReference.orderByChild("meal").equalTo(mealType).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                recipeList.clear();
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    Recipe recipe = childSnapshot.getValue(Recipe.class);
                    String recipeID = childSnapshot.getKey(); // Correctly get the key here
                    if (recipe != null) {
                        recipe.setRecipeID(recipeID);  // Set the recipeID
                        recipeList.add(recipe);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Home", "Database error: " + error.getMessage());
            }
        });
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        FloatingActionButton fab = findViewById(R.id.addBtn);

        NavBar.setupBottomNavigation(this, bottomNavigationView, R.id.home, fab);

        TextView UsernameTextView = findViewById(R.id.displayname);
        UserDataFetch.fetchUsername(UsernameTextView);
    }

    private void setupSearchView() {
        searchView = findViewById(R.id.search_view);

        searchView.setOnClickListener(view -> {
            // Programmatically switch to the search tab
            BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
            bottomNavigationView.setSelectedItemId(R.id.search);
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Navigate to search tab when search is submitted
                BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
                bottomNavigationView.setSelectedItemId(R.id.search);
                return true;  // Return true to indicate that the query submission is handled
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void setupProfileIcon() {
        profileIcon = findViewById(R.id.imageView_profile);

        profileIcon.setOnClickListener(v -> {
            // Navigate to the edit profile activity
            Intent intent = new Intent(Home.this, Profile.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // Clears previous activities
            startActivity(intent);
            finish();
        });
    }
}

// IM/2021/020 - M.A.P.M Karunathilaka

