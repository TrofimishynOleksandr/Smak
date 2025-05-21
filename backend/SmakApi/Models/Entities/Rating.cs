using System.ComponentModel.DataAnnotations;

namespace SmakApi.Models.Entities;

public class Rating
{
    public Guid Id { get; set; }
    [Range(1, 5)]
    public int Value { get; set; }

    public Guid UserId { get; set; }
    public User User { get; set; } = null!;

    public Guid RecipeId { get; set; }
    public Recipe Recipe { get; set; } = null!;
}
