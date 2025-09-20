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

    private void saveImagesWithoutThumbnail(Product product, List<String> images) {
        for (String imageUrl : images) {
            ProductImage image = ProductImage.builder()
                    .product(product)
                    .imageUrl(imageUrl)
                    .isThumbnail(false)
                    .build();
            productImageRepository.save(image);
        }
    }

    public Long updateProduct(Long productId, ProductUpdateReqDto dto) {
        Product product = productRepository.findById(productId).orElseThrow(
                () -> new BadRequestException(ErrorStatus.PRODUCT_NOT_FOUND.getMessage())
        );

        VehicleTypeName vehicleTypeName;
        try {
            vehicleTypeName = VehicleTypeName.valueOf(dto.getVehicleType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(ErrorStatus.INVALID_VEHICLE_TYPE.getMessage() + dto.getVehicleType());
        }
        Type type = typeRepository.findBytypeName(vehicleTypeName);

        Model model = modelRepository.findByTypeAndModelName(type, dto.getVehicleModel());

        Car car = carRepository.findByModel(model);

        // 남이 수정하는 경우 막아야 하나? 요청한 유저랑 기존 셀러랑 비교해서
        Member member = memberRepository.findById(1L)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.MEMBER_NOT_FOUND.getMessage()));

        updateProductStrings(product, car, dto);

        updateProductImages(product, dto.getMainProductImageUrl(), dto.getProductImageUrl());

        updateProductOptions(product, dto.getOption());

        return productId;
    }

    private void updateProductOptions(Product product, List<OptionDto> newOptions) {
        List<ProductOption> existingOption = productOptionRepository.findAllByProduct(product);
        List<String> existingOptionName = existingOption.stream().map(ProductOption::getCarOption)
                .map(CarOption::getName).toList();
        List<String> newOptionNames = newOptions.stream().map(OptionDto::getOptionName).toList();

        List<ProductOption> toDelete = existingOption.stream().filter(
                opt -> !newOptionNames.contains(opt.getCarOption().getName())
        ).toList();

        List<OptionDto> toAdd = newOptions.stream().filter(
                newOpt -> !existingOptionName.contains(newOpt.getOptionName())
        ).toList();

        productOptionRepository.deleteAll(toDelete);

        if (!toAdd.isEmpty())
            saveOptions(product, toAdd);
    }

    private void updateProductStrings(Product product, Car car, ProductUpdateReqDto dto) {
        product.setTitle(dto.getTitle());
        product.setDescription(dto.getDescription());
        product.setPlateHash(dto.getPlateHash());
        product.setLocation(dto.getLocation());
        product.setGeneration(dto.getGeneration());
        product.setCost(Integer.parseInt(dto.getPrice()));
        product.setMileage(Integer.parseInt(dto.getMileage()));
        product.setCar(car);
        productRepository.save(product);
    }

    private void updateProductImages(Product product, String thumbnailUrl, List<String> imageUrls) {
        // 이거 너무 구린데 아이디어 제안 받습니다.
        List<ProductImage> existingImage = productImageRepository.findAllByProduct(product);
        List<String> existingImageUrls = existingImage.stream().map(ProductImage::getImageUrl).toList();

        // 새로 들어온 것과 다르다는 건 지워졌다는 것. 지움
        List<ProductImage> toDelete = existingImage.stream()
                .filter(img -> !imageUrls.contains(img.getImageUrl()))
                .toList();
        productImageRepository.deleteAll(toDelete);

        // 기존에 없는 이미지 주소
        List<String> toAdd = imageUrls.stream()
                .filter(img -> !existingImageUrls.contains(img))
                .toList();

        if (!toAdd.isEmpty()) {
            if (toAdd.contains(thumbnailUrl))
                saveImages(product, thumbnailUrl, toAdd);
            else
                saveImagesWithoutThumbnail(product, toAdd);
        }
    }

    public void deleteProduct(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(
                () -> new BadRequestException(ErrorStatus.PRODUCT_NOT_FOUND.getMessage())
        );

        product.setIsDeleted(true);
        productRepository.save(product);
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
