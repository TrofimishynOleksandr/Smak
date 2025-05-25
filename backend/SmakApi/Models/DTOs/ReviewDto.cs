namespace SmakApi.Models.DTOs;

public class ReviewDto
{
    public string Author { get; set; } = null!;
    public Guid AuthorId { get; set; }
    public string? AvatarUrl { get; set; }
    public int Rating { get; set; }
    public string? Comment { get; set; }
    public DateTime CreatedAt { get; set; }
}