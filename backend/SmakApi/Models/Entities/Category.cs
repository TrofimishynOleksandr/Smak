using System.ComponentModel.DataAnnotations;

namespace SmakApi.Models.Entities;

public class Category
{
    public Guid Id { get; set; }
    [MaxLength(100)]
    public string Name { get; set; } = null!;
    [MaxLength(255)]
    public string? ImageUrl { get; set; }

    public ICollection<Recipe> Recipes { get; set; } = new List<Recipe>();
}
