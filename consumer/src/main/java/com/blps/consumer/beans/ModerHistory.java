package com.blps.consumer.beans;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ModerHistory {
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
    private String moderID;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ModerAction action;

    private Long interacted_post;

    private String note;
    @Column(nullable = false)
    private Date datetime;

}
