package com.littlefish.app.repository;

import com.littlefish.app.model.Friendship;
import com.littlefish.app.model.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

    public List<Friendship> findByAddressee_PseudoAndStatus(String pseudo, com.littlefish.app.model.enums.FriendStatus status);
    public Optional<Friendship> findByRequesterAndAddressee(User requester, User addressee);
}
