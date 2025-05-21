using System.ComponentModel.DataAnnotations;

namespace SmakApi.Models.Entities;

public class Comment
{
    public Guid Id { get; set; }
    [MaxLength(1000)]
    public string Content { get; set; } = null!;
    public DateTime CreatedAt { get; set; }

    public Guid UserId { get; set; }
    public User User { get; set; } = null!;

    public Guid RecipeId { get; set; }
    public Recipe Recipe { get; set; } = null!;
}
