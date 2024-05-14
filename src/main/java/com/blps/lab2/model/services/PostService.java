package com.blps.lab2.model.services;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.blps.lab2.exceptions.AccessDeniedException;
import com.blps.lab2.exceptions.InvalidDataException;
import com.blps.lab2.exceptions.NotFoundException;
import com.blps.lab2.model.beans.post.Address;
import com.blps.lab2.model.beans.post.Metro;
import com.blps.lab2.model.beans.post.Post;
import com.blps.lab2.model.beans.user.User;
import com.blps.lab2.model.repository.post.AddressRepository;
import com.blps.lab2.model.repository.post.MetroRepository;
import com.blps.lab2.model.repository.post.PostRepository;
import com.blps.lab2.model.repository.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final MetroRepository metroRepository;

    private final MetroValidationService metroValidationService;
    private final PostValidationService postValidationService;

    @Qualifier("postTxManager")
    private final PlatformTransactionManager ptm;

    public Post post(String phone, Long addressID, Long metroID, Post post)
            throws InvalidDataException, NotFoundException, AccessDeniedException {
        User user = userRepository.findByPhoneNumber(phone);
        if (user == null) {
            throw new InvalidDataException("User not found");
        }

        if (post.getId() != null) {
            Post savedPost = postRepository.findById(post.getId()).orElse(null);

            if (savedPost == null)
                throw new NotFoundException("Post not found");

            if (savedPost.getUser().getId() != user.getId())
                throw new AccessDeniedException("You can't edit this post. It's not yours");

            post.setPaidUntil(savedPost.getPaidUntil());
        }

        Address address = addressRepository.findById(addressID).orElse(null);
        if (address == null) {
            throw new InvalidDataException("Address not found");
        }
        Metro metro = null;
        if (metroID != null)
            metro = metroRepository.findById(metroID).orElse(null);

        // Validation of metro
        if (metro != null && !metroValidationService.checkMetroAddress(metro, address)) {
            throw new InvalidDataException("Invalid metro address");
        }

        post.setUser(user);
        post.setAddress(address);
        post.setMetro(metro);

        // add additional fields
        post.setCreationDate(Date.from(java.time.Instant.now()));
        post.setArchived(false);

        if (!postValidationService.checkPost(post)) {
            throw new InvalidDataException("Invalid post data");
        }

        Post savedPost = postRepository.save(post);
        return savedPost;

    }

    public void delete(long postId, String userPhone) throws NotFoundException, AccessDeniedException {
        User me = userRepository.findByPhoneNumber(userPhone);
        if (me == null)
            throw new AccessDeniedException("Invalid token. User not found");

        Post post = postRepository.findById(postId).orElse(null);
        if (post == null)
            throw new NotFoundException("Post not found");

        if (me.getId() != post.getUser().getId())
            throw new AccessDeniedException("You can't delete this post. It's not yours");

        post.setArchived(true);
        postRepository.save(post);
    }

    public class GetResult {
        private List<Post> posts;
        private int totalPages;

        public GetResult(List<Post> posts, int pages) {
            this.posts = posts;
            this.totalPages = pages;
        }

        public List<Post> getPosts() {
            return posts;
        }

        public int getTotalPages() {
            return totalPages;
        }
    }

    public GetResult getByUserPhoneNumber(String phoneNumber, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> postPage = postRepository.findByUser_PhoneNumber(phoneNumber, pageable);
        return new GetResult(postPage.getContent(), postPage.getTotalPages());
    }

    public GetResult getByFilterParams(int page, int size, String city, String street, Integer houseNumber,
            Character houseLetter,
            Double minArea,
            Double maxArea, Double minPrice, Double maxPrice, Integer roomNumber, Integer minFloor, Integer maxFloor,
            String stationName, Integer branchNumber) {

        Pageable pageable = PageRequest.of(page, size);

        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("Getting posts transaction");
        TransactionStatus status = ptm.getTransaction(def);
        Page<Post> postPage;
        try {
            postPage = postRepository.findByMany(city, street, houseNumber, houseLetter, minArea, maxArea,
                    minPrice,
                    maxPrice, roomNumber, minFloor, maxFloor, stationName, branchNumber, pageable);
            System.out.println("we are in tx");
        } catch (Exception ex) {
            ptm.rollback(status);
            throw ex;
        }
        ptm.commit(status);
        List<Post> posts = postPage.getContent();

        return new GetResult(posts, postPage.getTotalPages());
    }

}
