using System.ComponentModel.DataAnnotations;

namespace SmakApi.Models.DTOs;

public class CreateCategoryDto
{
    [Required]
    public string Name { get; set; } = null!;

    public IFormFile? Image { get; set; }
}
