using SmakApi.Data;
using SmakApi.Data.Repositories;
using SmakApi.Exceptions;
using SmakApi.Helpers;
using SmakApi.Models.DTOs;

namespace SmakApi.Services.Category;

public class CategoryService : ICategoryService
{
    private readonly IRepository<Models.Entities.Category> _repo;
    private readonly SmakDbContext _context;

    public CategoryService(IRepository<Models.Entities.Category> repo, SmakDbContext context)
    {
        _repo = repo;
        _context = context;
    }

    public async Task<IEnumerable<Models.Entities.Category>> GetAllAsync()
    {
        return await _repo.GetAllAsync();
    }

    public async Task<Guid> CreateWithImageAsync(CreateCategoryDto dto)
    {
        var category = new Models.Entities.Category
        {
            Id = Guid.NewGuid(),
            Name = dto.Name,
            ImageUrl = dto.Image != null ? await ImageHelper.SaveImageAsync(dto.Image, "category-images") : null
        };

        await _repo.AddAsync(category);
        await _repo.SaveChangesAsync();
        return category.Id;
    }

    public async Task UpdateAsync(Guid id, string name, IFormFile? image)
    {
        var category = await _context.Categories.FindAsync(id)
                       ?? throw new CustomException("Category not found", 404);

        category.Name = name;

        if (image != null)
            category.ImageUrl = await ImageHelper.SaveImageAsync(image, "category-images");

        await _repo.SaveChangesAsync();
    }
    
    public async Task DeleteAsync(Guid id)
    {
        var category = await _repo.GetByIdAsync(id) ?? throw new CustomException("Category not found", 404);
        _repo.Delete(category);
        await _repo.SaveChangesAsync();
    }
}
