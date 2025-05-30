package com.itstep.smakmobile.service;

import com.itstep.smakmobile.dto.CategoryDto;
import com.itstep.smakmobile.dto.ChefDto;
import com.itstep.smakmobile.dto.IngredientDto;
import com.itstep.smakmobile.dto.RecipeCollectionDto;
import com.itstep.smakmobile.dto.RecipeDetailsDto;
import com.itstep.smakmobile.dto.RecipeShortDto;
import com.itstep.smakmobile.dto.ReviewRequest;
import com.itstep.smakmobile.dto.UnitDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public interface RecipeApiService {

    @GET("recipe/collections")
    Call<List<RecipeCollectionDto>> getRecipeCollections();

    @GET("recipe/{id}")
    Call<RecipeDetailsDto> getRecipeDetails(@Path("id") UUID recipeId);

    @POST("recipe/{id}/favorite")
    Call<Void> addToFavorites(@Path("id") UUID recipeId);

    @DELETE("recipe/{id}/favorite")
    Call<Void> removeFromFavorites(@Path("id") UUID recipeId);

    @POST("recipe/{id}/review")
    Call<Void> addReview(@Path("id") UUID recipeId, @Body ReviewRequest dto);

    @DELETE("review/{recipeId}")
    Call<Void> deleteReview(@Path("recipeId") UUID recipeId);

    @DELETE("recipe/{id}")
    Call<Void> deleteRecipe(@Path("id") UUID recipeId);

    @GET("category")
    Call<List<CategoryDto>> getCategories();

    @GET("recipe/search")
    Call<List<RecipeShortDto>> searchRecipes(@QueryMap Map<String, String> query);

    @GET("recipe/favorites")
    Call<List<RecipeShortDto>> getFavoriteRecipes();

    @GET("user/{id}")
    Call<ChefDto> getChefById(@Path("id") UUID id);

    @Multipart
    @POST("api/recipe")
    Call<Void> createRecipe(
            @Part MultipartBody.Part image,
            @Part("Title") RequestBody title,
            @Part("Description") RequestBody description,
            @Part("CookTimeMinutes") RequestBody cookTimeMinutes,
            @Part("CategoryId") RequestBody categoryId,
            @Part("Ingredients") RequestBody ingredientsJson,
            @Part("Instructions") RequestBody instructionsJson,
            @Part List<MultipartBody.Part> InstructionImages
    );

    @GET("ingredient")
    Call<List<IngredientDto>> getAvailableIngredients();

    @GET("ingredient/units")
    Call<List<UnitDto>> getUnits();
}

