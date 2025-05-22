namespace SmakApi.Models.DTOs;

public class RecipeSearchQuery
{
    public string? Search { get; set; }
    public Guid? CategoryId { get; set; }
    public string? AuthorName { get; set; }
    public Guid? AuthorId { get; set; }
    public int? MaxCookTime { get; set; }
    public double? MinRating { get; set; }
    public bool? OnlyFavorites { get; set; }
    public string? SortBy { get; set; } // "rating", "time", "popular"
}

