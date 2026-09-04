package com.example.image_processing_service.service;

import com.example.image_processing_service.dto.ImageResponse;
import com.example.image_processing_service.entity.Image;
import com.example.image_processing_service.entity.User;
import com.example.image_processing_service.repository.ImageRepository;
import com.example.image_processing_service.repository.UserRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ImageService {

    private final ImageRepository imageRepository;
    private final UserRepository userRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public ImageService(
            ImageRepository imageRepository,
            UserRepository userRepository) {

        this.imageRepository = imageRepository;
        this.userRepository = userRepository;
    }

    public ImageResponse uploadImage(
            MultipartFile file,
            String email) throws IOException {

        if (file.isEmpty()) {
            throw new RuntimeException("Image file cannot be empty");
        }

        BufferedImage bufferedImage =
                ImageIO.read(file.getInputStream());

        if (bufferedImage == null) {
            throw new RuntimeException(
                    "Uploaded file is not a valid image"
            );
        }

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(
                        () -> new RuntimeException("User not found")
                );

        String originalFilename =
                StringUtils.cleanPath(
                        file.getOriginalFilename() != null
                                ? file.getOriginalFilename()
                                : "image"
                );

        String extension =
                StringUtils.getFilenameExtension(originalFilename);

        String storedFilename =
                UUID.randomUUID()
                        + (extension != null
                        ? "." + extension.toLowerCase()
                        : "");

        Path uploadPath =
                Paths.get(uploadDir)
                        .toAbsolutePath()
                        .normalize();

        Files.createDirectories(uploadPath);

        Path targetLocation =
                uploadPath.resolve(storedFilename);

        Files.copy(
                file.getInputStream(),
                targetLocation,
                StandardCopyOption.REPLACE_EXISTING
        );

        Image image = new Image();

        image.setOriginalFilename(originalFilename);
        image.setStoredFilename(storedFilename);

        image.setContentType(
                file.getContentType() != null
                        ? file.getContentType()
                        : "application/octet-stream"
        );

        image.setSize(file.getSize());

        image.setWidth(bufferedImage.getWidth());
        image.setHeight(bufferedImage.getHeight());

        image.setFilePath(
                targetLocation.toString()
        );

        image.setUploadedAt(
                LocalDateTime.now()
        );

        image.setUser(user);

        Image savedImage =
                imageRepository.save(image);

        return new ImageResponse(
                savedImage.getId(),
                savedImage.getOriginalFilename(),
                savedImage.getContentType(),
                savedImage.getSize(),
                savedImage.getWidth(),
                savedImage.getHeight(),
                savedImage.getUploadedAt()
        );
    }

    public List<ImageResponse> getUserImages(String email) {

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(
                        () -> new RuntimeException("User not found")
                );

        return imageRepository
                .findByUserId(user.getId())
                .stream()
                .map(image -> new ImageResponse(
                        image.getId(),
                        image.getOriginalFilename(),
                        image.getContentType(),
                        image.getSize(),
                        image.getWidth(),
                        image.getHeight(),
                        image.getUploadedAt()
                ))
                .toList();
    }
    public Image getImageForUser(Long imageId, String email) {

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(
                        () -> new RuntimeException("User not found")
                );

        Image image = imageRepository
                .findById(imageId)
                .orElseThrow(
                        () -> new RuntimeException("Image not found")
                );

        if (!image.getUser().getId().equals(user.getId())) {
            throw new RuntimeException(
                    "You are not allowed to access this image"
            );
        }

        return image;
    }
    public void deleteImage(Long imageId, String email) throws IOException {

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(
                        () -> new RuntimeException("User not found")
                );

        Image image = imageRepository
                .findById(imageId)
                .orElseThrow(
                        () -> new RuntimeException("Image not found")
                );

        if (!image.getUser().getId().equals(user.getId())) {
            throw new RuntimeException(
                    "You are not allowed to delete this image"
            );
        }

        Path filePath = Paths.get(image.getFilePath());

        Files.deleteIfExists(filePath);

        imageRepository.delete(image);
    }
}