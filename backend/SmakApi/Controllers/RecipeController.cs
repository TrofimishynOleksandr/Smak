using System.Security.Claims;
using System.Text.Json;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using SmakApi.Exceptions;
using SmakApi.Helpers;
using SmakApi.Models.DTOs;
using SmakApi.Services.Recipe;

namespace SmakApi.Controllers;

[ApiController]
[Route("api/[controller]")]
public class RecipeController : ControllerBase
{
    private readonly IRecipeService _recipeService;

    public RecipeController(IRecipeService recipeService)
    {
        _recipeService = recipeService;
    }
    
    [HttpGet]
    public async Task<IActionResult> GetAll()
    {
        Guid? userId = User.Identity?.IsAuthenticated == true
            ? Guid.Parse(User.FindFirstValue(ClaimTypes.NameIdentifier)!)
            : null;

        var recipes = await _recipeService.GetAllAsync(userId);
        return Ok(recipes);
    }

    [HttpGet("{id}")]
    public async Task<IActionResult> GetById(Guid id)
    {
        var userId = User.Identity!.IsAuthenticated
            ? Guid.Parse(User.FindFirstValue(ClaimTypes.NameIdentifier)!)
            : Guid.Empty;

        var recipe = await _recipeService.GetByIdAsync(id, userId);
        return Ok(recipe);
    }
    
    [HttpPost]
    [Authorize(Roles = "Chef")]
    [Consumes("multipart/form-data")]
    public async Task<IActionResult> Create([FromForm] CreateRecipeDto dto)
    {
        var userId = Guid.Parse(User.FindFirstValue(ClaimTypes.NameIdentifier)!);
        var recipeId = await _recipeService.CreateAsync(userId, dto);
        return CreatedAtAction(nameof(GetById), new { id = recipeId }, null);
    }
    
    [HttpPut("{id}")]
    [Authorize(Roles = "Chef")]
    [Consumes("multipart/form-data")]
    public async Task<IActionResult> Update(Guid id, [FromForm] UpdateRecipeDto dto)
    {
        var userId = Guid.Parse(User.FindFirstValue(ClaimTypes.NameIdentifier)!);
        await _recipeService.UpdateAsync(id, userId, dto);
        return NoContent();
    }

    [HttpDelete("{id}")]
    [Authorize(Roles = "Chef")]
    public async Task<IActionResult> Delete(Guid id)
    {
        var userId = Guid.Parse(User.FindFirstValue(ClaimTypes.NameIdentifier)!);
        await _recipeService.DeleteAsync(id, userId);
        return NoContent();
    }
    
    [HttpGet("favorites")]
    [Authorize]
    public async Task<IActionResult> GetFavorite()
    {
        var userId = Guid.Parse(User.FindFirstValue(ClaimTypes.NameIdentifier)!);
        var recipes = await _recipeService.GetFavoriteAsync(userId);
        return Ok(recipes);
    }
    
    [HttpGet("search")]
    public async Task<IActionResult> Search([FromQuery] RecipeSearchQuery query)
    {
        Guid? userId = User.Identity?.IsAuthenticated == true
            ? Guid.Parse(User.FindFirstValue(ClaimTypes.NameIdentifier)!)
            : null;

        var result = await _recipeService.SearchAsync(query, userId);
        return Ok(result);
    }
    
    [HttpGet("collections")]
    public async Task<IActionResult> GetCollections()
    {
        Guid? userId = User.Identity?.IsAuthenticated == true
            ? Guid.Parse(User.FindFirstValue(ClaimTypes.NameIdentifier)!)
            : null;

        var result = await _recipeService.GetCollectionsAsync(userId);
        return Ok(result);
    }

    [HttpPost("{id}/favorite")]
    [Authorize]
    public async Task<IActionResult> AddToFavorites(Guid id)
    {
        var userId = Guid.Parse(User.FindFirstValue(ClaimTypes.NameIdentifier)!);
        await _recipeService.AddToFavoritesAsync(id, userId);
        return Ok();
    }

    [HttpDelete("{id}/favorite")]
    [Authorize]
    public async Task<IActionResult> RemoveFromFavorites(Guid id)
    {
        var userId = Guid.Parse(User.FindFirstValue(ClaimTypes.NameIdentifier)!);
        await _recipeService.RemoveFromFavoritesAsync(id, userId);
        return NoContent();
    }

    [HttpPost("{id}/review")]
    [Authorize]
    public async Task<IActionResult> AddReview(Guid id, [FromBody] ReviewRequest dto)
    {
        var userId = Guid.Parse(User.FindFirstValue(ClaimTypes.NameIdentifier)!);
        dto.RecipeId = id;
        await _recipeService.AddReviewAsync(userId, dto);
        return Ok();
    }
}
