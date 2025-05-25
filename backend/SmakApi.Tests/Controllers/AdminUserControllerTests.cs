namespace SmakApi.Tests.Controllers;

using System.Security.Claims;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Moq;
using SmakApi.Controllers;
using SmakApi.Services.AdminUser;
using Xunit;

public class AdminUserControllerTests
{
    private readonly Mock<IAdminUserService> _serviceMock;
    private readonly AdminUserController _controller;

    public AdminUserControllerTests()
    {
        _serviceMock = new Mock<IAdminUserService>();
        _controller = new AdminUserController(_serviceMock.Object);

        // Мокаємо користувача
        var user = new ClaimsPrincipal(new ClaimsIdentity(new[]
        {
            new Claim(ClaimTypes.NameIdentifier, Guid.NewGuid().ToString()),
            new Claim(ClaimTypes.Role, "Admin"),
        }, "mock"));

        _controller.ControllerContext = new ControllerContext
        {
            HttpContext = new DefaultHttpContext { User = user }
        };
    }

    [Fact]
    public async Task GetAll_ReturnsOkResult()
    {
        // Arrange
        _serviceMock.Setup(s => s.GetAllAsync()).ReturnsAsync([]);

        // Act
        var result = await _controller.GetAll();

        // Assert
        var okResult = Assert.IsType<OkObjectResult>(result);
        Assert.Equal(200, okResult.StatusCode);
    }

    [Fact]
    public async Task Delete_ValidId_ReturnsNoContent()
    {
        // Arrange
        var id = Guid.NewGuid();

        // Act
        var result = await _controller.Delete(id);

        // Assert
        Assert.IsType<NoContentResult>(result);
        _serviceMock.Verify(s => s.DeleteAsync(id, It.IsAny<Guid>()), Times.Once);
    }

    [Fact]
    public async Task AssignChef_ValidId_ReturnsNoContent()
    {
        // Arrange
        var id = Guid.NewGuid();

        // Act
        var result = await _controller.AssignChef(id);

        // Assert
        Assert.IsType<NoContentResult>(result);
        _serviceMock.Verify(s => s.AssignChefRoleAsync(id), Times.Once);
    }
}
