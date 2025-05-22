namespace SmakApi.Models.DTOs;

public class CreateInstructionDto
{
    public string Description { get; set; } = null!;
    public IFormFile? Image { get; set; }
}