package com.example.recipegenie;

import android.content.Context;
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

    public RecipeAdapter(List<Recipe> recipeList) {
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

        // Load image using Picasso
        Picasso.get().load(recipe.getImageUrl()).into(holder.imageViewCard);
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    public static class RecipeViewHolder extends RecyclerView.ViewHolder {

        TextView mealCard, titleCard, servinginfoCard, cooktimeCard;
        ImageView imageViewCard;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            mealCard = itemView.findViewById(R.id.mealCard);
            titleCard = itemView.findViewById(R.id.titleCard);
            servinginfoCard = itemView.findViewById(R.id.servinginfoCard);
            cooktimeCard = itemView.findViewById(R.id.cooktimeCard);
            imageViewCard = itemView.findViewById(R.id.imageViewCard);
        }
    }
}


