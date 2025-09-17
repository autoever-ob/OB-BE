package com.campick.server.common.config;

import com.campick.server.api.dealer.entity.Dealer;
import com.campick.server.api.dealer.repository.DealerRepository;
import com.campick.server.api.dealership.entity.DealerShip;
import com.campick.server.api.dealership.repository.DealershipRepository;
import com.campick.server.api.member.entity.Member;
import com.campick.server.api.member.entity.Role;
import com.campick.server.api.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final MemberRepository memberRepository;
    private final DealerRepository dealerRepository;
    private final DealershipRepository dealershipRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner seedDataRunner() {
        return args -> seed();
    }

    @Transactional
    protected void seed() {
        // Idempotency: only seed if our marker user doesn't exist
        if (memberRepository.findByEmail("user1@example.com").isPresent()) {
            log.info("Seed data already present; skipping initialization");
            return;
        }

        String rawPassword = "Passw0rd!";
        String encoded = passwordEncoder.encode(rawPassword);

        // 1) 4 ROLE_USER members
        createMemberIfAbsent("user1@example.com", encoded, "user1", "010-1000-0001", Role.ROLE_USER);
        createMemberIfAbsent("user2@example.com", encoded, "user2", "010-1000-0002", Role.ROLE_USER);
        createMemberIfAbsent("user3@example.com", encoded, "user3", "010-1000-0003", Role.ROLE_USER);
        createMemberIfAbsent("user4@example.com", encoded, "user4", "010-1000-0004", Role.ROLE_USER);

        // 2) 2 ROLE_DEALER members without Dealer entity (just role set)
        Member dealerNoEntity1 = createMemberIfAbsent("dealer_no_entity1@example.com", encoded, "dealerNoEntity1", "010-2000-0001", Role.ROLE_DEALER);
        Member dealerNoEntity2 = createMemberIfAbsent("dealer_no_entity2@example.com", encoded, "dealerNoEntity2", "010-2000-0002", Role.ROLE_DEALER);

        // 3) 2 ROLE_DEALER members with Dealer entity only (no dealership)
        Member dealerOnly1 = createMemberIfAbsent("dealer_only1@example.com", encoded, "dealerOnly1", "010-3000-0001", Role.ROLE_DEALER);
        createDealerIfAbsent(dealerOnly1, "BIZ-3000-0001", null);

        Member dealerOnly2 = createMemberIfAbsent("dealer_only2@example.com", encoded, "dealerOnly2", "010-3000-0002", Role.ROLE_DEALER);
        createDealerIfAbsent(dealerOnly2, "BIZ-3000-0002", null);

        // 4) 2 ROLE_DEALER members with Dealer and DealerShip
        DealerShip ship1 = findOrCreateDealership("Prime Motors", "Seoul, Gangnam-gu", "REG-4000-0001");
        Member dealerWithShop1 = createMemberIfAbsent("dealer_with_shop1@example.com", encoded, "dealerWithShop1", "010-4000-0001", Role.ROLE_DEALER);
        createDealerIfAbsent(dealerWithShop1, ship1.getRegistrationNumber(), ship1);

        DealerShip ship2 = findOrCreateDealership("Auto Stars", "Busan, Haeundae-gu", "REG-4000-0002");
        Member dealerWithShop2 = createMemberIfAbsent("dealer_with_shop2@example.com", encoded, "dealerWithShop2", "010-4000-0002", Role.ROLE_DEALER);
        createDealerIfAbsent(dealerWithShop2, ship2.getRegistrationNumber(), ship2);

        log.info("Seed data inserted: demo users and dealers created. Default password='{}'", rawPassword);
    }

    private Member createMemberIfAbsent(String email, String encodedPassword, String nickname, String mobile, Role role) {
        Optional<Member> existing = memberRepository.findByEmail(email);
        if (existing.isPresent()) {
            return existing.get();
        }
        Member m = Member.builder()
                .email(email)
                .password(encodedPassword)
                .nickname(nickname)
                .mobileNumber(mobile)
                .role(role)
                .isDeleted(false)
                .build();
        return memberRepository.save(m);
    }

    private Dealer createDealerIfAbsent(Member user, String businessNo, DealerShip ship) {
        // There is no repository method to find Dealer by user or business number; we will create unconditionally
        // but guard by checking if Member already has an associated Dealer via user.getDealer() if loaded.
        if (user == null) return null;
        // Reload member to ensure fresh link
        Member persisted = memberRepository.findById(user.getId()).orElse(user);
        if (persisted.getDealer() != null) {
            return persisted.getDealer();
        }
        Dealer dealer = Dealer.builder()
                .businessNo(businessNo)
                .rating(0.0)
                .user(persisted)
                .dealerShip(ship)
                .build();
        Dealer saved = dealerRepository.save(dealer);
        // back-reference on Member so member.dealer_id is populated
        persisted.assignDealer(saved);
        memberRepository.save(persisted);
        return saved;
    }

    private DealerShip findOrCreateDealership(String name, String address, String registrationNumber) {
        return dealershipRepository.findByRegistrationNumber(registrationNumber)
                .orElseGet(() -> dealershipRepository.save(DealerShip.builder()
                        .name(name)
                        .address(address)
                        .registrationNumber(registrationNumber)
                        .build()));
    }
}
