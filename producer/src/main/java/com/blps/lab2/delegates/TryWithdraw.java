package com.blps.lab2.delegates;

import com.blps.common.UserHistoryDto;
import com.blps.lab2.model.beans.post.Post;
import com.blps.lab2.model.repository.post.PostRepository;
import com.blps.lab2.model.services.KafkaService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
public class TryWithdraw implements JavaDelegate {


    @Autowired
    private PostRepository postRepository;

    @Autowired
    private KafkaService kafkaService;

    private static final double PRICE_PER_DAY = 100;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        System.out.println(this.getClass().getSimpleName());
        String userId = execution.getProcessEngineServices()
            .getIdentityService().getCurrentAuthentication().getUserId();
        List<String> groups = execution.getProcessEngineServices()
            .getIdentityService().getCurrentAuthentication().getGroupIds();
        System.out.println(userId);
        System.out.println(groups);

        if (!groups.contains("user")) {
            throw new Exception("You are not allowed to withdraw payment");
        }

        Integer postDayPay = (Integer) execution.getVariable("post_day_pay");
        Integer postMonthPay = (Integer) execution.getVariable("post_month_pay");
        Integer postYearPay = (Integer) execution.getVariable("post_year_pay");
        Long postId = (Long) execution.getVariable("saved_post_id");

        Calendar calendar = Calendar.getInstance();
        calendar.set(postYearPay, postMonthPay - 1, postDayPay);
        Date paymentDate = calendar.getTime();

        Date currentDate = new Date();
        long diffInMillis = paymentDate.getTime() - currentDate.getTime();
        if (diffInMillis <= 0) {
            throw new Exception("Invalid date: Payment date must be in the future");
        }
        long days = diffInMillis / (24 * 60 * 60 * 1000) + 1;
        double price = days * PRICE_PER_DAY;


        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new Exception("Post not found"));


        if (post.getPaidUntil() != null && !post.getPaidUntil().before(paymentDate)) {
            throw new Exception("Payment Error: Post is already paid until the specified date");
        }

        post.setPaidUntil(paymentDate);
        postRepository.save(post);

        UserHistoryDto userHistory = new UserHistoryDto(
            null, userId, UserHistoryDto.UserAction.PAY, post.getId(),
            null, currentDate
        );
        kafkaService.send("user_audit", userId, userHistory);

        execution.setVariable("payment_successful", true);
    }
}
