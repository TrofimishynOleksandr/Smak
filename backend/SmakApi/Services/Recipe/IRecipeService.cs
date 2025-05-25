using SmakApi.Models.DTOs;

namespace SmakApi.Services.Recipe;

public interface IRecipeService
{
    Task<Guid> CreateAsync(Guid userId, CreateRecipeDto dto);
    Task UpdateAsync(Guid recipeId, Guid userId, UpdateRecipeDto dto);
    Task DeleteAsync(Guid recipeId, Guid userId);
    Task<RecipeDetailsDto> GetByIdAsync(Guid recipeId, Guid currentUserId);
    Task<IEnumerable<RecipeShortDto>> GetAllAsync(Guid? userId = null);
    Task<IEnumerable<RecipeShortDto>> GetFavoriteAsync(Guid? userId = null);
    Task AddToFavoritesAsync(Guid recipeId, Guid userId);
    Task RemoveFromFavoritesAsync(Guid recipeId, Guid userId);
    Task AddReviewAsync(Guid userId, ReviewRequest dto);
    Task<IEnumerable<RecipeShortDto>> SearchAsync(RecipeSearchQuery query, Guid? userId);
    Task<List<RecipeCollectionDto>> GetCollectionsAsync(Guid? userId);
}
