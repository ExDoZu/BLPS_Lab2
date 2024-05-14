package com.blps.lab2.controllers.dto;

import com.blps.lab2.model.beans.post.User;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseUser {

    private String firstName;

    private String lastName;

    private String email;

    private String phoneNumber;

    public ResponseUser(User user) {
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.phoneNumber = user.getPhoneNumber();
    }
}
