package com.blps.common;

import lombok.*;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
public class ModerHistoryDto {
    public enum ModerAction {
        APPROVE,
        REJECT,
        LOGIN,
        REGISTER,
        GET_POSTS,
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "mh_seq")
    @SequenceGenerator(name = "mh_seq", sequenceName = "mh_id_sequence")
    private Long id;

    @Column(nullable = false)
    private Long moderID;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ModerAction action;

    private Long interacted_post;

    private String note;
    @Column(nullable = false)
    private Date datetime;

}
