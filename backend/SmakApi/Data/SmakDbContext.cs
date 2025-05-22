using Microsoft.EntityFrameworkCore;
using SmakApi.Models.Entities;

namespace SmakApi.Data;

public class SmakDbContext(DbContextOptions<SmakDbContext> options) : DbContext(options)
{
    public DbSet<User> Users => Set<User>();
    public DbSet<Recipe> Recipes => Set<Recipe>();
    public DbSet<Category> Categories => Set<Category>();
    public DbSet<IngredientItem> IngredientItems => Set<IngredientItem>();
    public DbSet<Ingredient> Ingredients => Set<Ingredient>();
    public DbSet<InstructionStep> InstructionSteps => Set<InstructionStep>();
    public DbSet<Review> Reviews => Set<Review>();
    public DbSet<Favorite> Favorites => Set<Favorite>();

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        modelBuilder.Entity<User>()
            .HasMany(u => u.Recipes)
            .WithOne(r => r.Author)
            .HasForeignKey(r => r.AuthorId)
            .OnDelete(DeleteBehavior.Cascade);

        modelBuilder.Entity<User>()
            .HasMany(u => u.Reviews)
            .WithOne(r => r.User)
            .HasForeignKey(r => r.UserId)
            .OnDelete(DeleteBehavior.Cascade);
        
        modelBuilder.Entity<Recipe>()
            .HasMany(r => r.Ingredients)
            .WithOne(i => i.Recipe)
            .HasForeignKey(i => i.RecipeId);

        modelBuilder.Entity<Recipe>()
            .HasMany(r => r.Instructions)
            .WithOne(s => s.Recipe)
            .HasForeignKey(s => s.RecipeId);

        modelBuilder.Entity<Recipe>()
            .HasMany(r => r.Favorites)
            .WithOne(f => f.Recipe)
            .HasForeignKey(f => f.RecipeId);

        modelBuilder.Entity<Category>()
            .HasMany(c => c.Recipes)
            .WithOne(r => r.Category)
            .HasForeignKey(r => r.CategoryId);

        modelBuilder.Entity<Review>()
            .HasIndex(r => new { r.UserId, r.RecipeId })
            .IsUnique();

        modelBuilder.Entity<Review>()
            .Property(r => r.Rating)
            .IsRequired();

        modelBuilder.Entity<Review>()
            .Property(r => r.CreatedAt)
            .HasDefaultValueSql("NOW()");

        modelBuilder.Entity<Favorite>()
            .HasIndex(f => new { f.UserId, f.RecipeId })
            .IsUnique();

        modelBuilder.Entity<User>()
            .HasIndex(u => u.Email)
            .IsUnique();

        modelBuilder.Entity<User>()
            .Property(u => u.Role)
            .HasConversion<string>();

        modelBuilder.Entity<IngredientItem>()
            .Property(i => i.Unit)
            .HasConversion<string>()
            .HasMaxLength(20);
        
        modelBuilder.Entity<Ingredient>()
            .HasOne(i => i.CreatedByUser)
            .WithMany(u => u.CreatedIngredients)
            .HasForeignKey(i => i.CreatedByUserId)
            .OnDelete(DeleteBehavior.Restrict);

        modelBuilder.Entity<IngredientItem>()
            .HasOne(ii => ii.Ingredient)
            .WithMany(i => i.IngredientItems)
            .HasForeignKey(ii => ii.IngredientId)
            .OnDelete(DeleteBehavior.Cascade);

    }

}
