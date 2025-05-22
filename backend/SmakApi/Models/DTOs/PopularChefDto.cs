namespace SmakApi.Models.DTOs;

public class PopularChefDto
{
    public Guid Id { get; set; }
    public string Name { get; set; } = null!;
    public int RecipesCount { get; set; }
    public double AverageRating { get; set; }
}
