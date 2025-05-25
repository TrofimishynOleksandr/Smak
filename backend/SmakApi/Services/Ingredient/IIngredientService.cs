using SmakApi.Models.DTOs;

namespace SmakApi.Services.Ingredient;

public interface IIngredientService
{
    Task<List<IngredientDto>> GetIngredientsAsync(Guid userId);
}