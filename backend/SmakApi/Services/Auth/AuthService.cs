using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Text;
using Microsoft.AspNetCore.Identity;
using Microsoft.IdentityModel.Tokens;
using SmakApi.Data.Repositories;
using SmakApi.Exceptions;
using SmakApi.Models.Entities;

namespace SmakApi.Services.Auth;

public class AuthService : IAuthService
{
    private readonly IRepository<Models.Entities.User> _userRepo;
    private readonly IPasswordHasher<Models.Entities.User> _passwordHasher;
    private readonly IConfiguration _config;

    public AuthService(IRepository<Models.Entities.User> userRepo, IPasswordHasher<Models.Entities.User> passwordHasher, IConfiguration config)
    {
        _userRepo = userRepo;
        _passwordHasher = passwordHasher;
        _config = config;
    }

    public async Task RegisterAsync(string email, string password, string name)
    {
        var exists = (await _userRepo.GetAllAsync()).Any(u => u.Email == email);
        if (exists) throw new Exception("Email already registered");

        var user = new Models.Entities.User
        {
            Id = Guid.NewGuid(),
            Name = name,
            Email = email,
            CreatedAt = DateTime.UtcNow
        };

        user.PasswordHash = _passwordHasher.HashPassword(user, password);

        await _userRepo.AddAsync(user);
        await _userRepo.SaveChangesAsync();
    }

    public async Task<string> LoginAsync(string email, string password)
    {
        var user = (await _userRepo.GetAllAsync()).FirstOrDefault(u => u.Email == email);
        if (user == null)
            throw new CustomException("User not found", 404);

        var result = _passwordHasher.VerifyHashedPassword(user, user.PasswordHash, password);
        if (result == PasswordVerificationResult.Failed)
            throw new CustomException("Invalid credentials", 401);

        return GenerateJwt(user);
    }

    private string GenerateJwt(Models.Entities.User user)
    {
        var claims = new[]
        {
            new Claim(ClaimTypes.NameIdentifier, user.Id.ToString()),
            new Claim(ClaimTypes.Email, user.Email),
            new Claim(ClaimTypes.Role, user.Role.ToString()),
        };

        var key = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(_config["Jwt:Key"]!));
        var creds = new SigningCredentials(key, SecurityAlgorithms.HmacSha256);
        var token = new JwtSecurityToken(
            claims: claims,
            expires: DateTime.UtcNow.AddHours(1),
            signingCredentials: creds
        );

        return new JwtSecurityTokenHandler().WriteToken(token);
    }
}
