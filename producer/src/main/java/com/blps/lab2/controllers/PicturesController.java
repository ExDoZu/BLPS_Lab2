package com.blps.lab2.controllers;

import java.io.IOException;
import java.net.URI;
import java.util.Date;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.blps.common.UserHistoryDto;
import com.blps.common.UserHistoryDto.UserAction;
import com.blps.lab2.model.services.PictureService;
import com.blps.lab2.model.services.PictureService.GetResult;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.http.MediaType;

@RestController
@RequiredArgsConstructor
public class PicturesController {
    private final PictureService pictureService;

    @GetMapping("/pictures/{name}")
    public ResponseEntity<?> getPicture(@PathVariable String name, Authentication auth) {
        try {
            GetResult result = pictureService.getPicture(name, auth != null ? auth.getName() : null);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(result.getMimeType()))
                    .body(result.getImageBytes());
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Failed to get picture");
        }
    }

    @PostMapping("/pictures")
    public ResponseEntity<?> uploadPicture(@RequestParam("file") MultipartFile file, Authentication auth) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please select a file to upload");
        }
        try {
            String newFileName = pictureService.savePicture(file, auth.getName());
            URI location = URI.create("/pictures/" + newFileName);
            return ResponseEntity.created(location).build();
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Failed to upload file");
        }
    }
}
