namespace SmakApi.Tests.Services;

using Microsoft.EntityFrameworkCore;
using SmakApi.Data;
using SmakApi.Models.Entities;
using SmakApi.Services.Ingredient;
using Xunit;

public class IngredientServiceTests
{
    [Fact]
    public async Task GetIngredientsAsync_ReturnsFilteredList()
    {
        // Arrange
        var userId = Guid.NewGuid();
        var options = new DbContextOptionsBuilder<SmakDbContext>()
            .UseInMemoryDatabase(databaseName: Guid.NewGuid().ToString())
            .Options;

        var context = new SmakDbContext(options);
        context.Ingredients.AddRange(
            new Ingredient { Id = Guid.NewGuid(), Name = "Sugar", CreatedByUserId = userId },
            new Ingredient { Id = Guid.NewGuid(), Name = "Salt", CreatedByUserId = null },
            new Ingredient { Id = Guid.NewGuid(), Name = "Pepper", CreatedByUserId = Guid.NewGuid() }
        );
        await context.SaveChangesAsync();

        var service = new IngredientService(context);

        // Act
        var result = await service.GetIngredientsAsync(userId);

        // Assert
        Assert.Equal(2, result.Count);
        Assert.Contains(result, i => i.Name == "Sugar");
        Assert.Contains(result, i => i.Name == "Salt");
    }
}
