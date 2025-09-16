package com.campick.server.api.option.controller;

import com.campick.server.api.option.entity.CarOption;
import com.campick.server.api.option.entity.ProductOption;
import com.campick.server.api.option.service.OptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/options")
@RequiredArgsConstructor
public class OptionController {
    private final OptionService optionService;

    @GetMapping("/cars")
    public List<CarOption> getCarOptions() {
        return optionService.findCarOptions();
    }

    @GetMapping("/products")
    public List<ProductOption> getProductOptions() {
        return optionService.findProductOptions();
    }
}
