package com.example.recipegenie;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewRecipe extends AppCompatActivity {

    private RatingBar userRatingBar, averageRatingBar;
    private TextView averageRatingText;
    private Button submitRatingButton;
    private ImageView saveIcon, playIcon;
    private boolean isRecipeSaved = false;
    SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "SavedRecipes";

    // Variables for UI component
    TextView nameOfrecipetxt, NoOfpersontxt, timeforcooktxt;
    VideoView videoView;
    ImageView thumbnailImageView;
    LinearLayout ingrediantContainer, methodContainer;
    ImageView shareIcon;

    // To handle retrieved data
    String recipeName, time, NoOfpeople;
    List<String> instructions, ingrediants;
    DatabaseReference databaseReference;  // Firebase Realtime Database instance
    String recipeId;
    String video_Url;
    String thumbnailUrl;

    float totalRating; // Total rating sum
    int numberOfRatings; // Number of ratings


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewrecipe);

        // Initialize Views
//        recipeId = "-O7T-pg8xxLi84tB7efe";
        recipeId = getIntent().getStringExtra("RECIPE_ID"); //get the recipe ID passed from homepage recipecard
        Log.d("ViewRecipe", "Received Recipe ID: " + recipeId);

        if (recipeId != null) {
            Log.d("ViewRecipe", "Received recipe ID: " + recipeId);
            getRecipeDetails(recipeId); // Fetch recipe details if the ID is available
        } else {
            Log.e("ViewRecipe", "Recipe ID not received from intent");
        }

        // Find UI components
        nameOfrecipetxt = findViewById(R.id.recipeTitle);
        timeforcooktxt = findViewById(R.id.Time);
        NoOfpersontxt = findViewById(R.id.persons);
        methodContainer = findViewById(R.id.methodList);
        ingrediantContainer = findViewById(R.id.ingrediantList);
        videoView = findViewById(R.id.recipevideo);
        thumbnailImageView = findViewById(R.id.videoThumbnail);

        userRatingBar = findViewById(R.id.userRatingBar);
        averageRatingBar = findViewById(R.id.averageRatingBar);
        averageRatingText = findViewById(R.id.averageRatingText);
        submitRatingButton = findViewById(R.id.submitRatingButton);
        saveIcon = findViewById(R.id.saveIcon);
        shareIcon = findViewById(R.id.shareIcon);
        playIcon = findViewById(R.id.playIcon);

        // Initialize Firebase Realtime Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Recipe");

        // Fetch recipe details from Realtime Database when the activity is created
        if (recipeId != null) {
            getRecipeDetails(recipeId);
        }

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // Check if the recipe is already saved
        checkIfRecipeIsSaved();

        // Set the onClickListener for the save button
        saveIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isRecipeSaved) {
                    removeRecipe();  // If it's already saved, remove it
                } else {
                    saveRecipe();  // If not, save it and update icon color
                }
            }
        });

        // Set up the submit rating button
        submitRatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get user rating
                float userRating = userRatingBar.getRating();

                // Update total rating and number of ratings
                totalRating += userRating;
                numberOfRatings++;

                // Calculate new average rating
                float newAverageRating = totalRating / numberOfRatings;

                // Update Realtime Database with new average rating
                updateAverageRatingInDatabase(newAverageRating);

                // Update the UI with the new average rating
                updateAverageRatingDisplay(newAverageRating);
            }
        });

        // Set click listener for the share icon
        shareIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareRecipe();
            }
        });

    }

    private void updateAverageRatingDisplay(float averageRating) {
        // Update RatingBar and TextView with the new average rating
        averageRatingBar.setRating(averageRating);
        averageRatingText.setText(String.format("%.1f", averageRating));
    }

    private void updateAverageRatingInDatabase(float averageRating) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("averageRating", averageRating);
        updates.put("numberOfRatings", numberOfRatings);

        databaseReference.child(recipeId).updateChildren(updates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ViewRecipe.this, "Rating submitted successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ViewRecipe.this, "Failed to submit rating.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // Save Recipe Feature
    private void checkIfRecipeIsSaved() {
        // Check SharedPreferences if the recipe is already saved
        String savedRecipe = sharedPreferences.getString(recipeId, null);
        if (savedRecipe != null) {
            isRecipeSaved = true;
            saveIcon.setImageResource(R.drawable.saved_bookmark); // Set to yellow icon if saved
        }
    }

    private void saveRecipe() {
        // Save recipe details in SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(recipeId, recipeName);
        editor.apply();

        // Change the icon to yellow
        saveIcon.setImageResource(R.drawable.saved_bookmark);
        isRecipeSaved = true;

        Toast.makeText(ViewRecipe.this, "Recipe saved!", Toast.LENGTH_SHORT).show();
    }

    private void removeRecipe() {
        // Remove recipe from SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(recipeId);
        editor.apply();

        // Change the icon back to normal
        saveIcon.setImageResource(R.drawable.save);
        isRecipeSaved = false;

        Toast.makeText(ViewRecipe.this, "Recipe removed from saved!", Toast.LENGTH_SHORT).show();
    }

    // Get recipe details from Realtime Database
    public void getRecipeDetails(String recipeID) {
        DatabaseReference recipeDb = FirebaseDatabase.getInstance().getReference("Recipe").child(recipeId);
        recipeDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Retrieve recipe details from Realtime Database
                    recipeName = snapshot.child("title").getValue(String.class);
                    ingrediants = (List<String>) snapshot.child("ingredients").getValue();
                    instructions = (List<String>) snapshot.child("steps").getValue();
                    time = snapshot.child("cooktime").getValue(String.class);
                    NoOfpeople = snapshot.child("servingInfo").getValue(String.class);
                    video_Url = snapshot.child("videoUrl").getValue(String.class);
                    thumbnailUrl = snapshot.child("imageUrl").getValue(String.class);
                    totalRating = snapshot.child("averageRating").getValue(Float.class);
                    numberOfRatings = snapshot.child("numberOfRatings").getValue(Integer.class);

                    // Calculate initial average rating if there are any ratings
                    float averageRating = numberOfRatings > 0 ? totalRating / numberOfRatings : 0;

                    // Update UI with the retrieved data
                    updateUI();
                    updateAverageRatingDisplay(averageRating);

                } else {
                    Toast.makeText(ViewRecipe.this, "Data not available", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ViewRecipe.this, "Error getting recipe", Toast.LENGTH_SHORT).show();
                Log.e("DatabaseError", error.getMessage());
            }
        });
    }

    private void updateUI() {
        // Show recipe name, time and servings
        nameOfrecipetxt.setText(recipeName);
        timeforcooktxt.setText(time);
        NoOfpersontxt.setText(NoOfpeople);

        // Populate ingredient and method lists dynamically
        for (String ingredient : ingrediants) {
            TextView textView = new TextView(this);
            textView.setText(ingredient);
            ingrediantContainer.addView(textView);
        }
        for (String step : instructions) {
            TextView textView = new TextView(this);
            textView.setText(step);
            methodContainer.addView(textView);
        }

        // Load video thumbnail with Glide
        Glide.with(this)
                .load(thumbnailUrl)
                .placeholder(R.drawable.placeholder)
                .into(thumbnailImageView);

        // Set up video playback
        playIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playIcon.setVisibility(View.GONE);
                thumbnailImageView.setVisibility(View.GONE);
                videoView.setVisibility(View.VISIBLE);

                videoView.setVideoURI(Uri.parse(video_Url));
                MediaController mediaController = new MediaController(ViewRecipe.this);
                videoView.setMediaController(mediaController);
                mediaController.setAnchorView(videoView);
                videoView.start();
            }
        });
    }

    private void shareRecipe() {
        // Share recipe details via intent
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Check out this recipe: " + recipeName);
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }
}
