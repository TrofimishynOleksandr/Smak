using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using SmakApi.Models.DTOs;
using SmakApi.Models.Entities;
using SmakApi.Services.Auth;

namespace SmakApi.Controllers;

[ApiController]
[Route("api/[controller]")]
public class AuthController : ControllerBase
{
    private readonly IAuthService _authService;

    public AuthController(IAuthService authService) => _authService = authService;

    [HttpPost("register")]
    public async Task<IActionResult> Register(RegisterRequest request)
    {
        await _authService.RegisterAsync(request.Email, request.Password, request.Name);
        return Ok("Registered");
    }

    [HttpPost("login")]
    public async Task<IActionResult> Login(LoginRequest request)
    {
        var tokens = await _authService.LoginAsync(request.Email, request.Password);
        return Ok(tokens);
    }
    
    [HttpPost("refresh")]
    public IActionResult Refresh([FromBody] RefreshTokenRequest request)
    {
        var newAccessToken = _authService.RefreshAccessToken(request.RefreshToken);
        return Ok(new { accessToken = newAccessToken });
    }
}
