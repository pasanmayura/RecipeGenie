package com.example.recipegenie;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class fragment_SavedRecipes extends Fragment {
    // IM/2021/009 - Y.A.D.S.C.Basnayake
// To search and view saved recipes of the user
    private RecipeAdapterSaved recipeAdapterSaved;
    private List<Recipe> recipeList, filteredList;
    private DatabaseReference db;
    private String currentUserId;
    private SearchView searchView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_saved_recipes, container, false);


        searchView = view.findViewById(R.id.searchView);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recipeList = new ArrayList<>();
        filteredList = new ArrayList<>();
        recipeAdapterSaved = new RecipeAdapterSaved(getContext(),filteredList,RecipeAdapterSaved.VIEW_TYPE_SAVED_RECIPE);
        recyclerView.setAdapter(recipeAdapterSaved);

        // IM/2021/020 - M.A.P.M Karunathilaka
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db = FirebaseDatabase.getInstance().getReference("Users").child(currentUserId);

        // Fetch saved recipes
        db.child("savedRecipes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, Boolean> savedRecipes = (HashMap<String, Boolean>) dataSnapshot.getValue();
                if (savedRecipes != null) {
                    for (String recipeId : savedRecipes.keySet()) {
                        // Fetch each saved recipe by its ID
                        fetchRecipeById(recipeId);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load saved recipes", Toast.LENGTH_SHORT).show();
            }
        });
        // IM/2021/020 - M.A.P.M Karunathilaka


        // Set up SearchView listener to filter recipes based on query
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

        return view;
    }

    // IM/2021/020 - M.A.P.M Karunathilaka
    private void fetchRecipeById(String recipeId) {
        DatabaseReference recipeDb = FirebaseDatabase.getInstance().getReference("Recipe").child(recipeId);
        recipeDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Recipe recipe = snapshot.getValue(Recipe.class);
                if (recipe != null) {
                    recipe.setRecipeID(recipeId); // Set the recipeID here
                    recipeList.add(recipe);
                    filteredList.add(recipe); // initially show all the recipes
                    recipeAdapterSaved.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load recipe", Toast.LENGTH_SHORT).show();
            }
        });
    }
    // IM/2021/020 - M.A.P.M Karunathilaka


    // Filter recipes based on the search query
    private  void filterRecipes(String query) {
        filteredList.clear();

        // check whether the search query is not empty
        if(query == null || query.trim().isEmpty()){
            filteredList.addAll(recipeList);
        } else {
            for (Recipe recipe : recipeList)
            {
                if (recipe.getTitle() != null && recipe.getTitle().toLowerCase().contains(query.toLowerCase())){
                    filteredList.add(recipe);
                }
            }
        }
        recipeAdapterSaved.notifyDataSetChanged();
    }
}
//