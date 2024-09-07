package com.blps.lab2.controllers;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.blps.lab2.exceptions.InvalidDataException;
import com.blps.lab2.exceptions.NotFoundException;
import com.blps.lab2.model.services.AddressService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blps.common.UserHistoryDto;
import com.blps.common.UserHistoryDto.UserAction;
import com.blps.lab2.model.beans.post.Address;
import com.blps.lab2.model.beans.post.Metro;
import com.blps.lab2.model.beans.post.User;
import com.blps.lab2.model.repository.post.AddressRepository;
import com.blps.lab2.model.repository.post.MetroRepository;
import com.blps.lab2.model.repository.post.UserRepository;
import com.blps.lab2.model.services.AddressValidationService;
import com.blps.lab2.model.services.KafkaService;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class AddressesController {

    private final AddressService addressService;
    @GetMapping("/addresses")
    public ResponseEntity<?> getAddresses(
            @NotBlank String city,
            String street,
            Integer houseNumber,
            Character houseLetter,
            Authentication auth) {

        try {
            Map<String, Object> response = addressService.getAddresses(city, street, houseNumber, houseLetter, auth);
            return ResponseEntity.ok().body(response);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (InvalidDataException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
