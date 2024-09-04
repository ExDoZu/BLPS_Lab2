package com.blps.common;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserHistoryDto {
    public enum UserAction {
        CREATE,
        ADD_PHOTO,
        GET_PHOTO,
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
    @Enumerated(EnumType.STRING)
    private UserAction action;

    private Long interacted_post;

    private String note;
    @Column(nullable = false)
    private Date datetime;

}
