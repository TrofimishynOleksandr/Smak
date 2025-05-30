package com.itstep.smakmobile.dto;

import android.net.Uri;

public class InstructionCreateDto {
    private String description;
    private Uri imageUri;

    public InstructionCreateDto(String description, Uri imageUri) {
        this.description = description;
        this.imageUri = imageUri;
    }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Uri getImageUri() { return imageUri; }
    public void setImageUri(Uri imageUri) { this.imageUri = imageUri; }
}

