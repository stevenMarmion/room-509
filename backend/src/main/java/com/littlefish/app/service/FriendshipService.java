package com.littlefish.app.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.littlefish.app.dto.FriendshipDTO;
import com.littlefish.app.model.Friendship;
import com.littlefish.app.model.User;
import com.littlefish.app.model.enums.FriendStatus;
import com.littlefish.app.repository.FriendshipRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FriendshipService {

    private final FriendshipRepository friendshipRepository;
    private final UserService userRepository;

    public List<FriendshipDTO> findAcceptedFriends(String pseudo) {
        return userRepository.findByPseudo(pseudo)
            .map(user -> user.getFriendships().stream()
                    .filter(f -> f.getStatus() == FriendStatus.ACCEPTED)
                    .map(f -> toDTO(f, pseudo))
                    .toList())
                .orElse(List.of());
    }

    public List<FriendshipDTO> findPendingRequestsReceivedBy(String pseudo) {
        return friendshipRepository.findByAddressee_PseudoAndStatus(pseudo, FriendStatus.PENDING)
            .stream()
            .map(f -> toDTO(f, pseudo))
            .toList();
    }

    public Optional<User> addFriend(String pseudo, String friendPseudo) {
        User user = userRepository.findByPseudo(pseudo).orElse(null);
        User friend = userRepository.findByPseudo(friendPseudo).orElse(null);

        if (user == null || friend == null) {
            return Optional.empty();
        }

        Friendship friendship = new Friendship();
        friendship.setRequester(user);
        friendship.setAddressee(friend);
        friendship.setStatus(FriendStatus.PENDING);
        friendship.setSince(LocalDateTime.now());

        user.getFriendships().add(friendship);
        friendshipRepository.save(friendship);

        return Optional.of(user);
    }

    public Optional<User> acceptFriend(String pseudo, String friendPseudo) {
        User user = userRepository.findByPseudo(pseudo).orElse(null);
        User friend = userRepository.findByPseudo(friendPseudo).orElse(null);

        if (user == null || friend == null) {
            return Optional.empty();
        }

        Friendship friendship = friendshipRepository
            .findByRequesterAndAddressee(friend, user)
            .orElse(null);

        if (friendship == null) {
            return Optional.empty();
        }

        // Mark the original request as accepted
        friendship.setStatus(FriendStatus.ACCEPTED);
        friendshipRepository.save(friendship);

        // Create the symmetric friendship so it appears on both sides
        Friendship reverse = friendshipRepository
            .findByRequesterAndAddressee(user, friend)
            .orElseGet(Friendship::new);

        reverse.setRequester(user);
        reverse.setAddressee(friend);
        reverse.setStatus(FriendStatus.ACCEPTED);
        reverse.setSince(friendship.getSince());
        friendshipRepository.save(reverse);

        return Optional.of(user);
    }

    public Optional<User> rejectFriend(String pseudo, String friendPseudo) {
        User user = userRepository.findByPseudo(pseudo).orElse(null);
        User friend = userRepository.findByPseudo(friendPseudo).orElse(null);

        if (user == null || friend == null) {
            return Optional.empty();
        }

        Friendship friendship = friendshipRepository
            .findByRequesterAndAddressee(friend, user)
            .orElse(null);

        if (friendship == null) {
            return Optional.empty();
        }

        friendship.setStatus(FriendStatus.BLOCKED);
        friendshipRepository.save(friendship);

        // Reflect the same status on the reverse relationship too
        Friendship reverse = friendshipRepository
            .findByRequesterAndAddressee(user, friend)
            .orElseGet(Friendship::new);

        reverse.setRequester(user);
        reverse.setAddressee(friend);
        reverse.setStatus(FriendStatus.BLOCKED);
        reverse.setSince(friendship.getSince());
        friendshipRepository.save(reverse);

        return Optional.of(user);
    }

    public List<Friendship> findAll() {
        return friendshipRepository.findAll();
    }

    public void deleteById(Long id) {
        friendshipRepository.deleteById(id);
    }

    @Transactional
    public void deleteFriendship(Long id) {
        Optional<Friendship> friendshipOpt = friendshipRepository.findById(id);
        if (friendshipOpt.isEmpty()) return;

        Friendship friendship = friendshipOpt.get();
        User sender   = friendship.getRequester();
        User receiver = friendship.getAddressee();

        friendshipRepository.deleteByRequesterAndAddressee(sender, receiver);
        friendshipRepository.deleteByRequesterAndAddressee(receiver, sender);
    }

    public FriendshipDTO toDTO(Friendship f, String requesterPseudo) {
        User friend = f.getRequester().getPseudo().equals(requesterPseudo) ? f.getAddressee(): f.getRequester();

        return new FriendshipDTO(
            f.getStatus(),
            f.getSince(),
            friend.getPseudo(),
            f.getSince(),
            friend.getRole().name()
        );
    }
    
}