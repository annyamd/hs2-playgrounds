package com.example.hs2playgrounds.repository;


import com.example.hs2playgrounds.model.entity.Sport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SportRepository extends JpaRepository<Sport, Long> {
}
