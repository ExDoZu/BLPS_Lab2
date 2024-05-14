package com.blps.lab2.model.services;

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
import com.blps.lab2.exceptions.NotFoundException;
import com.blps.lab2.model.beans.post.Post;
import com.blps.lab2.model.beans.user.User;
import com.blps.lab2.model.repository.post.PostRepository;
import com.blps.lab2.model.repository.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ModerationService {

    @Qualifier("postTxManager")
    PlatformTransactionManager txManager;

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public class ModerationResult {
        public List<Post> posts;
        public int totalPages;

        public ModerationResult(List<Post> posts, int totalPages) {
            this.posts = posts;
            this.totalPages = totalPages;
        }

        public List<Post> getPosts() {
            return posts;
        }

        public int getTotalPages() {
            return totalPages;
        }
    }

    public ModerationResult getModerationPosts(int page, int size, String moderatorPhone) {
        User me = userRepository.findByPhoneNumber(moderatorPhone);
        if (me == null) {
            throw new AccessDeniedException("User not found");
        }

        if (!me.isModerator()) {
            throw new AccessDeniedException("Not a moderator");
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Post> postPage = postRepository.findByArchivedAndApproved(false, null, pageable);
        List<Post> posts = postPage.getContent();

        return new ModerationResult(posts, postPage.getTotalPages());
    }

    public void approve(long postId, String moderatorPhone, boolean approved)
            throws AccessDeniedException, NotFoundException {
        User me = userRepository.findByPhoneNumber(moderatorPhone);
        if (me == null) {
            throw new AccessDeniedException("User not found");
        }
        if (!me.isModerator()) {
            throw new AccessDeniedException("Not a moderator");
        }

        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("Approving transaction");
        TransactionStatus status = txManager.getTransaction(def);
        try {
            Post post = postRepository.findById(postId).orElse(null);
            if (post == null) {
                throw new NotFoundException("Post not found");
            }
            post.setApproved(approved);
            if (approved) {
                post.setArchived(false);
            }
            postRepository.save(post);
        } catch (Exception ex) {
            txManager.rollback(status);
            throw ex;
        }
        txManager.commit(status);
    }
}
