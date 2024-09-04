package com.blps.lab2.controllers;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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
import com.blps.common.UserHistoryDto;
import com.blps.common.UserHistoryDto.UserAction;
import com.blps.lab2.model.beans.post.Post;
import com.blps.lab2.model.beans.post.User;
//import com.blps.lab2.model.repository.logstats.UserHistoryRepository;
import com.blps.lab2.model.repository.post.PostRepository;
import com.blps.lab2.model.repository.post.UserRepository;
import com.blps.lab2.model.services.PostService;
import com.blps.lab2.model.services.PostService.GetResult;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class PostsController {

    private final PostService postService;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
//    private final UserHistoryRepository userHistoryRepository;

    @DeleteMapping("/posts")
    public ResponseEntity<?> archivePost(
            Authentication auth,
            long postId) {

        try {
            postService.delete(postId, auth.getName());
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        return ResponseEntity.ok().build();

    }

    @GetMapping("/posts/mine")
    public ResponseEntity<?> getMethodName(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication auth) {
        if (size <= 0)
            return ResponseEntity.badRequest().body("Invalid page size");

        GetResult getResult = postService.getByUserPhoneNumber(auth.getName(), page, size);

        if (page >= getResult.getTotalPages())
            return ResponseEntity.badRequest().body("No such page");

        List<ResponseSimplePost> responsePosts = new ArrayList<>();
        for (Post post : getResult.getPosts()) {
            responsePosts.add(new ResponseSimplePost(post));
        }
        var response = new HashMap<String, Object>();
        response.put("posts", responsePosts);
        response.put("totalPages", getResult.getTotalPages());
        response.put("currentPage", page);
        return ResponseEntity.ok().body(response);

    }

    @PostMapping("/posts")
    public ResponseEntity<?> setPost(
            @RequestBody ReceivePost entity,
            Authentication auth) {

        Post savedPost;
        try {
            savedPost = postService.post(auth.getName(), entity.getAddressId(), entity.getMetroId(),
                    entity.toPostNoFK());
        } catch (NotFoundException e) {
            return new ResponseEntity<>("Post not found", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        URI location = URI.create("/posts/" + String.valueOf(savedPost.getId()));
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/posts/{postId}")
    public ResponseEntity<?> updatePost(
            @PathVariable long postId,
            @RequestBody ReceivePost entity,
            Authentication auth) {

        Post post = entity.toPostNoFK();
        post.setId(postId);
        Long addressID = entity.getAddressId();
        Long metroID = entity.getMetroId();

        Post newPost;
        try {
            newPost = postService.post(auth.getName(), addressID, metroID, post);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (InvalidDataException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }

        return ResponseEntity.ok(new ResponsePost(newPost));
    }

    @GetMapping("/posts/{postId}")
    public ResponseEntity<?> getPost(
            @PathVariable("postId") long postId,
            Authentication auth) {

        Post post = postRepository.findById(postId).orElse(null);
        if (post == null)
            return ResponseEntity.notFound().build();

        boolean archived = post.getArchived();
        Boolean approved = post.getApproved();
        Date paidUntil = post.getPaidUntil();
        if (auth == null) {

            if (!archived && approved && paidUntil != null && paidUntil.after(Date.from(java.time.Instant.now())))
                return ResponseEntity.ok().body(new ResponsePost(post));
        } else {
            User me = userRepository.findByPhoneNumber(auth.getName());

            if ((post.getUser().getId() == me.getId()) || (!archived && approved && paidUntil != null
                    && paidUntil.after(Date.from(java.time.Instant.now())))) {

//                userHistoryRepository.save(new UserHistoryDto(null, me.getId(), UserAction.GET_ONE_POST, post.getId(),
//                        null, Date.from(java.time.Instant.now())));
                return ResponseEntity.ok().body(new ResponsePost(post));
            }
//            userHistoryRepository.save(new UserHistoryDto(null, me.getId(), UserAction.GET_ONE_POST, post.getId(),
//                    "Access denied", Date.from(java.time.Instant.now())));
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

    }

    @GetMapping("/posts")
    public ResponseEntity<?> getPosts(
            @RequestParam(defaultValue = "0") @Min(value = 0) int page,
            @RequestParam(defaultValue = "10") @Min(value = 1) int size,
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

        GetResult getResult = postService.getByFilterParams(
                page, size, city,
                street, houseNumber, houseLetter,
                minArea, maxArea, minPrice,
                maxPrice, roomNumber, minFloor,
                maxFloor, stationName, branchNumber);

        List<ResponseSimplePost> responsePosts = new ArrayList<>();
        for (Post post : getResult.getPosts()) {
            responsePosts.add(new ResponseSimplePost(post));
        }

        if (auth != null) {
            User user = userRepository.findByPhoneNumber(auth.getName());
//            userHistoryRepository.save(new UserHistoryDto(null, user.getId(), UserAction.GET_POSTS, null, null,
//                    Date.from(java.time.Instant.now())));
        }

        if (page >= getResult.getTotalPages())
            return ResponseEntity.badRequest().body("No such page");
        var response = new HashMap<String, Object>();
        response.put("posts", responsePosts);
        response.put("totalPages", getResult.getTotalPages());
        response.put("currentPage", page);
        return ResponseEntity.ok().body(response);

    }

}
