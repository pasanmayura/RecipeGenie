package com.example.recipegenie;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.widget.Toast;

import java.util.List;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class RecipeAdapterSaved extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // Declare constants here
    public static final int VIEW_TYPE_SAVED_RECIPE = 0;
    public static final int VIEW_TYPE_MY_RECIPE = 1;

    private Context context;
    private List<Recipe> recipeList;
    private int viewType;
    Button no_button_delete, yes_button_delete;
    Dialog dialog_recipe_delete;
    String recipeID;
    private DatabaseReference databaseReference;

    // Constructor 1
    public RecipeAdapterSaved(Context context, List<Recipe> recipeList, int viewType) {
        this.recipeList = recipeList;
        this.viewType = viewType;
        this.context = context;
        databaseReference = FirebaseDatabase.getInstance().getReference("Recipe");  // Initialize here
    }

    // Constructor 2
    public RecipeAdapterSaved(Context context, List<Recipe> recipeList) {
        this.context = context;
        this.recipeList = recipeList;
        databaseReference = FirebaseDatabase.getInstance().getReference("Recipe");
    }

    @Override
    public int getItemViewType(int position) {
        return viewType;  // Return the view type based on what was passed in the constructor
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_SAVED_RECIPE) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe, parent, false);
            return new SavedRecipeViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_your_recipes, parent, false);
            return new MyRecipeViewHolder(view);
        }
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Recipe recipe = recipeList.get(position);

        if (holder.getItemViewType() == VIEW_TYPE_SAVED_RECIPE) {
            SavedRecipeViewHolder savedHolder = (SavedRecipeViewHolder) holder;
            savedHolder.nameCard.setText(recipe.getTitle());
            savedHolder.typeCard.setText(recipe.getMeal());
            savedHolder.servingInfoCard.setText(recipe.getServingInfo());
            savedHolder.timeCard.setText(recipe.getCooktime());
            savedHolder.ratingCard.setText(String.valueOf(recipe.getAverageRating()));

            // Load image using Picasso
            Picasso.get().load(recipe.getImageUrl()).into(savedHolder.imageViewCard);

            // On card click, pass the recipeID to the ViewRecipe activity
            savedHolder.itemView.setOnClickListener(view -> {

                Log.d("RecipeAdapter", "Clicked Recipe ID: " + recipe.getRecipeID());

                Intent intent = new Intent(context, ViewRecipe.class);
                intent.putExtra("RECIPE_ID", recipe.getRecipeID());  // Pass the recipeID
                context.startActivity(intent);  // Start the new activity
            });

        } else {
            MyRecipeViewHolder myHolder = (MyRecipeViewHolder) holder;
            myHolder.nameCard.setText(recipe.getTitle());
            myHolder.typeCard.setText(recipe.getMeal());
            myHolder.servingInfoCard.setText(recipe.getServingInfo());
            myHolder.timeCard.setText(recipe.getCooktime());

            // Load image using Picasso
            Picasso.get().load(recipe.getImageUrl()).into(myHolder.imageViewCard);

            myHolder.itemView.setOnClickListener(view -> {

                Log.d("RecipeAdapter", "Clicked Recipe ID: " + recipe.getRecipeID());

                Intent intent = new Intent(context, ViewRecipe.class);
                intent.putExtra("RECIPE_ID", recipe.getRecipeID());  // Pass the recipeID
                context.startActivity(intent);  // Start the new activity
            });

            // IM/2021/020 - M.A.P.M Karunathilaka

            myHolder.imgDelete.setOnClickListener(v -> {
                // Show the confirmation dialog
                dialog_recipe_delete = new Dialog(context);
                dialog_recipe_delete.setContentView(R.layout.delete_recipe);
                dialog_recipe_delete.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog_recipe_delete.getWindow().setBackgroundDrawable(context.getDrawable(R.drawable.dialogbox_bg));
                dialog_recipe_delete.setCancelable(false); // Prevent dismissal by tapping outside

                // Initialize the yes, no buttons in delete dialog
                yes_button_delete = dialog_recipe_delete.findViewById(R.id.yes_button_delete);
                no_button_delete = dialog_recipe_delete.findViewById(R.id.no_button_delete);

                // Handle No button - dismiss dialog
                no_button_delete.setOnClickListener(view -> dialog_recipe_delete.dismiss());

                // Handle Yes button - confirm delete
                yes_button_delete.setOnClickListener(view -> {
                    recipeID = recipe.getRecipeID();
                    if (recipeID != null && !recipeID.isEmpty()) {
                        // Call the delete method only after confirmation
                        deleteRecipe(recipeID, recipe.getImageUrl(), position);
                        dialog_recipe_delete.dismiss();  // Close dialog once deletion is done
                    } else {
                        Log.e("RecipeAdapter", "Recipe ID is null or empty. Cannot delete recipe.");
                    }
                });

                // Show the dialog
                dialog_recipe_delete.show();
            });

            // IM/2021/020 - M.A.P.M Karunathilaka


            myHolder.imgEdit.setOnClickListener(v -> {
                // Get the recipe ID of the clicked item
                String recipeID = recipeList.get(myHolder.getAdapterPosition()).getRecipeID();
                Log.d("RecipeAdapterSaved", "sending Recipe ID: " + recipeID);

                // Create an Intent to navigate to the activity
                Intent intent = new Intent(context, editRecipe.class);

                // Add the recipe ID
                intent.putExtra("RECIPE_ID", recipeID);

                context.startActivity(intent);
            });

        }
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    // ViewHolder for Saved Recipes
    public static class SavedRecipeViewHolder extends RecyclerView.ViewHolder {
        TextView nameCard, typeCard, servingInfoCard, timeCard, ratingCard;
        ImageView imageViewCard;

        public SavedRecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            nameCard = itemView.findViewById(R.id.recipe_name);
            typeCard = itemView.findViewById(R.id.recipe_type);
            servingInfoCard = itemView.findViewById(R.id.recipe_serving_info);
            timeCard = itemView.findViewById(R.id.cook_time);
            ratingCard = itemView.findViewById(R.id.recipe_rating);
            imageViewCard = itemView.findViewById(R.id.recipe_image);
        }
    }

    // ViewHolder for My Recipes
    public static class MyRecipeViewHolder extends RecyclerView.ViewHolder {
        TextView nameCard, typeCard, servingInfoCard, timeCard;
        ImageView imageViewCard, imgEdit, imgDelete;

        public MyRecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            nameCard = itemView.findViewById(R.id.recipe_name);
            typeCard = itemView.findViewById(R.id.recipe_type);
            servingInfoCard = itemView.findViewById(R.id.recipe_serving_info);
            timeCard = itemView.findViewById(R.id.cook_time);
            imageViewCard = itemView.findViewById(R.id.recipe_image);
            imgEdit = itemView.findViewById(R.id.img_edit);
            imgDelete = itemView.findViewById(R.id.img_delete);
        }
    }

    private void deleteRecipe(String recipeId, String imageUrl, int position) {
        // Reference to the specific recipe in the database
        databaseReference.child(recipeId).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Delete the image from Firebase Storage
                deleteImageFromStorage(imageUrl);
                Toast.makeText(context, "Recipe deleted successfully", Toast.LENGTH_SHORT).show();
                recipeList.remove(position);
                notifyItemRemoved(position);
            } else {
                Toast.makeText(context, "Failed to delete recipe", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteImageFromStorage(String imageUrl) {
        // Create a reference to the image to delete
        StorageReference imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
        imageRef.delete().addOnSuccessListener(aVoid -> {
            Log.d("RecipeAdapter", "Image deleted successfully from storage.");
        }).addOnFailureListener(exception -> {
            // Handle any errors
            Log.e("RecipeAdapter", "Failed to delete image: " + exception.getMessage());
        });
    }
}

