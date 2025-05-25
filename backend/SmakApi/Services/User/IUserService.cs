using SmakApi.Models.DTOs;

namespace SmakApi.Services.User;

public interface IUserService
{
    Task<List<PopularChefDto>> GetPopularChefsAsync();
    Task<UserInfoDto> GetCurrentUserAsync(Guid userId);
    Task<ChefInfoDto> GetChefAsync(Guid chefId);
    Task UpdateNameAsync(Guid userId, string name);
    Task<string> UploadAvatarAsync(Guid userId, IFormFile image);
    Task DeleteAvatarAsync(Guid userId);

}