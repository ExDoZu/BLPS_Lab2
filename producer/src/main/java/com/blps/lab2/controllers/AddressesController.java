package com.blps.lab2.controllers;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blps.common.UserHistoryDto;
import com.blps.common.UserHistoryDto.UserAction;
import com.blps.lab2.model.beans.post.Address;
import com.blps.lab2.model.beans.post.Metro;
import com.blps.lab2.model.beans.post.User;
//import com.blps.lab2.model.repository.logstats.UserHistoryRepository;
import com.blps.lab2.model.repository.post.AddressRepository;
import com.blps.lab2.model.repository.post.MetroRepository;
import com.blps.lab2.model.repository.post.UserRepository;
import com.blps.lab2.model.services.AddressValidationService;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class AddressesController {

    private final AddressRepository addressRepository;
    private final MetroRepository metroRepository;
    private final UserRepository userRepository;
//    private final UserHistoryRepository userHistoryRepository;

    private final AddressValidationService addressValidationService;

    @GetMapping("/addresses")
    public ResponseEntity<?> getAddresses(
            @NotBlank String city,
            String street,
            Integer houseNumber,
            Character houseLetter,
            Authentication auth) {

        if (!addressValidationService.checkAddressParams(city, street, houseNumber, houseLetter)) {
            return ResponseEntity.badRequest().build();
        }

        List<Address> addresses = addressRepository.findByMany(city, street, houseNumber, houseLetter);
        if (addresses.size() == 0) {
            return ResponseEntity.notFound().build();
        }
        List<Metro> metros = metroRepository.findByAddressСity(addresses.get(0).getCity());

        if (auth != null) {
            User user = userRepository.findByPhoneNumber(auth.getName());
//            userHistoryRepository.save(new UserHistoryDto(null, user.getId(), UserAction.GET_ADDRESSES, null,
//                    (city + " " +
//                            (street == null ? "" : street) + " " +
//                            (houseNumber == null ? "" : houseNumber.toString()) + " " +
//                            (houseLetter == null ? "" : houseLetter)),
//                    Date.from(java.time.Instant.now())));
        }

        var response = new HashMap<String, Object>();
        response.put("addresses", addresses);
        response.put("metros", metros);

        return ResponseEntity.ok().body(response);
    }

}
