package com.konkuk.hackerthonrelay.pictureupload;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/upload")
public class ImageUploadController {

    private final ImageUploadService uploadService;
    private final PostRepository postRepository;

    public ImageUploadController(ImageUploadService uploadService, PostRepository postRepository) {
        this.uploadService = uploadService;
        this.postRepository = postRepository;
    }

    @PostMapping
    public String uploadImages(@RequestParam("images") List<MultipartFile> multipartFiles) {
        try {
            Post post = new Post();
            List<Image> images = multipartFiles.stream()
                    .map(multipartFile -> {
                        try {
                            return uploadService.uploadImage(multipartFile);
                        } catch (IOException e) {
                            throw new RuntimeException("Could not upload file. Please try again.", e);
                        }
                    })
                    .collect(Collectors.toList());
            post.setImages(images);
			if (!images.isEmpty()) {
				post.setMainImage(images.get(0)); // Set the first image as the main image
			}
            postRepository.save(post);
            return "Files uploaded successfully: " + multipartFiles.size();
        } catch (RuntimeException e) {
            throw new RuntimeException("Could not upload files. Please try again.", e);
        }
    }
}

