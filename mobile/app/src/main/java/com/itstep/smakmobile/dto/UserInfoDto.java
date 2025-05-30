package com.itstep.smakmobile.dto;

import java.util.UUID;

public class UserInfoDto
{
    public UUID id;
    public String name;
    public String email;
    public String avatarUrl;
    public String role;

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getRole() {
        return role;
    }
}
