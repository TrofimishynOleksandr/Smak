using Microsoft.EntityFrameworkCore;

namespace SmakApi.Data.Repositories.Recipe;

public class RecipeRepository : Repository<Models.Entities.Recipe>, IRecipeRepository
{
    public RecipeRepository(SmakDbContext context) : base(context) {}

    public async Task<IEnumerable<Models.Entities.Recipe>> SearchByTitleAsync(string titlePart)
    {
        return await _context.Set<Models.Entities.Recipe>()
            .Where(r => r.Title.ToLower().Contains(titlePart.ToLower()))
            .ToListAsync();
    }
}
