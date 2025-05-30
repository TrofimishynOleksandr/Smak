package com.itstep.smakmobile.dto;

import java.util.List;
import java.util.UUID;

public class RecipeDetailsDto {
    public UUID id;
    public String title;
    public String description;
    public String imageUrl;
    public int cookTimeMinutes;
    public UUID authorId;
    public String authorName;
    public String categoryName;
    public List<IngredientItemDto> ingredients;
    public List<InstructionStepDto> instructions;
    public double averageRating;
    public int ratingsCount;
    public List<ReviewDto> reviews;
    public boolean isFavorite;
}
