using System.ComponentModel.DataAnnotations;

namespace SmakApi.Models.Entities;

public class IngredientItem
{
    public Guid Id { get; set; }
    public float? Quantity { get; set; }
    public UnitOfMeasure? Unit { get; set; }
    
    public Guid RecipeId { get; set; }
    public Recipe Recipe { get; set; } = null!;
    
    public Guid IngredientId { get; set; }
    public Ingredient Ingredient { get; set; } = null!;
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