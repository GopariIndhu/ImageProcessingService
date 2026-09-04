package com.example.image_processing_service.dto;

import java.time.LocalDateTime;

public class ImageResponse {

    private Long id;
    private String originalFilename;
    private String contentType;
    private Long size;
    private Integer width;
    private Integer height;
    private LocalDateTime uploadedAt;

    public ImageResponse(
            Long id,
            String originalFilename,
            String contentType,
            Long size,
            Integer width,
            Integer height,
            LocalDateTime uploadedAt) {

        this.id = id;
        this.originalFilename = originalFilename;
        this.contentType = contentType;
        this.size = size;
        this.width = width;
        this.height = height;
        this.uploadedAt = uploadedAt;
    }

    public Long getId() {
        return id;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public String getContentType() {
        return contentType;
    }

    public Long getSize() {
        return size;
    }

    public Integer getWidth() {
        return width;
    }

    public Integer getHeight() {
        return height;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }
}