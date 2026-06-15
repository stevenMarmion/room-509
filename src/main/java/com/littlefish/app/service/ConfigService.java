package com.littlefish.app.service;

import com.littlefish.app.model.Config;
import com.littlefish.app.repository.ConfigRepository;
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
        return configRepository.findByKeyConfig(key);
    }
}
