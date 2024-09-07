package com.blps.lab2.controllers;

import java.net.URI;
import java.util.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.blps.lab2.controllers.dto.ReceivePost;
import com.blps.lab2.controllers.dto.ResponsePost;
import com.blps.lab2.controllers.dto.ResponseSimplePost;
import com.blps.lab2.exceptions.AccessDeniedException;
import com.blps.lab2.exceptions.InvalidDataException;
import com.blps.lab2.exceptions.NotFoundException;
import com.blps.lab2.model.beans.post.Post;
import com.blps.lab2.model.services.PostService;
import com.blps.lab2.model.services.PostService.GetResult;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/posts")
public class PostsController {

    private final PostService postService;

    @DeleteMapping("/{postId}")
    public ResponseEntity<?> archivePost(@PathVariable long postId, Authentication auth) {
        try {
            postService.delete(postId, auth.getName());
            return ResponseEntity.ok().build();
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/mine")
    public ResponseEntity<?> getUserPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication auth) {

        if (size <= 0) {
            return ResponseEntity.badRequest().body("Invalid page size");
        }

        GetResult result = postService.getByUserPhoneNumber(auth.getName(), page, size);

        if (page >= result.getTotalPages()) {
            return ResponseEntity.badRequest().body("No such page");
        }

        List<ResponseSimplePost> responsePosts = new ArrayList<>();
        for (Post post : result.getPosts()) {
            responsePosts.add(new ResponseSimplePost(post));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("posts", responsePosts);
        response.put("totalPages", result.getTotalPages());
        response.put("currentPage", page);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<?> createPost(@RequestBody ReceivePost entity, Authentication auth) {
        try {
            Post savedPost = postService.post(auth.getName(), entity.getAddressId(), entity.getMetroId(), entity.toPostNoFK());
            URI location = URI.create("/posts/" + savedPost.getId());
            return ResponseEntity.created(location).build();
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Post not found");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{postId}")
    public ResponseEntity<?> updatePost(
            @PathVariable long postId,
            @RequestBody ReceivePost entity,
            Authentication auth) {

        Post post = entity.toPostNoFK();
        post.setId(postId);

        try {
            Post updatedPost = postService.post(auth.getName(), entity.getAddressId(), entity.getMetroId(), post);
            return ResponseEntity.ok(new ResponsePost(updatedPost));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (InvalidDataException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/{postId}")
    public ResponseEntity<?> getPost(@PathVariable long postId, Authentication auth) {
        try {
            ResponsePost postResponse = postService.getPost(postId, auth);
            return ResponseEntity.ok(postResponse);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping
    public ResponseEntity<?> getPosts(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size,
            String city,
            String street,
            Integer houseNumber,
            Character houseLetter,
            Double minArea,
            Double maxArea,
            Double minPrice,
            Double maxPrice,
            Integer roomNumber,
            Integer minFloor,
            Integer maxFloor,
            String stationName,
            Integer branchNumber,
            Authentication auth) {

        try {
            Map<String, Object> response = postService.getFilteredPosts(
                    page, size, city, street, houseNumber, houseLetter, minArea, maxArea, minPrice,
                    maxPrice, roomNumber, minFloor, maxFloor, stationName, branchNumber, auth);

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
