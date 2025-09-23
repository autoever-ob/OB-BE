package com.campick.server.api.car.repository;

import com.campick.server.api.car.entity.Car;
import com.campick.server.api.model.entity.Model;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface CarRepository extends JpaRepository<Car, Long> {
    Optional<Car> findByModel(Model model);

    List<Car> findCarsByModel(Model model);
}
