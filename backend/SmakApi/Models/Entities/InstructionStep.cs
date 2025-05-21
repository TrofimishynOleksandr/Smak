using System.ComponentModel.DataAnnotations;

namespace SmakApi.Models.Entities;

public class InstructionStep
{
    public Guid Id { get; set; }
    public int StepNumber { get; set; }
    [MaxLength(1000)]
    public string Description { get; set; } = null!;
    [MaxLength(255)]
    public string? ImageUrl { get; set; }

    public Guid RecipeId { get; set; }
    public Recipe Recipe { get; set; } = null!;
}
