package com.itstep.smakmobile.dto;

import java.util.UUID;

public class CategoryDto
{
    public UUID id;
    public String name;
    public String imageUrl;

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
