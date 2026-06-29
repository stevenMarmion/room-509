package com.littlefish.app.service;

import com.littlefish.app.dto.FishCreateDTO;
import com.littlefish.app.dto.FishUpdateDTO;
import com.littlefish.app.model.Aquarium;
import com.littlefish.app.model.Fish;
import com.littlefish.app.repository.FishRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FishService")
class FishServiceTest {

    @Mock FishRepository fishRepository;
    @Mock AquariumService aquariumService;

    @InjectMocks FishService fishService;

    private Aquarium makeAquarium(Long id, int capacity) {
        Aquarium aq = new Aquarium();
        aq.setId(id);
        aq.setCapacity(capacity);
        aq.setLevel(1);
        aq.setName("Test Aquarium");
        return aq;
    }

    private Fish makeFish(Long id, String name) {
        Fish f = new Fish();
        f.setId(id);
        f.setName(name);
        f.setSpecies("Clownfish");
        f.setColor("Orange");
        f.setSize(2);
        f.setAge(1);
        f.setLifePoints(100);
        f.setPrice(50);
        f.setLastFedAt(LocalDateTime.now());
        return f;
    }

    // ──────────────────────────────────────────────────────────────
    @Nested @DisplayName("findAll")
    class FindAll {
        @Test @DisplayName("returns all fish")
        void returnsList() {
            List<Fish> fish = List.of(makeFish(1L, "Nemo"), makeFish(2L, "Dory"));
            when(fishRepository.findAll()).thenReturn(fish);

            assertThat(fishService.findAll()).hasSize(2);
        }
    }

    // ──────────────────────────────────────────────────────────────
    @Nested @DisplayName("findById")
    class FindById {
        @Test @DisplayName("returns fish when it exists")
        void found() {
            Fish f = makeFish(1L, "Nemo");
            when(fishRepository.findById(1L)).thenReturn(Optional.of(f));

            assertThat(fishService.findById(1L)).isPresent().contains(f);
        }

        @Test @DisplayName("returns empty when fish does not exist")
        void notFound() {
            when(fishRepository.findById(99L)).thenReturn(Optional.empty());

            assertThat(fishService.findById(99L)).isEmpty();
        }
    }

    // ──────────────────────────────────────────────────────────────
    @Nested @DisplayName("create")
    class Create {

        @Test @DisplayName("creates fish and links it to the aquarium")
        void success() {
            Aquarium aq = makeAquarium(1L, 10);
            when(aquariumService.findById(1L)).thenReturn(Optional.of(aq));
            when(fishRepository.save(any())).thenAnswer(inv -> {
                Fish f = inv.getArgument(0);
                f.setId(10L);
                return f;
            });

            FishCreateDTO dto = new FishCreateDTO( "Nemo", "Clownfish", "Orange", 50, 2, 0, 100, 1L);
            Optional<Fish> result = fishService.create(dto);

            assertThat(result).isPresent();
            assertThat(result.get().getName()).isEqualTo("Nemo");
            assertThat(result.get().getAquarium()).isEqualTo(aq);
            assertThat(result.get().getLastFedAt()).isNotNull();
        }

        @Test @DisplayName("returns empty when aquarium does not exist")
        void aquariumNotFound() {
            when(aquariumService.findById(99L)).thenReturn(Optional.empty());

            FishCreateDTO dto = new FishCreateDTO( "Nemo", "Clownfish", "Orange", 50, 2, 0, 100, 99L);
            assertThat(fishService.create(dto)).isEmpty();
            verify(fishRepository, never()).save(any());
        }
    }

    // ──────────────────────────────────────────────────────────────
    @Nested @DisplayName("update")
    class Update {

        @Test @DisplayName("updates name and color when provided")
        void updatesNameAndColor() {
            Fish existing = makeFish(1L, "OldName");
            Aquarium aq = makeAquarium(1L, 10);
            existing.setAquarium(aq);
            when(fishRepository.findById(1L)).thenReturn(Optional.of(existing));
            when(fishRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            FishUpdateDTO patch = new FishUpdateDTO("NewName", null, "Blue", 0, 0, 0, 0, null, null);
            Optional<Fish> result = fishService.update(1L, patch);

            assertThat(result).isPresent();
            assertThat(result.get().getName()).isEqualTo("NewName");
            assertThat(result.get().getColor()).isEqualTo("Blue");
        }

        @Test @DisplayName("updates life points when provided")
        void updatesLifePoints() {
            Fish existing = makeFish(1L, "Nemo");
            when(fishRepository.findById(1L)).thenReturn(Optional.of(existing));
            when(fishRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            FishUpdateDTO patch = new FishUpdateDTO(null, null, null, 0, 0, 0, 75, null, null);
            Optional<Fish> result = fishService.update(1L, patch);

            assertThat(result).isPresent();
            assertThat(result.get().getLifePoints()).isEqualTo(75);
        }

        @Test @DisplayName("moves fish to another aquarium when aquariumId provided")
        void movesAquarium() {
            Fish existing = makeFish(1L, "Nemo");
            Aquarium oldAq = makeAquarium(1L, 5);
            Aquarium newAq = makeAquarium(2L, 10);
            existing.setAquarium(oldAq);

            when(fishRepository.findById(1L)).thenReturn(Optional.of(existing));
            when(aquariumService.findById(2L)).thenReturn(Optional.of(newAq));
            when(fishRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            FishUpdateDTO patch = new FishUpdateDTO(null, null, null, 0, 0, 0, 0, null, 2L);
            Optional<Fish> result = fishService.update(1L, patch);

            assertThat(result).isPresent();
            assertThat(result.get().getAquarium().getId()).isEqualTo(2L);
        }

        @Test @DisplayName("returns empty when fish not found")
        void notFound() {
            when(fishRepository.findById(99L)).thenReturn(Optional.empty());

            FishUpdateDTO patch = new FishUpdateDTO("New", null, null, 0, 0, 0, 0, null, null);
            assertThat(fishService.update(99L, patch)).isEmpty();
        }
    }

    // ──────────────────────────────────────────────────────────────
    @Nested @DisplayName("deleteById")
    class DeleteById {
        @Test @DisplayName("delegates to repository")
        void delegates() {
            fishService.deleteById(1L);
            verify(fishRepository).deleteById(1L);
        }
    }
}
