package com.campick.server.api.product.service;

import com.campick.server.api.car.entity.Car;
import com.campick.server.api.car.repository.CarRepository;
import com.campick.server.api.member.entity.Member;
import com.campick.server.api.member.repository.MemberRepository;
import com.campick.server.api.model.entity.Model;
import com.campick.server.api.model.repository.ModelRepository;
import com.campick.server.api.option.entity.CarOption;
import com.campick.server.api.option.entity.ProductOption;
import com.campick.server.api.option.repository.CarOptionRepository;
import com.campick.server.api.option.repository.ProductOptionRepository;
import com.campick.server.api.product.dto.AllProductResponseDto;
import com.campick.server.api.product.dto.ProductCreateRequestDto;
import com.campick.server.api.product.dto.ProductCreateWithImageRequestDto;
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
import com.campick.server.common.storage.FirebaseStorageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
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

    @Transactional
    public Long createProduct(ProductCreateRequestDto dto) {
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

        Product product = Product.builder()
                .member(member)
                .car(car)
                .title(dto.getTitle())
                .cost(Integer.parseInt(dto.getPrice()))
                .mileage(Integer.parseInt(dto.getMileage()))
                .description(dto.getDescription())
                .plateHash(dto.getPlateHash())
                .location(dto.getLocation())
                .type(ProductType.SELLING) // 딜러/유저 구분 필요
                .status(ProductStatus.AVAILABLE)
                .isDeleted(false)
                .build();
        productRepository.save(product);

        // 이미지 저장
        for (String imageUrl : dto.getProductImageUrl()) {
            ProductImage image;

            if (Objects.equals(imageUrl, dto.getMainProductImageUrl())) {
                image = ProductImage.builder()
                        .product(product)
                        .imageUrl(imageUrl)
                        .isThumbnail(true)
                        .build();
            } else {
                image = ProductImage.builder()
                        .product(product)
                        .imageUrl(imageUrl)
                        .isThumbnail(false)
                        .build();
            }
            productImageRepository.save(image);
        }


        // 옵션 저장
        for (ProductCreateRequestDto.OptionDTO optionDto : dto.getOption()) {
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

        return product.getId();
    }

//    @Transactional
//    public List<AllProductResponseDto> findAll() {
//        List<Product> products = productRepository.findAll();
//
//        return products.stream()
//                .map(product -> {
//                    String thumbnailUrl = productImageRepository
//                            .findByProductAndIsThumbnailTrue(product)
//                            .getImageUrl();
//
//                    return new AllProductResponseDto(
//                            product.getTitle(),
//                            product.getCost().toString(),
//                            product.getMileage().toString(),
//                            product.getLocation(),
//                            product.getCreatedAt(),
//                            thumbnailUrl,
//                            product.getId().toString(),
//                            product.getStatus().toString()
//                    );
//                })
//                .collect(Collectors.toList());
//    }
  
    @Transactional
    public Long createProductWithImages(ProductCreateWithImageRequestDto dto, List<MultipartFile> images, MultipartFile mainImage) throws IOException {
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

        Product product = Product.builder()
                .member(member)
                .car(car)
                .title(dto.getTitle())
                .cost(Integer.parseInt(dto.getPrice()))
                .mileage(Integer.parseInt(dto.getMileage()))
                .description(dto.getDescription())
                .plateHash(dto.getPlateHash())
                .location(dto.getLocation())
                .type(ProductType.SELLING) // 딜러/유저 구분 필요
                .status(ProductStatus.AVAILABLE)
                .isDeleted(false)
                .build();
        productRepository.save(product);

        // 메인 이미지 저장
        String mainImageUrl = firebaseStorageService.uploadProductImage(product.getId(), mainImage);
        ProductImage mainProductImage = ProductImage.builder()
                .product(product)
                .imageUrl(mainImageUrl)
                .isThumbnail(true)
                .build();
        productImageRepository.save(mainProductImage);


        // 나머지 이미지 저장
        for (MultipartFile imageFile : images) {
            String imageUrl = firebaseStorageService.uploadProductImage(product.getId(), imageFile);
            ProductImage image = ProductImage.builder()
                    .product(product)
                    .imageUrl(imageUrl)
                    .isThumbnail(false)
                    .build();
            productImageRepository.save(image);
        }


        // 옵션 저장
        for (ProductCreateRequestDto.OptionDTO optionDto : dto.getOption()) {
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

        return product.getId();
    }

    @Transactional
    public List<AllProductResponseDto> findAll() {
        List<Product> products = productRepository.findAll();

        return products.stream()
                .map(product -> {
                    String thumbnailUrl = productImageRepository
                            .findByProductAndIsThumbnailTrue(product)
                            .getImageUrl();

                    return new AllProductResponseDto(
                            product.getTitle(),
                            product.getCost().toString(),
                            product.getMileage().toString(),
                            product.getLocation(),
                            product.getCreatedAt(),
                            thumbnailUrl,
                            product.getId().toString(),
                            product.getStatus().toString()
                    );
                })
                .collect(Collectors.toList());
    }
}
