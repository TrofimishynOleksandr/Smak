namespace SmakApi.Services.Image;

public interface IImageService
{
    Task<string> SaveImageAsync(IFormFile image, string folderName);
    void DeleteImage(string? relativePath);
}
