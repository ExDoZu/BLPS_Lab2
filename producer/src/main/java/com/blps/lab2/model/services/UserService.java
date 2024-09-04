package com.blps.lab2.model.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import com.blps.lab2.model.beans.post.User;
import com.blps.lab2.model.repository.post.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Value("user")
    private String topicName;

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("User: " + username);

        User user = userRepository.findByPhoneNumber(username);
        if (user == null)
            throw new UsernameNotFoundException(username);

        return user;
    }
}