package com.blps.lab2.model.repository.post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.blps.lab2.model.beans.post.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
    User findByPhoneNumber(String phoneNumber);
}
