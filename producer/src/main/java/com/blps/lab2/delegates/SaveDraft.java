package com.blps.lab2.delegates;

import com.blps.common.UserHistoryDto;
import com.blps.lab2.model.beans.post.Address;
import com.blps.lab2.model.beans.post.Metro;
import com.blps.lab2.model.beans.post.Post;
import com.blps.lab2.model.repository.post.AddressRepository;
import com.blps.lab2.model.repository.post.MetroRepository;
import com.blps.lab2.model.repository.post.PostRepository;
import com.blps.lab2.model.services.KafkaService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
@Component
public class SaveDraft implements JavaDelegate {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private MetroRepository metroRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private KafkaService kafkaService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        System.out.println(this.getClass().getSimpleName());
    
        String userId = execution.getProcessEngineServices().getIdentityService().getCurrentAuthentication().getUserId();
        List<String> groups = execution.getProcessEngineServices().getIdentityService().getCurrentAuthentication().getGroupIds();
    
        if (!groups.contains("user")) {
            throw new Exception("You are not allowed to save draft");
        }
    
        String title = (String) execution.getVariable("post_title");
        String description = (String) execution.getVariable("post_description");
    
        Integer roomNumber = Integer.parseInt(execution.getVariable("post_room_number").toString());
        Double area = Double.parseDouble(execution.getVariable("post_area").toString());
        Integer floor = Integer.parseInt(execution.getVariable("post_floor").toString());
    
        Long addressId = Long.parseLong(execution.getVariable("post_address").toString());
        Long metroId = Long.parseLong(execution.getVariable("post_metro").toString());
    
        Double price = Double.parseDouble(execution.getVariable("post_price").toString());
    
        Post post = new Post();
        post.setTitle(title);
        post.setDescription(description);
        post.setRoomNumber(roomNumber);
        post.setArea(area);
        post.setFloor(floor);
        post.setPrice(price);
        post.setUserId(userId);
        post.setCreationDate(new Date());
        post.setArchived(false); 
        post.setApproved(false); 
    
        Address address = addressRepository.findById(addressId).orElseThrow();
        Metro metro = metroRepository.findById(metroId).orElseThrow();
        post.setAddress(address);
        post.setMetro(metro);
    
        Post savedPost = postRepository.save(post);
    
        execution.setVariable("saved_post_id", savedPost.getId());

        UserHistoryDto userHistory = new UserHistoryDto(
                null, userId,
                UserHistoryDto.UserAction.CREATE,
                savedPost.getId(),
                null,
                Date.from(java.time.Instant.now())
            );
            kafkaService.send("user_audit", userId, userHistory);
    }
}
