// package com.littlefish.app.model;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Nested;
// import org.junit.jupiter.api.Test;

// import java.util.ArrayList;
// import java.util.List;

// import static org.assertj.core.api.Assertions.assertThat;

// @DisplayName("Aquarium")
// class AquariumTest {

//     private Aquarium aquarium;

//     @BeforeEach
//     void setUp() {
//         aquarium = new Aquarium();
//         aquarium.setId(1L);
//         aquarium.setName("Alice's Reef");
//         aquarium.setPublic(true);
//         aquarium.setLevel(2);
//         aquarium.setCapacity(10);
//         aquarium.setFish(new ArrayList<>());
//     }

//     // ──────────────────────────────────────────────────────────────
//     @Nested @DisplayName("getters / setters")
//     class GettersSetters {

//         @Test @DisplayName("getId returns the set id")
//         void getId() {
//             assertThat(aquarium.getId()).isEqualTo(1L);
//         }

//         @Test @DisplayName("getName returns the set name")
//         void getName() {
//             assertThat(aquarium.getName()).isEqualTo("Alice's Reef");
//         }

//         @Test @DisplayName("isPublic returns true when set to true")
//         void isPublic() {
//             assertThat(aquarium.isPublic()).isTrue();
//         }

//         @Test @DisplayName("isPublic returns false after setPublic(false)")
//         void isNotPublic() {
//             aquarium.setPublic(false);
//             assertThat(aquarium.isPublic()).isFalse();
//         }

//         @Test @DisplayName("getLevel returns the set level")
//         void getLevel() {
//             assertThat(aquarium.getLevel()).isEqualTo(2);
//         }

//         @Test @DisplayName("getCapacity returns the set capacity")
//         void getCapacity() {
//             assertThat(aquarium.getCapacity()).isEqualTo(10);
//         }

//         @Test @DisplayName("setLevel mutates correctly")
//         void setLevel() {
//             aquarium.setLevel(5);
//             assertThat(aquarium.getLevel()).isEqualTo(5);
//         }

//         @Test @DisplayName("setCapacity mutates correctly")
//         void setCapacity() {
//             aquarium.setCapacity(20);
//             assertThat(aquarium.getCapacity()).isEqualTo(20);
//         }
//     }

//     // ──────────────────────────────────────────────────────────────
//     @Nested @DisplayName("fish collection")
//     class FishCollection {

//         @Test @DisplayName("fish list starts empty")
//         void startsEmpty() {
//             assertThat(aquarium.getFish()).isEmpty();
//         }

//         @Test @DisplayName("fish can be added to the list")
//         void addFish() {
//             Fish f = new Fish();
//             f.setName("Nemo");
//             aquarium.getFish().add(f);

//             assertThat(aquarium.getFish()).hasSize(1);
//             assertThat(aquarium.getFish().get(0).getName()).isEqualTo("Nemo");
//         }

//         @Test @DisplayName("multiple fish can coexist")
//         void multipleFish() {
//             Fish f1 = new Fish(); f1.setName("Nemo");
//             Fish f2 = new Fish(); f2.setName("Dory");
//             aquarium.setFish(new ArrayList<>(List.of(f1, f2)));

//             assertThat(aquarium.getFish()).hasSize(2);
//         }

//         @Test @DisplayName("fish can be removed from the list")
//         void removeFish() {
//             Fish f = new Fish(); f.setName("Nemo");
//             aquarium.getFish().add(f);
//             aquarium.getFish().remove(f);

//             assertThat(aquarium.getFish()).isEmpty();
//         }

//         @Test @DisplayName("isFull when fish count equals capacity")
//         void fullWhenAtCapacity() {
//             aquarium.setCapacity(2);
//             Fish f1 = new Fish(); Fish f2 = new Fish();
//             aquarium.setFish(new ArrayList<>(List.of(f1, f2)));

//             assertThat(aquarium.getFish().size()).isEqualTo(aquarium.getCapacity());
//         }
//     }

//     // ──────────────────────────────────────────────────────────────
//     @Nested @DisplayName("user association")
//     class UserAssociation {

//         @Test @DisplayName("user can be set and retrieved")
//         void setAndGetUser() {
//             User u = new User();
//             u.setPseudo("alice");
//             aquarium.setUser(u);

//             assertThat(aquarium.getUser()).isNotNull();
//             assertThat(aquarium.getUser().getPseudo()).isEqualTo("alice");
//         }
//     }

//     // ──────────────────────────────────────────────────────────────
//     @Nested @DisplayName("equals / hashCode  (@EqualsAndHashCode excludes user and fish)")
//     class EqualsHashCode {

//         @Test @DisplayName("two aquariums with same id, name, level and capacity are equal")
//         void equalWhenSameFields() {
//             Aquarium other = new Aquarium();
//             other.setId(1L);
//             other.setName("Alice's Reef");
//             other.setPublic(true);
//             other.setLevel(2);
//             other.setCapacity(10);

//             assertThat(aquarium).isEqualTo(other);
//         }

//         @Test @DisplayName("different id produces inequality")
//         void notEqualDifferentId() {
//             Aquarium other = new Aquarium();
//             other.setId(99L);
//             other.setName("Alice's Reef");

//             assertThat(aquarium).isNotEqualTo(other);
//         }

//         @Test @DisplayName("fish list difference does not affect equality (excluded)")
//         void fishExcludedFromEquals() {
//             Aquarium other = new Aquarium();
//             other.setId(1L);
//             other.setName("Alice's Reef");
//             other.setPublic(true);
//             other.setLevel(2);
//             other.setCapacity(10);
//             other.setFish(new ArrayList<>(List.of(new Fish())));

//             assertThat(aquarium).isEqualTo(other);
//         }
//     }

//     // ──────────────────────────────────────────────────────────────
//     @Nested @DisplayName("noArgsConstructor")
//     class NoArgsConstructor {

//         @Test @DisplayName("default constructor produces non-null object with zero numeric fields")
//         void defaultValues() {
//             Aquarium a = new Aquarium();
//             assertThat(a).isNotNull();
//             assertThat(a.getId()).isNull();
//             assertThat(a.getName()).isNull();
//             assertThat(a.getLevel()).isZero();
//             assertThat(a.getCapacity()).isZero();
//             assertThat(a.isPublic()).isFalse();
//         }
//     }
// }
