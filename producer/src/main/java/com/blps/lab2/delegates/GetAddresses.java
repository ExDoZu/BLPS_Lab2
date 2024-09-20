package com.blps.lab2.delegates;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetAddresses implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        System.out.println(this.getClass().getSimpleName());

        // Создаем список адресов
        List<Map<String, Object>> addresses = new ArrayList<>();
        addresses.add(createEntry("Some address 1", 1));
        addresses.add(createEntry("Some address 2", 2));
        addresses.add(createEntry("Some address 3", 3));

        // Создаем список метро
        List<Map<String, Object>> metros = new ArrayList<>();
        metros.add(createEntry("Some metro 1", 1));
        metros.add(createEntry("Some metro 2", 2));
        metros.add(createEntry("Some metro 3", 3));

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
