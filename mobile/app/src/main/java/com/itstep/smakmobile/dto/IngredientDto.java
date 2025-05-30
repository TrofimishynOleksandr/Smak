package com.itstep.smakmobile.dto;

import java.util.UUID;

public class IngredientDto {
    private UUID id;
    private String name;
    private boolean isCustom;

    public UUID getId() { return id; }
    public String getName() { return name; }
    public boolean isCustom() { return isCustom; }
}
