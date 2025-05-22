using SmakApi.Models.DTOs;

namespace SmakApi.Helpers;

public static class ImageHelper
{
    public static async Task<string> SaveImageAsync(IFormFile image, string folderName)
    {
        var folder = Path.Combine("wwwroot", folderName);
        Directory.CreateDirectory(folder);

        var fileName = $"{Guid.NewGuid()}_{image.FileName}";
        var path = Path.Combine(folder, fileName);

        await using var stream = new FileStream(path, FileMode.Create);
        await image.CopyToAsync(stream);

        return $"/{folderName}/{fileName}";
    }

    public static void DeleteImage(string? relativePath)
    {
        if (string.IsNullOrWhiteSpace(relativePath)) return;

        var fullPath = Path.Combine("wwwroot", relativePath.TrimStart('/'));
        if (File.Exists(fullPath)) File.Delete(fullPath);
    }

    public static string? ToFullImageUrl(string? relativePath, HttpRequest request)
    {
        if (string.IsNullOrWhiteSpace(relativePath)) return null;
        return $"{request.Scheme}://{request.Host}{relativePath}";
    }
}

