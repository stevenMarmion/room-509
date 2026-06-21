package com.littlefish.app.service;

import com.littlefish.app.model.Aquarium;
import com.littlefish.app.repository.AquariumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AquariumService {

    private final AquariumRepository aquariumRepository;

    public List<Aquarium> findAll() {
        return aquariumRepository.findAll();
    }

    public Optional<Aquarium> findById(Long id) {
        return aquariumRepository.findById(id);
    }

    public Aquarium save(Aquarium aquarium) {
        return aquariumRepository.save(aquarium);
    }

    public Optional<Aquarium> update(Long id, Aquarium patch) {
        Aquarium existingAquarium = aquariumRepository.findById(id).orElse(null);
        if (patch.getName() != null) {
            existingAquarium.setName(patch.getName());
        }
        if (patch.getLevel() != 0) {
            existingAquarium.setLevel(existingAquarium.getLevel() + patch.getLevel());
        }
        if (patch.getCapacity() != 0) {
            existingAquarium.setCapacity(existingAquarium.getCapacity() + patch.getCapacity());
        }
        if (patch.isPublic() != existingAquarium.isPublic()) {
            existingAquarium.setPublic(patch.isPublic());
        }
        return Optional.of(aquariumRepository.save(existingAquarium));
    }

    public Optional<Aquarium> switchVisibility(Long id, boolean isPublic) {
        return update(id, new Aquarium() {{ setPublic(isPublic); }});
    }

    public Optional<Aquarium> addFish(Long id, Aquarium patch) {
        Aquarium existingAquarium = aquariumRepository.findById(id).orElse(null);
        if (existingAquarium != null && patch.getFish() != null) {
            // Avoid duplicates fish in list
            existingAquarium.getFish().removeIf(fish -> patch.getFish().stream().anyMatch(f -> f.getId() == fish.getId()));
            existingAquarium.getFish().addAll(patch.getFish()); 
            return Optional.of(aquariumRepository.save(existingAquarium));
        }
        return Optional.empty();
    }

    public void deleteById(Long id) {
        aquariumRepository.deleteById(id);
    }
}
