package com.example.recipegenie;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class fragment_SavedRecipes extends Fragment {

    private RecipeAdapterSaved recipeAdapterSaved;  // Changed to RecipeAdapterSaved
    private List<Recipe> recipeList;
    private DatabaseReference db;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_saved_recipes, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recipeList = new ArrayList<>();
        recipeAdapterSaved = new RecipeAdapterSaved(getContext(), recipeList, RecipeAdapterSaved.VIEW_TYPE_SAVED_RECIPE);  // Updated to RecipeAdapterSaved
        recyclerView.setAdapter(recipeAdapterSaved);

        db = FirebaseDatabase.getInstance().getReference("Recipe");

        // Fetch data from Firebase
        db.addValueEventListener(new ValueEventListener() {

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                recipeList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Recipe recipe = snapshot.getValue(Recipe.class);
                    recipeList.add(recipe);
                }
                recipeAdapterSaved.notifyDataSetChanged();  // Updated to recipeAdapterSaved
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}



