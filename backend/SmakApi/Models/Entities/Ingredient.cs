using System.ComponentModel.DataAnnotations;

namespace SmakApi.Models.Entities;

public class Ingredient
{
    public Guid Id { get; set; }

    [MaxLength(100)]
    public string Name { get; set; } = null!;

    public Guid? CreatedByUserId { get; set; }
    public User? CreatedByUser { get; set; }

    public ICollection<IngredientItem> IngredientItems { get; set; } = new List<IngredientItem>();
}
