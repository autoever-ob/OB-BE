package com.campick.server.api.type.repository;

import com.campick.server.api.type.entity.Type;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TypeRepository extends JpaRepository<Type, Long> {
}
