package com.campick.server.api.option.service;

import com.campick.server.api.option.entity.CarOption;
import com.campick.server.api.option.entity.ProductOption;
import com.campick.server.api.option.repository.CarOptionRepository;
import com.campick.server.api.option.repository.ProductOptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OptionService {
    private final CarOptionRepository carOptionRepository;
    private final ProductOptionRepository productOptionRepository;

    public List<CarOption> findCarOptions() {
        return carOptionRepository.findAll();
    }

    public List<ProductOption> findProductOptions() {
        return productOptionRepository.findAll();
    }
}
