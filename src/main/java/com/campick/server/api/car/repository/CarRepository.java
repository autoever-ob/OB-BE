package com.campick.server.api.car.repository;

import com.campick.server.api.car.entity.Car;
import com.campick.server.api.model.entity.Model;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface CarRepository extends JpaRepository<Car, Long> {
    Car findByModel(Model model);
}
