namespace SmakApi.Models.DTOs;

public class RecipeCollectionDto
{
    public string Title { get; set; } = null!;
    public List<RecipeShortDto> Recipes { get; set; } = new();
}