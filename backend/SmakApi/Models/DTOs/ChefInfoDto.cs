namespace SmakApi.Models.DTOs;

public class ChefInfoDto
{
    public Guid Id { get; set; }
    public string Name { get; set; } = null!;
    public string? AvatarUrl { get; set; }
}