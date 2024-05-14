package com.blps.lab2.model.beans.logstats;

import lombok.*;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserHistory {
    public enum UserAction {
        CREATE,
        ADD_PHOTO,
        PAY,
        ARCHIVE,
        GET_POSTS,
        GET_ONE_POST,
        GET_ADDRESSES,
        EDIT,
        LOGIN,
        REGISTER,
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "uh_seq")
    @SequenceGenerator(name = "uh_seq", sequenceName = "uh_id_sequence")
    private Long id;

    @Column(nullable = false)
    private Long userID;

    @Column(nullable = false)
    private UserAction action;

    private Long interacted_post;

    private String note;
    @Column(nullable = false)
    private Date datetime;

}
