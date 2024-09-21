//package com.blps.lab2.model.services;
//
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.stream.Collectors;
//
//import org.springframework.data.domain.Pageable;
////import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//
//import org.springframework.stereotype.Service;
//// import org.springframework.transaction.PlatformTransactionManager;
//// import org.springframework.transaction.TransactionStatus;
//// import org.springframework.transaction.support.DefaultTransactionDefinition;
//
//import com.blps.lab2.exceptions.AccessDeniedException;
//import com.blps.lab2.exceptions.NotFoundException;
//import com.blps.common.ModerHistoryDto;
//import com.blps.common.ModerHistoryDto.ModerAction;
//import com.blps.lab2.model.beans.post.Post;
//import com.blps.lab2.model.beans.post.User;
//import com.blps.lab2.model.repository.post.PostRepository;
//import com.blps.lab2.model.repository.post.UserRepository;
//import com.blps.lab2.controllers.dto.ResponsePost;
//
//import lombok.RequiredArgsConstructor;
//
//@Service
//@RequiredArgsConstructor
//public class ModerationService {
//
//    // @Qualifier("postTxManager")
//    // private final PlatformTransactionManager postTxManager;
//
//    private final PostRepository postRepository;
//    private final UserRepository userRepository;
//    private final KafkaService kafkaService;
//
//    public HashMap<String, Object> getModerationPosts(int page, int size, String moderatorPhone) throws AccessDeniedException {
//        User user = userRepository.findByPhoneNumber(moderatorPhone);
//
//        Pageable pageable = PageRequest.of(page, size);
//        Page<Post> postPage = postRepository.findByArchivedAndApproved(false, null, pageable);
//
//        if (page >= postPage.getTotalPages()) {
//            throw new IllegalArgumentException("Invalid page number");
//        }
//
//        List<ResponsePost> responsePosts = postPage.getContent()
//                .stream()
//                .map(ResponsePost::new)
//                .collect(Collectors.toList());
//
//        ModerHistoryDto moderHistory = new ModerHistoryDto(
//                null, user.getId(), ModerAction.GET_POSTS, null, null, Date.from(java.time.Instant.now()));
//        kafkaService.send("moder_audit", user.getId().toString(), moderHistory);
//
//        var response = new HashMap<String, Object>();
//        response.put("currentPage", page);
//        response.put("totalPages", postPage.getTotalPages());
//        response.put("posts", responsePosts);
//        return response;
//    }
//
//    public void approve(long postId, String moderatorPhone, boolean approved)
//            throws AccessDeniedException, NotFoundException {
//
//        // DefaultTransactionDefinition def = new DefaultTransactionDefinition();
//        // def.setName("Approving transaction");
//        // TransactionStatus status = postTxManager.getTransaction(def);
//
//        try {
//            User me = userRepository.findByPhoneNumber(moderatorPhone);
//            Post post = postRepository.findById(postId).orElseThrow(() -> new NotFoundException("Post not found"));
//
//            post.setApproved(approved);
//            ModerAction action = approved ? ModerAction.APPROVE : ModerAction.REJECT;
//
//            ModerHistoryDto moderHistory = new ModerHistoryDto(
//                null, me.getId(),
//                action,
//                post.getId(),
//                null,
//                Date.from(java.time.Instant.now())
//            );
//            kafkaService.send("moder_audit", me.getId().toString(), moderHistory);
//
//            if (approved) {
//                post.setArchived(false);
//            }
//            postRepository.save(post);
//
//        } catch (Exception ex) {
////            postTxManager.rollback(status);
//            throw ex;
//        }
////        postTxManager.commit(status);
//    }
//}
