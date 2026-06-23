package com.littlefish.app.service;

import com.littlefish.app.dto.FishCreateDTO;
import com.littlefish.app.dto.FishUpdateDTO;
import com.littlefish.app.model.Aquarium;
import com.littlefish.app.model.Fish;
import com.littlefish.app.repository.FishRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FishService {

    private final FishRepository fishRepository;
    private final AquariumService aquariumService;

    public List<Fish> findAll() {
        return fishRepository.findAll();
    }

    public Optional<Fish> findById(Long id) {
        return fishRepository.findById(id);
    }

    @Transactional
    public Optional<Fish> create(FishCreateDTO dto) {
        Aquarium aquarium = aquariumService.findById(dto.aquariumId()).orElse(null);
        if (aquarium == null) return Optional.empty();

        Fish fish = new Fish();
        fish.setName(dto.name());
        fish.setSpecies(dto.species());
        fish.setColor(dto.color());
        fish.setPrice(dto.price());
        fish.setSize(dto.size());
        fish.setAge(dto.age());
        fish.setLifePoints(dto.lifePoints());
        fish.setLastFedAt(LocalDateTime.now());
        fish.setAquarium(aquarium);

        return Optional.of(fishRepository.save(fish));
    }

    @Transactional
    public Optional<Fish> update(Long id, FishUpdateDTO patch) {
        Fish existing = fishRepository.findById(id).orElse(null);
        if (existing == null) return Optional.empty();

        if (patch.name() != null) existing.setName(patch.name());
        if (patch.species() != null) existing.setSpecies(patch.species());
        if (patch.color() != null) existing.setColor(patch.color());
        if (patch.price() != 0) existing.setPrice(patch.price());
        if (patch.size() != 0) existing.setSize(patch.size());
        if (patch.age() != 0) existing.setAge(patch.age());
        if (patch.lifePoints() != 0) existing.setLifePoints(patch.lifePoints());
        if (patch.lastFedAt() != null) existing.setLastFedAt(patch.lastFedAt());

        // handle aquarium update if provided
        if (patch.aquariumId() != null) {
            Aquarium aquarium = aquariumService.findById(patch.aquariumId()).orElse(null);
            if (aquarium != null) {
                existing.setAquarium(aquarium);
            }
        }

        return Optional.of(fishRepository.save(existing));
    }

    @Transactional
    public void deleteById(Long id) {
        fishRepository.deleteById(id);
    }
}
