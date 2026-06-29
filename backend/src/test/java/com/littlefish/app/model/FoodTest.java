package com.littlefish.app.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Food")
class FoodTest {

    private Food food;

    @BeforeEach
    void setUp() {
        food = new Food();
        food.setId(1L);
        food.setName("Premium Flakes");
        food.setPrice(25);
        food.setNutritionValue(30);
    }

    @Nested @DisplayName("getters / setters — ShopItem fields")
    class ShopItemFields {

        @Test @DisplayName("getId returns the set id (inherited from ShopItem)")
        void getId() {
            assertThat(food.getId()).isEqualTo(1L);
        }

        @Test @DisplayName("getName returns the set name (inherited from ShopItem)")
        void getName() {
            assertThat(food.getName()).isEqualTo("Premium Flakes");
        }

        @Test @DisplayName("getPrice returns the set price (inherited from ShopItem)")
        void getPrice() {
            assertThat(food.getPrice()).isEqualTo(25);
        }

        @Test @DisplayName("setName mutates correctly")
        void setName() {
            food.setName("Live Brine Shrimp");
            assertThat(food.getName()).isEqualTo("Live Brine Shrimp");
        }

        @Test @DisplayName("setPrice mutates correctly")
        void setPrice() {
            food.setPrice(50);
            assertThat(food.getPrice()).isEqualTo(50);
        }
    }

    @Nested @DisplayName("getters / setters — Food-specific fields")
    class FoodSpecificFields {

        @Test @DisplayName("getNutritionValue returns the set value")
        void getNutritionValue() {
            assertThat(food.getNutritionValue()).isEqualTo(30);
        }

        @Test @DisplayName("setNutritionValue mutates correctly")
        void setNutritionValue() {
            food.setNutritionValue(55);
            assertThat(food.getNutritionValue()).isEqualTo(55);
        }

        @Test @DisplayName("nutritionValue of 0 is valid (worthless food)")
        void zeroNutritionValue() {
            food.setNutritionValue(0);
            assertThat(food.getNutritionValue()).isZero();
        }
    }

    @Nested @DisplayName("equals / hashCode  (@EqualsAndHashCode(callSuper = false))")
    class EqualsHashCode {

        @Test @DisplayName("two Food items with same fields are equal")
        void equalWhenSameFields() {
            Food other = new Food();
            other.setId(1L);
            other.setName("Premium Flakes");
            other.setPrice(25);
            other.setNutritionValue(30);

            assertThat(food).isEqualTo(other);
            assertThat(food.hashCode()).isEqualTo(other.hashCode());
        }

        @Test @DisplayName("different nutritionValue produces inequality")
        void notEqualDifferentNutrition() {
            Food other = new Food();
            other.setId(1L);
            other.setName("Premium Flakes");
            other.setPrice(25);
            other.setNutritionValue(99);

            assertThat(food).isNotEqualTo(other);
        }

        @Test @DisplayName("different name produces inequality")
        void notEqualDifferentName() {
            Food other = new Food();
            other.setId(1L);
            other.setName("Basic Pellets");
            other.setPrice(25);
            other.setNutritionValue(30);

            assertThat(food).isNotEqualTo(other);
        }
    }

    @Nested @DisplayName("noArgsConstructor")
    class NoArgsConstructor {

        @Test @DisplayName("default constructor produces non-null object with zero fields")
        void defaultValues() {
            Food f = new Food();
            assertThat(f).isNotNull();
            assertThat(f.getId()).isNull();
            assertThat(f.getName()).isNull();
            assertThat(f.getPrice()).isZero();
            assertThat(f.getNutritionValue()).isZero();
        }
    }
}
