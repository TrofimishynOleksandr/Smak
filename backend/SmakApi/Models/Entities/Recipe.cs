using System.ComponentModel.DataAnnotations;

namespace SmakApi.Models.Entities;

public class Recipe
{
    public Guid Id { get; set; }
    [MaxLength(150)]
    public string Title { get; set; } = null!;
    [MaxLength(1000)]
    public string Description { get; set; } = null!;
    [MaxLength(255)]
    public string? ImageUrl { get; set; }
    public int CookTimeMinutes { get; set; }
    public DateTime CreatedAt { get; set; }

    public Guid AuthorId { get; set; }
    public User Author { get; set; } = null!;

    public Guid CategoryId { get; set; }
    public Category Category { get; set; } = null!;

    public ICollection<IngredientItem> Ingredients { get; set; } = new List<IngredientItem>();
    public ICollection<InstructionStep> Instructions { get; set; } = new List<InstructionStep>();

    public ICollection<Review> Reviews { get; set; } = new List<Review>();
    public ICollection<Favorite> Favorites { get; set; } = new List<Favorite>();
}
