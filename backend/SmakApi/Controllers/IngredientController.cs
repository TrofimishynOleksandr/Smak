using System.Security.Claims;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using SmakApi.Models.DTOs;
using SmakApi.Models.Entities;
using SmakApi.Services.Ingredient;

namespace SmakApi.Controllers;

[ApiController]
[Route("api/[controller]")]
public class IngredientController : ControllerBase
{
    private readonly IIngredientService _ingredientService;

    public IngredientController(IIngredientService ingredientService)
    {
        _ingredientService = ingredientService;
    }

    [HttpGet]
    [Authorize(Roles = "Chef")]
    public async Task<ActionResult<List<IngredientDto>>> GetIngredients()
    {
        var userId = Guid.Parse(User.FindFirstValue(ClaimTypes.NameIdentifier)!);
        var ingredients = await _ingredientService.GetIngredientsAsync(userId);

        return Ok(ingredients);
    }

    [HttpGet("units")]
    public ActionResult<List<UnitDto>> GetUnits()
    {
        var units = Enum.GetValues(typeof(UnitOfMeasure))
            .Cast<UnitOfMeasure>()
            .Select(u => new UnitDto
            {
                Value = (int)u,
                Label = u switch
                {
                    UnitOfMeasure.Piece => "шт",
                    UnitOfMeasure.Gram => "г",
                    UnitOfMeasure.Kilogram => "кг",
                    UnitOfMeasure.Milliliter => "мл",
                    UnitOfMeasure.Liter => "л",
                    UnitOfMeasure.Teaspoon => "ч.л.",
                    UnitOfMeasure.Tablespoon => "ст.л.",
                    UnitOfMeasure.Cup => "склянка",
                    UnitOfMeasure.Pinch => "щіпка",
                    UnitOfMeasure.ToTaste => "за смаком",
                    _ => u.ToString()
                }
            })
            .ToList();
        return Ok(units);
    }
}