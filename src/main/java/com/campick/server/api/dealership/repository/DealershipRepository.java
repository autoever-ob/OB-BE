package com.campick.server.api.dealership.repository;

import com.campick.server.api.dealership.entity.DealerShip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DealershipRepository extends JpaRepository<DealerShip, Long> {
    Optional<DealerShip> findByRegistrationNumber(String registrationNumber);
}
