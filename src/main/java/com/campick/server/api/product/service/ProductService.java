package com.campick.server.api.product.service;

import com.campick.server.api.car.entity.Car;
import com.campick.server.api.car.repository.CarRepository;
import com.campick.server.api.engine.entity.Engine;
import com.campick.server.api.engine.entity.FuelType;
import com.campick.server.api.engine.repository.EngineRepository;
import com.campick.server.api.member.entity.Member;
import com.campick.server.api.member.repository.MemberRepository;
import com.campick.server.api.model.entity.Model;
import com.campick.server.api.model.repository.ModelRepository;
import com.campick.server.api.option.entity.CarOption;
import com.campick.server.api.option.entity.ProductOption;
import com.campick.server.api.option.repository.CarOptionRepository;
import com.campick.server.api.option.repository.ProductOptionRepository;
import com.campick.server.api.product.dto.*;
import com.campick.server.api.product.entity.Product;
import com.campick.server.api.product.entity.ProductImage;
import com.campick.server.api.product.entity.ProductStatus;
import com.campick.server.api.product.entity.ProductType;
import com.campick.server.api.product.repository.ProductImageRepository;
import com.campick.server.api.product.repository.ProductRepository;
import com.campick.server.api.type.entity.Type;
import com.campick.server.api.type.entity.VehicleTypeName;
import com.campick.server.api.type.repository.TypeRepository;
import com.campick.server.common.exception.BadRequestException;
import com.campick.server.common.exception.NotFoundException;
import com.campick.server.common.response.ErrorStatus;
import com.campick.server.common.storage.FirebaseStorageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductOptionRepository productOptionRepository;
    private final CarOptionRepository carOptionRepository;
    private final ModelRepository modelRepository;
    private final CarRepository carRepository;
    private final TypeRepository typeRepository;
    private final MemberRepository memberRepository;
    private final FirebaseStorageService firebaseStorageService;
    private final ProductImageService productImageService;
    private final EngineRepository engineRepository;

    @Transactional
    public Long createProduct(ProductCreateReqDto dto) {
        VehicleTypeName vehicleTypeName;
        try {
            vehicleTypeName = VehicleTypeName.valueOf(dto.getVehicleType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid vehicle type: " + dto.getVehicleType());
        }
        Type type = typeRepository.findBytypeName(vehicleTypeName);

        Model model = modelRepository.findByTypeAndModelName(type, dto.getVehicleModel());

        Car car = carRepository.findByModel(model);

        Member member = memberRepository.findById(1L)
                .orElseThrow(() -> new BadRequestException("Member not found"));

        Product product = ProductDtoToEntity(member, car, dto);
        productRepository.save(product);

        // 이미지 저장 - 5장 초과인지 백에서도 검사해야 함?
        saveImages(product, dto.getMainProductImageUrl(), dto.getProductImageUrl());
        // 옵션 저장
        saveOptions(product, dto.getOption());

        return product.getId();
    }

    private Product ProductDtoToEntity(Member member, Car car, ProductCreateReqDto dto) {
        return Product.builder()
                .seller(member)
                .car(car)
                .title(dto.getTitle())
                .generation(dto.getGeneration())
                .cost(Integer.parseInt(dto.getPrice()))
                .mileage(Integer.parseInt(dto.getMileage()))
                .description(dto.getDescription())
                .plateHash(dto.getPlateHash())
                .location(dto.getLocation())
                .type(ProductType.SELLING) // 딜러/유저 구분 필요
                .status(ProductStatus.AVAILABLE)
                .isDeleted(false)
                .build();
    }

    private void saveOptions(Product product, List<OptionDto> options) {
        for (OptionDto optionDto : options) {
            Optional<CarOption> existingOption = carOptionRepository.findByName(optionDto.getOptionName());

            CarOption carOption;
            if (existingOption.isPresent()) {
                carOption = existingOption.get();
            } else {
                carOption = CarOption.builder()
                        .name(optionDto.getOptionName())
                        .build();
                carOptionRepository.save(carOption);
            }

            ProductOption option = ProductOption.builder()
                    .product(product)
                    .carOption(carOption)
                    .isEquipped(optionDto.getIsInclude())
                    .build();
            productOptionRepository.save(option);
        }
    }

    private void saveImages(Product product, String main, List<String> images) {
        // 썸네일
        ProductImage thumbnail = ProductImage.builder()
                .product(product)
                .imageUrl(main)
                .isThumbnail(true)
                .build();
        productImageRepository.save(thumbnail);

        // 나머지 사진들
        for (String imageUrl : images) {
            ProductImage image = ProductImage.builder()
                    .product(product)
                    .imageUrl(imageUrl)
                    .isThumbnail(false)
                    .build();
            productImageRepository.save(image);
        }
    }


    @Transactional
    public List<ProductResDto> findAll() {
        List<Product> products = productRepository.findAll();

        return products.stream()
                .map(product -> {
                    String thumbnailUrl = productImageRepository
                            .findByProductAndIsThumbnailTrue(product)
                            .getImageUrl();
                    Car car = product.getCar();
                    Engine engine = car.getEngine();

                    return new ProductResDto(
                            product.getTitle(),
                            product.getCost().toString(),
                            product.getGeneration(),
                            engine.getFuelType().toString(),
                            engine.getTransmission().toString(),
                            product.getMileage().toString(),
                            product.getLocation(),
                            product.getCreatedAt(),
                            thumbnailUrl,
                            product.getId(),
                            product.getStatus().toString()
                    );
                })
                .collect(Collectors.toList());
    }

    public RecommendResDto getRecommend() {
        Product newVehicle = productRepository.findTopByOrderByCreatedAtDesc();
        Product hotVehicle = productRepository.findTopByOrderByLikeCountDesc();

        ProductResDto newVehicleResDto = new ProductResDto();
        ProductResDto hotVehicleResDto = new ProductResDto();

        RecommendResDto recommendResDto = new RecommendResDto();
        recommendResDto.setHotVehicle(hotVehicleResDto);
        recommendResDto.setHotVehicle(newVehicleResDto);

        return recommendResDto;
    }
}
