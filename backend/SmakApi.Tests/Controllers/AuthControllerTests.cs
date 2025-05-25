using System.Threading.Tasks;
using FluentAssertions;
using Microsoft.AspNetCore.Mvc;
using Moq;
using SmakApi.Controllers;
using SmakApi.Models.DTOs;
using SmakApi.Services.Auth;
using Xunit;

namespace SmakApi.Tests.Controllers
{
    public class AuthControllerTests
    {
        private readonly Mock<IAuthService> _authServiceMock;
        private readonly AuthController _controller;

        public AuthControllerTests()
        {
            _authServiceMock = new Mock<IAuthService>();
            _controller = new AuthController(_authServiceMock.Object);
        }

        [Fact]
        public async Task Register_ShouldReturnOk_WhenRegistrationIsSuccessful()
        {
            // Arrange
            var request = new RegisterRequest
            {
                Email = "test@example.com",
                Password = "Password123!",
                Name = "Test User"
            };

            _authServiceMock
                .Setup(s => s.RegisterAsync(request.Email, request.Password, request.Name))
                .Returns(Task.CompletedTask);

            // Act
            var result = await _controller.Register(request);

            // Assert
            var okResult = result as OkObjectResult;
            okResult.Should().NotBeNull();
            okResult!.Value.Should().Be("Registered");

            _authServiceMock.Verify(s => s.RegisterAsync(request.Email, request.Password, request.Name), Times.Once);
        }

        [Fact]
        public async Task Login_ShouldReturnTokenResponse_WhenCredentialsAreValid()
        {
            // Arrange
            var request = new LoginRequest
            {
                Email = "test@example.com",
                Password = "Password123!"
            };

            var expectedTokens = new TokenResponse
            {
                AccessToken = "access_token",
                RefreshToken = "refresh_token"
            };

            _authServiceMock
                .Setup(s => s.LoginAsync(request.Email, request.Password))
                .ReturnsAsync(expectedTokens);

            // Act
            var result = await _controller.Login(request);

            // Assert
            var okResult = result as OkObjectResult;
            okResult.Should().NotBeNull();
            okResult!.Value.Should().BeEquivalentTo(expectedTokens);

            _authServiceMock.Verify(s => s.LoginAsync(request.Email, request.Password), Times.Once);
        }

        [Fact]
        public void Refresh_ShouldReturnAccessToken_WhenRefreshTokenIsValid()
        {
            // Arrange
            var request = new RefreshTokenRequest { RefreshToken = "valid_refresh_token" };
            var expectedAccessToken = "new_access_token";

            _authServiceMock
                .Setup(s => s.RefreshAccessToken(request.RefreshToken))
                .Returns(expectedAccessToken);

            // Act
            var result = _controller.Refresh(request);

            // Assert
            var okResult = result as OkObjectResult;
            okResult.Should().NotBeNull();
            okResult!.Value.Should().BeEquivalentTo(new { accessToken = expectedAccessToken });

            _authServiceMock.Verify(s => s.RefreshAccessToken(request.RefreshToken), Times.Once);
        }
    }
}
