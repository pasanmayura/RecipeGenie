//IM/2021/014
package com.example.recipegenie;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AddRecipe extends AppCompatActivity {

    private static final int VIDEO_PICK_CODE = 1000;  // Request code for video pick
    private static final int IMAGE_PICK_CODE = 2000;  // Request code for image pick

    private LinearLayout ingredientContainer, stepsContainer;
    private EditText titleInput, cookTimeInput, servesInput;
    private Spinner mealTypeSpinner;  // Spinner for meal type
    private Uri videoUri, imageUri; // Variables for video and image
    private Button selectVideoButton, addRecipeButton, selectImageButton; // Add a button to select image
    private ImageView recipeImagePreview; // ImageView for displaying the image

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);

        // Initialize Firebase and Firebase Database
        FirebaseApp.initializeApp(this);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference recipeRef = database.getReference("Recipe");  // Reference to the "recipes"

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();


        // Initialize Firebase
        FirebaseApp.initializeApp(this);

        ingredientContainer = findViewById(R.id.ingredient_container);
        stepsContainer = findViewById(R.id.steps_container);

        titleInput = findViewById(R.id.recipe_title_input);
        cookTimeInput = findViewById(R.id.cookTimeIn);
        servesInput = findViewById(R.id.servesIn);
        selectVideoButton = findViewById(R.id.addVideo); // Initialize the select video button
        selectImageButton = findViewById(R.id.uploadImageButton); // Initialize the select image button
        addRecipeButton = findViewById(R.id.buttonAddRecipe);
        mealTypeSpinner = findViewById(R.id.mealTypeSpinner);  // Initialize Spinner
        recipeImagePreview = findViewById(R.id.recipe_image_preview); // Initialize ImageView

        Button addIngredientButton = findViewById(R.id.addButton);
        Button addStepButton = findViewById(R.id.addButton1);

        // Add items to Spinner (example: Breakfast, Lunch, Dinner)
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.meal_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mealTypeSpinner.setAdapter(adapter);

        // Add new ingredient input field when "Add" button is clicked
        addIngredientButton.setOnClickListener(v -> addNewIngredientInput()); //lambda expression eka v eken kiyanne view eka

        // Add new step input field when "Add" button is clicked
        addStepButton.setOnClickListener(v -> addNewStepInput());

        // Select video button
        selectVideoButton.setOnClickListener(v -> selectVideo());

        // Select image button
        selectImageButton.setOnClickListener(v -> selectImage());

        // Add recipe button
        addRecipeButton.setOnClickListener(v -> {
            // Collect all the data first
            String title = titleInput.getText().toString();
            String cookTime = cookTimeInput.getText().toString();
            String serves = servesInput.getText().toString();
            String mealType = mealTypeSpinner.getSelectedItem().toString();

            // Collect ingredients
            ArrayList<String> ingredients = new ArrayList<>();
            for (int i = 0; i < ingredientContainer.getChildCount(); i++) {
                EditText ingredientInput = (EditText) ingredientContainer.getChildAt(i);
                ingredients.add(ingredientInput.getText().toString());
            }

            // Collect steps
            ArrayList<String> steps = new ArrayList<>();
            for (int i = 0; i < stepsContainer.getChildCount(); i++) {
                EditText stepInput = (EditText) stepsContainer.getChildAt(i);
                steps.add(stepInput.getText().toString());
            }

            // Check if the image URI is set
            if (imageUri == null) {
                Toast.makeText(AddRecipe.this, "Please select an image", Toast.LENGTH_SHORT).show();
                return; // Exit early if no image
            }

            // Proceed to upload video and image
            uploadVideoAndImageToFirebase(videoUri, imageUri, title, cookTime, serves, mealType, ingredients, steps);
        });

        ImageView closeButton = findViewById(R.id.close_button);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the activity when the button is clicked
                finish();
            }
        });


    }

    private void addNewIngredientInput() {
        EditText newIngredientInput = new EditText(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                dpToPx(330), // Width set to 330dp
                dpToPx(48)   // Height set to 48dp
        );

        params.setMargins(0, dpToPx(3), 0, dpToPx(16)); // Top margin 3dp, bottom margin 16dp
        newIngredientInput.setLayoutParams(params);
        newIngredientInput.setBackgroundResource(R.drawable.bg_textfield); // Set your drawable background
        newIngredientInput.setHint("Type an ingredient");
        newIngredientInput.setPadding(dpToPx(12), dpToPx(0), dpToPx(0), dpToPx(0)); // Padding as in XML
        newIngredientInput.setTextSize(12); // Set text size in SP
        newIngredientInput.setTextColor(ContextCompat.getColor(this, R.color.black)); // Set text color as black
        newIngredientInput.setSingleLine(true); // Ensure single line

        Typeface typeface = ResourcesCompat.getFont(this, R.font.poppins_semibold);
        newIngredientInput.setTypeface(typeface);
        newIngredientInput.setTextColor(getResources().getColor(R.color.black)); // Use your hint color
        int existingEditTextIndex = ingredientContainer.indexOfChild(findViewById(R.id.IngredientsIn));

        ingredientContainer.addView(newIngredientInput, ingredientContainer.getChildCount());
    }



    private void addNewStepInput() {
        EditText newStepInput = new EditText(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                dpToPx(330), // Width set to 330dp
                dpToPx(95)   // Height set to 95dp
        );

        params.setMargins(0, dpToPx(3), 0, dpToPx(16)); // Top margin 3dp, bottom margin 16dp
        newStepInput.setLayoutParams(params);
        newStepInput.setBackgroundResource(R.drawable.bg_textfield); // Set the same background as XML
        newStepInput.setHint("Type a step");
        newStepInput.setPadding(dpToPx(12), dpToPx(12), dpToPx(12), dpToPx(12)); // Padding as in XML
        newStepInput.setTextSize(12); // Set text size in SP
        newStepInput.setTextColor(ContextCompat.getColor(this, R.color.black)); // Set text color as black
        newStepInput.setSingleLine(true); // Ensure single line

        Typeface typeface = ResourcesCompat.getFont(this, R.font.poppins_semibold);
        newStepInput.setTypeface(typeface);

        // Find the index of the existing EditText from the XML
        int existingEditTextIndex = stepsContainer.indexOfChild(findViewById(R.id.stepsIn));

        stepsContainer.addView(newStepInput, stepsContainer.getChildCount());
    }

    // Helper method to convert dp to pixels
    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }



    private void selectVideo() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        intent.setType("video/*");
        startActivityForResult(intent, VIDEO_PICK_CODE);
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == VIDEO_PICK_CODE && resultCode == RESULT_OK && data != null) { //directoryata yana eka
            videoUri = data.getData();
            Toast.makeText(this, "Video Selected!", Toast.LENGTH_SHORT).show();
        } else if (requestCode == IMAGE_PICK_CODE && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            Glide.with(this)
                    .load(imageUri) // Load the selected image
                    .placeholder(R.drawable.placeholder_image) // Show placeholder while loading
                    .into(recipeImagePreview); // Display the image in the ImageView
            Toast.makeText(this, "Image Selected!", Toast.LENGTH_SHORT).show();
        }
    }

    // Upload video and image together, only if both are selected
    private void uploadVideoAndImageToFirebase(Uri videoUri, Uri imageUri, String title, String cookTime, String serves,
                                               String mealType, ArrayList<String> ingredients, ArrayList<String> steps) {
        FirebaseStorage storage = FirebaseStorage.getInstance(); //storage object eka
        StorageReference imageRef = storage.getReference().child("recipe_images/" + UUID.randomUUID().toString() + ".jpg");

        // First, upload the image
        imageRef.putFile(imageUri).addOnSuccessListener(taskSnapshotImage -> {
            imageRef.getDownloadUrl().addOnSuccessListener(imageDownloadUrl -> {
                // Now handle video upload if a video URI exists
                if (videoUri != null) { //video eka optional kara
                    StorageReference videoRef = storage.getReference().child("recipe_videos/" + UUID.randomUUID().toString() + ".mp4");
                    videoRef.putFile(videoUri).addOnSuccessListener(taskSnapshot -> {
                        videoRef.getDownloadUrl().addOnSuccessListener(videoDownloadUrl -> {
                            // Save the recipe data
                            saveRecipe(title, cookTime, serves, mealType, ingredients, steps, videoDownloadUrl.toString(), imageDownloadUrl.toString());
                        }).addOnFailureListener(e -> {
                            Toast.makeText(AddRecipe.this, "Failed to get video URL", Toast.LENGTH_SHORT).show();
                        });
                    }).addOnFailureListener(e -> {
                        Toast.makeText(AddRecipe.this, "Failed to upload video", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    // No video to upload, save recipe data directly
                    saveRecipe(title, cookTime, serves, mealType, ingredients, steps, null, imageDownloadUrl.toString());
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(AddRecipe.this, "Failed to get image URL", Toast.LENGTH_SHORT).show();
            });
        }).addOnFailureListener(e -> {
            Toast.makeText(AddRecipe.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
        });
    }


    private void saveRecipe(String title, String cookTime, String serves, String mealType, ArrayList<String> ingredients,
                            ArrayList<String> steps, String videoUrl, String imageUrl) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference recipeRef = database.getReference("Recipe");

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(AddRecipe.this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a map to store recipe data
        Map<String, Object> recipe = new HashMap<>();
        recipe.put("title", title);
        recipe.put("cooktime", cookTime);
        recipe.put("servingInfo", serves);
        recipe.put("videoUrl", videoUrl);
        recipe.put("imageUrl", imageUrl);
        recipe.put("meal", mealType);
        recipe.put("ingredients", ingredients);
        recipe.put("steps", steps);
        recipe.put("userId", currentUser.getUid());
        recipe.put("averageRating", 0.0);
        recipe.put("numberOfRatings", 0);

        // Push a new recipe entry to the "recipes" node
        String recipeId = recipeRef.push().getKey(); // Get the unique key for the recipe
        recipeRef.child(recipeId).setValue(recipe)
                .addOnSuccessListener(aVoid -> {
                    // Recipe added successfully, now update the user record with this recipe ID
                    updateUserWithRecipeId(currentUser.getUid(), recipeId);
                    Toast.makeText(AddRecipe.this, "Recipe added successfully!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(AddRecipe.this, "Failed to add recipe", Toast.LENGTH_SHORT).show());

    }
    private void updateUserWithRecipeId(String userId, String recipeId) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("recipes");

        // Update the user's recipe list by appending the new recipe ID
        userRef.child(recipeId).setValue(true)
                .addOnSuccessListener(aVoid -> {

                    // Navigate to the HomeActivity after success
                    Intent intent = new Intent(AddRecipe.this, Home.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // Clears previous activities
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    Toast.makeText(AddRecipe.this, "Failed to update user record with recipe ID", Toast.LENGTH_SHORT).show();
                });
    }
}
//IM/2021/014