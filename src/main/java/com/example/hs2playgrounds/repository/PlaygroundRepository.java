package com.example.hs2playgrounds.repository;


import com.example.hs2playgrounds.model.entity.Playground;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaygroundRepository extends JpaRepository<Playground, Long> {
}
