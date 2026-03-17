package com.courier.management.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

    @Value("${file.upload-dir}")
    private String uploadDir; 
    public String saveImage(MultipartFile file) throws Exception {

        if (file == null || file.isEmpty())
            return null;

        String original = file.getOriginalFilename();
        String ext = original.substring(original.lastIndexOf("."));

        String hash = UUID.randomUUID().toString().replace("-", "");
        String fileName = hash + ext;

        // ✅ Correct path join
        Path path = Paths.get(uploadDir, "customer", fileName);

        // ✅ Ensure folder exists
        Files.createDirectories(path.getParent());

        Files.write(path, file.getBytes());

        // Return the URL path for DB
        return "/uploads/customer/" + fileName;
    }
}