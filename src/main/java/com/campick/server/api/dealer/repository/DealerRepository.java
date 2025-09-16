package com.campick.server.api.dealer.repository;

import com.campick.server.api.dealer.entity.Dealer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DealerRepository extends JpaRepository<Dealer, Long> {
}
