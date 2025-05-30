package com.itstep.smakmobile.dto;

import java.util.Date;
import java.util.UUID;

public class ReviewDto {
    public UUID id;
    public String author;
    public UUID authorId;
    public String avatarUrl;
    public int rating;
    public String comment;
    public Date createdAt;

    public UUID getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public UUID getAuthorId() {
        return authorId;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public int getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public Date getCreatedAt() {
        return createdAt;
    }
}
