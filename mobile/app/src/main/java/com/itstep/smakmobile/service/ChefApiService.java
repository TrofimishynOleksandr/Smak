package com.itstep.smakmobile.service;

import com.itstep.smakmobile.dto.ChefDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ChefApiService {
    @GET("user/popular-chefs")
    Call<List<ChefDto>> getPopularChefs();
}
