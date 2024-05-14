package com.blps.lab2.model.services;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import com.blps.lab2.model.beans.post.Address;
import com.blps.lab2.model.beans.post.Metro;

@Service
@RequiredArgsConstructor
public class MetroValidationService {

    public boolean checkMetroAddress(Metro metro, Address postAddress) {
        if (metro.getAddress().getCity().equals(postAddress.getCity())) {
            return true;
        }
        return false;
    }
}
