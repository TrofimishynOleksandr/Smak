using AutoMapper;
using Microsoft.EntityFrameworkCore;
using SmakApi.Data;
using SmakApi.Exceptions;
using SmakApi.Helpers;
using SmakApi.Models.DTOs;
using SmakApi.Models.Entities;

namespace SmakApi.Services.User;

public class UserService : IUserService
{
    private readonly SmakDbContext _context;
    private readonly IImageHelper _imageHelper;
    
    public UserService(SmakDbContext context, IImageHelper imageHelper)
    {
        _context = context;
        _imageHelper = imageHelper;
    }
    
    public async Task<List<PopularChefDto>> GetPopularChefsAsync()
    {
        var chefs = await _context.Users
            .Where(u => u.Role == UserRole.Chef)
            .Include(u => u.Recipes)
            .ThenInclude(r => r.Reviews)
            .ToListAsync();

        return chefs
            .Where(c => c.Recipes.Count >= 1)
            .Select(c => new PopularChefDto
            {
                Id = c.Id,
                Name = c.Name,
                AvatarUrl = c.AvatarUrl,
                RecipesCount = c.Recipes.Count,
                AverageRating = c.Recipes
                    .Where(r => r.Reviews.Any())
                    .Select(r => r.Reviews.Average(rev => rev.Rating))
                    .DefaultIfEmpty(0)
                    .Average()
            })
            .OrderByDescending(c => c.AverageRating)
            .Take(5)
            .ToList();
    }

    public async Task<UserInfoDto> GetCurrentUserAsync(Guid userId)
    {
        var user = await _context.Users.FirstOrDefaultAsync(u => u.Id == userId)
                   ?? throw new CustomException("User not found", 404);

        return new UserInfoDto
        {
            Id = user.Id,
            Name = user.Name,
            Email = user.Email,
            AvatarUrl = user.AvatarUrl,
            Role = user.Role.ToString()
        };
    }
    
    public async Task<ChefInfoDto> GetChefAsync(Guid chefId)
    {
        var user = await _context.Users.FirstOrDefaultAsync(u => u.Id == chefId)
                   ?? throw new CustomException("User not found", 404);

        return new ChefInfoDto
        {
            Id = user.Id,
            Name = user.Name,
            AvatarUrl = user.AvatarUrl,
        };
    }

    public async Task UpdateNameAsync(Guid userId, string name)
    {
        var user = await _context.Users.FindAsync(userId)
                   ?? throw new CustomException("User not found", 404);

        user.Name = name;
        await _context.SaveChangesAsync();
    }
    
    public async Task<string> UploadAvatarAsync(Guid userId, IFormFile image)
    {
        var user = await _context.Users.FindAsync(userId) ?? throw new CustomException("User not found", 404);

        _imageHelper.DeleteImage(user.AvatarUrl);

        var path = await _imageHelper.SaveImageAsync(image, "avatar-images");
        user.AvatarUrl = path;

        await _context.SaveChangesAsync();
        return path;
    }

    public async Task DeleteAvatarAsync(Guid userId)
    {
        var user = await _context.Users.FindAsync(userId) ?? throw new CustomException("User not found", 404);

        _imageHelper.DeleteImage(user.AvatarUrl);
        user.AvatarUrl = null;

        await _context.SaveChangesAsync();
    }


}