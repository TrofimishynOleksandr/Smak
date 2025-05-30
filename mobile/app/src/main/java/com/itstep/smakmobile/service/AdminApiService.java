package com.itstep.smakmobile.service;

import com.itstep.smakmobile.dto.CategoryDto;
import com.itstep.smakmobile.dto.UserAdminDto;

import java.util.List;
import java.util.UUID;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface AdminApiService {
    @GET("admin/users")
    Call<List<UserAdminDto>> getAllUsers();

    @DELETE("admin/users/{id}")
    Call<Void> deleteUser(@Path("id") String userId);

    @POST("admin/users/{id}/assign-chef")
    Call<Void> assignChef(@Path("id") String userId);

    @GET("category")
    Call<List<CategoryDto>> getAllCategories();

    @Multipart
    @POST("category")
    Call<Void> createCategory(
            @Part("name") RequestBody name,
            @Part MultipartBody.Part image
    );

    @DELETE("category/{id}")
    Call<Void> deleteCategory(@Path("id") UUID categoryId);
}
