package com.itstep.smakmobile.dto;

public class IngredientItemDto {
    public String name;
    public Float quantity;
    public String unit;

    public String getName() {
        return name;
    }

    public Float getQuantity() {
        return quantity;
    }

    public String getUnit() {
        return unit;
    }

    public String getAmountWithUnit() {
        return String.format("%.0f %s", quantity, unit);
    }
}
