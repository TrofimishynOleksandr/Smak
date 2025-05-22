namespace SmakApi.Models.DTOs;

public class RecipeShortDto
{
    public Guid Id { get; set; }
    public string Title { get; set; } = null!;
    public string Description { get; set; } = null!;
    public string? ImageUrl { get; set; }
    public int CookTimeMinutes { get; set; }
    public string AuthorName { get; set; } = null!;
    public double AverageRating { get; set; }
    public int RatingsCount { get; set; }
    public bool IsFavorite { get; set; }
}