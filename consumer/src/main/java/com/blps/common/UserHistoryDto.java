package com.blps.common;

import lombok.*;

import java.util.Date;

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

    private Long id;

    private Long userID;

    private UserAction action;

    private Long interacted_post;

    private String note;

    private Date datetime;
}
