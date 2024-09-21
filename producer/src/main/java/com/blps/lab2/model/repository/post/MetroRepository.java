package com.blps.lab2.model.repository.post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.blps.lab2.model.beans.post.Metro;

import java.util.List;

@Repository
public interface MetroRepository extends JpaRepository<Metro, Long> {
      
  List<Metro> findAll();

}
