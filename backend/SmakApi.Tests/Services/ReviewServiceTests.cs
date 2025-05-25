namespace SmakApi.Tests.Services;

using Microsoft.EntityFrameworkCore;
using SmakApi.Data;
using SmakApi.Models.DTOs;
using SmakApi.Models.Entities;
using SmakApi.Services.Review;

public class ReviewServiceTests
{
    private SmakDbContext GetDbContext()
    {
        var options = new DbContextOptionsBuilder<SmakDbContext>()
            .UseInMemoryDatabase(databaseName: Guid.NewGuid().ToString())
            .Options;
        return new SmakDbContext(options);
    }

    [Fact]
    public async Task GetUserReviewsAsync_ShouldReturnUserReviews()
    {
        var dbContext = GetDbContext();

        var userId = Guid.NewGuid();
        var recipe = new Recipe { Id = Guid.NewGuid(), Title = "Pizza", Description = "Simple desc", };
        var review = new Review
        {
            Id = Guid.NewGuid(),
            UserId = userId,
            RecipeId = recipe.Id,
            Rating = 4,
            Comment = "Nice",
            CreatedAt = DateTime.UtcNow,
            Recipe = recipe
        };

        dbContext.Recipes.Add(recipe);
        dbContext.Reviews.Add(review);
        await dbContext.SaveChangesAsync();

        var service = new ReviewService(dbContext);
        var result = await service.GetUserReviewsAsync(userId);

        Assert.Single(result);
        Assert.Equal("Pizza", result[0].RecipeTitle);
    }

    [Fact]
    public async Task UpdateReviewAsync_ShouldUpdateExistingReview()
    {
        var dbContext = GetDbContext();

        var userId = Guid.NewGuid();
        var recipeId = Guid.NewGuid();
        dbContext.Reviews.Add(new Review
        {
            Id = Guid.NewGuid(),
            UserId = userId,
            RecipeId = recipeId,
            Rating = 2,
            Comment = "Old",
            CreatedAt = DateTime.UtcNow
        });
        await dbContext.SaveChangesAsync();

        var service = new ReviewService(dbContext);
        var dto = new ReviewRequest
        {
            RecipeId = recipeId,
            Rating = 5,
            Comment = "Updated"
        };

        await service.UpdateReviewAsync(userId, dto);

        var updated = await dbContext.Reviews.FirstAsync();
        Assert.Equal(5, updated.Rating);
        Assert.Equal("Updated", updated.Comment);
    }

    [Fact]
    public async Task DeleteReviewAsync_ShouldRemoveReview()
    {
        var dbContext = GetDbContext();

        var userId = Guid.NewGuid();
        var recipeId = Guid.NewGuid();
        var review = new Review
        {
            Id = Guid.NewGuid(),
            UserId = userId,
            RecipeId = recipeId,
            Rating = 3,
            Comment = "Good"
        };

        dbContext.Reviews.Add(review);
        await dbContext.SaveChangesAsync();

        var service = new ReviewService(dbContext);
        await service.DeleteReviewAsync(recipeId, userId);

        var exists = await dbContext.Reviews.AnyAsync();
        Assert.False(exists);
    }
}
