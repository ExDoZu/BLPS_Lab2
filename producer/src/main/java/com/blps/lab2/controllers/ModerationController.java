package com.blps.lab2.controllers;

import java.util.HashMap;
import java.util.List;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.blps.lab2.controllers.dto.ReceiveModerationApprovement;
import com.blps.lab2.controllers.dto.ResponsePost;
import com.blps.lab2.exceptions.AccessDeniedException;
import com.blps.lab2.exceptions.NotFoundException;
import com.blps.lab2.model.services.ModerationService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class ModerationController {

    private final ModerationService moderationService;

    @GetMapping("/moderation")
    public ResponseEntity<?> getModerationPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication auth) {
        if (size <= 0) {
            return ResponseEntity.badRequest().body("Invalid page size");
        }

        try {
            var response = moderationService.getModerationPosts(page, size, auth.getName());
            return ResponseEntity.ok().body(response);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/moderation")
    public ResponseEntity<?> setModeration(
            @RequestBody ReceiveModerationApprovement body,
            Authentication auth) {
        try {
            moderationService.approve(body.getPostId(), auth.getName(), body.isApproved());
            return ResponseEntity.ok().build();
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
