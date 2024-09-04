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

//import com.blps.common.UserHistory;
//import com.blps.common.UserHistory.UserAction;
import com.blps.lab2.model.beans.post.Address;
import com.blps.lab2.model.beans.post.Metro;
import com.blps.lab2.model.beans.post.Post;
import com.blps.lab2.model.beans.post.User;
//import com.blps.lab2.model.repository.logstats.UserHistoryRepository;
import com.blps.lab2.model.repository.post.AddressRepository;
import com.blps.lab2.model.repository.post.MetroRepository;
import com.blps.lab2.model.repository.post.PostRepository;
import com.blps.lab2.model.repository.post.UserRepository;

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
    private final PlatformTransactionManager txManager;

//    private final UserHistoryRepository userHistoryRepository;

    public Post post(String phone, Long addressID, Long metroID, Post post)
            throws InvalidDataException, NotFoundException, AccessDeniedException {

        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("Post new/edit transaction");
        TransactionStatus status = txManager.getTransaction(def);

        Post savedPost;
        try {
            User user = userRepository.findByPhoneNumber(phone);
            boolean edit = false;
            if (post.getId() != null) {
                edit = true;
                savedPost = postRepository.findById(post.getId()).orElse(null);

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

            
            if (metro != null && !metroValidationService.checkMetroAddress(metro, address)) {
                throw new InvalidDataException("Invalid metro address");
            }

            post.setUser(user);
            post.setAddress(address);
            post.setMetro(metro);

            
            post.setCreationDate(Date.from(java.time.Instant.now()));
            post.setArchived(false);

            if (!postValidationService.checkPost(post)) {
                throw new InvalidDataException("Invalid post data");
            }

            savedPost = postRepository.save(post);

//            userHistoryRepository.save(new UserHistory(null, user.getId(),
//                    edit ? UserHistory.UserAction.EDIT : UserHistory.UserAction.CREATE,
//                    savedPost.getId(),
//                    null,
//                    Date.from(java.time.Instant.now())));

        } catch (Exception ex) {
            txManager.rollback(status);
            throw ex;
        }
        txManager.commit(status);

        return savedPost;

    }

    public void delete(long postId, String userPhone) throws NotFoundException, AccessDeniedException {

        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("Post delete transaction");
        TransactionStatus status = txManager.getTransaction(def);

        Post post;
        try {
            User me = userRepository.findByPhoneNumber(userPhone);
            post = postRepository.findById(postId).orElse(null);
            if (post == null)
                throw new NotFoundException("Post not found");

            if (me.getId() != post.getUser().getId())
                throw new AccessDeniedException("You can't delete this post. It's not yours");

            post.setArchived(true);
            post = postRepository.save(post);
//            userHistoryRepository.save(new UserHistory(null, me.getId(),
//                    UserHistory.UserAction.ARCHIVE,
//                    post.getId(),
//                    null,
//                    Date.from(java.time.Instant.now())));
        } catch (Exception ex) {
            txManager.rollback(status);
            throw ex;
        }
        txManager.commit(status);

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

        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("get posts by phone number");
        TransactionStatus status = txManager.getTransaction(def);

        Pageable pageable = PageRequest.of(page, size);
        Page<Post> postPage;
        try {
            postPage = postRepository.findByUser_PhoneNumber(phoneNumber, pageable);

            final User user = userRepository.findByPhoneNumber(phoneNumber);
//            userHistoryRepository.save(new UserHistory(null,
//                    user.getId(),
//                    UserAction.GET_POSTS,
//                    null,
//                    "as owner",
//                    Date.from(java.time.Instant.now())));

        } catch (Exception ex) {
            txManager.rollback(status);
            throw ex;
        }
        txManager.commit(status);

        return new GetResult(postPage.getContent(), postPage.getTotalPages());
    }

    public GetResult getByFilterParams(int page, int size, String city, String street, Integer houseNumber,
            Character houseLetter,
            Double minArea,
            Double maxArea, Double minPrice, Double maxPrice, Integer roomNumber, Integer minFloor, Integer maxFloor,
            String stationName, Integer branchNumber) {

        Pageable pageable = PageRequest.of(page, size);

        Page<Post> postPage;

        postPage = postRepository.findByMany(city, street, houseNumber, houseLetter, minArea, maxArea,
                minPrice,
                maxPrice, roomNumber, minFloor, maxFloor, stationName, branchNumber, pageable);

        List<Post> posts = postPage.getContent();

        return new GetResult(posts, postPage.getTotalPages());
    }

}
