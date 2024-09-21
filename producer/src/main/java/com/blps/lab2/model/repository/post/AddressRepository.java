package com.blps.lab2.model.repository.post;

import com.blps.lab2.model.beans.post.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

}
