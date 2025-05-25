using SmakApi.Models.DTOs;

namespace SmakApi.Helpers;

public class ImageHelper : IImageHelper
{
    public async Task<string> SaveImageAsync(IFormFile image, string folderName)
    {
        if (image == null || image.Length == 0)
            throw new ArgumentException("Файл зображення відсутній або порожній");

        var folder = Path.Combine("wwwroot", folderName);
        Directory.CreateDirectory(folder);

        var fileName = $"{Guid.NewGuid()}_{image.FileName}";
        var path = Path.Combine(folder, fileName);

        await using var stream = new FileStream(path, FileMode.Create);
        await image.CopyToAsync(stream);

        return $"/{folderName}/{fileName}";
    }

    public void DeleteImage(string? relativePath)
    {
        if (string.IsNullOrWhiteSpace(relativePath)) return;

        var fullPath = Path.Combine("wwwroot", relativePath.TrimStart('/'));
        if (File.Exists(fullPath)) File.Delete(fullPath);
    }
}

