namespace SmakApi.Data.Repositories.Recipe;

public interface IRecipeRepository : IRepository<Models.Entities.Recipe>
{
    Task<IEnumerable<Models.Entities.Recipe>> SearchByTitleAsync(string titlePart);
}