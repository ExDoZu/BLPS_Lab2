package com.blps.lab2.delegates;

import com.blps.lab2.model.services.KafkaService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SendClearMsg implements JavaDelegate {

    @Autowired
    private KafkaService kafkaService;
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        System.out.println(this.getClass().getSimpleName());
        kafkaService.send("ping", "ping", "ping");
    }
}
