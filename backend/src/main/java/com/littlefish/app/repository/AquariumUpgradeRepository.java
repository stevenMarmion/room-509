package com.littlefish.app.repository;

import com.littlefish.app.model.AquariumUpgrade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AquariumUpgradeRepository extends JpaRepository<AquariumUpgrade, Long> {
}
