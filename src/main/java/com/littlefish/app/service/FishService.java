package com.littlefish.app.service;

import com.littlefish.app.model.Fish;
import com.littlefish.app.repository.FishRepository;
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

    public Fish save(Fish fish) {
        return fishRepository.save(fish);
    }

    public void deleteById(Long id) {
        fishRepository.deleteById(id);
    }
}
