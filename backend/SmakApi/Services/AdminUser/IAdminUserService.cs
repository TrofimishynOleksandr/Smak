using SmakApi.Models.DTOs;

namespace SmakApi.Services.AdminUser;

public interface IAdminUserService
{
    Task<List<UserAdminDto>> GetAllAsync();
    Task DeleteAsync(Guid id, Guid currentAdminId);
    Task AssignChefRoleAsync(Guid userId);
}
