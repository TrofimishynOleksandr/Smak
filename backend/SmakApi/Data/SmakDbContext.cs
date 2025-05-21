using Microsoft.EntityFrameworkCore;
using SmakApi.Models.Entities;

namespace SmakApi.Data;

public class SmakDbContext(DbContextOptions<SmakDbContext> options) : DbContext(options)
{
    public DbSet<User> Users => Set<User>();
    public DbSet<Recipe> Recipes => Set<Recipe>();
    public DbSet<Category> Categories => Set<Category>();
    public DbSet<IngredientItem> IngredientItems => Set<IngredientItem>();
    public DbSet<InstructionStep> InstructionSteps => Set<InstructionStep>();
    public DbSet<Comment> Comments => Set<Comment>();
    public DbSet<Rating> Ratings => Set<Rating>();
    public DbSet<Favorite> Favorites => Set<Favorite>();

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        modelBuilder.Entity<User>()
            .HasMany(u => u.Recipes)
            .WithOne(r => r.Author)
            .HasForeignKey(r => r.AuthorId)
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
            .HasMany(r => r.Comments)
            .WithOne(c => c.Recipe)
            .HasForeignKey(c => c.RecipeId);

        modelBuilder.Entity<Recipe>()
            .HasMany(r => r.Ratings)
            .WithOne(c => c.Recipe)
            .HasForeignKey(c => c.RecipeId);

        modelBuilder.Entity<Recipe>()
            .HasMany(r => r.Favorites)
            .WithOne(f => f.Recipe)
            .HasForeignKey(f => f.RecipeId);

        modelBuilder.Entity<Category>()
            .HasMany(c => c.Recipes)
            .WithOne(r => r.Category)
            .HasForeignKey(r => r.CategoryId);

        modelBuilder.Entity<Comment>()
            .HasOne(c => c.User)
            .WithMany(u => u.Comments)
            .HasForeignKey(c => c.UserId);

        modelBuilder.Entity<Rating>()
            .HasIndex(r => new { r.UserId, r.RecipeId }).IsUnique();

        modelBuilder.Entity<Favorite>()
            .HasIndex(f => new { f.UserId, f.RecipeId }).IsUnique();
        
        
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
    }
}
