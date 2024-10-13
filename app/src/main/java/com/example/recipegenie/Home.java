// IM/2021/020 - M.A.P.M Karunathilaka

package com.example.recipegenie;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
    private List<Recipe> filteredList;
    private DatabaseReference databaseReference;
    private SearchView searchView;
    private ImageView profileIcon;

    private String currentMealType = "All Day"; // To track selected meal type

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        recyclerView = findViewById(R.id.recipeRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recipeList = new ArrayList<>();
        filteredList = new ArrayList<>();
        adapter = new RecipeAdapter(this, filteredList);
        recyclerView.setAdapter(adapter);

        // Set up Firebase database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Recipe");

        fetchAllRecipes();

        setupMealButtons();

        setupBottomNavigation();

        setupSearchView();

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
                filterRecipes("", currentMealType); // Initially show all recipes based on current meal type
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Home", "Database error: " + databaseError.getMessage());
            }
        });
    }

    //IM/2021/058 - K.D. Kolonnage
    //new modified method to set up meal buttons
    private void setupMealButtons() {
        Button breakfastButton = findViewById(R.id.breakfastButton);
        breakfastButton.setOnClickListener(v -> {
            currentMealType = "Breakfast";
            filterRecipes(searchView.getQuery().toString(), currentMealType);  // Filter when breakfast is selected
        });

        Button lunchButton = findViewById(R.id.lunchButton);
        lunchButton.setOnClickListener(v -> {
            currentMealType = "Lunch";
            filterRecipes(searchView.getQuery().toString(), currentMealType);  // Filter when lunch is selected
        });

        Button dinnerButton = findViewById(R.id.dinnerButton);
        dinnerButton.setOnClickListener(v -> {
            currentMealType = "Dinner";
            filterRecipes(searchView.getQuery().toString(), currentMealType);  // Filter when dinner is selected
        });

        Button alldayButton = findViewById(R.id.alldayButton);
        alldayButton.setOnClickListener(v -> {
            currentMealType = "All Day";
            filterRecipes(searchView.getQuery().toString(), currentMealType);  // Filter when All Day is selected
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

//        searchView.setOnClickListener(view -> {
//            // Programmatically switch to the search tab
//            BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
//            bottomNavigationView.setSelectedItemId(R.id.search);
//        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterRecipes(query, currentMealType);  // Filter recipes based on search query and current meal type
                return true;  // Return true
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterRecipes(newText, currentMealType);  // Filter as user types
                return true;  // Return true to handle real-time filtering
            }
        });
    }

    //IM/2021/058 - K.D. Kolonnage
    private void filterRecipes(String query, String currentMealType) {
        filteredList.clear(); // clear current list

        if (query == null || query.trim().isEmpty()) {
            // if search bar is empty, filter by current meal type
            for (Recipe recipe : recipeList) {
                if (recipe.getMeal().equalsIgnoreCase(currentMealType)) {
                    filteredList.add(recipe);
                }
            }
        } else {
            String[] queryWords = query.trim().toLowerCase().split("\\s+");

            for (Recipe recipe : recipeList) {
                // Filter by current meal type
                if (!recipe.getMeal().equalsIgnoreCase(currentMealType)) {
                    continue; // skip recipes not in the current meal type
                }

                String lowerCaseTitle = recipe.getTitle().toLowerCase();
                String[] titleWords = lowerCaseTitle.split("\\s+");

                // first letter match search
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

                // Substring search
                if (lowerCaseTitle.contains(query.trim().toLowerCase())) {
                    filteredList.add(recipe);
                }
            }
        }

        // Handle no results found
        if (filteredList.isEmpty()) {
            // Show a toast message
            Toast.makeText(Home.this, "No recipes match your search", Toast.LENGTH_SHORT).show();
        }

        // Notify adapter about updated list
        adapter.notifyDataSetChanged();
    }
    //IM/2021/058 - K.D Kolonnage

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
