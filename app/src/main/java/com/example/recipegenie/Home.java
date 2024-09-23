package com.example.recipegenie;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
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
import android.view.View;

public class Home extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecipeAdapter adapter;
    private List<Recipe> recipeList;
    private DatabaseReference databaseReference;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        recyclerView = findViewById(R.id.recipeRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recipeList = new ArrayList<>();
        adapter = new RecipeAdapter(recipeList);
        recyclerView.setAdapter(adapter);

        // Set up Firebase database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Recipe");

        // Fetch data from Firebase
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                recipeList.clear();  // Clear list to avoid duplication
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Recipe recipe = snapshot.getValue(Recipe.class);
                    recipeList.add(recipe);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Home", "Database error: " + databaseError.getMessage());
            }
        });

        Button breakfastButton = findViewById(R.id.breakfastButton);
        breakfastButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchBreakfastRecipes();
            }
        });

        Button lunchButton = findViewById(R.id.lunchButton);
        lunchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchLunchRecipes();
            }
        });

        Button dinnerButton = findViewById(R.id.dinnerButton);
        dinnerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchDinnerRecipes();
            }
        });

        Button alldayButton = findViewById(R.id.alldayButton);
        alldayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchAllDayRecipes();
            }
        });

        // Set up Bottom Navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        FloatingActionButton fab = findViewById(R.id.addBtn);

        NavBar.setupBottomNavigation(this, bottomNavigationView, R.id.home, fab);

        TextView UsernameTextView = findViewById(R.id.displayname);
        UserDataFetch.fetchUsername(UsernameTextView);

        searchView = findViewById(R.id.search_view);

        searchView.setOnClickListener(view -> {
            // Programmatically switch to the search tab
            bottomNavigationView.setSelectedItemId(R.id.search);
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Navigate to search tab when search is submitted (icon clicked)
                bottomNavigationView.setSelectedItemId(R.id.search);
                return true;  // Return true to indicate that the query submission is handled
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Handle text change if needed (optional)
                return false;
            }
        });
    }
    private void fetchBreakfastRecipes() {
        databaseReference.orderByChild("meal").equalTo("Breakfast").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                recipeList.clear();
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    Recipe recipe = childSnapshot.getValue(Recipe.class);
                    recipeList.add(recipe);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Home", "Database error: " + error.getMessage());
            }
        });
    }

    private void fetchLunchRecipes() {
        databaseReference.orderByChild("meal").equalTo("Lunch").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                recipeList.clear();
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    Recipe recipe = childSnapshot.getValue(Recipe.class);
                    recipeList.add(recipe);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Home", "Database error: " + error.getMessage());
            }
        });
    }

    private void fetchDinnerRecipes() {
        databaseReference.orderByChild("meal").equalTo("Dinner").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                recipeList.clear();
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    Recipe recipe = childSnapshot.getValue(Recipe.class);
                    recipeList.add(recipe);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Home", "Database error: " + error.getMessage());
            }
        });
    }

    private void fetchAllDayRecipes() {
        databaseReference.orderByChild("meal").equalTo("All Day").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                recipeList.clear();
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    Recipe recipe = childSnapshot.getValue(Recipe.class);
                    recipeList.add(recipe);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Home", "Database error: " + error.getMessage());
            }
        });
    }
}
