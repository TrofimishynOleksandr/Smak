namespace SmakApi.Models.DTOs;

public class RecipeDetailsDto
{
    public Guid Id { get; set; }
    public string Title { get; set; } = null!;
    public string Description { get; set; } = null!;
    public string? ImageUrl { get; set; }
    public int CookTimeMinutes { get; set; }
    public Guid AuthorId { get; set; }
    public string AuthorName { get; set; } = null!;
    public string CategoryName { get; set; } = null!;
    public List<IngredientItemDto> Ingredients { get; set; } = new();
    public List<InstructionStepDto> Instructions { get; set; } = new();
    public double AverageRating { get; set; }
    public int RatingsCount { get; set; }

    public List<ReviewDto> Reviews { get; set; } = new();
    public bool IsFavorite { get; set; }
}