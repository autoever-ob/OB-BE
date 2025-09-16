package com.campick.server.api.type.controller;

import com.campick.server.api.type.entity.Type;
import com.campick.server.api.type.service.TypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/types")
@RequiredArgsConstructor
public class TypeController {
    private final TypeService typeService;

    @GetMapping
    public List<Type> getTypes() {
        return typeService.findAll();
    }
}
