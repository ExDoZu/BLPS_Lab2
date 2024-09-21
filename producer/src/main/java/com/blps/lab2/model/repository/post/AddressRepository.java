package com.blps.lab2.model.repository.post;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.blps.lab2.model.beans.post.Address;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
  
  List<Address> findAll();

}
