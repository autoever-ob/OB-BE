package com.campick.server.api.option.repository;

import com.campick.server.api.option.entity.CarOption;
import com.campick.server.api.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CarOptionRepository extends JpaRepository<CarOption, Long> {
    Optional<CarOption> findByName(String optionName);
}