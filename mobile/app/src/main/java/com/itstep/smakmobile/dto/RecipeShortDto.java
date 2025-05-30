package com.itstep.smakmobile.dto;


import java.util.UUID;

public class RecipeShortDto {
    public UUID id;
    public String title;
    public String description;
    public String imageUrl;
    public int cookTimeMinutes;
    public String authorName;
    public double averageRating;
    public int ratingsCount;
    public boolean isFavorite;

    public RecipeShortDto(String title, String description, String imageUrl, int cookTimeMinutes, String authorName, double averageRating, int ratingsCount, boolean isFavorite) {
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.cookTimeMinutes = cookTimeMinutes;
        this.authorName = authorName;
        this.averageRating = averageRating;
        this.ratingsCount = ratingsCount;
        this.isFavorite = isFavorite;
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public int getCookTimeMinutes() {
        return cookTimeMinutes;
    }

    public String getAuthorName() {
        return authorName;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public int getRatingsCount() {
        return ratingsCount;
    }

    public boolean isFavorite() {
        return isFavorite;
    }
}
