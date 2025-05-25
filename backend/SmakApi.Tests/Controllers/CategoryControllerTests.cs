namespace SmakApi.Tests.Controllers;

using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Moq;
using SmakApi.Controllers;
using SmakApi.Models.DTOs;
using SmakApi.Services.Category;
using Xunit;

public class CategoryControllerTests
{
    private readonly Mock<ICategoryService> _mockService;
    private readonly CategoryController _controller;

    public CategoryControllerTests()
    {
        _mockService = new Mock<ICategoryService>();
        _controller = new CategoryController(_mockService.Object);
    }

    [Fact]
    public async Task GetAll_ReturnsOkWithCategories()
    {
        // Arrange
        var categories = new List<SmakApi.Models.Entities.Category>();
        _mockService.Setup(s => s.GetAllAsync()).ReturnsAsync(categories);

        // Act
        var result = await _controller.GetAll();

        // Assert
        var okResult = Assert.IsType<OkObjectResult>(result);
        Assert.Equal(categories, okResult.Value);
    }

    [Fact]
    public async Task CreateCategory_ReturnsOkWithId()
    {
        // Arrange
        var dto = new CreateCategoryDto { Name = "Test" };
        var expectedId = Guid.NewGuid();
        _mockService.Setup(s => s.CreateWithImageAsync(dto)).ReturnsAsync(expectedId);

        // Act
        var result = await _controller.CreateCategory(dto);

        // Assert
        var okResult = Assert.IsType<OkObjectResult>(result);
        Assert.Equal(expectedId, okResult.Value);
    }

    [Fact]
    public async Task Update_ReturnsNoContent()
    {
        // Arrange
        var id = Guid.NewGuid();
        var name = "Updated";
        IFormFile? file = null;

        // Act
        var result = await _controller.Update(id, name, file);

        // Assert
        Assert.IsType<NoContentResult>(result);
    }

    [Fact]
    public async Task Delete_ReturnsNoContent()
    {
        // Arrange
        var id = Guid.NewGuid();

        // Act
        var result = await _controller.Delete(id);

        // Assert
        Assert.IsType<NoContentResult>(result);
    }
}
