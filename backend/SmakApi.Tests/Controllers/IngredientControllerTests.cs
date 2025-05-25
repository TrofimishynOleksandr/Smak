using SmakApi.Models.Entities;

namespace SmakApi.Tests.Controllers;

using System;
using System.Collections.Generic;
using System.Security.Claims;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Moq;
using SmakApi.Controllers;
using SmakApi.Models.DTOs;
using SmakApi.Services.Ingredient;
using Xunit;

public class IngredientControllerTests
{
    private readonly Mock<IIngredientService> _mockIngredientService;
    private readonly IngredientController _controller;

    public IngredientControllerTests()
    {
        _mockIngredientService = new Mock<IIngredientService>();
        _controller = new IngredientController(_mockIngredientService.Object);
    }

    [Fact]
    public async Task GetIngredients_ReturnsIngredients_ForChef()
    {
        // Arrange
        var userId = Guid.NewGuid();
        var ingredients = new List<IngredientDto>
        {
            new IngredientDto { Id = Guid.NewGuid(), Name = "Tomato", IsCustom = false },
            new IngredientDto { Id = Guid.NewGuid(), Name = "Salt", IsCustom = true }
        };

        _mockIngredientService.Setup(s => s.GetIngredientsAsync(userId))
            .ReturnsAsync(ingredients);

        var user = new ClaimsPrincipal(new ClaimsIdentity(new[]
        {
            new Claim(ClaimTypes.NameIdentifier, userId.ToString()),
            new Claim(ClaimTypes.Role, "Chef")
        }, "mock"));

        _controller.ControllerContext = new ControllerContext
        {
            HttpContext = new DefaultHttpContext { User = user }
        };

        // Act
        var result = await _controller.GetIngredients();

        // Assert
        var okResult = Assert.IsType<OkObjectResult>(result.Result);
        var returnedIngredients = Assert.IsType<List<IngredientDto>>(okResult.Value);
        Assert.Equal(2, returnedIngredients.Count);
    }

    [Fact]
    public void GetUnits_ReturnsUnitList()
    {
        // Act
        var result = _controller.GetUnits();

        // Assert
        var okResult = Assert.IsType<OkObjectResult>(result.Result);
        var units = Assert.IsType<List<UnitDto>>(okResult.Value);

        Assert.Contains(units, u => u.Label == "г" && u.Value == (int)UnitOfMeasure.Gram);
        Assert.Contains(units, u => u.Label == "щіпка" && u.Value == (int)UnitOfMeasure.Pinch);
        Assert.True(units.Count >= 1);
    }
}
