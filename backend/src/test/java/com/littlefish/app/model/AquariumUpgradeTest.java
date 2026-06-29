// package com.littlefish.app.model;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Nested;
// import org.junit.jupiter.api.Test;

// import static org.assertj.core.api.Assertions.assertThat;

// @DisplayName("AquariumUpgrade")
// class AquariumUpgradeTest {

//     private AquariumUpgrade upgrade;

//     @BeforeEach
//     void setUp() {
//         upgrade = new AquariumUpgrade();
//         upgrade.setId(1L);
//         upgrade.setName("Deluxe Bundle");
//         upgrade.setPrice(400);
//         upgrade.setCapacityBonus(5);
//         upgrade.setLevelBonus(1);
//     }

//     @Nested @DisplayName("getters / setters — ShopItem fields")
//     class ShopItemFields {

//         @Test @DisplayName("getId returns the set id")
//         void getId() {
//             assertThat(upgrade.getId()).isEqualTo(1L);
//         }

//         @Test @DisplayName("getName returns the set name")
//         void getName() {
//             assertThat(upgrade.getName()).isEqualTo("Deluxe Bundle");
//         }

//         @Test @DisplayName("getPrice returns the set price")
//         void getPrice() {
//             assertThat(upgrade.getPrice()).isEqualTo(400);
//         }
//     }

//     @Nested @DisplayName("getters / setters — AquariumUpgrade-specific fields")
//     class UpgradeSpecificFields {

//         @Test @DisplayName("getCapacityBonus returns the set bonus")
//         void getCapacityBonus() {
//             assertThat(upgrade.getCapacityBonus()).isEqualTo(5);
//         }

//         @Test @DisplayName("getLevelBonus returns the set bonus")
//         void getLevelBonus() {
//             assertThat(upgrade.getLevelBonus()).isEqualTo(1);
//         }

//         @Test @DisplayName("capacity-only upgrade has levelBonus of 0")
//         void capacityOnlyUpgrade() {
//             AquariumUpgrade capOnly = new AquariumUpgrade();
//             capOnly.setCapacityBonus(5);
//             capOnly.setLevelBonus(0);

//             assertThat(capOnly.getLevelBonus()).isZero();
//             assertThat(capOnly.getCapacityBonus()).isEqualTo(5);
//         }

//         @Test @DisplayName("level-only upgrade has capacityBonus of 0")
//         void levelOnlyUpgrade() {
//             AquariumUpgrade lvlOnly = new AquariumUpgrade();
//             lvlOnly.setCapacityBonus(0);
//             lvlOnly.setLevelBonus(1);

//             assertThat(lvlOnly.getCapacityBonus()).isZero();
//             assertThat(lvlOnly.getLevelBonus()).isEqualTo(1);
//         }

//         @Test @DisplayName("setCapacityBonus mutates correctly")
//         void setCapacityBonus() {
//             upgrade.setCapacityBonus(10);
//             assertThat(upgrade.getCapacityBonus()).isEqualTo(10);
//         }

//         @Test @DisplayName("setLevelBonus mutates correctly")
//         void setLevelBonus() {
//             upgrade.setLevelBonus(2);
//             assertThat(upgrade.getLevelBonus()).isEqualTo(2);
//         }
//     }

//     @Nested @DisplayName("equals / hashCode  (@EqualsAndHashCode(callSuper = false))")
//     class EqualsHashCode {

//         @Test @DisplayName("two upgrades with same fields are equal")
//         void equalWhenSameFields() {
//             AquariumUpgrade other = new AquariumUpgrade();
//             other.setId(1L);
//             other.setName("Deluxe Bundle");
//             other.setPrice(400);
//             other.setCapacityBonus(5);
//             other.setLevelBonus(1);

//             assertThat(upgrade).isEqualTo(other);
//             assertThat(upgrade.hashCode()).isEqualTo(other.hashCode());
//         }

//         @Test @DisplayName("different capacityBonus produces inequality")
//         void notEqualDifferentCapacityBonus() {
//             AquariumUpgrade other = new AquariumUpgrade();
//             other.setId(1L);
//             other.setName("Deluxe Bundle");
//             other.setPrice(400);
//             other.setCapacityBonus(99);
//             other.setLevelBonus(1);

//             assertThat(upgrade).isNotEqualTo(other);
//         }
//     }

//     @Nested @DisplayName("noArgsConstructor")
//     class NoArgsConstructor {

//         @Test @DisplayName("default constructor produces non-null object with zero bonuses")
//         void defaultValues() {
//             AquariumUpgrade u = new AquariumUpgrade();
//             assertThat(u).isNotNull();
//             assertThat(u.getId()).isNull();
//             assertThat(u.getCapacityBonus()).isZero();
//             assertThat(u.getLevelBonus()).isZero();
//         }
//     }
// }
