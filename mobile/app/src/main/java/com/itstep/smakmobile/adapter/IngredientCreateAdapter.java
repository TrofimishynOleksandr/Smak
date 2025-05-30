package com.itstep.smakmobile.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.itstep.smakmobile.R;
import com.itstep.smakmobile.dto.IngredientCreateDto;
import com.itstep.smakmobile.dto.IngredientDto;
import com.itstep.smakmobile.dto.UnitDto;
import com.itstep.smakmobile.utils.SimpleTextWatcher;

import java.util.List;
import java.util.stream.Collectors;

public class IngredientCreateAdapter extends RecyclerView.Adapter<IngredientCreateAdapter.IngredientCreateViewHolder> {

    private final List<IngredientCreateDto> ingredients;
    private final List<IngredientDto> availableIngredients;
    private final List<UnitDto> availableUnits;

    public IngredientCreateAdapter(List<IngredientCreateDto> ingredients,
                             List<IngredientDto> availableIngredients,
                             List<UnitDto> availableUnits) {
        this.ingredients = ingredients;
        this.availableIngredients = availableIngredients;
        this.availableUnits = availableUnits;
    }

    public void addEmpty() {
        ingredients.add(new IngredientCreateDto(null, null, null));
        notifyItemInserted(ingredients.size() - 1);
    }

    public List<IngredientCreateDto> getIngredients() {
        return ingredients;
    }

    @NonNull
    @Override
    public IngredientCreateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ingredient, parent, false);
        return new IngredientCreateViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull IngredientCreateViewHolder holder, int position) {
        IngredientCreateDto model = ingredients.get(position);

        ArrayAdapter<String> ingredientAdapter = new ArrayAdapter<>(
                holder.itemView.getContext(),
                android.R.layout.simple_spinner_item,
                availableIngredients.stream().map(IngredientDto::getName).collect(Collectors.toList())
        );
        ingredientAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.ingredientSpinner.setAdapter(ingredientAdapter);

        holder.ingredientSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                model.setName(availableIngredients.get(pos).getName());
            }

            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        holder.amountInput.setText(model.getQuantity() != null ? String.valueOf(model.getQuantity()) : "");
        holder.amountInput.addTextChangedListener(new SimpleTextWatcher(() -> {
            try {
                model.setQuantity(Float.parseFloat(holder.amountInput.getText().toString()));
            } catch (NumberFormatException e) {
                model.setQuantity(null);
            }
        }));

        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(
                holder.itemView.getContext(),
                android.R.layout.simple_spinner_item,
                availableUnits.stream().map(UnitDto::getLabel).collect(Collectors.toList())
        );
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.unitSpinner.setAdapter(unitAdapter);

        holder.unitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                model.setUnit(availableUnits.get(pos).getValue());
            }

            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    public static class IngredientCreateViewHolder extends RecyclerView.ViewHolder {
        Spinner ingredientSpinner, unitSpinner;
        EditText amountInput;

        public IngredientCreateViewHolder(@NonNull View itemView) {
            super(itemView);
            ingredientSpinner = itemView.findViewById(R.id.spinnerIngredient);
            unitSpinner = itemView.findViewById(R.id.spinnerUnit);
            amountInput = itemView.findViewById(R.id.inputAmount);
        }
    }

    public List<IngredientCreateDto> getItems() {
        return ingredients;
    }
}
