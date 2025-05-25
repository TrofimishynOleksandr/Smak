using SmakApi.Models.DTOs;

namespace SmakApi.Services.Review;

public interface IReviewService
{
    Task<List<MyReviewDto>> GetUserReviewsAsync(Guid userId);
    Task UpdateReviewAsync(Guid userId, ReviewRequest dto);
    Task DeleteReviewAsync(Guid recipeId, Guid userId);
}
