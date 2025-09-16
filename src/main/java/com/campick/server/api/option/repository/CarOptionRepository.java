package com.campick.server.api.option.repository;

import com.campick.server.api.option.entity.CarOption;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarOptionRepository extends JpaRepository<CarOption, Long> {
}
