package com.example.recipegenie;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
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
import java.util.List;


public class fragment_MyRecipes extends Fragment {

    private RecipeAdapterSaved recipeAdapterSaved;
    private List<Recipe> recipeList;
    private DatabaseReference db;
    private String currentUserId;  // Store the current user's ID

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_recipes, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recipeList = new ArrayList<>();
        recipeAdapterSaved = new RecipeAdapterSaved(getContext(), recipeList, RecipeAdapterSaved.VIEW_TYPE_MY_RECIPE);

        recyclerView.setAdapter(recipeAdapterSaved);

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();  // Get the current user's ID

        db = FirebaseDatabase.getInstance().getReference("Recipe");

        // Fetch only the recipes added by the current user
        db.orderByChild("userId").equalTo(currentUserId).addValueEventListener(new ValueEventListener() {

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                recipeList.clear(); // Clear the existing list

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Recipe recipe = snapshot.getValue(Recipe.class);
                    if (recipe != null) {
                        // Set the recipeID to the key from the snapshot
                        recipe.setRecipeID(snapshot.getKey());
                        if (recipe.getImageUrl() != null) {
                            recipeList.add(recipe);  // Add the recipe to the list
                        } else {
                            Log.e("FirebaseError", "Image URL is missing for recipe: " + recipe.getTitle());
                        } // Add the recipe to the list
                    }
                }
                recipeAdapterSaved.notifyDataSetChanged(); // Notify the adapter of the data change
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}

