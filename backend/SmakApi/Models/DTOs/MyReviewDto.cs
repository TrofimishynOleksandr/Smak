namespace SmakApi.Models.DTOs;

public class MyReviewDto
{
    public int Rating { get; set; }
    public string? Comment { get; set; }
    public DateTime CreatedAt { get; set; }
    public string RecipeTitle { get; set; } = null!;
}
