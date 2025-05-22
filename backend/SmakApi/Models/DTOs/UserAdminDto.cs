namespace SmakApi.Models.DTOs;

public class UserAdminDto
{
    public Guid Id { get; set; }
    public string Name { get; set; } = null!;
    public string Email { get; set; } = null!;
    public string Role { get; set; } = null!;
    public DateTime CreatedAt { get; set; }
    public string? AvatarUrl { get; set; }
}
