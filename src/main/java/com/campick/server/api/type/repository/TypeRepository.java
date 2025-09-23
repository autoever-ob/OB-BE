package com.campick.server.api.type.repository;

import com.campick.server.api.type.entity.Type;
import com.campick.server.api.type.entity.VehicleTypeName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface TypeRepository extends JpaRepository<Type, Long> {
    Optional<Type> findByTypeName(VehicleTypeName typeName);

    Optional<Type> getTypeByTypeName(VehicleTypeName vehicleTypeName);
}
