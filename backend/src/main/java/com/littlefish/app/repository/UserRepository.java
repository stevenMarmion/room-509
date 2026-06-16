package com.littlefish.app.repository;

import com.littlefish.app.model.User;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    public Optional<User> findByPseudo(String pseudo);
}
