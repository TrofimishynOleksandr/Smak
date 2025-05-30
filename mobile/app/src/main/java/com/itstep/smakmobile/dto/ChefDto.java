package com.itstep.smakmobile.dto;

import java.util.UUID;

public class ChefDto {
    public UUID id;
    public String name;
    public String avatarUrl;

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }
}
