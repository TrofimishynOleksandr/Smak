package com.itstep.smakmobile.dto;

import com.google.gson.annotations.SerializedName;

public class AvatarResponse {
    @SerializedName("avatarUrl")
    private String avatarUrl;

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
