package com.itstep.smakmobile.service;

import com.itstep.smakmobile.dto.AvatarResponse;
import com.itstep.smakmobile.dto.UserInfoDto;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface UserApiService {
    @GET("user/me")
    Call<UserInfoDto> getMe();

    @Multipart
    @POST("user/avatar")
    Call<AvatarResponse> uploadAvatar(@Part MultipartBody.Part image);

    @DELETE("user/avatar")
    Call<Void> deleteAvatar();
}
