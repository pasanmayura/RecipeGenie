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

    public RecipeAdapterSaved(Context context, List<Recipe> recipeList, int viewType) {
        this.recipeList = recipeList;
        this.viewType = viewType;
        this.context = context;
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
            myHolder.imgEdit.setOnClickListener(v -> {
                Intent intent = new Intent(context, Profile.class);
                context.startActivity(intent);
                // Handle edit action
            });

            myHolder.imgDelete.setOnClickListener(v -> {
                dialog_recipe_delete = new Dialog(context);
                dialog_recipe_delete.setContentView(R.layout.delete_recipe);
                dialog_recipe_delete.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog_recipe_delete.getWindow().setBackgroundDrawable(context.getDrawable(R.drawable.dialogbox_bg));
                dialog_recipe_delete.setCancelable(false);

                // initialize the yes, no buttons in delete account dialog box
                yes_button_delete = dialog_recipe_delete.findViewById(R.id.yes_button_delete);
                no_button_delete = dialog_recipe_delete.findViewById(R.id.no_button_delete);

                no_button_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog_recipe_delete.dismiss(); // close delete dialog
                    }
                });

                yes_button_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        recipeID = recipe.getRecipeID();

                        DatabaseReference recipeRef = FirebaseDatabase.getInstance().getReference("recipes").child(recipeID);

                        // Delete the recipe from Firebase
                        recipeRef.removeValue().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(context, "Recipe deleted successfully!", Toast.LENGTH_SHORT).show();

                                // Remove from local list and notify adapter
                                recipeList.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, recipeList.size());

                                dialog_recipe_delete.dismiss(); // Close the dialog
                            } else {
                                Toast.makeText(context, "Recipe deletion failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
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
}
