using System.ComponentModel.DataAnnotations;

namespace SmakApi.Models.Entities;

public class IngredientItem
{
    public Guid Id { get; set; }
    [MaxLength(100)]
    public string Name { get; set; } = null!;
    public float? Quantity { get; set; }
    public UnitOfMeasure? Unit { get; set; }     // Наприклад: "г", "мл", "шт", null
    
    public Guid RecipeId { get; set; }
    public Recipe Recipe { get; set; } = null!;
}

public enum UnitOfMeasure
{
    Piece,     // шт
    Gram,      // г
    Kilogram,  // кг
    Milliliter,// мл
    Liter,     // л
    Teaspoon,  // ч.л.
    Tablespoon,// ст.л.
    Cup,       // склянка
    Pinch,     // щіпка
    ToTaste    // за смаком
}