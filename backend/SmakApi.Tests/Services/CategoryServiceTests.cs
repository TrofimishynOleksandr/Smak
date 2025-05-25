using SmakApi.Exceptions;
using SmakApi.Services.Image;

namespace SmakApi.Tests.Services;

using Microsoft.AspNetCore.Http;
using Microsoft.EntityFrameworkCore;
using Moq;
using SmakApi.Data;
using SmakApi.Data.Repositories;
using SmakApi.Helpers;
using SmakApi.Models.DTOs;
using SmakApi.Models.Entities;
using SmakApi.Services.Category;
using Xunit;

public class CategoryServiceTests
{
    private readonly SmakDbContext _context;
    private readonly Repository<Category> _repo;
    private readonly Mock<IImageService> _imageServiceMock;

    public CategoryServiceTests()
    {
        var options = new DbContextOptionsBuilder<SmakDbContext>()
            .UseInMemoryDatabase(databaseName: Guid.NewGuid().ToString())
            .Options;

        _context = new SmakDbContext(options);
        _repo = new Repository<Category>(_context);
        _imageServiceMock = new Mock<IImageService>();
    }

    [Fact]
    public async Task GetAllAsync_ReturnsAllCategories()
    {
        // Arrange
        _context.Categories.AddRange(
            new Category { Id = Guid.NewGuid(), Name = "First" },
            new Category { Id = Guid.NewGuid(), Name = "Second" }
        );
        await _context.SaveChangesAsync();

        var service = new CategoryService(_repo, _context, _imageServiceMock.Object);

        // Act
        var result = await service.GetAllAsync();

        // Assert
        Assert.Equal(2, result.Count());
    }

    [Fact]
    public async Task CreateWithImageAsync_CreatesCategoryWithImage()
    {
        // Arrange
        var mockFormFile = new Mock<IFormFile>();
        mockFormFile.Setup(f => f.FileName).Returns("test.jpg");
        mockFormFile.Setup(f => f.Length).Returns(1);
        mockFormFile.Setup(f => f.OpenReadStream()).Returns(new MemoryStream(new byte[] { 1, 2, 3 }));

        _imageServiceMock.Setup(x => x.SaveImageAsync(It.IsAny<IFormFile>(), "category-images"))
                         .ReturnsAsync("/category-images/test.jpg");

        var service = new CategoryService(_repo, _context, _imageServiceMock.Object);
        var dto = new CreateCategoryDto
        {
            Name = "TestCategory",
            Image = mockFormFile.Object
        };

        // Act
        var id = await service.CreateWithImageAsync(dto);

        // Assert
        var created = await _context.Categories.FindAsync(id);
        Assert.NotNull(created);
        Assert.Equal("TestCategory", created.Name);
        Assert.Equal("/category-images/test.jpg", created.ImageUrl);
    }

    [Fact]
    public async Task UpdateAsync_UpdatesCategoryNameAndImage()
    {
        // Arrange
        var category = new Category { Id = Guid.NewGuid(), Name = "OldName" };
        _context.Categories.Add(category);
        await _context.SaveChangesAsync();

        var newImage = new Mock<IFormFile>();
        newImage.Setup(f => f.FileName).Returns("new.jpg");
        newImage.Setup(f => f.Length).Returns(1);
        newImage.Setup(f => f.OpenReadStream()).Returns(new MemoryStream(new byte[] { 4, 5 }));

        _imageServiceMock.Setup(x => x.SaveImageAsync(It.IsAny<IFormFile>(), "category-images"))
                         .ReturnsAsync("/category-images/new.jpg");

        var service = new CategoryService(_repo, _context, _imageServiceMock.Object);

        // Act
        await service.UpdateAsync(category.Id, "UpdatedName", newImage.Object);

        // Assert
        var updated = await _context.Categories.FindAsync(category.Id);
        Assert.Equal("UpdatedName", updated.Name);
        Assert.Equal("/category-images/new.jpg", updated.ImageUrl);
    }

    [Fact]
    public async Task UpdateAsync_Throws_WhenCategoryNotFound()
    {
        var service = new CategoryService(_repo, _context, _imageServiceMock.Object);

        var ex = await Assert.ThrowsAsync<CustomException>(() =>
            service.UpdateAsync(Guid.NewGuid(), "Name", null));

        Assert.Equal("Category not found", ex.Message);
        Assert.Equal(404, ex.StatusCode);
    }

    [Fact]
    public async Task DeleteAsync_RemovesCategory()
    {
        var category = new Category { Id = Guid.NewGuid(), Name = "ToDelete" };
        _context.Categories.Add(category);
        await _context.SaveChangesAsync();

        var service = new CategoryService(_repo, _context, _imageServiceMock.Object);

        // Act
        await service.DeleteAsync(category.Id);

        // Assert
        var exists = await _context.Categories.FindAsync(category.Id);
        Assert.Null(exists);
    }

    [Fact]
    public async Task DeleteAsync_Throws_WhenCategoryNotFound()
    {
        var service = new CategoryService(_repo, _context, _imageServiceMock.Object);

        var ex = await Assert.ThrowsAsync<CustomException>(() =>
            service.DeleteAsync(Guid.NewGuid()));

        Assert.Equal("Category not found", ex.Message);
        Assert.Equal(404, ex.StatusCode);
    }
}
