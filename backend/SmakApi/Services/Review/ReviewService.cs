using Microsoft.EntityFrameworkCore;
using SmakApi.Data;
using SmakApi.Exceptions;
using SmakApi.Models.DTOs;

namespace SmakApi.Services.Review;

public class ReviewService : IReviewService
{
    private readonly SmakDbContext _context;

    public ReviewService(SmakDbContext context)
    {
        _context = context;
    }

    public async Task<List<MyReviewDto>> GetUserReviewsAsync(Guid userId)
    {
        var reviews = await _context.Reviews
            .Include(r => r.Recipe)
            .Where(r => r.UserId == userId)
            .OrderByDescending(r => r.CreatedAt)
            .ToListAsync();

        return reviews.Select(r => new MyReviewDto
        {
            Rating = r.Rating,
            Comment = r.Comment,
            CreatedAt = r.CreatedAt,
            RecipeTitle = r.Recipe.Title
        }).ToList();
    }

    public async Task UpdateReviewAsync(Guid userId, ReviewRequest dto)
    {
        var review = await _context.Reviews
                         .FirstOrDefaultAsync(r => r.UserId == userId && r.RecipeId == dto.RecipeId)
                     ?? throw new CustomException("Review not found", 404);

        review.Rating = dto.Rating;
        review.Comment = dto.Comment;
        review.CreatedAt = DateTime.UtcNow;

        await _context.SaveChangesAsync();
    }

    public async Task DeleteReviewAsync(Guid recipeId, Guid userId)
    {
        var review = await _context.Reviews
                         .FirstOrDefaultAsync(r => r.UserId == userId && r.RecipeId == recipeId)
                     ?? throw new CustomException("Review not found", 404);

        _context.Reviews.Remove(review);
        await _context.SaveChangesAsync();
    }
}
