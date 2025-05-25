using Microsoft.AspNetCore.Http;
using Microsoft.EntityFrameworkCore;
using Moq;
using SmakApi.Data;
using SmakApi.Helpers;
using SmakApi.Models.Entities;
using SmakApi.Services.User;

namespace SmakApi.Tests.Services;

public class UserServiceTests
{
    private SmakDbContext GetDbContext()
    {
        var options = new DbContextOptionsBuilder<SmakDbContext>()
            .UseInMemoryDatabase(Guid.NewGuid().ToString())
            .Options;

        var context = new SmakDbContext(options);
        context.Database.EnsureCreated();

        return context;
    }

    private IUserService GetUserService(SmakDbContext context, IImageHelper? imageHelper = null)
    {
        return new UserService(context, imageHelper ?? Mock.Of<IImageHelper>());
    }

    [Fact]
    public async Task GetChefAsync_ShouldReturnChef()
    {
        using var context = GetDbContext();
        var user = new User { Id = Guid.NewGuid(), Name = "Chef", 
            Email = "test@example.com",
            PasswordHash = "hashed",
            Role = UserRole.Chef };
        context.Users.Add(user);
        await context.SaveChangesAsync();

        var service = GetUserService(context);
        var result = await service.GetChefAsync(user.Id);

        Assert.Equal(user.Id, result.Id);
        Assert.Equal(user.Name, result.Name);
    }

    [Fact]
    public async Task GetPopularChefsAsync_ShouldReturnTopChefs()
    {
        using var context = GetDbContext();
        var chef = new User
        {
            Id = Guid.NewGuid(),
            Name = "Top Chef",
            Email = "test@example.com",
            PasswordHash = "hashed",
            Role = UserRole.Chef,
            Recipes = new List<Recipe>
            {
                new Recipe
                {
                    Id = Guid.NewGuid(),
                    Title = "Test",
                    Description = "Test",
                    Reviews = new List<Review>
                    {
                        new Review { Id = Guid.NewGuid(), Rating = 5 }
                    }
                }
            }
        };

        context.Users.Add(chef);
        await context.SaveChangesAsync();

        var service = GetUserService(context);
        var result = await service.GetPopularChefsAsync();

        Assert.Single(result);
        Assert.Equal(5, result.First().AverageRating);
    }

    [Fact]
    public async Task GetCurrentUserAsync_ShouldReturnUserInfo()
    {
        using var context = GetDbContext();
        var user = new User { Id = Guid.NewGuid(), Name = "User", 
            Email = "u@x.com",
            PasswordHash = "hashed",
            Role = UserRole.User };
        context.Users.Add(user);
        await context.SaveChangesAsync();

        var service = GetUserService(context);
        var result = await service.GetCurrentUserAsync(user.Id);

        Assert.Equal(user.Id, result.Id);
        Assert.Equal(user.Name, result.Name);
        Assert.Equal(user.Email, result.Email);
    }

    [Fact]
    public async Task UpdateNameAsync_ShouldUpdateUserName()
    {
        using var context = GetDbContext();
        var user = new User { Id = Guid.NewGuid(), Name = "Old Name", Email = "test@example.com",
            PasswordHash = "hashed",
        };
        context.Users.Add(user);
        await context.SaveChangesAsync();

        var service = GetUserService(context);
        await service.UpdateNameAsync(user.Id, "New Name");

        var updatedUser = await context.Users.FindAsync(user.Id);
        Assert.Equal("New Name", updatedUser!.Name);
    }

    [Fact]
    public async Task UploadAvatarAsync_ShouldSetAvatarUrl()
    {
        using var context = GetDbContext();
        var user = new User { Id = Guid.NewGuid(), Name = "User", 
            Email = "test@example.com",
            PasswordHash = "hashed",
            AvatarUrl = "old.png" };
        context.Users.Add(user);
        await context.SaveChangesAsync();

        var imageMock = new Mock<IFormFile>();
        var stream = new MemoryStream(System.Text.Encoding.UTF8.GetBytes("dummy content"));
        imageMock.Setup(f => f.OpenReadStream()).Returns(stream);
        imageMock.Setup(f => f.FileName).Returns("avatar.png");
        imageMock.Setup(f => f.Length).Returns(stream.Length);

        var imageHelperMock = new Mock<IImageHelper>();
        imageHelperMock
            .Setup(x => x.SaveImageAsync(It.IsAny<IFormFile>(), It.IsAny<string>()))
            .ReturnsAsync("new-avatar.png");
        imageHelperMock.Setup(x => x.DeleteImage(It.IsAny<string>()));

        var service = GetUserService(context, imageHelperMock.Object);
        var result = await service.UploadAvatarAsync(user.Id, imageMock.Object);

        var updatedUser = await context.Users.FindAsync(user.Id);
        Assert.Equal("new-avatar.png", updatedUser!.AvatarUrl);
        Assert.Equal("new-avatar.png", result);
    }

    [Fact]
    public async Task DeleteAvatarAsync_ShouldRemoveAvatar()
    {
        using var context = GetDbContext();
        var user = new User { Id = Guid.NewGuid(), 
            Name = "User",
            Email = "test@example.com",
            PasswordHash = "hashed",
            AvatarUrl = "avatar.png" };
        context.Users.Add(user);
        await context.SaveChangesAsync();

        var imageHelperMock = new Mock<IImageHelper>();
        imageHelperMock.Setup(x => x.DeleteImage(It.IsAny<string>()));

        var service = GetUserService(context, imageHelperMock.Object);
        await service.DeleteAvatarAsync(user.Id);

        var updatedUser = await context.Users.FindAsync(user.Id);
        Assert.Null(updatedUser!.AvatarUrl);
    }
}