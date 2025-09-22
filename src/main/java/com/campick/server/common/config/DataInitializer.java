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

@Configuration
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

        // type 저장
        typeRepository.save(Type.builder().typeName(VehicleTypeName.TRUCK_CAMPER).build());
        typeRepository.save(Type.builder().typeName(VehicleTypeName.TRAILER).build());
        typeRepository.save(Type.builder().typeName(VehicleTypeName.ETC).build());

        // option
        List<String> options = Arrays.asList("에어컨", "난방", "냉장고", "전자레인지", "화장실", "샤워실", "침대", "TV");

        for (String option : options) {
            carOptionRepository.save(CarOption.builder().name(option).build());
        }
    }

    private void seedProducts(List<Member> members) {
        if (productRepository.count() > 0) {
            log.info("Product seed data already present; skipping initialization");
            return;
        }

        // Create reusable entities
        Type type1 = typeRepository.save(Type.builder().typeName(VehicleTypeName.MOTOR_HOME).build());
        Engine engine1 = engineRepository.save(Engine.builder().fuelType(FuelType.DIESEL).transmission(Transmission.AUTOMATIC).horsePower(180).build());
        Model model1 = modelRepository.save(Model.builder().type(type1).modelName("그랜드 스타렉스").marketName("현대 그랜드 스타렉스").build());
        Car car1 = carRepository.save(Car.builder().model(model1).engine(engine1).build());

        Type type2 = typeRepository.save(Type.builder().typeName(VehicleTypeName.CARAVAN).build());
        Engine engine2 = engineRepository.save(Engine.builder().fuelType(FuelType.GASOLINE).transmission(Transmission.AUTOMATIC).horsePower(250).build());
        Model model2 = modelRepository.save(Model.builder().type(type2).modelName("익스플로어").marketName("포드 익스플로어").build());
        Car car2 = carRepository.save(Car.builder().model(model2).engine(engine2).build());

        List<Car> cars = List.of(car1, car2);
        AtomicInteger carIndex = new AtomicInteger(0);

        log.info("Seeding products for {} members...", members.size());

        for (Member member : members) {
            for (int i = 1; i <= 10; i++) {
                Car car = cars.get(carIndex.getAndIncrement() % cars.size());
                Product product = Product.builder()
                        .seller(member)
                        .car(car)
                        .title(member.getNickname() + "'s Camping Car - " + i)
                        .cost(10000000 + (i * 1000000))
                        .mileage(50000 + (i * 1000))
                        .generation(2017)
                        .description("A very nice camping car for sale. Well-maintained and ready for adventure.")
                        .plateHash(UUID.randomUUID().toString().substring(0, 8)) // Simplified hash
                        .exteriorColor("White")
                        .interiorColor("Black")
                        .location("Seoul")
                        .type(ProductType.SELLING)
                        .status(ProductStatus.AVAILABLE)
                        .isDeleted(false)
                        .build();
                productRepository.save(product);
            }
        }

        // Create multiple sold products to simulate purchases
        if (members.size() > 1) {
            for (int i = 0; i < 3; i++) {
                Member seller = members.get(i % members.size());
                Member buyer = members.get((i + 1) % members.size());
                Car car = cars.get(i % cars.size());

                Product soldProduct = Product.builder()
                        .seller(seller)
                        .car(car)
                        .title(seller.getNickname() + "'s Sold Camping Car " + (i + 1))
                        .cost(12000000 + (i * 1000000))
                        .mileage(55000 + (i * 5000))
                        .generation(2018)
                        .description("This camping car was sold to " + buyer.getNickname() + ".")
                        .plateHash(UUID.randomUUID().toString().substring(0, 8))
                        .exteriorColor("Silver")
                        .interiorColor("Grey")
                        .location("Busan")
                        .type(ProductType.SELLING)
                        .status(ProductStatus.SOLD)
                        .isDeleted(false)
                        .build();
                productRepository.save(soldProduct);

                Transaction transaction = new Transaction(null, buyer, seller, soldProduct, LocalDateTime.now(), TransactionType.SELL);
                transactionRepository.save(transaction);
            }
        }

        log.info("Seed data inserted: {} products created.", productRepository.count());
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
