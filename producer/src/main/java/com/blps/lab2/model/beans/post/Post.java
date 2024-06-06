package com.blps.lab2.model.beans.post;

import lombok.*;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "post_seq")
    @SequenceGenerator(name = "post_seq", sequenceName = "post_id_sequence")
    private Long id;

    @Column(nullable = false)
    private Date creationDate;

    private String[] pathsToPhotos;

    private Date paidUntil;

    @Column(nullable = false)
    private Boolean archived;

    private Boolean approved;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Flat fields

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private Double price;
    @Column(nullable = false)
    private Integer roomNumber;
    @Column(nullable = false)
    private Double area;
    @Column(nullable = false)
    private Integer floor;

    @ManyToOne
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;

    @ManyToOne
    @JoinColumn(name = "metro_id", nullable = false)
    private Metro metro;
}