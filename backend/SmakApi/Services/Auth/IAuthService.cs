using SmakApi.Models.Entities;

namespace SmakApi.Services.Auth;

public interface IAuthService
{
    Task RegisterAsync(string email, string password, string name);
    Task<string> LoginAsync(string email, string password);
}