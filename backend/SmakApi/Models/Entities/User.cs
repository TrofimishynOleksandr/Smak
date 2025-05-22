using System.ComponentModel.DataAnnotations;

namespace SmakApi.Models.Entities;

public class User
{
    public Guid Id { get; set; }
    [MaxLength(100)]
    public string Name { get; set; } = null!;
    [MaxLength(255)]
    public string Email { get; set; } = null!;
    [MaxLength(255)]
    public string PasswordHash { get; set; } = null!;
    public string? AvatarUrl { get; set; }
    public UserRole Role { get; set; } = UserRole.User;
    public DateTime CreatedAt { get; set; }

    public ICollection<Recipe> Recipes { get; set; } = new List<Recipe>();
    public ICollection<Review> Reviews { get; set; } = new List<Review>();
    public ICollection<Favorite> Favorites { get; set; } = new List<Favorite>();
    public ICollection<Ingredient> CreatedIngredients { get; set; } = new List<Ingredient>();
}

public enum UserRole
{
    User,
    Chef,
    Admin
}