package com.campick.server.api.engine.controller;

import com.campick.server.api.engine.entity.Engine;
import com.campick.server.api.engine.service.EngineService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/engines")
@RequiredArgsConstructor
public class EngineController {
    private final EngineService engineService;

    @GetMapping
    public List<Engine> getEngines() {
        return engineService.findAll();
    }
}
