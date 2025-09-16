package com.campick.server.api.model.repository;

import com.campick.server.api.model.entity.Model;
import com.campick.server.api.type.entity.Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ModelRepository extends JpaRepository<Model, Long> {
    Model findByTypeAndModelName(Type type, String modelName);
}
