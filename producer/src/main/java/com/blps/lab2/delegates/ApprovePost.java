package com.blps.lab2.delegates;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import java.util.List;


public class ApprovePost implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        System.out.println(this.getClass().getSimpleName());
        String userId = execution.getProcessEngineServices().getIdentityService().getCurrentAuthentication().getUserId();
        List<String> groups = execution.getProcessEngineServices().getIdentityService().getCurrentAuthentication().getGroupIds();
        System.out.println(userId);
        System.out.println(groups);

        // if not in group moderator, throw exception
        if (!groups.contains("moderator")) {
            throw new Exception("You are not allowed to approve post");
        }

        
    }
}
