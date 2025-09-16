package com.campick.server.api.dealer.controller;

import com.campick.server.api.dealer.entity.Dealer;
import com.campick.server.api.dealer.service.DealerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dealers")
@RequiredArgsConstructor
public class DealerController {
    private final DealerService dealerService;

    @GetMapping
    public List<Dealer> getDealers() {
        return dealerService.findAll();
    }
}
