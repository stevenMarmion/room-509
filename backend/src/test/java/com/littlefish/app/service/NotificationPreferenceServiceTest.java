package com.littlefish.app.service;

import com.littlefish.app.dto.NotificationPreferenceUpdateDTO;
import com.littlefish.app.model.NotificationPreference;
import com.littlefish.app.model.User;
import com.littlefish.app.repository.NotificationPreferenceRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationPreferenceService")
class NotificationPreferenceServiceTest {

    @Mock NotificationPreferenceRepository notificationPreferenceRepository;

    @InjectMocks NotificationPreferenceService notificationPreferenceService;

    private User makeUser(String pseudo) {
        User u = new User();
        u.setPseudo(pseudo);
        return u;
    }

    private NotificationPreference makePref(Long id, String userPseudo,
                                            boolean onDeath, boolean beforeDeath, boolean daily) {
        NotificationPreference p = new NotificationPreference();
        p.setId(id);
        p.setNotifyOnDeath(onDeath);
        p.setNotifyBeforeDeath(beforeDeath);
        p.setDailyReminder(daily);
        p.setUser(makeUser(userPseudo));
        return p;
    }

    @Nested @DisplayName("update")
    class Update {

        @Test @DisplayName("updates all three fields when all provided")
        void updatesAllFields() {
            NotificationPreference pref = makePref(1L, "alice", true, true, false);
            when(notificationPreferenceRepository.findAll()).thenReturn(List.of(pref));
            when(notificationPreferenceRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            NotificationPreferenceUpdateDTO dto =
                new NotificationPreferenceUpdateDTO(false, false, true, "alice");

            Optional<NotificationPreference> result =
                notificationPreferenceService.update(1L, dto);

            assertThat(result).isPresent();
            assertThat(result.get().isNotifyOnDeath()).isFalse();
            assertThat(result.get().isNotifyBeforeDeath()).isFalse();
            assertThat(result.get().isDailyReminder()).isTrue();
        }

        @Test @DisplayName("skips null fields and keeps existing values")
        void skipsNullFields() {
            NotificationPreference pref = makePref(1L, "alice", true, false, true);
            when(notificationPreferenceRepository.findAll()).thenReturn(List.of(pref));
            when(notificationPreferenceRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            // Only updating dailyReminder
            NotificationPreferenceUpdateDTO dto =
                new NotificationPreferenceUpdateDTO(null, null, false, "alice");

            Optional<NotificationPreference> result =
                notificationPreferenceService.update(1L, dto);

            assertThat(result).isPresent();
            assertThat(result.get().isNotifyOnDeath()).isTrue();      // unchanged
            assertThat(result.get().isNotifyBeforeDeath()).isFalse(); // unchanged
            assertThat(result.get().isDailyReminder()).isFalse();     // updated
        }

        @Test @DisplayName("returns empty when no preference found for pseudo")
        void notFound() {
            when(notificationPreferenceRepository.findAll()).thenReturn(List.of());

            NotificationPreferenceUpdateDTO dto =
                new NotificationPreferenceUpdateDTO(true, true, true, "ghost");

            assertThat(notificationPreferenceService.update(1L, dto)).isEmpty();
        }
    }

    @Nested @DisplayName("findAll")
    class FindAll {
        @Test @DisplayName("delegates to repository and returns all preferences")
        void delegates() {
            List<NotificationPreference> prefs = List.of(
                makePref(1L, "alice", true, true, false),
                makePref(2L, "bob", false, false, true)
            );
            when(notificationPreferenceRepository.findAll()).thenReturn(prefs);

            assertThat(notificationPreferenceService.findAll()).hasSize(2);
        }
    }
}
