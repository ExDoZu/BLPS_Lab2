package com.blps.lab2.controllers.dto;

import com.blps.lab2.model.beans.post.Post;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@RequiredArgsConstructor
@Getter
@Setter
public class ReceivePost {

    private String[] pathsToPhotos;

    // Flat fields
    @NotBlank
    private String title;
    @NotBlank
    private String description;

    private double price;

    private int roomNumber;

    private double area;

    private int floor;

    private long addressId;

    private long metroId;

    @Override
    public String toString() {

        return "Title: " + title + "\n" +
                "Description: " + description + "\n" +
                "Price: " + price + "\n" +
                "Room number: " + roomNumber + "\n" +
                "Area: " + area + "\n" +
                "Floor: " + floor + "\n" +
                "Address id: " + addressId + "\n" +
                "Metro id: " + metroId + "\n" +
                "Paths to photos: " + pathsToPhotos + "\n";
    }

    // No Foreign Key - No Metro and no Address fields
    public Post toPostNoFK() {
        Post post = new Post();
        post.setPathsToPhotos(pathsToPhotos);
        post.setTitle(title);
        post.setDescription(description);
        post.setPrice(price);
        post.setRoomNumber(roomNumber);
        post.setArea(area);
        post.setFloor(floor);
        return post;
    }

}
