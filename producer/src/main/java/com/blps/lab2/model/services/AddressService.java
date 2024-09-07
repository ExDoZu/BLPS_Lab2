package com.blps.lab2.model.services;

import com.blps.common.UserHistoryDto;
import com.blps.lab2.exceptions.InvalidDataException;
import com.blps.lab2.exceptions.NotFoundException;
import com.blps.lab2.model.beans.post.Address;
import com.blps.lab2.model.beans.post.Metro;
import com.blps.lab2.model.beans.post.User;
import com.blps.lab2.model.repository.post.AddressRepository;
import com.blps.lab2.model.repository.post.MetroRepository;
import com.blps.lab2.model.repository.post.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AddressService {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private MetroRepository metroRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private KafkaService kafkaService;

    @Autowired
    private AddressValidationService addressValidationService;

    public Map<String, Object> getAddresses(String city, String street, Integer houseNumber, Character houseLetter, Authentication auth) throws NotFoundException, InvalidDataException {

        if (!addressValidationService.checkAddressParams(city, street, houseNumber, houseLetter)) {
            throw new InvalidDataException("Invalid address parameters");
        }

        List<Address> addresses = addressRepository.findByMany(city, street, houseNumber, houseLetter);
        if (addresses.isEmpty()) {
            throw new NotFoundException("No addresses found");
        }

        List<Metro> metros = metroRepository.findByAddressCity(addresses.get(0).getCity());

        if (auth != null) {
            User user = userRepository.findByPhoneNumber(auth.getName());
            String searchDetails = city + " " +
                    (street == null ? "" : street) + " " +
                    (houseNumber == null ? "" : houseNumber.toString()) + " " +
                    (houseLetter == null ? "" : houseLetter);

            UserHistoryDto userHistory = new UserHistoryDto(
                    null,
                    user.getId(),
                    UserHistoryDto.UserAction.GET_ADDRESSES,
                    null,
                    searchDetails,
                    Date.from(java.time.Instant.now())
            );
            kafkaService.send("user_audit", user.getId().toString(), userHistory);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("addresses", addresses);
        response.put("metros", metros);

        return response;
    }
}
