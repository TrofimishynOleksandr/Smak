namespace SmakApi.Models.DTOs;

public class ReviewRequest
{
    public int Rating { get; set; }
    public string? Comment { get; set; }
}