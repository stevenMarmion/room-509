package com.littlefish.app.service;

import com.littlefish.app.model.Fish;
import com.littlefish.app.repository.FishRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FishService {

    private final FishRepository fishRepository;

    public List<Fish> findAll() {
        return fishRepository.findAll();
    }

    public Optional<Fish> findById(Long id) {
        return fishRepository.findById(id);
    }

    @Transactional
    public Fish save(Fish fish) {
        return fishRepository.save(fish);
    }

    @Transactional
    public Optional<Fish> update(Long id, Fish patch) {
        Fish existing = fishRepository.findById(id).orElse(null);
        if (existing == null) return Optional.empty();

        if (patch.getName() != null) existing.setName(patch.getName());
        if (patch.getSpecies() != null) existing.setSpecies(patch.getSpecies());
        if (patch.getColor() != null) existing.setColor(patch.getColor());
        if (patch.getPrice() != 0) existing.setPrice(patch.getPrice());
        if (patch.getSize() != 0) existing.setSize(patch.getSize());
        if (patch.getAge() != 0) existing.setAge(patch.getAge());
        if (patch.getLifePoints() != 0) existing.setLifePoints(patch.getLifePoints());
        if (patch.getLastFedAt() != null) existing.setLastFedAt(patch.getLastFedAt());

        return Optional.of(fishRepository.save(existing));
    }

    @Transactional
    public void deleteById(Long id) {
        fishRepository.deleteById(id);
    }
}
