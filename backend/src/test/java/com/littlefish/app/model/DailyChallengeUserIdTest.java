package com.littlefish.app.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("DailyChallengeUserId")
class DailyChallengeUserIdTest {

    @Nested @DisplayName("getters / setters")
    class GettersSetters {

        @Test @DisplayName("dailyChallengeId can be set and retrieved")
        void dailyChallengeId() {
            DailyChallengeUserId id = new DailyChallengeUserId();
            id.setDailyChallengeId(5L);
            assertThat(id.getDailyChallengeId()).isEqualTo(5L);
        }

        @Test @DisplayName("userId can be set and retrieved")
        void userId() {
            DailyChallengeUserId id = new DailyChallengeUserId();
            id.setUserId(3L);
            assertThat(id.getUserId()).isEqualTo(3L);
        }
    }

    @Nested @DisplayName("equals / hashCode  (required for @EmbeddedId)")
    class EqualsHashCode {

        @Test @DisplayName("two ids with same challengeId and userId are equal")
        void equalWhenSameFields() {
            DailyChallengeUserId a = new DailyChallengeUserId();
            a.setDailyChallengeId(1L); a.setUserId(2L);

            DailyChallengeUserId b = new DailyChallengeUserId();
            b.setDailyChallengeId(1L); b.setUserId(2L);

            assertThat(a).isEqualTo(b);
            assertThat(a.hashCode()).isEqualTo(b.hashCode());
        }

        @Test @DisplayName("different userId produces inequality")
        void notEqualDifferentUserId() {
            DailyChallengeUserId a = new DailyChallengeUserId();
            a.setDailyChallengeId(1L); a.setUserId(2L);

            DailyChallengeUserId b = new DailyChallengeUserId();
            b.setDailyChallengeId(1L); b.setUserId(99L);

            assertThat(a).isNotEqualTo(b);
        }

        @Test @DisplayName("different challengeId produces inequality")
        void notEqualDifferentChallengeId() {
            DailyChallengeUserId a = new DailyChallengeUserId();
            a.setDailyChallengeId(1L); a.setUserId(2L);

            DailyChallengeUserId b = new DailyChallengeUserId();
            b.setDailyChallengeId(99L); b.setUserId(2L);

            assertThat(a).isNotEqualTo(b);
        }

        @Test @DisplayName("id is not equal to null")
        void notEqualToNull() {
            DailyChallengeUserId a = new DailyChallengeUserId();
            a.setDailyChallengeId(1L); a.setUserId(2L);

            assertThat(a).isNotEqualTo(null);
        }

        @Test @DisplayName("id is not equal to an object of a different type")
        void notEqualToDifferentType() {
            DailyChallengeUserId a = new DailyChallengeUserId();
            assertThat(a).isNotEqualTo("not-an-id");
        }
    }

    @Nested @DisplayName("Serializable contract")
    class SerializableContract {

        @Test @DisplayName("DailyChallengeUserId implements Serializable")
        void implementsSerializable() {
            DailyChallengeUserId id = new DailyChallengeUserId();
            assertThat(id).isInstanceOf(java.io.Serializable.class);
        }
    }

    @Nested @DisplayName("noArgsConstructor")
    class NoArgsConstructor {

        @Test @DisplayName("default constructor produces non-null object with null fields")
        void defaultValues() {
            DailyChallengeUserId id = new DailyChallengeUserId();
            assertThat(id).isNotNull();
            assertThat(id.getDailyChallengeId()).isNull();
            assertThat(id.getUserId()).isNull();
        }
    }
}
