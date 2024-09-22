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
        adapter = new RecipeAdapter(filteredList);
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

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        NavBar.setupBottomNavigation(Search.this, bottomNavigationView, R.id.search);
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
                        if (recipe != null && recipe.getTitle() != null) { // Validate that recipe and title are not null
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
                Toast.makeText(Search.this, "Failed to load data. Please check your internet connection.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Filter recipes based on the search query
    private void filterRecipes(String query) {
        filteredList.clear(); // Clear the current filtered list

        // Check if the search query is not empty
        if (query == null || query.trim().isEmpty()) {
            filteredList.addAll(recipeList); // Show all recipes if the search query is empty
        } else {
            for (Recipe recipe : recipeList) {
                // Handle potential null values in recipe titles
                if (recipe.getTitle() != null && recipe.getTitle().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(recipe); // Add recipe if it matches the query
                }
            }
        }

        // Show or hide the no results text based on the filtered list
        if (filteredList.isEmpty()) {
            noResultsTextView.setVisibility(View.VISIBLE);
            noResultsTextView.setText("No recipes found matching your search.");
        } else {
            noResultsTextView.setVisibility(View.GONE);
        }

        adapter.notifyDataSetChanged(); // Notify adapter to update the displayed list
    }
}