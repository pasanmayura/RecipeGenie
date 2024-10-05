// IM/2021/020 - M.A.P.M Karunathilaka

package com.example.recipegenie;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private List<Recipe> recipeList;
    private Context context;  // Store the context to use for starting the new activity

    public RecipeAdapter(Context context, List<Recipe> recipeList) {
        this.context = context;  // Initialize the context in the constructor
        this.recipeList = recipeList;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item_layout, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = recipeList.get(position);
        holder.mealCard.setText(recipe.getMeal());
        holder.titleCard.setText(recipe.getTitle());
        holder.servinginfoCard.setText(recipe.getServingInfo());
        holder.cooktimeCard.setText(recipe.getCooktime());
        holder.ratingCard.setText(String.valueOf(recipe.getAverageRating()));

        // Load image using Picasso
        Picasso.get().load(recipe.getImageUrl()).into(holder.imageViewCard);

        // Set click listener to open ViewRecipe activity
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("RecipeAdapter", "Clicked Recipe ID: " + recipe.getRecipeID());

                // Pass the recipeID to ViewRecipe activity
                Intent intent = new Intent(context, ViewRecipe.class);
                intent.putExtra("RECIPE_ID", recipe.getRecipeID());  // Assuming the Recipe class has getRecipeID()
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    public static class RecipeViewHolder extends RecyclerView.ViewHolder {

        TextView mealCard, titleCard, servinginfoCard, cooktimeCard, ratingCard;
        ImageView imageViewCard;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            mealCard = itemView.findViewById(R.id.mealCard);
            titleCard = itemView.findViewById(R.id.titleCard);
            servinginfoCard = itemView.findViewById(R.id.servinginfoCard);
            cooktimeCard = itemView.findViewById(R.id.cooktimeCard);
            imageViewCard = itemView.findViewById(R.id.imageViewCard);
            ratingCard = itemView.findViewById(R.id.ratingCard);

        }
    }
}
// IM/2021/020 - M.A.P.M Karunathilaka



