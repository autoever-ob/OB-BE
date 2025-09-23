package com.campick.server.common.config;

import com.campick.server.api.car.entity.Car;
import com.campick.server.api.car.repository.CarRepository;
import com.campick.server.api.dealer.entity.Dealer;
import com.campick.server.api.dealer.repository.DealerRepository;
import com.campick.server.api.dealership.entity.DealerShip;
import com.campick.server.api.dealership.repository.DealershipRepository;
import com.campick.server.api.engine.entity.Engine;
import com.campick.server.api.engine.entity.FuelType;
import com.campick.server.api.engine.entity.Transmission;
import com.campick.server.api.engine.repository.EngineRepository;
import com.campick.server.api.member.entity.Member;
import com.campick.server.api.member.entity.Role;
import com.campick.server.api.member.repository.MemberRepository;
import com.campick.server.api.model.entity.Model;
import com.campick.server.api.model.repository.ModelRepository;
import com.campick.server.api.option.entity.CarOption;
import com.campick.server.api.option.repository.CarOptionRepository;
import com.campick.server.api.product.entity.Product;
import com.campick.server.api.product.entity.ProductStatus;
import com.campick.server.api.product.entity.ProductType;
import com.campick.server.api.product.repository.ProductRepository;
import com.campick.server.api.transaction.entity.Transaction;
import com.campick.server.api.transaction.entity.TransactionType;
import com.campick.server.api.transaction.repository.TransactionRepository;
import com.campick.server.api.type.entity.Type;
import com.campick.server.api.type.entity.VehicleTypeName;
import com.campick.server.api.type.repository.TypeRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

// @Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final MemberRepository memberRepository;
    private final DealerRepository dealerRepository;
    private final DealershipRepository dealershipRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProductRepository productRepository;
    private final CarRepository carRepository;
    private final ModelRepository modelRepository;
    private final EngineRepository engineRepository;
    private final TypeRepository typeRepository;
    private final TransactionRepository transactionRepository;
    private final CarOptionRepository carOptionRepository;


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

        String rawPassword = "123456";
//        String encoded = passwordEncoder.encode(rawPassword);
        String encoded = rawPassword;
        List<Member> members = new ArrayList<>();

        // 1) 4 ROLE_USER members
        members.add(createMemberIfAbsent("user1@example.com", encoded, "user1", "010-1000-0001", Role.USER));
        members.add(createMemberIfAbsent("user2@example.com", encoded, "user2", "010-1000-0002", Role.USER));
        members.add(createMemberIfAbsent("user3@example.com", encoded, "user3", "010-1000-0003", Role.USER));
        members.add(createMemberIfAbsent("user4@example.com", encoded, "user4", "010-1000-0004", Role.USER));

        // 2) 2 ROLE_DEALER members without Dealer entity (just role set)
        Member dealerNoEntity1 = createMemberIfAbsent("dealer_no_entity1@example.com", encoded, "dealerNoEntity1", "010-2000-0001", Role.DEALER);
        members.add(dealerNoEntity1);
        Member dealerNoEntity2 = createMemberIfAbsent("dealer_no_entity2@example.com", encoded, "dealerNoEntity2", "010-2000-0002", Role.DEALER);
        members.add(dealerNoEntity2);

        // 3) 2 ROLE_DEALER members with Dealer entity only (no dealership)
        Member dealerOnly1 = createMemberIfAbsent("dealer_only1@example.com", encoded, "dealerOnly1", "010-3000-0001", Role.DEALER);
        members.add(dealerOnly1);
        createDealerIfAbsent(dealerOnly1, "BIZ-3000-0001", null);

        Member dealerOnly2 = createMemberIfAbsent("dealer_only2@example.com", encoded, "dealerOnly2", "010-3000-0002", Role.DEALER);
        members.add(dealerOnly2);
        createDealerIfAbsent(dealerOnly2, "BIZ-3000-0002", null);

        // 4) 2 ROLE_DEALER members with Dealer and DealerShip
        DealerShip ship1 = findOrCreateDealership("Prime Motors", "Seoul, Gangnam-gu", "REG-4000-0001");
        Member dealerWithShop1 = createMemberIfAbsent("dealer_with_shop1@example.com", encoded, "dealerWithShop1", "010-4000-0001", Role.DEALER);
        members.add(dealerWithShop1);
        createDealerIfAbsent(dealerWithShop1, ship1.getRegistrationNumber(), ship1);

        DealerShip ship2 = findOrCreateDealership("Auto Stars", "Busan, Haeundae-gu", "REG-4000-0002");
        Member dealerWithShop2 = createMemberIfAbsent("dealer_with_shop2@example.com", encoded, "dealerWithShop2", "010-4000-0002", Role.DEALER);
        members.add(dealerWithShop2);
        createDealerIfAbsent(dealerWithShop2, ship2.getRegistrationNumber(), ship2);

        log.info("Seed data inserted: demo users and dealers created. Default password='{}'", rawPassword);

        // 5) Create Products for each member
        seedProducts(members);

        // option
        List<String> options = Arrays.asList("에어컨", "난방", "냉장고", "전자레인지", "화장실", "샤워실", "침대", "TV");

        for (String option : options) {
            carOptionRepository.save(CarOption.builder().name(option).build());
        }
    }

    private void seedProducts(List<Member> members) {
        if (modelRepository.count() > 0) {
            log.info("Model seed data already present; skipping initialization");
            return;
        }

        log.info("Seeding new Type and Model data as requested.");

        // Create Types
        Type motorHomeType = typeRepository.save(Type.builder().typeName(VehicleTypeName.MOTOR_HOME).build());
        Type caravanType = typeRepository.save(Type.builder().typeName(VehicleTypeName.CARAVAN).build());
        Type trailerType = typeRepository.save(Type.builder().typeName(VehicleTypeName.TRAILER).build());
        Type truckCamperType = typeRepository.save(Type.builder().typeName(VehicleTypeName.TRUCK_CAMPER).build());
        typeRepository.save(Type.builder().typeName(VehicleTypeName.ETC).build());

        // Create Models based on user request
        // 모터홈
        modelRepository.save(Model.builder().type(motorHomeType).modelName("1톤축연장").build());
        modelRepository.save(Model.builder().type(motorHomeType).modelName("1톤축미연장").build());
        modelRepository.save(Model.builder().type(motorHomeType).modelName("칸").build());
        modelRepository.save(Model.builder().type(motorHomeType).modelName("마스터").build());
        modelRepository.save(Model.builder().type(motorHomeType).modelName("스타렉스").build());
        modelRepository.save(Model.builder().type(motorHomeType).modelName("스타리아").build());
        modelRepository.save(Model.builder().type(motorHomeType).modelName("카운티").build());

        // 카라반
        modelRepository.save(Model.builder().type(caravanType).modelName("300급").build());
        modelRepository.save(Model.builder().type(caravanType).modelName("400급").build());
        modelRepository.save(Model.builder().type(caravanType).modelName("500급").build());
        modelRepository.save(Model.builder().type(caravanType).modelName("600급").build());

        // 트레일러
        modelRepository.save(Model.builder().type(trailerType).modelName("폴딩형").build());
        modelRepository.save(Model.builder().type(trailerType).modelName("카고형").build());

        // 트럭캠퍼
        modelRepository.save(Model.builder().type(truckCamperType).modelName("그외").build());

        log.info("Successfully seeded Type and Model data. Dummy Engine, Car, and Product creation has been removed.");
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
