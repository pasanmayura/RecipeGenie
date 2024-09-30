package com.example.recipegenie;

import static androidx.core.content.ContextCompat.startActivity;

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
            savedHolder.ratingCard.setText(String.valueOf(recipe.getRating()));

            // Load image using Picasso
            Picasso.get().load(recipe.getImageUrl()).into(savedHolder.imageViewCard);

        } else {
            MyRecipeViewHolder myHolder = (MyRecipeViewHolder) holder;
            myHolder.nameCard.setText(recipe.getTitle());
            myHolder.typeCard.setText(recipe.getMeal());
            myHolder.servingInfoCard.setText(recipe.getServingInfo());
            myHolder.timeCard.setText(recipe.getCooktime());

            // Load image using Picasso
            Picasso.get().load(recipe.getImageUrl()).into(myHolder.imageViewCard);

            // Implement your logic for Edit and Delete buttons
//            myHolder.imgEdit.setOnClickListener(v -> {
//                Intent intent = new Intent(context, Profile.class);
//                context.startActivity(intent);
//                // Handle edit action
//            });

            myHolder.imgDelete.setOnClickListener(v -> {
                if (recipe.getRecipeID() != null && !recipe.getRecipeID().isEmpty()) {
                    // Pass the correct recipeID and position to deleteRecipe()
                    deleteRecipe(recipe.getRecipeID(), recipe.getImageUrl(), position);
                } else {
                    Log.e("RecipeAdapter", "Recipe ID is null or empty. Cannot delete recipe.");
                }
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

    // Updated deleteRecipe method
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
            // File deleted successfully
            Log.d("RecipeAdapter", "Image deleted successfully from storage.");
        }).addOnFailureListener(exception -> {
            // Handle any errors
            Log.e("RecipeAdapter", "Failed to delete image: " + exception.getMessage());
        });
    }

}

