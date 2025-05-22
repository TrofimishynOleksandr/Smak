using System.ComponentModel.DataAnnotations;
using SmakApi.Models.Entities;

namespace SmakApi.Models.DTOs;

public class RegisterRequest
{
    [Required]
    [MaxLength(100)]
    public string Name { get; set; } = null!;

    [Required]
    [EmailAddress]
    public string Email { get; set; } = null!;

    [Required]
    [MinLength(8, ErrorMessage = "Пароль має бути щонайменше 8 символів")]
    [RegularExpression(@"^(?=.*[A-Za-z])(?=.*\d).+$",
        ErrorMessage = "Пароль має містити щонайменше одну букву і одну цифру")]
    public string Password { get; set; } = null!;
}

