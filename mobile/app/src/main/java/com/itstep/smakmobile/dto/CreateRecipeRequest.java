package com.itstep.smakmobile.dto;

import java.util.List;
import java.util.UUID;

public class CreateRecipeRequest {
    public String title;
    public String description;
    public int cookTimeMinutes;
    public UUID categoryId;
    public List<IngredientCreateDto> ingredients;
    public List<InstructionCreateDto> instructions;
}

