package com.campick.server.api.type.service;

import com.campick.server.api.type.entity.Type;
import com.campick.server.api.type.repository.TypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TypeService {
    private final TypeRepository typeRepository;

    public List<Type> findAll() {
        return typeRepository.findAll();
    }
}
