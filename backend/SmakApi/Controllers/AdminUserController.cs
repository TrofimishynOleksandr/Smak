using System.Security.Claims;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using SmakApi.Services.AdminUser;

namespace SmakApi.Controllers;

[ApiController]
[Route("api/admin/users")]
[Authorize(Roles = "Admin")]
public class AdminUserController : ControllerBase
{
    private readonly IAdminUserService _adminService;

    public AdminUserController(IAdminUserService adminService)
    {
        _adminService = adminService;
    }
    
    [HttpGet]
    public async Task<IActionResult> GetAll()
    {
        var users = await _adminService.GetAllAsync();
        return Ok(users);
    }
    
    [HttpDelete("{id}")]
    public async Task<IActionResult> Delete(Guid id)
    {
        var currentAdminId = Guid.Parse(User.FindFirstValue(ClaimTypes.NameIdentifier)!);
        await _adminService.DeleteAsync(id, currentAdminId);
        return NoContent();
    }
    
    [HttpPost("{id}/assign-chef")]
    public async Task<IActionResult> AssignChef(Guid id)
    {
        await _adminService.AssignChefRoleAsync(id);
        return NoContent();
    }
}
