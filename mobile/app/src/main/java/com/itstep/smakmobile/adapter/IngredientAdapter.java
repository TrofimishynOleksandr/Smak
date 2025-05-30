package com.itstep.smakmobile.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.itstep.smakmobile.R;
import com.itstep.smakmobile.dto.IngredientItemDto;

import java.util.List;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.ViewHolder> {
    private List<IngredientItemDto> ingredients;

    public IngredientAdapter(List<IngredientItemDto> ingredients) {
        this.ingredients = ingredients;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView quantity;

        public ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.ingredientName);
            quantity = view.findViewById(R.id.ingredientQuantity);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ingredient, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        IngredientItemDto ingredient = ingredients.get(position);
        holder.name.setText(ingredient.getName());
        holder.quantity.setText(ingredient.getAmountWithUnit());
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }
}

