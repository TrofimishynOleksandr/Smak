package com.itstep.smakmobile.dto;

import java.util.UUID;

public class RecipeSearchQuery {
    public String search;
    public UUID categoryId;
    public String authorName;
    public UUID authorId;
    public Integer maxCookTime;
    public Double minRating;
    public Boolean onlyFavorites;
    public String sortBy;
}

