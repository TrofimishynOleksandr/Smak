using Microsoft.EntityFrameworkCore;
using SmakApi.Data;
using SmakApi.Models.DTOs;

namespace SmakApi.Services.Ingredient;

public class IngredientService : IIngredientService
{
    private readonly SmakDbContext _context;

    public IngredientService(SmakDbContext context)
    {
        _context = context;
    }
    
    public async Task<List<IngredientDto>> GetIngredientsAsync(Guid userId)
    {
        var ingredients = await _context.Ingredients
            .OrderBy(i => i.Name)
            .Where(i => i.CreatedByUserId == userId || i.CreatedByUserId == null)
            .Select(i => new IngredientDto
            {
                Id = i.Id,
                Name = i.Name,
                IsCustom = i.CreatedByUserId == userId
            })
            .ToListAsync();
        return ingredients;
    }
}