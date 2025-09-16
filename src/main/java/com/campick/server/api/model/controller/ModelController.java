package com.campick.server.api.model.controller;

import com.campick.server.api.model.entity.Model;
import com.campick.server.api.model.service.ModelService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/models")
@RequiredArgsConstructor
public class ModelController {
    private final ModelService modelService;

    @GetMapping
    public List<Model> getModels() {
        return modelService.findAll();
    }
}
