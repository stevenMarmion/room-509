package com.littlefish.app.repository;

import com.littlefish.app.model.Aquarium;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AquariumRepository extends JpaRepository<Aquarium, Long> {
}
