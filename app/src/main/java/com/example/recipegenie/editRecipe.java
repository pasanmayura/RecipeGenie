//IM-2021-018 start
package com.example.recipegenie;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class editRecipe extends AppCompatActivity {

    private static final int VIDEO_PICK_CODE = 1000;  // Request code for video pick
    private static final int IMAGE_PICK_CODE = 2000;  // Request code for image pick
    private static final int INGREDIENT = 1; // Constant value to identify ingredients view
    private static final int STEP = 2; // Constant value to identify step view

    private LinearLayout ingredientContainer, stepsContainer;
    private EditText titleInput, cookTimeInput, servesInput;
    private Spinner mealTypeSpinner;  // Spinner for meal type
    private Uri videoUri, imageUri; // Variables for video and image
    private Button selectVideoButton, addRecipeButton, selectImageButton, addIngredientButton, addStepButton;
    private ImageView recipeImagePreview; // ImageView for displaying the image

    // To handle retrieved data
    String recipeName, time, serves;
    List<String> instructions, ingredients;
    DatabaseReference databaseRef;  // Firebase Realtime Database reference
    String recipeId;
    String videoUrl;
    String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editrecipe);

        // Initialize Firebase
        FirebaseApp.initializeApp(this);
        databaseRef = FirebaseDatabase.getInstance().getReference("Recipe");  // Realtime Database reference

        ingredientContainer = findViewById(R.id.ingredient_container);
        stepsContainer = findViewById(R.id.steps_container);

        recipeId = getIntent().getStringExtra("RECIPE_ID");  // Get the recipe ID passed from homepage recipe card
        Log.d("editRecipe", "Received Recipe ID: " + recipeId);

        titleInput = findViewById(R.id.recipe_title_input);
        cookTimeInput = findViewById(R.id.cookTimeIn);
        servesInput = findViewById(R.id.servesIn);
        selectVideoButton = findViewById(R.id.addVideo);  // Initialize the select video button
        selectImageButton = findViewById(R.id.uploadImageButton);  // Initialize the select image button
        addRecipeButton = findViewById(R.id.buttonAddRecipe);
        mealTypeSpinner = findViewById(R.id.mealTypeSpinner);  // Initialize Spinner
        recipeImagePreview = findViewById(R.id.recipe_image_preview);  // Initialize ImageView
        addIngredientButton = findViewById(R.id.addButton);
        addStepButton = findViewById(R.id.addButton1);

        if (recipeId != null) {
            getRecipeDetails(recipeId);
        }

        // Populate Spinner with meal types (Breakfast, Lunch, Dinner)
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.meal_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mealTypeSpinner.setAdapter(adapter);

        // Add new ingredient input field when "Add" button is clicked
        addIngredientButton.setOnClickListener(v -> addNewIngredientInput());

        // Add new step input field when "Add" button is clicked
        addStepButton.setOnClickListener(v -> addNewStepInput());

        // Select video button
        selectVideoButton.setOnClickListener(v -> selectVideo());

        // Select image button
        selectImageButton.setOnClickListener(v -> selectImage());

        // Add recipe button
        addRecipeButton.setOnClickListener(v -> {
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

            uploadVideoAndImageToFirebase(videoUri, imageUri, recipeId, title, cookTime, serves, mealType, ingredients, steps);
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
        // Same as in the original code, adding a new ingredient input field
        EditText newIngredientInput = new EditText(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                dpToPx(330), dpToPx(48)
        );
        params.setMargins(0, dpToPx(3), 0, dpToPx(16));
        newIngredientInput.setLayoutParams(params);
        newIngredientInput.setBackgroundResource(R.drawable.bg_textfield);
        newIngredientInput.setHint("Type an ingredient");
        newIngredientInput.setPadding(dpToPx(12), dpToPx(0), dpToPx(0), dpToPx(0));
        newIngredientInput.setTextSize(12);
        newIngredientInput.setTextColor(ContextCompat.getColor(this, R.color.black));
        newIngredientInput.setSingleLine(true);
        Typeface typeface = ResourcesCompat.getFont(this, R.font.poppins_semibold);
        newIngredientInput.setTypeface(typeface);

        ingredientContainer.addView(newIngredientInput, ingredientContainer.getChildCount());
    }

    private void addNewStepInput() {
        // Same as in the original code, adding a new step input field
        EditText newStepInput = new EditText(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                dpToPx(330), dpToPx(95)
        );
        params.setMargins(0, dpToPx(3), 0, dpToPx(16));
        newStepInput.setLayoutParams(params);
        newStepInput.setBackgroundResource(R.drawable.bg_textfield);
        newStepInput.setHint("Type a step");
        newStepInput.setPadding(dpToPx(12), dpToPx(12), dpToPx(12), dpToPx(12));
        newStepInput.setTextSize(12);
        newStepInput.setTextColor(ContextCompat.getColor(this, R.color.black));
        newStepInput.setSingleLine(true);
        Typeface typeface = ResourcesCompat.getFont(this, R.font.poppins_semibold);
        newStepInput.setTypeface(typeface);

        stepsContainer.addView(newStepInput, stepsContainer.getChildCount());
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

        if (requestCode == VIDEO_PICK_CODE && resultCode == RESULT_OK && data != null) {
            videoUri = data.getData();
            Toast.makeText(this, "Video Selected!", Toast.LENGTH_SHORT).show();
        } else if (requestCode == IMAGE_PICK_CODE && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            Glide.with(this)
                    .load(imageUri)
                    .placeholder(R.drawable.placeholder_image)
                    .into(recipeImagePreview);
            Toast.makeText(this, "Image Selected!", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadVideoAndImageToFirebase(Uri videoUri, Uri imageUri, String recipeId, String title, String cookTime,
                                               String serves, String mealType, ArrayList<String> ingredients,
                                               ArrayList<String> steps) {
        FirebaseStorage storage = FirebaseStorage.getInstance();

        // Recipe data map
        Map<String, Object> updatedRecipe = new HashMap<>();
        updatedRecipe.put("title", title);
        updatedRecipe.put("cookTime", cookTime);
        updatedRecipe.put("serves", serves);
        updatedRecipe.put("mealType", mealType);
        updatedRecipe.put("ingredients", ingredients);
        updatedRecipe.put("steps", steps);

        if (imageUri == null && videoUri == null) {
            // Update recipe data without video and image
            databaseRef.child(recipeId).updateChildren(updatedRecipe).addOnSuccessListener(unused ->
                    Toast.makeText(editRecipe.this, "Recipe updated successfully!", Toast.LENGTH_SHORT).show());
            return;
        }

        if (videoUri != null) {
            // Upload video to Firebase Storage
            StorageReference videoRef = storage.getReference().child("videos/" + UUID.randomUUID().toString());
            videoRef.putFile(videoUri).addOnSuccessListener(taskSnapshot -> videoRef.getDownloadUrl().addOnSuccessListener(videoUrl -> {
                updatedRecipe.put("videoUrl", videoUrl.toString());

                if (imageUri == null) {
                    // If imageUri is null, update recipe only with video URL
                    databaseRef.child(recipeId).updateChildren(updatedRecipe).addOnSuccessListener(unused ->
                            Toast.makeText(editRecipe.this, "Recipe updated successfully!", Toast.LENGTH_SHORT).show());
                } else {
                    uploadImageToFirebase(imageUri, recipeId, updatedRecipe);
                }
            }));
        } else {
            uploadImageToFirebase(imageUri, recipeId, updatedRecipe);
        }
    }

    private void uploadImageToFirebase(Uri imageUri, String recipeId, Map<String, Object> updatedRecipe) {
        FirebaseStorage storage = FirebaseStorage.getInstance();

        if (imageUri != null) {
            // Upload image to Firebase Storage
            StorageReference imageRef = storage.getReference().child("images/" + UUID.randomUUID().toString());
            imageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(imageUrl -> {
                updatedRecipe.put("imageUrl", imageUrl.toString());

                // Update recipe with image URL
                databaseRef.child(recipeId).updateChildren(updatedRecipe).addOnSuccessListener(unused ->
                        Toast.makeText(editRecipe.this, "Recipe updated successfully!", Toast.LENGTH_SHORT).show());
            }));
        }
    }

    private void getRecipeDetails(String recipeId) {
        // Get recipe details from Firebase Realtime Database and populate fields
        databaseRef.child(recipeId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot snapshot = task.getResult();
                if (snapshot.exists()) {
                    recipeName = snapshot.child("title").getValue(String.class);
                    time = snapshot.child("cookTime").getValue(String.class);
                    serves = snapshot.child("serves").getValue(String.class);
                    instructions = (List<String>) snapshot.child("steps").getValue();
                    ingredients = (List<String>) snapshot.child("ingredients").getValue();
                    videoUrl = snapshot.child("videoUrl").getValue(String.class);
                    imageUrl = snapshot.child("imageUrl").getValue(String.class);

                    titleInput.setText(recipeName);
                    cookTimeInput.setText(time);
                    servesInput.setText(serves);

                    // Populate ingredients and steps fields
                    if (ingredients != null) {
                        for (String ingredient : ingredients) {
                            EditText ingredientInput = new EditText(editRecipe.this);
                            ingredientInput.setText(ingredient);
                            ingredientContainer.addView(ingredientInput);
                        }
                    }

                    if (instructions != null) {
                        for (String instruction : instructions) {
                            EditText stepInput = new EditText(editRecipe.this);
                            stepInput.setText(instruction);
                            stepsContainer.addView(stepInput);
                        }
                    }

                    if (imageUrl != null) {
                        Glide.with(editRecipe.this)
                                .load(imageUrl)
                                .placeholder(R.drawable.placeholder_image)
                                .into(recipeImagePreview);
                    }
                } else {
                    Toast.makeText(editRecipe.this, "Recipe not found!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(editRecipe.this, "Failed to fetch recipe details!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}
