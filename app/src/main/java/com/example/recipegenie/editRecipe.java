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
import android.widget.MediaController;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
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
    private static final int INGREDIENT = 1; //conctant value to identify ingrediantsview
    private static final int STEP = 2; //constant value to identify stepview

    private LinearLayout ingredientContainer, stepsContainer;
    private EditText titleInput, cookTimeInput, servesInput;
    private Spinner mealTypeSpinner;  // Spinner for meal type
    private Uri videoUri, imageUri; // Variables for video and image
    private Button selectVideoButton, addRecipeButton, selectImageButton ,addIngredientButton,addStepButton; // Add a button to select image
    private ImageView recipeImagePreview; // ImageView for displaying the image



    //to handle retrived data
    String recipeName,time,serves;
    List<String> instructions ,ingrediants;
    DatabaseReference databaseRef;  // Firebase Realtime Database reference
    String recipeId;
    String video_Url;
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


        recipeId = getIntent().getStringExtra("RECIPE_ID"); //get the recipe ID passed from homepage recipecard

        titleInput = findViewById(R.id.recipe_title_input);
        cookTimeInput = findViewById(R.id.cookTimeIn);
        servesInput = findViewById(R.id.servesIn);
        selectVideoButton = findViewById(R.id.addVideo); // Initialize the select video button
        selectImageButton = findViewById(R.id.uploadImageButton); // Initialize the select image button
        addRecipeButton = findViewById(R.id.buttonAddRecipe);
        mealTypeSpinner = findViewById(R.id.mealTypeSpinner);  // Initialize Spinner
        recipeImagePreview = findViewById(R.id.recipe_image_preview); // Initialize ImageView
        addIngredientButton = findViewById(R.id.addButton);
        addStepButton = findViewById(R.id.addButton1);

        if (recipeId != null) {
            getRecipeDetails(recipeId);
        }
        //IM-2021-014 start
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



            // Proceed to upload video and image
            uploadVideoAndImageToFirebase(videoUri, imageUri,recipeId, title, cookTime, serves, mealType, ingredients, steps);
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
    //Im-2021-014 end


    private void uploadVideoAndImageToFirebase(Uri videoUri, Uri imageUri, String recipeId, String title, String cookTime,
                                               String serves, String mealType, ArrayList<String> ingredients,
                                               ArrayList<String> steps) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        //FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Map for recipe data to be updated
        Map<String, Object> updatedRecipe = new HashMap<>();

        // Update basic details
        updatedRecipe.put("title", title);
        updatedRecipe.put("cooktime", cookTime);
        updatedRecipe.put("servingInfo", serves);
        updatedRecipe.put("meal", mealType);
        updatedRecipe.put("ingredients", ingredients);
        updatedRecipe.put("steps", steps);

        // If no new image or video, update without uploading media
        if (imageUri == null && videoUri == null) {
            updateRecipeInRealtimeDatabase(updatedRecipe, recipeId);
        } else {
            // First, upload the image if there's a new one
            if (imageUri != null) {
                StorageReference imageRef = storage.getReference().child("recipe_images/" + UUID.randomUUID().toString() + ".jpg");
                imageRef.putFile(imageUri).addOnSuccessListener(taskSnapshotImage -> {
                    imageRef.getDownloadUrl().addOnSuccessListener(imageDownloadUrl -> {
                        updatedRecipe.put("imageUrl", imageDownloadUrl.toString());
                        // Now handle video upload if a video URI exists
                        if (videoUri != null) {
                            uploadVideoAndFinalize(videoUri, updatedRecipe, recipeId);
                        } else {
                            // No new video, just update Firestore with new image
                            updateRecipeInRealtimeDatabase(updatedRecipe, recipeId);
                        }
                    }).addOnFailureListener(e -> {
                        Toast.makeText(editRecipe.this, "Failed to get image URL", Toast.LENGTH_SHORT).show();
                    });
                }).addOnFailureListener(e -> {
                    Toast.makeText(editRecipe.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                });
            }

            // If only video is being uploaded (no new image)
            if (videoUri != null && imageUri == null) {
                uploadVideoAndFinalize(videoUri, updatedRecipe, recipeId);
            }
        }
    }

    private void uploadVideoAndFinalize(Uri videoUri, Map<String, Object> updatedRecipe, String recipeId) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference videoRef = storage.getReference().child("recipe_videos/" + UUID.randomUUID().toString() + ".mp4");

        videoRef.putFile(videoUri).addOnSuccessListener(taskSnapshot -> {
            videoRef.getDownloadUrl().addOnSuccessListener(videoDownloadUrl -> {
                updatedRecipe.put("videoUrl", videoDownloadUrl.toString());
                updateRecipeInRealtimeDatabase(updatedRecipe, recipeId);  // Update Firestore after video URL is obtained
            }).addOnFailureListener(e -> {
                Toast.makeText(editRecipe.this, "Failed to get video URL", Toast.LENGTH_SHORT).show();
            });
        }).addOnFailureListener(e -> {
            Toast.makeText(editRecipe.this, "Failed to upload video", Toast.LENGTH_SHORT).show();
        });
    }

    //update the details in realtime
    private void updateRecipeInRealtimeDatabase(Map<String, Object> updatedRecipe, String recipeId) {
        // Get an instance of the Firebase Realtime Database
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Recipe");

        // Update the recipe using the recipeId
        databaseRef.child(recipeId).updateChildren(updatedRecipe)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(editRecipe.this, "Recipe updated successfully!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(editRecipe.this, "Failed to update recipe", Toast.LENGTH_SHORT).show());
    }




    // Method to fetch recipe details from Firestore based on the recipeID
    private void getRecipeDetails(String recipeId) {
        // Get recipe details from Firebase Realtime Database and populate fields
        databaseRef.child(recipeId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot snapshot = task.getResult();
                if (snapshot.exists()) {
                    recipeName = snapshot.child("title").getValue(String.class);
                    time = snapshot.child("cooktime").getValue(String.class);
                    serves = snapshot.child("servingInfo").getValue(String.class);
                    instructions = (List<String>) snapshot.child("steps").getValue();
                    ingrediants = (List<String>) snapshot.child("ingredients").getValue();
                    video_Url = snapshot.child("videoUrl").getValue(String.class);
                    imageUrl = snapshot.child("imageUrl").getValue(String.class);

                    titleInput.setText(recipeName);
                    cookTimeInput.setText(time);
                    servesInput.setText(serves);

                    // Populate ingredients and steps fields
                    if (ingrediants != null) {
                        for (String ingredient : ingrediants) {
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


    //method to update UI with retrived data
    private void updateUI( ){

        titleInput.setText(recipeName);
        servesInput.setText(serves);
        cookTimeInput.setText(time);

        //show method list
        stepsContainer.removeAllViews();  // Clear any previous views in method container

        // Dynamically add TextView for each method
        for (String method : instructions) { //get each step in array

            addViewAndEditText(method,STEP);

        }


        //show the ingrediant list
        ingredientContainer.removeAllViews();  // Clear any previous views

        // Dynamically add TextView for each ingredient
        for (String ingredient : ingrediants) {
            addViewAndEditText(ingredient,INGREDIENT);
        }




        // Load and display the thumbnail using Glide
        Glide.with(this)//initializes Glide and tells it to work with the current activity
                .load(imageUrl) //image source for Glide to load.
                .placeholder(R.drawable.placeholder_image)//sets a placeholder image that will be displayed in the ImageView while Glide is loading the actual image
                .into(recipeImagePreview);//load the fetched image into the  ImageView





    }



    //add viewable and editable fields to relevent container (step/ingrediant) base on identifier(predifined constatn top of the code)
    private void addViewAndEditText(String ingredient,int identifier) {
        // Create a new EditText for the ingredient or method
        EditText viewEditText = new EditText(this);

        // Set the layout parameters for the EditText
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                dpToPx(330), // Width set to 330dp
                dpToPx(48)   // Height set to 48dp
        );
        params.setMargins(0, dpToPx(3), 0, dpToPx(16)); // Margins for spacing
        viewEditText.setLayoutParams(params);

        // Set the style, background, and hint for the EditText
        viewEditText.setBackgroundResource(R.drawable.bg_textfield); // Set background drawable
        //EditText.setHint("Type an ingredient");
        viewEditText.setPadding(dpToPx(12), dpToPx(0), dpToPx(0), dpToPx(0)); // Padding as in XML
        viewEditText.setTextSize(12); // Set text size in SP
        viewEditText.setTextColor(ContextCompat.getColor(this, R.color.black)); // Set text color
        viewEditText.setSingleLine(true); // Ensure single line input

        // Set a custom typeface
        Typeface typeface = ResourcesCompat.getFont(this, R.font.poppins_semibold);
        viewEditText.setTypeface(typeface);

        // Set the  text
        viewEditText.setText(ingredient);



        // Add the EditText to the relevant container
        if(identifier==1){
            ingredientContainer.addView(viewEditText);
        }else if(identifier == 2){
            stepsContainer.addView(viewEditText);
        }
    }
}
//IM-2021-018 end