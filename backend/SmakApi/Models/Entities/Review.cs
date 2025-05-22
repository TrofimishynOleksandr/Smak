using System.ComponentModel.DataAnnotations;

namespace SmakApi.Models.Entities;

public class Review
{
    public Guid Id { get; set; }

    [Range(1, 5)]
    public int Rating { get; set; }
    [MaxLength(1000)]
    public string? Comment { get; set; }
    public DateTime CreatedAt { get; set; }

    public Guid UserId { get; set; }
    public User User { get; set; } = null!;
    
    public Guid RecipeId { get; set; }
    public Recipe Recipe { get; set; } = null!;
}
