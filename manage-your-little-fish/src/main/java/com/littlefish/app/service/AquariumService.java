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

    public void deleteById(Long id) {
        aquariumRepository.deleteById(id);
    }
}
