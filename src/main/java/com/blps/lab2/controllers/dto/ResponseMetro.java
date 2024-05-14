package com.blps.lab2.controllers.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseMetro {
    private String name;
    private Integer branchNumber;
    private ResponseAddress address;

    public ResponseMetro(com.blps.lab2.model.beans.post.Metro metro) {
        this.name = metro.getName();
        this.branchNumber = metro.getBranchNumber();
        this.address = new ResponseAddress(metro.getAddress());
    }

}
