package com.blps.lab2.controllers;

import java.io.IOException;
import java.net.URI;
import java.util.Date;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.blps.lab2.model.beans.logstats.UserHistory;
import com.blps.lab2.model.beans.logstats.UserHistory.UserAction;
import com.blps.lab2.model.beans.post.User;
import com.blps.lab2.model.repository.post.UserRepository;
import com.blps.lab2.model.services.PictureService;
import com.blps.lab2.model.services.PictureService.GetResult;
import com.blps.lab2.model.services.KafkaService;

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
    private final UserRepository userRepository;
    private final KafkaService KafkaService;

    @GetMapping("/pictures/{name}")
    public ResponseEntity<?> getMethodName(
            @PathVariable String name,
            Authentication auth) {

        GetResult result;
        try {
            result = pictureService.getPicture(name);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Failed to get picture");
        }
        if (auth != null) {
            User user = userRepository.findByPhoneNumber(auth.getName());
            UserHistory userHistory = new UserHistory(null, user.getId(), UserAction.GET_PHOTO, null, name,
                    Date.from(java.time.Instant.now()));
            KafkaService.send("user_audit", user.getId().toString(), userHistory);
        }
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(result.getMimeType()))
                .body(result.getImageBytes());
    }

    @PostMapping("/pictures")
    public ResponseEntity<?> postMethodName(
            @RequestParam("file") MultipartFile file,
            Authentication auth) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please select a file to upload");
        }
        String newFileName;
        try {
            newFileName = pictureService.savePicture(file);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Failed to upload file");
        }
        User user = userRepository.findByPhoneNumber(auth.getName());
        UserHistory userHistory = new UserHistory(null, user.getId(), UserAction.ADD_PHOTO, null, newFileName,
                Date.from(java.time.Instant.now()));
        KafkaService.send("user_audit", user.getId().toString(), userHistory);

        URI location = URI.create("/pictures/" + newFileName);
        return ResponseEntity.created(location).build();
    }

}
