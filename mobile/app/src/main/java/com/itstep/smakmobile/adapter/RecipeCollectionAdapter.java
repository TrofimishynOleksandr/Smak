package com.itstep.smakmobile.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.itstep.smakmobile.R;
import com.itstep.smakmobile.dto.RecipeCollectionDto;

import java.util.List;

public class RecipeCollectionAdapter extends RecyclerView.Adapter<RecipeCollectionAdapter.CollectionViewHolder> {

    private List<RecipeCollectionDto> collections;
    private Context context;

    public RecipeCollectionAdapter(Context context, List<RecipeCollectionDto> collections) {
        this.context = context;
        this.collections = collections;
    }

    @NonNull
    @Override
    public CollectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recipe_collection, parent, false);
        return new CollectionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CollectionViewHolder holder, int position) {
        RecipeCollectionDto collection = collections.get(position);
        holder.title.setText(collection.getTitle());

        RecipeAdapter adapter = new RecipeAdapter(context, collection.getRecipes());
        holder.recipesRecyclerView.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        return collections.size();
    }

    public static class CollectionViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        RecyclerView recipesRecyclerView;

        public CollectionViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.collectionTitle);
            recipesRecyclerView = itemView.findViewById(R.id.collectionRecipesRecyclerView);

            LinearLayoutManager layoutManager = new LinearLayoutManager(
                    itemView.getContext(), LinearLayoutManager.HORIZONTAL, false);
            recipesRecyclerView.setLayoutManager(layoutManager);
        }
    }
}
