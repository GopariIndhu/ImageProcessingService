package com.example.image_processing_service.controller;

import com.example.image_processing_service.dto.ImageResponse;
import com.example.image_processing_service.service.ImageService;
import com.example.image_processing_service.entity.Image;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.io.IOException;
import java.security.Principal;

import java.util.List;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping
    public ResponseEntity<ImageResponse> uploadImage(
            @RequestParam("file") MultipartFile file,
            Principal principal) throws IOException {

        ImageResponse response =
                imageService.uploadImage(
                        file,
                        principal.getName()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
    @GetMapping("/{id}/file")
    public ResponseEntity<Resource> getImageFile(
            @PathVariable Long id,
            Principal principal) throws IOException {

        Image image = imageService.getImageForUser(
                id,
                principal.getName()
        );

        Path filePath = Paths.get(
                image.getFilePath()
        );

        Resource resource =
                new UrlResource(filePath.toUri());

        if (!resource.exists()) {
            throw new RuntimeException(
                    "Image file not found"
            );
        }

        String contentType =
                Files.probeContentType(filePath);

        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(
                        MediaType.parseMediaType(contentType)
                )
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" +
                                image.getOriginalFilename() +
                                "\""
                )
                .body(resource);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteImage(
            @PathVariable Long id,
            Principal principal) throws IOException {

        imageService.deleteImage(
                id,
                principal.getName()
        );

        return ResponseEntity.noContent().build();
    }
}
