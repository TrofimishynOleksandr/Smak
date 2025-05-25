namespace SmakApi.Models.DTOs;

public class IngredientDto
{
    public Guid Id { get; set; }
    public string Name { get; set; } = null!;
    public bool IsCustom { get; set; }
}