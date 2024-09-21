package com.blps.lab2.delegates;

import com.blps.common.ModerHistoryDto;
import com.blps.lab2.model.beans.post.Post;
import com.blps.lab2.model.repository.post.PostRepository;
import com.blps.lab2.model.services.KafkaService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class ApprovePost implements JavaDelegate {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private KafkaService kafkaService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        System.out.println(this.getClass().getSimpleName());
        String userId = execution.getProcessEngineServices().getIdentityService().getCurrentAuthentication().getUserId();
        List<String> groups = execution.getProcessEngineServices().getIdentityService().getCurrentAuthentication().getGroupIds();
        System.out.println(userId);
        System.out.println(groups);

        if (!groups.contains("moderator")) {
            throw new Exception("You are not allowed to approve post");
        }

        Long postId = (Long) execution.getVariable("saved_post_id");
        String moderatorPhone = userId;
        Boolean approved = true;

        approve(postId, moderatorPhone, approved);
    }

    public void approve(long postId, String moderatorPhone, boolean approved) throws Exception {
        try {


            Post post = postRepository.findById(postId).orElseThrow(() -> new Exception("Post not found"));

            post.setApproved(approved);
            ModerHistoryDto.ModerAction action = approved ? ModerHistoryDto.ModerAction.APPROVE : ModerHistoryDto.ModerAction.REJECT;

            ModerHistoryDto moderHistory = new ModerHistoryDto(
                    null, moderatorPhone,
                    action,
                    post.getId(),
                    null,
                    Date.from(java.time.Instant.now())
            );
            kafkaService.send("moder_audit", moderatorPhone, moderHistory);

            if (approved) {
                post.setArchived(false);
            }

            postRepository.save(post);

        } catch (Exception ex) {
            throw ex;
        }
    }
}
