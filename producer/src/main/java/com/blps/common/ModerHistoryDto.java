package com.blps.common;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ModerHistoryDto {
    public enum ModerAction {
        APPROVE,
        REJECT,
        LOGIN,
        REGISTER,
        GET_POSTS,
    }

    private Long id;

    private String moderID;

    private ModerAction action;

    private Long interacted_post;

    private String note;

    private Date datetime;
}
