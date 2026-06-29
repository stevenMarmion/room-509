package com.littlefish.app.service;

import com.littlefish.app.model.Aquarium;
import com.littlefish.app.model.Fish;
import com.littlefish.app.repository.AquariumRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AquariumService")
class AquariumServiceTest {

    @Mock AquariumRepository aquariumRepository;

    @InjectMocks AquariumService aquariumService;

    private Aquarium makeAquarium(Long id, int level, int capacity, boolean isPublic) {
        Aquarium aq = new Aquarium();
        aq.setId(id);
        aq.setName("Test Tank");
        aq.setLevel(level);
        aq.setCapacity(capacity);
        aq.setPublic(isPublic);
        aq.setFish(new ArrayList<>());
        return aq;
    }

    private Fish makeFish(Long id, String name) {
        Fish f = new Fish();
        f.setId(id);
        f.setName(name);
        return f;
    }

    // ──────────────────────────────────────────────────────────────
    @Nested @DisplayName("findById")
    class FindById {
        @Test @DisplayName("returns aquarium when it exists")
        void found() {
            Aquarium aq = makeAquarium(1L, 1, 5, false);
            when(aquariumRepository.findById(1L)).thenReturn(Optional.of(aq));

            assertThat(aquariumService.findById(1L)).isPresent().contains(aq);
        }

        @Test @DisplayName("returns empty when aquarium does not exist")
        void notFound() {
            when(aquariumRepository.findById(99L)).thenReturn(Optional.empty());

            assertThat(aquariumService.findById(99L)).isEmpty();
        }
    }

    // ──────────────────────────────────────────────────────────────
    @Nested @DisplayName("update")
    class Update {

        @Test @DisplayName("updates name and level when provided")
        void updatesNameAndLevel() {
            Aquarium existing = makeAquarium(1L, 1, 5, false);
            when(aquariumRepository.findById(1L)).thenReturn(Optional.of(existing));
            when(aquariumRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            Aquarium patch = new Aquarium();
            patch.setName("New Name");
            patch.setLevel(2);

            Optional<Aquarium> result = aquariumService.update(1L, patch);

            assertThat(result).isPresent();
            assertThat(result.get().getName()).isEqualTo("New Name");
            assertThat(result.get().getLevel()).isEqualTo(2);
        }

        @Test @DisplayName("updates capacity when provided")
        void updatesCapacity() {
            Aquarium existing = makeAquarium(1L, 1, 5, false);
            when(aquariumRepository.findById(1L)).thenReturn(Optional.of(existing));
            when(aquariumRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            Aquarium patch = new Aquarium();
            patch.setCapacity(15);

            Optional<Aquarium> result = aquariumService.update(1L, patch);

            assertThat(result).isPresent();
            assertThat(result.get().getCapacity()).isEqualTo(15);
        }

        @Test @DisplayName("toggles visibility from private to public")
        void togglesVisibility() {
            Aquarium existing = makeAquarium(1L, 1, 5, false);
            when(aquariumRepository.findById(1L)).thenReturn(Optional.of(existing));
            when(aquariumRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            Aquarium patch = new Aquarium();
            patch.setPublic(true);

            Optional<Aquarium> result = aquariumService.update(1L, patch);

            assertThat(result).isPresent();
            assertThat(result.get().isPublic()).isTrue();
        }
    }

    // ──────────────────────────────────────────────────────────────
    @Nested @DisplayName("switchVisibility")
    class SwitchVisibility {

        @Test @DisplayName("makes a private aquarium public")
        void makesPublic() {
            Aquarium existing = makeAquarium(1L, 1, 5, false);
            when(aquariumRepository.findById(1L)).thenReturn(Optional.of(existing));
            when(aquariumRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            Optional<Aquarium> result = aquariumService.switchVisibility(1L, true);

            assertThat(result).isPresent();
            assertThat(result.get().isPublic()).isTrue();
        }
    }

    // ──────────────────────────────────────────────────────────────
    @Nested @DisplayName("addFishToAquarium")
    class AddFish {

        @Test @DisplayName("adds fish when capacity allows")
        void addsWhenCapacityOk() {
            Aquarium aq = makeAquarium(1L, 1, 5, false);
            when(aquariumRepository.findById(1L)).thenReturn(Optional.of(aq));
            when(aquariumRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            Fish fish = makeFish(10L, "Nemo");
            Optional<Aquarium> result = aquariumService.addFishToAquarium(1L, fish);

            assertThat(result).isPresent();
            assertThat(result.get().getFish()).hasSize(1);
            assertThat(fish.getAquarium()).isEqualTo(aq);
        }

        @Test @DisplayName("refuses fish when aquarium is at full capacity")
        void refusesWhenFull() {
            Aquarium aq = makeAquarium(1L, 1, 2, false);
            aq.setFish(new ArrayList<>(List.of(makeFish(1L, "A"), makeFish(2L, "B"))));
            when(aquariumRepository.findById(1L)).thenReturn(Optional.of(aq));

            Fish fish = makeFish(3L, "C");
            Optional<Aquarium> result = aquariumService.addFishToAquarium(1L, fish);

            assertThat(result).isEmpty();
            verify(aquariumRepository, never()).save(any());
        }

        @Test @DisplayName("returns empty when aquarium does not exist")
        void aquariumNotFound() {
            when(aquariumRepository.findById(99L)).thenReturn(Optional.empty());

            assertThat(aquariumService.addFishToAquarium(99L, makeFish(1L, "X"))).isEmpty();
        }
    }

    // ──────────────────────────────────────────────────────────────
    @Nested @DisplayName("deleteById")
    class DeleteById {
        @Test @DisplayName("delegates to repository")
        void delegates() {
            aquariumService.deleteById(1L);
            verify(aquariumRepository).deleteById(1L);
        }
    }
}
