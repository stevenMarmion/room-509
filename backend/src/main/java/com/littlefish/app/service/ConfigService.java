package com.littlefish.app.service;

import com.littlefish.app.model.Config;
import com.littlefish.app.repository.ConfigRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ConfigService {

    private final ConfigRepository configRepository;

    public List<Config> findAll() {
        return configRepository.findAll();
    }

    public Optional<Config> findByKey(String key) {
        return configRepository.findByKey(key);
    }

    @Transactional
    public Config save(Config config) {
        return configRepository.save(config);
    }

    @Transactional
    public Optional<Config> update(String key, Config patch) {
        Config existing = configRepository.findByKey(key).orElse(null);
        if (existing == null) return Optional.empty();

        if (patch.getValue() != null) existing.setValue(patch.getValue());
        if (patch.getDescription() != null) existing.setDescription(patch.getDescription());

        return Optional.of(configRepository.save(existing));
    }

    @Transactional
    public void deleteByKey(String key) {
        configRepository.deleteByKey(key);
    }
}
