namespace SmakApi.Models.DTOs;

public class InstructionStepDto
{
    public int StepNumber { get; set; }
    public string Description { get; set; } = null!;
    public string? ImageUrl { get; set; }
}