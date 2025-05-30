package com.itstep.smakmobile.dto;

import java.util.UUID;

public class IngredientCreateDto {
    private String name;
    private Float quantity;
    private Integer unit;

    public IngredientCreateDto(String name, Float quantity, Integer unit) {
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getQuantity() {
        return quantity;
    }

    public void setQuantity(Float quantity) {
        this.quantity = quantity;
    }

    public Integer getUnit() {
        return unit;
    }

    public void setUnit(Integer unit) {
        this.unit = unit;
    }
}

