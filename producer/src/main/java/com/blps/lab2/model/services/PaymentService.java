package com.blps.lab2.model.services;

import java.util.Date;

//import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
// import org.springframework.transaction.PlatformTransactionManager;
// import org.springframework.transaction.TransactionStatus;
// import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.blps.lab2.exceptions.AccessDeniedException;
import com.blps.lab2.exceptions.PaymentException;
import com.blps.common.UserHistoryDto;
import com.blps.common.UserHistoryDto.UserAction;
import com.blps.lab2.model.beans.post.Post;
import com.blps.lab2.model.beans.post.User;
import com.blps.lab2.model.repository.post.PostRepository;
import com.blps.lab2.model.repository.post.UserRepository;
//import com.blps.lab2.model.services.KafkaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    // @Qualifier("postTxManager")
    // private final PlatformTransactionManager postTxManager;

    private final KafkaService kafkaService;

    public class PayResult {
        private double balance;
        private double price;

        public PayResult(double balance, double price) {
            this.balance = balance;
            this.price = price;
        }

        public double getBalance() {
            return balance;
        }

        public double getPrice() {
            return price;
        }

    }

    final double PRICE_PER_DAY = 100;

    public PayResult pay(Date date, Long postId, String userPhone) throws AccessDeniedException, PaymentException {
        

        final Date currentDateTime = Date.from(java.time.Instant.now());

        final long diff = date.getTime() - currentDateTime.getTime();
        if (diff <= 0) {
            throw new PaymentException("Invalid date");
        }
        final long days = diff / (24 * 60 * 60 * 1000) + 1;
        final double price = days * PRICE_PER_DAY;

        // DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        // def.setName("Payment transaction");
        // TransactionStatus status = postTxManager.getTransaction(def);
        
        User user;
        try {
            final User me = userRepository.findByPhoneNumber(userPhone);
            Post post = postRepository.findById(postId).orElse(null);
            user = post.getUser();

            if (me.getId() != user.getId()) {
                throw new AccessDeniedException("Access denied. Not your post");
            }

            if (post.getPaidUntil() != null && !post.getPaidUntil().before(date)) {
                throw new PaymentException("Post is already paid");
            }

            if (user.getBalance() < price) {
                throw new PaymentException("Not enough money");
            }

            post.setPaidUntil(date);
            user.setBalance(user.getBalance() - price);
            userRepository.save(user);
            postRepository.save(post);

            UserHistoryDto userHistory = new UserHistoryDto(
                null, me.getId(), UserAction.PAY, post.getId(),
                null, currentDateTime
            );
            kafkaService.send("user_audit", me.getId().toString(), userHistory);

        } catch (Exception ex) {
           
//            postTxManager.rollback(status);
            throw ex;
        }
//        postTxManager.commit(status);

        return new PayResult(user.getBalance(), price);
    }

}
