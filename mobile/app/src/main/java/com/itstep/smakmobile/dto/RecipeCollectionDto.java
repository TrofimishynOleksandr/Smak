package com.itstep.smakmobile.dto;

import java.util.List;

public class RecipeCollectionDto {
    public String title;
    public List<RecipeShortDto> recipes;

    public RecipeCollectionDto(String title, List<RecipeShortDto> recipes) {
        this.title = title;
        this.recipes = recipes;
    }

    public String getTitle() {
        return title;
    }

    public List<RecipeShortDto> getRecipes() {
        return recipes;
    }
}
