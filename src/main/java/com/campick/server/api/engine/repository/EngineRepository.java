package com.campick.server.api.engine.repository;

import com.campick.server.api.engine.entity.Engine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EngineRepository extends JpaRepository<Engine, Long> {
}
