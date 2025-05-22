using System.ComponentModel.DataAnnotations;

namespace SmakApi.Models.DTOs;

public class CreateRecipeDto
{
    [Required]
    [MaxLength(100)]
    public string Title { get; set; } = null!;

    [Required]
    [MaxLength(1000)]
    public string Description { get; set; } = null!;

    [Required]
    public int CookTimeMinutes { get; set; }

    [Required]
    public Guid CategoryId { get; set; }

    public IFormFile? Image { get; set; }

    [Required]
    public List<CreateIngredientDto> Ingredients { get; set; } = new();

    [Required]
    public List<CreateInstructionDto> Instructions { get; set; } = new();
}