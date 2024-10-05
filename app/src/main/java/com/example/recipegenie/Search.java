//IM-2021-058 - K.D. Kolonnage
package com.example.recipegenie;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
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

public class Search extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecipeAdapter adapter;
    private List<Recipe> recipeList; // List to store all recipes from Firebase
    private List<Recipe> filteredList; // List to store filtered recipes
    private DatabaseReference databaseReference;
    private TextView noResultsTextView; // TextView to display when no results are found

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        SearchView searchView = findViewById(R.id.searchView);
        recyclerView = findViewById(R.id.recyclerView);
        noResultsTextView = findViewById(R.id.noResultsTextView); // Ensure you add this TextView in your layout file
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize lists
        recipeList = new ArrayList<>();
        filteredList = new ArrayList<>();
        adapter = new RecipeAdapter(this,filteredList);
        recyclerView.setAdapter(adapter);

        // Set up Firebase database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Recipe");

        // Fetch data from Firebase
        fetchRecipesFromFirebase();

        // Setup SearchView listener to filter recipes based on query
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false; // No action needed on submit
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterRecipes(newText);
                return true;
            }
        });

        // IM/2021/020 - M.A.P.M Karunathilaka

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        FloatingActionButton fab = findViewById(R.id.addBtn);

        NavBar.setupBottomNavigation(this, bottomNavigationView, R.id.home, fab);
        bottomNavigationView.setSelectedItemId(R.id.search);

        // IM/2021/020 - M.A.P.M Karunathilaka

    }

    // Method to fetch recipes from Firebase
    private void fetchRecipesFromFirebase() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                recipeList.clear(); // Clear list to avoid duplication
                try {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Recipe recipe = snapshot.getValue(Recipe.class);
                        if (recipe != null && recipe.getTitle() != null) { // Validate that recipe and title are not
                            String recipeID = snapshot.getKey();
                            recipe.setRecipeID(recipeID);
                            recipeList.add(recipe);
                        }
                    }
                    if (recipeList.isEmpty()) {
                        noResultsTextView.setVisibility(View.VISIBLE); // Show no results text if the list is empty
                        noResultsTextView.setText("No recipes available.");
                    } else {
                        noResultsTextView.setVisibility(View.GONE); // Hide the text when recipes are available
                    }
                    filteredList.addAll(recipeList); // Display all recipes initially
                    adapter.notifyDataSetChanged();
                } catch (Exception e) {
                    Log.e("Search", "Error processing data: " + e.getMessage());
                    Toast.makeText(Search.this, "Error loading recipes. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Search", "Database error: " + databaseError.getMessage());
                Toast.makeText(Search.this, "Failed to load data. Please check your internet connection.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Filter recipes
    private void filterRecipes(String query) {
        filteredList.clear(); // clear current list

        // Check if search bar is empty
        if (query == null || query.trim().isEmpty()) {
            filteredList.addAll(recipeList);
        } else {
            String[] queryWords = query.trim().toLowerCase().split("\\s+");
            for (Recipe recipe : recipeList) {
                if (recipe.getTitle() != null) {
                    String lowerCaseTitle = recipe.getTitle().toLowerCase();
                    String[] titleWords = lowerCaseTitle.split("\\s+");

                    // check if the recipe first letter matching with the user inputs
                    if (titleWords.length >= queryWords.length) {
                        boolean match = true;
                        for (int i = 0; i < queryWords.length; i++) {
                            if (!titleWords[i].startsWith(queryWords[i])) {
                                match = false;
                                break;
                            }
                        }
                        if (match) {
                            filteredList.add(recipe);
                            continue;
                        }
                    }

                    // substring search
                    if (lowerCaseTitle.contains(query.trim().toLowerCase())) {
                        filteredList.add(recipe);
                    }
                }
            }
        }

        // updated list
        adapter.notifyDataSetChanged();
    }

}

//IM-2021-058 - K.D. Kolonnage
