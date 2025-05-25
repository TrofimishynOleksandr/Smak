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

public static class UnitExtensions
{
    public static string ToUkrainian(this UnitOfMeasure unit)
    {
        return unit switch
        {
            UnitOfMeasure.Piece => "шт.",
            UnitOfMeasure.Gram => "г",
            UnitOfMeasure.Kilogram => "кг",
            UnitOfMeasure.Milliliter => "мл",
            UnitOfMeasure.Liter => "л",
            UnitOfMeasure.Teaspoon => "ч.л.",
            UnitOfMeasure.Tablespoon => "ст.л.",
            UnitOfMeasure.Cup => "склянка",
            UnitOfMeasure.Pinch => "щіпка",
            UnitOfMeasure.ToTaste => "за смаком",
            _ => unit.ToString()
        };
    }
}