using System.ComponentModel.DataAnnotations;

namespace SmakApi.Models.DTOs;

public class ReviewRequest
{
    public Guid RecipeId { get; set; }
    [Range(1, 5)]
    public int Rating { get; set; }
    [MaxLength(1000)]
    public string? Comment { get; set; }
}