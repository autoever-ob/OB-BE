package com.campick.server.api.model.service;

import com.campick.server.api.model.entity.Model;
import com.campick.server.api.model.repository.ModelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ModelService {
    private final ModelRepository modelRepository;

    public List<Model> findAll() {
        return modelRepository.findAll();
    }
}
