package com.blps.lab2.delegates;

import com.blps.lab2.model.repository.post.AddressRepository;
import com.blps.lab2.model.repository.post.MetroRepository;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class GetAddresses implements JavaDelegate {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private MetroRepository metroRepository;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        System.out.println(this.getClass().getSimpleName());
        String userId = execution.getProcessEngineServices().getIdentityService().getCurrentAuthentication().getUserId();
        List<String> groups = execution.getProcessEngineServices().getIdentityService().getCurrentAuthentication().getGroupIds();
        System.out.println(userId);
        System.out.println(groups);

        // if not in group user, throw exception
        if (!groups.contains("user")) {
            throw new Exception("You are not allowed to get addresses");
        }

        

        List<Map<String, Object>> addresses = new ArrayList<>();
        List<Map<String, Object>> metros = new ArrayList<>();

        addressRepository.findAll().forEach(address -> {
            Map<String, Object> entry = new HashMap<>();
            entry.put("label", address.getAddress());
            entry.put("value", address.getId());
            addresses.add(entry);
        });

        metroRepository.findAll().forEach(metro -> {
            Map<String, Object> entry = new HashMap<>();
            entry.put("label", metro.getName());
            entry.put("value", metro.getId());
            metros.add(entry);
        });

        

        // Устанавливаем переменные в контексте выполнения
        execution.setVariable("addresses", addresses);
        execution.setVariable("metros", metros);
    }

    // Вспомогательный метод для создания записи
    private Map<String, Object> createEntry(String label, int value) {
        Map<String, Object> entry = new HashMap<>();
        entry.put("label", label);
        entry.put("value", value);
        return entry;
    }
}
