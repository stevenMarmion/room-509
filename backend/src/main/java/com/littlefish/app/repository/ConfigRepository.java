package com.littlefish.app.repository;

import com.littlefish.app.model.Config;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ConfigRepository extends JpaRepository<Config, Long> {
    public Optional<Config> findByKey(String key);
}
