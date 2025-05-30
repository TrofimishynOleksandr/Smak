using AutoMapper;
using Microsoft.EntityFrameworkCore;
using SmakApi.Data;
using SmakApi.Exceptions;
using SmakApi.Helpers;
using SmakApi.Models.DTOs;
using SmakApi.Models.Entities;

namespace SmakApi.Services.Recipe;

public class RecipeService : IRecipeService
{
    private readonly SmakDbContext _context;
    private readonly IImageHelper _imageHelper;

    public RecipeService(SmakDbContext context, IImageHelper imageHelper)
    {
        _context = context;
        _imageHelper = imageHelper;
    }

    public async Task<Guid> CreateAsync(Guid userId, CreateRecipeDto dto)
    {
        var recipe = new Models.Entities.Recipe
        {
            Id = Guid.NewGuid(),
            Title = dto.Title,
            Description = dto.Description,
            CookTimeMinutes = dto.CookTimeMinutes,
            AuthorId = userId,
            CategoryId = dto.CategoryId,
            CreatedAt = DateTime.UtcNow,
            ImageUrl = dto.Image != null ? await _imageHelper.SaveImageAsync(dto.Image, "recipe-images") : null
        };

        recipe.Ingredients = new List<IngredientItem>();

        foreach (var i in dto.Ingredients)
        {
            var existingIngredient = await _context.Ingredients.FirstOrDefaultAsync(x =>
                x.Name.ToLower() == i.Name.ToLower() &&
                (x.CreatedByUserId == null || x.CreatedByUserId == userId));

            if (existingIngredient == null)
            {
                existingIngredient = new Models.Entities.Ingredient
                {
                    Id = Guid.NewGuid(),
                    Name = i.Name,
                    CreatedByUserId = userId
                };
                await _context.Ingredients.AddAsync(existingIngredient);
            }

            var ingredientItem = new IngredientItem
            {
                Id = Guid.NewGuid(),
                IngredientId = existingIngredient.Id,
                Quantity = i.Quantity,
                Unit = i.Unit,
                RecipeId = recipe.Id
            };

            recipe.Ingredients.Add(ingredientItem);
        }

        recipe.Instructions = new List<InstructionStep>();

        for (int i = 0; i < dto.Instructions.Count; i++)
        {
            var step = dto.Instructions[i];
            var stepImage = step.Image != null ? await _imageHelper.SaveImageAsync(step.Image, "step-images") : null;

            recipe.Instructions.Add(new InstructionStep
            {
                Id = Guid.NewGuid(),
                RecipeId = recipe.Id,
                StepNumber = i + 1,
                Description = step.Description,
                ImageUrl = stepImage
            });
        }

        await _context.Recipes.AddAsync(recipe);
        await _context.SaveChangesAsync();

        return recipe.Id;
    }
    
    public async Task UpdateAsync(Guid recipeId, Guid userId, UpdateRecipeDto dto)
    {
        var recipe = await _context.Recipes
                         .Include(r => r.Ingredients)
                         .Include(r => r.Instructions)
                         .FirstOrDefaultAsync(r => r.Id == recipeId && r.AuthorId == userId)
                     ?? throw new CustomException("Recipe not found or not permitted", 403);

        recipe.Title = dto.Title;
        recipe.Description = dto.Description;
        recipe.CookTimeMinutes = dto.CookTimeMinutes;
        recipe.CategoryId = dto.CategoryId;

        if (dto.Image != null)
            recipe.ImageUrl = await _imageHelper.SaveImageAsync(dto.Image, "recipe-images");

        _context.IngredientItems.RemoveRange(recipe.Ingredients);
        _context.InstructionSteps.RemoveRange(recipe.Instructions);

        recipe.Ingredients = await MapIngredientsAsync(dto.Ingredients, userId, recipe.Id);
        recipe.Instructions = await MapInstructionsAsync(dto.Instructions, recipe.Id);

        await _context.SaveChangesAsync();
    }

    public async Task DeleteAsync(Guid recipeId, Guid userId)
    {
        var recipe = await _context.Recipes.FirstOrDefaultAsync(r => r.Id == recipeId && r.AuthorId == userId)
                     ?? throw new CustomException("Recipe not found or not permitted", 403);

        _context.Recipes.Remove(recipe);
        await _context.SaveChangesAsync();
    }
    
    public async Task<IEnumerable<RecipeShortDto>> GetAllAsync(Guid? userId = null)
    {
        var recipes = await _context.Recipes
            .Include(r => r.Author)
            .Include(r => r.Category)
            .Include(r => r.Reviews)
            .Include(r => r.Favorites)
            .ToListAsync();

        var result = recipes.Select(r => new RecipeShortDto
        {
            Id = r.Id,
            Title = r.Title,
            Description = r.Description,
            ImageUrl = r.ImageUrl,
            CookTimeMinutes = r.CookTimeMinutes,
            AuthorName = r.Author.Name,
            AverageRating = r.Reviews.Any() ? Math.Round(r.Reviews.Average(rev => rev.Rating), 2) : 0,
            RatingsCount = r.Reviews.Count,
            IsFavorite = userId != null && r.Favorites.Any(f => f.UserId == userId)
        });

        return result.ToList();
    }

    public async Task<IEnumerable<RecipeShortDto>> GetFavoriteAsync(Guid? userId = null)
    {
        var recipes = await _context.Recipes
            .Include(r => r.Author)
            .Include(r => r.Category)
            .Include(r => r.Reviews)
            .Include(r => r.Favorites)
            .Where(r => r.Favorites.Any(f => f.UserId == userId))
            .ToListAsync();

        var result = recipes.Select(r => new RecipeShortDto
        {
            Id = r.Id,
            Title = r.Title,
            Description = r.Description,
            ImageUrl = r.ImageUrl,
            CookTimeMinutes = r.CookTimeMinutes,
            AuthorName = r.Author.Name,
            AverageRating = r.Reviews.Any() ? Math.Round(r.Reviews.Average(rev => rev.Rating), 2) : 0,
            RatingsCount = r.Reviews.Count,
            IsFavorite = true
        });

        return result.ToList();
    }

    public async Task<RecipeDetailsDto> GetByIdAsync(Guid recipeId, Guid currentUserId)
    {
        var recipe = await _context.Recipes
                         .Include(r => r.Author)
                         .Include(r => r.Category)
                         .Include(r => r.Ingredients)
                            .ThenInclude(ii => ii.Ingredient)
                         .Include(r => r.Instructions)
                         .Include(r => r.Reviews)
                            .ThenInclude(r => r.User)
                         .Include(r => r.Favorites)
                         .FirstOrDefaultAsync(r => r.Id == recipeId)
                     ?? throw new CustomException("Recipe not found", 404);
        
        recipe.Instructions = recipe.Instructions.OrderBy(i => i.StepNumber).ToList();


        return new RecipeDetailsDto
        {
            Id = recipe.Id,
            Title = recipe.Title,
            Description = recipe.Description,
            ImageUrl = recipe.ImageUrl,
            CookTimeMinutes = recipe.CookTimeMinutes,
            AuthorId = recipe.AuthorId,
            AuthorName = recipe.Author.Name,
            CategoryName = recipe.Category.Name,
            Ingredients = recipe.Ingredients.Select(ii => new IngredientItemDto
            {
                Name = ii.Ingredient.Name,
                Quantity = ii.Quantity,
                Unit = ii.Unit?.ToUkrainian()
            }).ToList(),
            Instructions = recipe.Instructions.Select(i => new InstructionStepDto
            {
                StepNumber = i.StepNumber,
                Description = i.Description,
                ImageUrl = i.ImageUrl
            }).ToList(),
            AverageRating = recipe.Reviews.Any() ? recipe.Reviews.Average(r => r.Rating) : 0,
            RatingsCount = recipe.Reviews.Count,
            Reviews = recipe.Reviews
                .Select(r => new ReviewDto
                {
                    Id = r.Id,
                    Author = r.User.Name,
                    AuthorId = r.UserId,
                    AvatarUrl = r.User.AvatarUrl,
                    Rating = r.Rating,
                    Comment = r.Comment!,
                    CreatedAt = r.CreatedAt
                }).ToList(),
            IsFavorite = recipe.Favorites.Any(f => f.UserId == currentUserId)
        };
    }
    
    public async Task AddToFavoritesAsync(Guid recipeId, Guid userId)
    {
        var exists = await _context.Favorites.AnyAsync(f => f.RecipeId == recipeId && f.UserId == userId);
        if (exists) return;

        var fav = new Favorite
        {
            Id = Guid.NewGuid(),
            UserId = userId,
            RecipeId = recipeId
        };
        await _context.Favorites.AddAsync(fav);
        await _context.SaveChangesAsync();
    }

    public async Task RemoveFromFavoritesAsync(Guid recipeId, Guid userId)
    {
        var fav = await _context.Favorites
            .FirstOrDefaultAsync(f => f.RecipeId == recipeId && f.UserId == userId);

        if (fav != null)
        {
            _context.Favorites.Remove(fav);
            await _context.SaveChangesAsync();
        }
    }

    public async Task AddReviewAsync(Guid userId, ReviewRequest dto)
    {
        if (dto.Rating < 1 || dto.Rating > 5)
            throw new CustomException("Rating must be between 1 and 5", 400);

        var existing = await _context.Reviews
            .FirstOrDefaultAsync(r => r.RecipeId == dto.RecipeId && r.UserId == userId);

        if (existing != null)
        {
            existing.Rating = dto.Rating;
            existing.Comment = dto.Comment;
            existing.CreatedAt = DateTime.UtcNow;
        }
        else
        {
            var review = new Models.Entities.Review
            {
                Id = Guid.NewGuid(),
                RecipeId = dto.RecipeId,
                UserId = userId,
                Rating = dto.Rating,
                Comment = dto.Comment,
                CreatedAt = DateTime.UtcNow
            };

            await _context.Reviews.AddAsync(review);
        }

        await _context.SaveChangesAsync();
    }

    public async Task<IEnumerable<RecipeShortDto>> SearchAsync(RecipeSearchQuery query, Guid? userId)
    {
        var q = _context.Recipes
            .Include(r => r.Author)
            .Include(r => r.Category)
            .Include(r => r.Reviews)
            .Include(r => r.Favorites)
            .AsQueryable();

        if (!string.IsNullOrWhiteSpace(query.Search))
            q = q.Where(r => r.Title.ToLower().Contains(query.Search.ToLower()));

        if (query.CategoryId.HasValue)
            q = q.Where(r => r.CategoryId == query.CategoryId.Value);

        if (!string.IsNullOrWhiteSpace(query.AuthorName))
            q = q.Where(r => r.Author.Name.ToLower().Contains(query.AuthorName.ToLower()));
        
        if (query.AuthorId.HasValue)
            q = q.Where(r => r.AuthorId == query.AuthorId.Value);

        if (query.MaxCookTime.HasValue)
            q = q.Where(r => r.CookTimeMinutes <= query.MaxCookTime);

        var list = await q.ToListAsync();
        
        if (query.OnlyFavorites == true && userId.HasValue)
            list = list.Where(r => r.Favorites.Any(f => f.UserId == userId)).ToList();
        else if (query.OnlyFavorites == false && userId.HasValue)
            list = list.Where(r => !r.Favorites.Any(f => f.UserId == userId)).ToList();

        var filtered = list
            .Where(r => !query.MinRating.HasValue || (r.Reviews.Any() && r.Reviews.Average(x => x.Rating) >= query.MinRating))
            .Select(r => new RecipeShortDto
            {
                Id = r.Id,
                Title = r.Title,
                Description = r.Description,
                ImageUrl = r.ImageUrl,
                CookTimeMinutes = r.CookTimeMinutes,
                AuthorName = r.Author.Name,
                AverageRating = r.Reviews.Any() ? Math.Round(r.Reviews.Average(r => r.Rating), 2) : 0,
                RatingsCount = r.Reviews.Count,
                IsFavorite = userId != null && r.Favorites.Any(f => f.UserId == userId)
            });

        filtered = query.SortBy switch
        {
            "rating" => filtered.OrderByDescending(r => r.AverageRating),
            "time" => filtered.OrderBy(r => r.CookTimeMinutes),
            "popular" => filtered.OrderByDescending(r => r.RatingsCount),
            _ => filtered.OrderBy(r => r.Title)
        };

        return filtered.ToList();
    }

    
    public async Task<List<RecipeCollectionDto>> GetCollectionsAsync(Guid? userId)
    {
        var allRecipes = await _context.Recipes
            .Include(r => r.Author)
            .Include(r => r.Category)
            .Include(r => r.Reviews)
            .Include(r => r.Favorites)
            .ToListAsync();

        List<RecipeShortDto> Project(IEnumerable<Models.Entities.Recipe> source) => source
            .Select(r => new RecipeShortDto
            {
                Id = r.Id,
                Title = r.Title,
                Description = r.Description,
                ImageUrl = r.ImageUrl,
                CookTimeMinutes = r.CookTimeMinutes,
                AuthorName = r.Author.Name,
                AverageRating = r.Reviews.Any() ? Math.Round(r.Reviews.Average(x => x.Rating), 2) : 0,
                RatingsCount = r.Reviews.Count,
                IsFavorite = userId.HasValue && r.Favorites.Any(f => f.UserId == userId)
            }).Take(10).ToList();

        return new List<RecipeCollectionDto>
        {
            new()
            {
                Title = "Популярні рецепти",
                Recipes = Project(allRecipes
                    .Where(r => r.Reviews.Count >= 1)
                    .OrderByDescending(r => r.Reviews.Average(x => x.Rating)))
            },
            new()
            {
                Title = "Швидкі рецепти",
                Recipes = Project(allRecipes
                    .Where(r => r.CookTimeMinutes <= 30)
                    .OrderBy(r => r.CookTimeMinutes))
            },
            new()
            {
                Title = "Нові рецепти",
                Recipes = Project(allRecipes
                    .OrderByDescending(r => r.CreatedAt))
            }
        };
    }

    private async Task<List<IngredientItem>> MapIngredientsAsync(List<CreateIngredientDto> ingredients, Guid userId, Guid recipeId)
    {
        var result = new List<IngredientItem>();

        foreach (var i in ingredients)
        {
            var existingIngredient = await _context.Ingredients.FirstOrDefaultAsync(x =>
                x.Name.ToLower() == i.Name.ToLower() &&
                (x.CreatedByUserId == null || x.CreatedByUserId == userId));

            if (existingIngredient == null)
            {
                existingIngredient = new Models.Entities.Ingredient
                {
                    Id = Guid.NewGuid(),
                    Name = i.Name,
                    CreatedByUserId = userId
                };
                await _context.Ingredients.AddAsync(existingIngredient);
            }

            result.Add(new IngredientItem
            {
                Id = Guid.NewGuid(),
                RecipeId = recipeId,
                IngredientId = existingIngredient.Id,
                Quantity = i.Quantity,
                Unit = i.Unit
            });
        }

        return result;
    }

    private async Task<List<InstructionStep>> MapInstructionsAsync(List<CreateInstructionDto> instructions, Guid recipeId)
    {
        var result = new List<InstructionStep>();

        for (int i = 0; i < instructions.Count; i++)
        {
            var step = instructions[i];
            var stepImage = step.Image != null
                ? await _imageHelper.SaveImageAsync(step.Image, "step-images")
                : null;

            result.Add(new InstructionStep
            {
                Id = Guid.NewGuid(),
                RecipeId = recipeId,
                StepNumber = i + 1,
                Description = step.Description,
                ImageUrl = stepImage
            });
        }

        return result;
    }

}
