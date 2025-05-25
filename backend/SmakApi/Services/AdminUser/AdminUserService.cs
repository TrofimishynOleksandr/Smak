using Microsoft.EntityFrameworkCore;
using SmakApi.Data;
using SmakApi.Exceptions;
using SmakApi.Models.DTOs;
using SmakApi.Models.Entities;

namespace SmakApi.Services.AdminUser;

public class AdminUserService : IAdminUserService
{
    private readonly SmakDbContext _context;

    public AdminUserService(SmakDbContext context)
    {
        _context = context;
    }

    public async Task<List<UserAdminDto>> GetAllAsync()
    {
        return await _context.Users
            .OrderBy(u => u.CreatedAt)
            .Where(u => u.Role != UserRole.Admin)
            .Select(u => new UserAdminDto
            {
                Id = u.Id,
                Name = u.Name,
                Email = u.Email,
                Role = u.Role.ToString(),
                CreatedAt = u.CreatedAt,
                AvatarUrl = u.AvatarUrl
            })
            .ToListAsync();
    }

    public async Task DeleteAsync(Guid id, Guid currentAdminId)
    {
        if (id == currentAdminId)
            throw new CustomException("An administrator cannot delete themselves.", 400);

        var user = await _context.Users.FindAsync(id)
                   ?? throw new CustomException("User not found", 404);

        _context.Users.Remove(user);
        await _context.SaveChangesAsync();
    }

    public async Task AssignChefRoleAsync(Guid userId)
    {
        var user = await _context.Users.FindAsync(userId)
                   ?? throw new CustomException("User not found", 404);

        user.Role = UserRole.Chef;
        await _context.SaveChangesAsync();
    }
}
