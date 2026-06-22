package com.littlefish.app.repository;

import com.littlefish.app.model.User;

import jakarta.transaction.Transactional;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    public Optional<User> findByPseudo(String pseudo);
    public Optional<User> findByEmail(String email);
    public void deleteByPseudo(String pseudo);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.coins = :coins WHERE u.pseudo = :pseudo")
    void updateCoins(@Param("pseudo") String pseudo, @Param("coins") int coins);
}
