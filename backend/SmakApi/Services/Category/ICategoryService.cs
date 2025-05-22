using SmakApi.Models.DTOs;

namespace SmakApi.Services.Category;

public interface ICategoryService
{
    Task<IEnumerable<Models.Entities.Category>> GetAllAsync();
    Task<Guid> CreateWithImageAsync(CreateCategoryDto dto);
    Task UpdateAsync(Guid id, string name, IFormFile? image);
    Task DeleteAsync(Guid id);
}
