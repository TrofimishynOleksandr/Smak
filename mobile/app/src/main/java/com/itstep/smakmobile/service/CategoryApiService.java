package com.itstep.smakmobile.service;

import com.itstep.smakmobile.dto.CategoryDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface CategoryApiService {
    @GET("category")
    Call<List<CategoryDto>> getCategories();
}
