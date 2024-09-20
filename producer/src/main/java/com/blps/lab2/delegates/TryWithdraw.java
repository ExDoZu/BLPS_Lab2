package com.blps.lab2.delegates;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class TryWithdraw implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        System.out.println(this.getClass().getSimpleName());

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
