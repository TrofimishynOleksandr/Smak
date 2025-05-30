package com.itstep.smakmobile.service;

import com.itstep.smakmobile.dto.*;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApiService {
    @POST("auth/login")
    Call<TokenResponse> login(@Body LoginRequest request);

    @POST("auth/register")
    Call<Void> register(@Body RegisterRequest request);

    @POST("auth/refresh")
    Call<TokenResponse> refresh(@Body RefreshTokenRequest request);
}
