namespace SmakApi.Tests.Services;

using Microsoft.EntityFrameworkCore;
using SmakApi.Data;
using SmakApi.Exceptions;
using SmakApi.Models.Entities;
using SmakApi.Services.AdminUser;
using Xunit;

public class AdminUserServiceTests
{
    private readonly SmakDbContext _context;
    private readonly AdminUserService _service;

    public AdminUserServiceTests()
    {
        var options = new DbContextOptionsBuilder<SmakDbContext>()
            .UseInMemoryDatabase("TestDb")
            .Options;

        _context = new SmakDbContext(options);
        _context.Database.EnsureDeleted();
        _context.Database.EnsureCreated();

        _service = new AdminUserService(_context);
    }

    [Fact]
    public async Task GetAllAsync_ReturnsNonAdminUsers()
    {
        _context.Users.AddRange(
            new User { Id = Guid.NewGuid(),
                Email = "user@example.com",
                PasswordHash = "hashedpassword",
                Name = "Admin",
                Role = UserRole.Admin },
            new User { Id = Guid.NewGuid(),
                Email = "user@example.com",
                PasswordHash = "hashedpassword",
                Name = "User", 
                Role = UserRole.User }
        );
        await _context.SaveChangesAsync();

        var result = await _service.GetAllAsync();

        Assert.Single(result);
        Assert.Equal("User", result[0].Name);
    }

    [Fact]
    public async Task DeleteAsync_CannotDeleteSelf_ThrowsException()
    {
        var id = Guid.NewGuid();
        await Assert.ThrowsAsync<CustomException>(() =>
            _service.DeleteAsync(id, id));
    }

    [Fact]
    public async Task DeleteAsync_DeletesUser()
    {
        var id = Guid.NewGuid();
        _context.Users.Add(new User { Id = id,
            Email = "user@example.com",
            PasswordHash = "hashedpassword",
            Name = "ToDelete", 
            Role = UserRole.User });
        await _context.SaveChangesAsync();

        await _service.DeleteAsync(id, Guid.NewGuid());

        Assert.Null(await _context.Users.FindAsync(id));
    }

    [Fact]
    public async Task AssignChefRoleAsync_AssignsRole()
    {
        var id = Guid.NewGuid();
        _context.Users.Add(new User { Id = id,
            Email = "user@example.com",
            PasswordHash = "hashedpassword",
            Name = "Test User", 
            Role = UserRole.User });
        await _context.SaveChangesAsync();

        await _service.AssignChefRoleAsync(id);
        var user = await _context.Users.FindAsync(id);

        Assert.Equal(UserRole.Chef, user?.Role);
    }
}
