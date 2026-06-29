// package com.littlefish.app.model;

// import com.littlefish.app.model.enums.Role;
// import com.littlefish.app.model.enums.Theme;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Nested;
// import org.junit.jupiter.api.Test;

// import java.time.LocalDateTime;
// import java.util.ArrayList;

// import static org.assertj.core.api.Assertions.assertThat;

// @DisplayName("User")
// class UserTest {

//     private User user;

//     @BeforeEach
//     void setUp() {
//         user = new User();
//         user.setId(1L);
//         user.setPseudo("alice");
//         user.setEmail("alice@test.com");
//         user.setPassword("hashed_password");
//         user.setAvatar("avatar.png");
//         user.setCoins(200);
//         user.setRole(Role.USER);
//         user.setTheme(Theme.LIGHT);
//         user.setCreatedAt(LocalDateTime.of(2026, 1, 15, 10, 0));
//         user.setFriendships(new ArrayList<>());
//         user.setTrades(new ArrayList<>());
//         user.setDailyChallengeEntries(new ArrayList<>());
//     }

//     // ──────────────────────────────────────────────────────────────
//     @Nested @DisplayName("getters / setters")
//     class GettersSetters {

//         @Test @DisplayName("getId returns the set id")
//         void getId() {
//             assertThat(user.getId()).isEqualTo(1L);
//         }

//         @Test @DisplayName("getPseudo returns the set pseudo")
//         void getPseudo() {
//             assertThat(user.getPseudo()).isEqualTo("alice");
//         }

//         @Test @DisplayName("getEmail returns the set email")
//         void getEmail() {
//             assertThat(user.getEmail()).isEqualTo("alice@test.com");
//         }

//         @Test @DisplayName("getPassword returns the set password")
//         void getPassword() {
//             assertThat(user.getPassword()).isEqualTo("hashed_password");
//         }

//         @Test @DisplayName("getAvatar returns the set avatar")
//         void getAvatar() {
//             assertThat(user.getAvatar()).isEqualTo("avatar.png");
//         }

//         @Test @DisplayName("getCoins returns the set coins")
//         void getCoins() {
//             assertThat(user.getCoins()).isEqualTo(200);
//         }

//         @Test @DisplayName("getRole returns USER by default in setUp")
//         void getRole() {
//             assertThat(user.getRole()).isEqualTo(Role.USER);
//         }

//         @Test @DisplayName("getTheme returns LIGHT")
//         void getTheme() {
//             assertThat(user.getTheme()).isEqualTo(Theme.LIGHT);
//         }

//         @Test @DisplayName("getCreatedAt returns the set timestamp")
//         void getCreatedAt() {
//             assertThat(user.getCreatedAt()).isEqualTo(LocalDateTime.of(2026, 1, 15, 10, 0));
//         }

//         @Test @DisplayName("setCoins mutates the coins field")
//         void setCoins() {
//             user.setCoins(500);
//             assertThat(user.getCoins()).isEqualTo(500);
//         }

//         @Test @DisplayName("setRole to ADMIN is reflected by getRole")
//         void setRoleAdmin() {
//             user.setRole(Role.ADMIN);
//             assertThat(user.getRole()).isEqualTo(Role.ADMIN);
//         }

//         @Test @DisplayName("setTheme to DARK is reflected by getTheme")
//         void setThemeDark() {
//             user.setTheme(Theme.DARK);
//             assertThat(user.getTheme()).isEqualTo(Theme.DARK);
//         }
//     }

//     // ──────────────────────────────────────────────────────────────
//     @Nested @DisplayName("associations")
//     class Associations {

//         @Test @DisplayName("aquarium association can be set and retrieved")
//         void aquarium() {
//             Aquarium aq = new Aquarium();
//             aq.setId(10L);
//             aq.setName("Alice's Tank");
//             user.setAquarium(aq);

//             assertThat(user.getAquarium()).isNotNull();
//             assertThat(user.getAquarium().getId()).isEqualTo(10L);
//         }

//         @Test @DisplayName("notificationPreference association can be set and retrieved")
//         void notificationPreference() {
//             NotificationPreference pref = new NotificationPreference();
//             pref.setNotifyOnDeath(true);
//             user.setNotificationPreference(pref);

//             assertThat(user.getNotificationPreference()).isNotNull();
//             assertThat(user.getNotificationPreference().isNotifyOnDeath()).isTrue();
//         }

//         @Test @DisplayName("friendships list is initially empty and can be populated")
//         void friendships() {
//             assertThat(user.getFriendships()).isEmpty();

//             Friendship f = new Friendship();
//             user.getFriendships().add(f);

//             assertThat(user.getFriendships()).hasSize(1);
//         }

//         @Test @DisplayName("trades list is initially empty and can be populated")
//         void trades() {
//             assertThat(user.getTrades()).isEmpty();

//             Trade t = new Trade();
//             user.getTrades().add(t);

//             assertThat(user.getTrades()).hasSize(1);
//         }

//         @Test @DisplayName("dailyChallengeEntries list is initially empty")
//         void dailyChallengeEntries() {
//             assertThat(user.getDailyChallengeEntries()).isEmpty();
//         }
//     }

//     // ──────────────────────────────────────────────────────────────
//     @Nested @DisplayName("equals / hashCode  (@EqualsAndHashCode excludes aquarium, dailyChallengeEntries, avatar, friendships and trades)")
//     class EqualsHashCode {

//         @Test @DisplayName("two users with the same id and pseudo are equal")
//         void equalWhenSameFields() {
//             User other = new User();
//             other.setId(1L);
//             other.setPseudo("alice");
//             other.setEmail("alice@test.com");
//             other.setPassword("hashed_password");
//             other.setCoins(200);
//             other.setRole(Role.USER);
//             other.setTheme(Theme.LIGHT);
//             other.setCreatedAt(LocalDateTime.of(2026, 1, 15, 10, 0));

//             assertThat(user).isEqualTo(other);
//         }

//         @Test @DisplayName("two users with different pseudo are not equal")
//         void notEqualWhenDifferentPseudo() {
//             User other = new User();
//             other.setId(2L);
//             other.setPseudo("bob");

//             assertThat(user).isNotEqualTo(other);
//         }

//         @Test @DisplayName("equal users have the same hashCode")
//         void hashCodeConsistency() {
//             User other = new User();
//             other.setId(1L);
//             other.setPseudo("alice");
//             other.setEmail("alice@test.com");
//             other.setPassword("hashed_password");
//             other.setCoins(200);
//             other.setRole(Role.USER);
//             other.setTheme(Theme.LIGHT);
//             other.setCreatedAt(LocalDateTime.of(2026, 1, 15, 10, 0));

//             assertThat(user.hashCode()).isEqualTo(other.hashCode());
//         }

//         @Test @DisplayName("aquarium difference does not affect equality (excluded field)")
//         void aquariumExcludedFromEquals() {
//             User other = new User();
//             other.setId(1L);
//             other.setPseudo("alice");
//             other.setEmail("alice@test.com");
//             other.setPassword("hashed_password");
//             other.setCoins(200);
//             other.setRole(Role.USER);
//             other.setTheme(Theme.LIGHT);
//             other.setCreatedAt(LocalDateTime.of(2026, 1, 15, 10, 0));

//             Aquarium aq = new Aquarium();
//             aq.setId(99L);
//             other.setAquarium(aq);

//             // aquarium is excluded — they should still be equal
//             assertThat(user).isEqualTo(other);
//         }
//     }

//     // ──────────────────────────────────────────────────────────────
//     @Nested @DisplayName("toString")
//     class ToString {

//         @Test @DisplayName("toString includes pseudo")
//         void includesPseudo() {
//             assertThat(user.toString()).contains("alice");
//         }

//         @Test @DisplayName("toString includes email")
//         void includesEmail() {
//             assertThat(user.toString()).contains("alice@test.com");
//         }
//     }

//     // ──────────────────────────────────────────────────────────────
//     @Nested @DisplayName("noArgsConstructor")
//     class NoArgsConstructor {

//         @Test @DisplayName("default constructor produces a non-null object with null fields")
//         void defaultConstructor() {
//             User u = new User();
//             assertThat(u).isNotNull();
//             assertThat(u.getId()).isNull();
//             assertThat(u.getPseudo()).isNull();
//             assertThat(u.getCoins()).isZero();
//         }
//     }
// }
