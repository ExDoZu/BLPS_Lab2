package com.blps.lab2.delegates;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import java.util.List;

public class TryWithdraw implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        System.out.println(this.getClass().getSimpleName());
        String userId = execution.getProcessEngineServices().getIdentityService().getCurrentAuthentication().getUserId();
        List<String> groups = execution.getProcessEngineServices().getIdentityService().getCurrentAuthentication().getGroupIds();
        System.out.println(userId);
        System.out.println(groups);

        // if not in group user, throw exception
        if (!groups.contains("user")) {
            throw new Exception("You are not allowed to withdraw payment");
        }

        // Retrieve variables from execution context
        Integer postDayPay = (Integer) execution.getVariable("post_day_pay");
        Integer postMonthPay = (Integer) execution.getVariable("post_month_pay");
        Integer postYearPay = (Integer) execution.getVariable("post_year_pay");

        // TODO: Add logic for withdrawing payment if needed

        // Optionally, you can perform some calculation or validation here
        // Example: Calculate total payment if necessary

        // Set 'payment_successfull' to true
        execution.setVariable("payment_successfull", true);
    }
}
