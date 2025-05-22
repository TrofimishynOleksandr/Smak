using System.ComponentModel.DataAnnotations;

namespace SmakApi.Models.DTOs;

public class UpdateNameDto
{
    [Required]
    [MaxLength(100)]
    public string Name { get; set; } = null!;
}
