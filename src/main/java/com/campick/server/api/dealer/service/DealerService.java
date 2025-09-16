package com.campick.server.api.dealer.service;

import com.campick.server.api.dealer.entity.Dealer;
import com.campick.server.api.dealer.repository.DealerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DealerService {
    private final DealerRepository dealerRepository;

    public List<Dealer> findAll() {
        return dealerRepository.findAll();
    }
}
