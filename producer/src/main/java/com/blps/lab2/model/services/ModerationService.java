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
import com.blps.lab2.exceptions.NotFoundException;
import com.blps.common.ModerHistoryDto;
import com.blps.common.ModerHistoryDto.ModerAction;
import com.blps.lab2.model.beans.post.Post;
import com.blps.lab2.model.beans.post.User;
import com.blps.lab2.model.repository.post.PostRepository;
import com.blps.lab2.model.repository.post.UserRepository;
import com.blps.lab2.model.services.KafkaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ModerationService {

    @Qualifier("postTxManager")
    private final PlatformTransactionManager postTxManager;

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final KafkaService kafkaService;

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

    public ModerationResult getModerationPosts(int page, int size) { 
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> postPage = postRepository.findByArchivedAndApproved(false, null, pageable);
        List<Post> posts = postPage.getContent();
        return new ModerationResult(posts, postPage.getTotalPages());
    }

    public void approve(long postId, String moderatorPhone, boolean approved)
            throws AccessDeniedException, NotFoundException {
        
        
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("Approving transaction");
        TransactionStatus status = postTxManager.getTransaction(def);
        try {
            User me = userRepository.findByPhoneNumber(moderatorPhone);
            Post post = postRepository.findById(postId).orElse(null);
            if (post == null) {
                throw new NotFoundException("Post not found");
            }
            post.setApproved(approved);
            ModerAction action = approved ? ModerAction.APPROVE : ModerAction.REJECT;

            ModerHistoryDto moderHistory = new ModerHistoryDto(
                null, me.getId(),
                action,
                post.getId(),
                null,
                Date.from(java.time.Instant.now())
            );
            kafkaService.send("moder_audit", me.getId().toString(), moderHistory);

            if (approved) {
                post.setArchived(false);
            }
            postRepository.save(post);

        } catch (Exception ex) {
            postTxManager.rollback(status);
            throw ex;
        }
        postTxManager.commit(status);
    }
}
