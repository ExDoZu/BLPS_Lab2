package com.blps.lab2.controllers;

import java.util.Date;
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
import com.blps.lab2.model.beans.post.Post;
import com.blps.lab2.model.beans.post.User;
import com.blps.lab2.model.repository.post.UserRepository;
import com.blps.lab2.model.services.ModerationService;
import com.blps.lab2.model.services.ModerationService.ModerationResult;
import com.blps.lab2.model.services.KafkaService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.blps.common.ModerHistoryDto;
import com.blps.common.ModerHistoryDto.ModerAction;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class ModerationController {

    private final ModerationService moderationService;

    private final UserRepository userRepository;
    private final KafkaService kafkaService;

    @GetMapping("/moderation")
    public ResponseEntity<?> getModerationPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication auth) {
        if (size <= 0)
            return ResponseEntity.badRequest().body("Invalid page size");

        ModerationResult moderationResult;
        try {
            moderationResult = moderationService.getModerationPosts(page, size);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }

        User user = userRepository.findByPhoneNumber(auth.getName());

        ModerHistoryDto moderHistory = new ModerHistoryDto(
                null, user.getId(), ModerAction.GET_POSTS, null, null, Date.from(java.time.Instant.now()));
        kafkaService.send("moder_audit", user.getId().toString(), moderHistory);

        if (page >= moderationResult.getTotalPages())
            return ResponseEntity.badRequest().body("Invalid page number");

        List<ResponsePost> responsePosts = new java.util.ArrayList<>();
        for (Post post : moderationResult.posts) {
            responsePosts.add(new ResponsePost(post));
        }
        var response = new HashMap<String, Object>();
        response.put("currentPage", page);
        response.put("totalPages", moderationResult.getTotalPages());
        response.put("posts", responsePosts);
        return ResponseEntity.ok().body(response);

    }

    @PostMapping("/moderation")
    public ResponseEntity<?> setModeration(
            @RequestBody ReceiveModerationApprovement body,
            Authentication auth) {
        try {
            moderationService.approve(body.getPostId(), auth.getName(), body.isApproved());
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok().build();
    }

}
