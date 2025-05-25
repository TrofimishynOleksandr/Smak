using SmakApi.Models.DTOs;
using SmakApi.Models.Entities;

namespace SmakApi.Services.Auth;

public interface IAuthService
{
    Task RegisterAsync(string email, string password, string name);
    Task<TokenResponse> LoginAsync(string email, string password);
    string RefreshAccessToken(string refreshToken);
}