namespace SmakApi.Helpers;

public interface IImageHelper
{
    Task<string> SaveImageAsync(IFormFile image, string folderName);
    void DeleteImage(string? relativePath);
}

