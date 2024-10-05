//im-2021-018 start
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewRecipe extends AppCompatActivity {

    //im-2021-014 start
    private RatingBar userRatingBar, averageRatingBar;
    private TextView averageRatingText;
    private Button submitRatingButton;
    private ImageView saveIcon, playIcon;
    private boolean isRecipeSaved = false;
    SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "SavedRecipes";
    //im-2021-014 end

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

        //im-2021-014 start
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

        ImageView backToHomeIcon = findViewById(R.id.back);

        // Set onClick listener for the ImageView
        backToHomeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToHome(v);  // Call the backToHome method when clicked
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


        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Get reference to the user's savedRecipes node
        DatabaseReference userSavedRecipesRef = FirebaseDatabase.getInstance().getReference("Users")
                .child(currentUserId)
                .child("savedRecipes")
                .child(recipeId); // Add recipeId under savedRecipes

        // Set the value to true to indicate this recipe is saved
        userSavedRecipesRef.setValue(true).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Successfully saved the recipe
                Toast.makeText(ViewRecipe.this, "Recipe saved!", Toast.LENGTH_SHORT).show();
            } else {
                // Failed to save the recipe
                Toast.makeText(ViewRecipe.this, " faild Recipe saved!", Toast.LENGTH_SHORT).show();
            }
        });
    }




    private void removeRecipe() {
        // Remove recipe from SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(recipeId);
        editor.apply();

        // Change the icon back to normal
        saveIcon.setImageResource(R.drawable.save);
        isRecipeSaved = false;



        // Get the current user ID
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Reference to the user's saved recipes in Firebase
        DatabaseReference userBookmarksRef = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(userId)
                .child("savedRecipes");

        // Remove the recipe ID from the user's bookmarks in Firebase
        userBookmarksRef.child(recipeId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Notify user that recipe has been unbookmarked
                Toast.makeText(ViewRecipe.this, "Recipe unsaved!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Notify user in case of failure
                Toast.makeText(ViewRecipe.this, "Failed to unsaved recipe", Toast.LENGTH_SHORT).show();
            }
        });
    }
    //im-2021-014 end

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

    private void updateUI( ){

        //show recipe name
        nameOfrecipetxt.setText(recipeName);
        //add no of people serverd and time fo cook
        NoOfpersontxt.setText(NoOfpeople);
        timeforcooktxt.setText(time);

        //show method list
        methodContainer.removeAllViews();  // Clear any previous views in method container

        // Dynamically add TextView for each method
        for (String method : instructions) { //get each step in array

            // Create a new TextView for the ingredient
            TextView methodTextView = new TextView(this);
            methodTextView.setText("\u2726\u00A0 " + method); //add a bullet before text
            methodTextView.setTextSize(16); // Set text size
            methodTextView.setPadding(50, 16, 16, 16); // Set padding for text

            // Add the TextView to the LinearLayout
            methodContainer.addView(methodTextView);

        }


        //show the ingrediant list
        ingrediantContainer.removeAllViews();  // Clear any previous views

        // Dynamically add TextView for each ingredient
        for (String ingredient : ingrediants) {
            // Create a new TextView for the ingredient
            TextView ingredientTextView = new TextView(this);
            ingredientTextView.setText(ingredient);//set the text
            ingredientTextView.setTextSize(16); // Set text size
            ingredientTextView.setPadding(45, 16, 16, 16); // Set padding

            // Add the TextView to the LinearLayout
            ingrediantContainer.addView(ingredientTextView);

            //  add a divider view for separation
            View divider = new View(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    2// Height of the divider
            );
            params.setMargins(10, 5, 10, 5);  // Set margins to space out the divider
            divider.setLayoutParams(params);
            divider.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));//set the color

            // Add the divider after the TextView
            ingrediantContainer.addView(divider);
        }

        // Load and display the thumbnail using Glide
        Glide.with(this)//initializes Glide and tells it to work with the current activity
                .load(thumbnailUrl) //image source for Glide to load.
                .placeholder(R.drawable.placeholder)//sets a placeholder image that will be displayed in the ImageView while Glide is loading the actual image
                .into(thumbnailImageView);//load the fetched image into the  ImageView

        // Set up the VideoView
        playRecipeVideo(video_Url);
    }

    private void playRecipeVideo(String videoUrl) {
        // Play video only when thumbnail is clicked
        thumbnailImageView.setOnClickListener(v -> {
            thumbnailImageView.setVisibility(View.GONE);
            playIcon.setVisibility(View.GONE);
            // Hide thumbnail
            Uri videoUri = Uri.parse(videoUrl);//converts the videoUrl  into a Uri object, which is required to load the video into the VideoView.
            videoView.setVideoURI(videoUri);// sets the video to be palyed using uri on videoview
            videoView.setMediaController(new MediaController(this));  // Adds video controls

            videoView.requestFocus();
            videoView.start(); //start video after click



        });

        // When the video is done playing, reset the thumbnail visibility
        videoView.setOnCompletionListener(mp -> {
            thumbnailImageView.setVisibility(View.VISIBLE);  // Show the thumbnail again after video ends
            playIcon.setVisibility(View.VISIBLE);
        });

        videoView.setOnClickListener(v -> {
            if (videoView.isPlaying()) {
                videoView.pause(); // Pause the video
                playIcon.setVisibility(View.VISIBLE); // Show play icon when paused
            } else {
                videoView.start(); // Resume the video
                playIcon.setVisibility(View.GONE); // Hide play icon when playing
            }
        });
    }

    //im-2021-014 start
    private void shareRecipe() {// Replace with your recipe link

        // Create the content to share
        String shareText = "Check out this recipe: " + recipeName + "\n"
                + "Serves: " + NoOfpeople + "\n"
                + "Cook time: " + time + "\n"
                + "Ingredients: " + ingrediants.toString() + "\n"
                + "Instructions: " + instructions.toString() + "\n"
                + "Watch the video: " + video_Url + "\n";

        // Create an Intent with ACTION_SEND
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");  // Share as plain text
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Delicious Recipe: " + recipeName);
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);  // The text to share

        // Optional: Check if WhatsApp is installed
        boolean isWhatsAppInstalled = isAppInstalled("com.whatsapp");
        if (isWhatsAppInstalled) {
            // If WhatsApp is installed, set the package to WhatsApp
            shareIntent.setPackage("com.whatsapp");
            startActivity(shareIntent);  // Share via WhatsApp
        } else {
            // If WhatsApp is not installed, use other platforms
            startActivity(Intent.createChooser(shareIntent, "Share Recipe via"));
        }
    }




    // Helper method to check if a specific app is installed
    private boolean isAppInstalled(String packageName) {
        try {
            getPackageManager().getPackageInfo(packageName, 0);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    //im-2021-014 end

    public void backToHome(View view) {
        startActivity(new Intent(this, Home.class));//MainRecipeView should be repalce with homepage java file name
    }

}

//im-2021-018 end