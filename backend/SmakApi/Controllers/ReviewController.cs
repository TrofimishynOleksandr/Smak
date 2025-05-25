using System.Security.Claims;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using SmakApi.Models.DTOs;
using SmakApi.Services.Review;

namespace SmakApi.Controllers;

[ApiController]
[Route("api/[controller]")]
public class ReviewController : ControllerBase
{
    private readonly IReviewService _service;

    public ReviewController(IReviewService service)
    {
        _service = service;
    }

    [HttpGet("my")]
    [Authorize]
    public async Task<IActionResult> GetMyReviews()
    {
        var userId = Guid.Parse(User.FindFirstValue(ClaimTypes.NameIdentifier)!);
        var reviews = await _service.GetUserReviewsAsync(userId);
        return Ok(reviews);
    }
    
    [HttpPut("{recipeId}")]
    [Authorize]
    public async Task<IActionResult> UpdateReview([FromBody] ReviewRequest dto)
    {
        var userId = Guid.Parse(User.FindFirstValue(ClaimTypes.NameIdentifier)!);
        await _service.UpdateReviewAsync(userId, dto);
        return Ok("Review updated");
    }

    [HttpDelete("{recipeId}")]
    [Authorize]
    public async Task<IActionResult> DeleteReview(Guid recipeId)
    {
        var userId = Guid.Parse(User.FindFirstValue(ClaimTypes.NameIdentifier)!);
        await _service.DeleteReviewAsync(recipeId, userId);
        return NoContent();
    }
}
