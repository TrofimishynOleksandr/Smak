using System.Security.Claims;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using SmakApi.Helpers;
using SmakApi.Models.DTOs;
using SmakApi.Services.User;

namespace SmakApi.Controllers;

[ApiController]
[Route("api/[controller]")]
public class UserController : ControllerBase
{
    private readonly IUserService _userService;

    public UserController(IUserService userService)
    {
        _userService = userService;
    }

    [HttpGet("{chefId}")]
    public async Task<IActionResult> GetChef(Guid chefId)
    {
        var chef = await _userService.GetChefAsync(chefId);
        return Ok(chef);
    }
    
    [HttpGet("popular-chefs")]
    public async Task<IActionResult> GetPopularChefs()
    {
        var chefs = await _userService.GetPopularChefsAsync();
        return Ok(chefs);
    }
    
    [HttpGet("me")]
    [Authorize]
    public async Task<IActionResult> GetMe()
    {
        var userId = Guid.Parse(User.FindFirstValue(ClaimTypes.NameIdentifier)!);
        var user = await _userService.GetCurrentUserAsync(userId);
        return Ok(user);
    }

    [HttpPut]
    [Authorize]
    public async Task<IActionResult> UpdateName([FromBody] UpdateNameDto dto)
    {
        var userId = Guid.Parse(User.FindFirstValue(ClaimTypes.NameIdentifier)!);
        await _userService.UpdateNameAsync(userId, dto.Name);
        return NoContent();
    }

    
    [HttpPost("avatar")]
    [Authorize]
    [Consumes("multipart/form-data")]
    public async Task<IActionResult> UploadAvatar([FromForm] AvatarUploadDto dto)
    {
        var userId = Guid.Parse(User.FindFirstValue(ClaimTypes.NameIdentifier)!);
        var path = await _userService.UploadAvatarAsync(userId, dto.Image);
        return Ok(new { avatarUrl = path });
    }

    [HttpDelete("avatar")]
    [Authorize]
    public async Task<IActionResult> DeleteAvatar()
    {
        var userId = Guid.Parse(User.FindFirstValue(ClaimTypes.NameIdentifier)!);
        await _userService.DeleteAvatarAsync(userId);
        return NoContent();
    }
}
