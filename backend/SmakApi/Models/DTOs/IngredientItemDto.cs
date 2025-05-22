using SmakApi.Models.Entities;

namespace SmakApi.Models.DTOs;

public class IngredientItemDto
{
    public string Name { get; set; } = null!;
    public float? Quantity { get; set; }
    public UnitOfMeasure? Unit { get; set; }
}