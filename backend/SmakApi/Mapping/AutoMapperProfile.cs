using AutoMapper;
using SmakApi.Models.DTOs;
using SmakApi.Models.Entities;

namespace SmakApi.Mapping;

public class AutoMapperProfile : Profile
{
    public AutoMapperProfile()
    {
        CreateMap<Recipe, RecipeShortDto>()
            .ForMember(dest => dest.AuthorName, opt => opt.MapFrom(src => src.Author.Name));

        CreateMap<IngredientItem, IngredientItemDto>()
            .ForMember(dest => dest.Name, opt => opt.MapFrom(src => src.Ingredient.Name));

        CreateMap<InstructionStep, InstructionStepDto>();
        CreateMap<Review, ReviewDto>()
            .ForMember(dest => dest.Author, opt => opt.MapFrom(src => src.User.Name));
    }
}
