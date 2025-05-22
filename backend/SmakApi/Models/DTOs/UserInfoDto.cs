namespace SmakApi.Models.DTOs;

public class UserInfoDto
{
    public Guid Id { get; set; }
    public string Name { get; set; } = null!;
    public string Email { get; set; } = null!;
    public string? AvatarUrl { get; set; }
    public string Role { get; set; } = null!;
}
