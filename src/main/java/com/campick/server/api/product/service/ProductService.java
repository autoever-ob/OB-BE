package com.campick.server.api.product.service;

import com.campick.server.api.car.entity.Car;
import com.campick.server.api.car.repository.CarRepository;
import com.campick.server.api.engine.entity.Engine;
import com.campick.server.api.engine.entity.FuelType;
import com.campick.server.api.engine.repository.EngineRepository;
import com.campick.server.api.favorite.entity.Favorite;
import com.campick.server.api.favorite.repository.FavoriteRepository;
import com.campick.server.api.member.entity.Member;
import com.campick.server.api.member.entity.Role;
import com.campick.server.api.member.repository.MemberRepository;
import com.campick.server.api.member.service.CountService;
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
import com.campick.server.common.dto.PageResponseDto;
import com.campick.server.common.exception.BadRequestException;
import com.campick.server.common.exception.NotFoundException;
import com.campick.server.common.response.ErrorStatus;
import com.campick.server.common.storage.FirebaseStorageService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
    private final FavoriteRepository favoriteRepository;
    private final EntityManager em;
    private final CountService countService;

    @Transactional
    public Long createProduct(ProductCreateReqDto dto, Long memberId) {
        Type type = typeRepository.findByTypeName(dto.getVehicleType())
                .orElseThrow(()-> new NotFoundException(ErrorStatus.TYPE_NOT_FOUND.getMessage()));

        Model model = modelRepository.findByTypeAndModelName(type, dto.getVehicleModel())
                .orElseThrow(() -> new NotFoundException(ErrorStatus.MODEL_NOT_FOUND.getMessage()));

        // 수정해야함
        List<Car> cars = carRepository.findCarsByModel(model);

        if(cars.isEmpty()){
            throw new NotFoundException(ErrorStatus.CAR_NOT_FOUND.getMessage());
        }

        Car car = cars.get(0);


        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BadRequestException(ErrorStatus.MEMBER_NOT_FOUND.getMessage()));

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
                .type(member.getRole() == Role.USER ? ProductType.PENDING : ProductType.SELLING)
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

    public Long updateProduct(Long productId, ProductUpdateReqDto dto, Long memberId) {
        Product product = productRepository.findById(productId).orElseThrow(
                () -> new BadRequestException(ErrorStatus.PRODUCT_NOT_FOUND.getMessage())
        );

        Type type = typeRepository.findByTypeName(dto.getVehicleType())
                .orElseThrow(()-> new NotFoundException(ErrorStatus.TYPE_NOT_FOUND.getMessage()));;

        Model model = modelRepository.findByTypeAndModelName(type, dto.getVehicleModel())
                .orElseThrow(() -> new NotFoundException(ErrorStatus.MODEL_NOT_FOUND.getMessage()));

        Car car = carRepository.findByModel(model).orElseThrow(
                () -> new BadRequestException(ErrorStatus.CAR_NOT_FOUND.getMessage())
        );

        if (!product.getSeller().getId().equals(memberId)) {
            throw new BadRequestException(ErrorStatus.NOT_SELLER_EXCEPTION.getMessage());
        }

        updateProductStrings(product, car, dto);

        updateProductImages(product, dto.getMainProductImageUrl(), dto.getProductImageUrl());

        updateProductOptions(product, dto.getOption());

        return productId;
    }

    private void updateProductOptions(Product product, List<OptionDto> newOptions) {
        List<ProductOption> existingOption = productOptionRepository.findAllByProductWithOption(product);
        Set<String> existingOptionName = existingOption.stream()
                .map(po -> po.getCarOption().getName())
                .collect(Collectors.toSet());
        Set<String> newOptionNames = newOptions.stream()
                .map(OptionDto::getOptionName)
                .collect(Collectors.toSet());

        List<ProductOption> toDelete = existingOption.stream().filter(
                opt -> !newOptionNames.contains(opt.getCarOption().getName())
        ).toList();

        List<OptionDto> toAdd = newOptions.stream().filter(
                newOpt -> !existingOptionName.contains(newOpt.getOptionName())
        ).toList();

        if (!toDelete.isEmpty())
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

    public void deleteProduct(Long productId, Long memberId) {
        Product product = productRepository.findById(productId).orElseThrow(
                () -> new BadRequestException(ErrorStatus.PRODUCT_NOT_FOUND.getMessage())
        );

        if (!product.getSeller().getId().equals(memberId)) {
            throw new BadRequestException(ErrorStatus.NOT_SELLER_EXCEPTION.getMessage());
        }

        product.setIsDeleted(true);
        productRepository.save(product);
    }

    @Transactional
    public PageResponseDto<ProductResDto> getProducts(FilterReqDto filter, Pageable pageable, Long memberId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        // ===== Main Query =====
        CriteriaQuery<Product> query = cb.createQuery(Product.class);
        Root<Product> productRoot = query.from(Product.class);

        // Join
        Join<Product, Car> carJoin = productRoot.join("car", JoinType.INNER);
        Join<Car, Model> modelJoin = carJoin.join("model", JoinType.INNER);
        Join<Model, Type> typeJoin = modelJoin.join("type", JoinType.INNER);

        // 이미지 fetch
        productRoot.fetch("images", JoinType.LEFT);

        // ===== Predicates =====
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.notEqual(productRoot.get("status"), ProductStatus.SOLD));
        predicates.add(cb.isFalse(productRoot.get("isDeleted")));

        // 범위 필터
        predicates.add(cb.between(productRoot.get("cost"), filter.getCostFrom(), filter.getCostTo()));
        predicates.add(cb.between(productRoot.get("generation"), filter.getGenerationFrom(), filter.getGenerationTo()));
        predicates.add(cb.between(productRoot.get("mileage"), filter.getMileageFrom(), filter.getMileageTo()));

        // 옵션 필터
        if (filter.getOptions() != null && !filter.getOptions().isEmpty()) {
            Subquery<Long> optionSub = query.subquery(Long.class);
            Root<ProductOption> optionRoot = optionSub.from(ProductOption.class);
            optionSub.select(optionRoot.get("product").get("id"))
                    .where(
                            cb.equal(optionRoot.get("product"), productRoot),
                            optionRoot.get("carOption").get("name").in(filter.getOptions()),
                            cb.isTrue(optionRoot.get("isEquipped"))
                    );
            predicates.add(cb.exists(optionSub));
        }

        // 타입 필터
        if (filter.getTypes() != null && !filter.getTypes().isEmpty()) {
            List<String> typeNames = filter.getTypes().stream()
                    .map(VehicleTypeName::fromKorean) // 한글 → Enum
                    .map(Enum::name)
                    .toList();

            predicates.add(typeJoin.get("typeName").in(typeNames));
        }

        query.where(predicates.toArray(new Predicate[0]));

        // 정렬
        List<Order> orders = pageable.getSort().stream()
                .map(order -> order.isAscending() ?
                        cb.asc(productRoot.get(order.getProperty())) :
                        cb.desc(productRoot.get(order.getProperty())))
                .toList();
        query.orderBy(orders);

        // 페이징
        TypedQuery<Product> typedQuery = em.createQuery(query);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());
        List<Product> products = typedQuery.getResultList();

        // ===== Count Query =====
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Product> countRoot = countQuery.from(Product.class);
        Join<Product, Car> countCarJoin = countRoot.join("car", JoinType.INNER);
        Join<Car, Model> countModelJoin = countCarJoin.join("model", JoinType.INNER);
        Join<Model, Type> countTypeJoin = countModelJoin.join("type", JoinType.INNER);

        List<Predicate> countPredicates = new ArrayList<>();
        countPredicates.add(cb.notEqual(countRoot.get("status"), ProductStatus.SOLD));
        countPredicates.add(cb.between(countRoot.get("cost"), filter.getCostFrom(), filter.getCostTo()));
        countPredicates.add(cb.between(countRoot.get("generation"), filter.getGenerationFrom(), filter.getGenerationTo()));
        countPredicates.add(cb.between(countRoot.get("mileage"), filter.getMileageFrom(), filter.getMileageTo()));
        countPredicates.add(cb.isFalse(countRoot.get("isDeleted")));

        if (filter.getOptions() != null && !filter.getOptions().isEmpty()) {
            Subquery<Long> countOptionSub = countQuery.subquery(Long.class);
            Root<ProductOption> optionRoot = countOptionSub.from(ProductOption.class);
            countOptionSub.select(optionRoot.get("product").get("id"))
                    .where(
                            cb.equal(optionRoot.get("product"), countRoot),
                            optionRoot.get("carOption").get("name").in(filter.getOptions()),
                            cb.isTrue(optionRoot.get("isEquipped"))
                    );
            countPredicates.add(cb.exists(countOptionSub));
        }

        if (filter.getTypes() != null && !filter.getTypes().isEmpty()) {
            List<String> typeNames = filter.getTypes().stream()
                    .map(VehicleTypeName::fromKorean) // 한글 → Enum
                    .map(Enum::name)
                    .toList();

            countPredicates.add(countTypeJoin.get("typeName").in(typeNames));
        }

        countQuery.select(cb.count(countRoot))
                .where(countPredicates.toArray(new Predicate[0]));
        Long total = em.createQuery(countQuery).getSingleResult();

        // ===== User's liked products 조회 =====
        Set<Long> likedProductIds = getLikedProductIds(memberId);

        // ===== DTO 변환 =====
        List<ProductResDto> content = products.stream().map(p -> productToDto(p, likedProductIds)).toList();

        return new PageResponseDto<>(new PageImpl<>(content, pageable, total));
    }

    private Set<Long> getLikedProductIds(Long memberId) {
        return favoriteRepository.findByMemberId(memberId).stream()
                .map(fav -> fav.getProduct().getId())
                .collect(Collectors.toSet());
    }

    public ProductDetailResDto getProductDetail(Long memberId, Long productId) {
        Product product = productRepository.findDetailById(productId).orElseThrow(
                () -> new NotFoundException(ErrorStatus.PRODUCT_NOT_FOUND.getMessage())
        );

        Car car = product.getCar();
        Engine engine = car.getEngine();
        Model model = car.getModel();
        Type type = model.getType();

        List<ProductOption> productOptions = productOptionRepository.findAllByProduct(product);
        List<OptionResDto> optionResDto = productOptions.stream().map(
               po -> new OptionResDto(po.getCarOption().getName(), po.getIsEquipped())
        ).toList();

        Set<Long> likedProductIds = getLikedProductIds(memberId);

        SellerResDto sellerResDto = SellerResDto.builder()
                .nickName(product.getSeller().getNickname())
                .role(product.getSeller().getRole().toString())
                .userId(product.getSeller().getId())
                .sellingCount(countService.getMemberProductAvailableCount(product.getSeller().getId()))
                .completeCount(countService.getMemberProductSoldCount(product.getSeller().getId()))
                .build();

        // 딜러인 경우만 별점
        if (product.getSeller().getRole().equals(Role.DEALER))
            sellerResDto.setRating(product.getSeller().getDealer().getRating());

        List<ProductImage> productImages = productImageRepository.findAllByProduct(product);

        return ProductDetailResDto.builder()
                .title(product.getTitle())
                .description(product.getDescription())
                .price(product.getCost().toString())
                .mileage(product.getMileage().toString())
                .generation(product.getGeneration())
                .fuelType(engine.getFuelType().getKorean())
                .transmission(engine.getTransmission().getKorean())
                .vehicleType(type.getTypeName().getKorean())
                .vehicleModel(model.getModelName())
                .location(product.getLocation())
                .option(optionResDto)
                .user(sellerResDto)
                .plateHash(product.getPlateHash())
                .productImageUrl(productImages.stream().map(ProductImage::getImageUrl).toList())
                .productId(product.getId())
                .createdAt(product.getCreatedAt())
                .status(product.getStatus().toString())
                .isLiked(likedProductIds.contains(productId))
                .likeCount(product.getLikeCount())
                .build();
    }

    public RecommendResDto getRecommend(Long memberId) {
        Product newVehicle = productRepository.findTopByOrderByCreatedAtDesc();
        Product hotVehicle = productRepository.findTopByOrderByLikeCountDesc();

        Set<Long> likedProductIds = favoriteRepository.findByMemberId(memberId).stream()
                .map(fav -> fav.getProduct().getId())
                .collect(Collectors.toSet());

        RecommendResDto recommendResDto = new RecommendResDto();
        recommendResDto.setHotVehicle(productToDto(newVehicle, likedProductIds));
        recommendResDto.setNewVehicle(productToDto(hotVehicle, likedProductIds));

        return recommendResDto;
    }

    private ProductResDto productToDto(Product p, Set<Long> likedProductIds) {
        if (p == null) return null;

        String thumbnailUrl = p.getImages().stream()
                .filter(ProductImage::getIsThumbnail)
                .map(ProductImage::getImageUrl)
                .findFirst()
                .orElse(null);

        Car car = p.getCar();
        Engine engine = car.getEngine();

        return ProductResDto.builder()
                .title(p.getTitle())
                .price(p.getCost().toString())
                .generation(p.getGeneration())
                .fuelType(engine.getFuelType().getKorean())
                .transmission(engine.getTransmission().getKorean())
                .mileage(p.getMileage().toString())
                .vehicleType(car.getModel().getType().getTypeName().getKorean())
                .vehicleModel(car.getModel().getModelName())
                .location(p.getLocation())
                .createdAt(p.getCreatedAt())
                .thumbNail(thumbnailUrl)
                .productId(p.getId())
                .status(p.getStatus().toString())
                .isLiked(likedProductIds.contains(p.getId()))
                .likeCount(p.getLikeCount())
                .build();
    }

    public void likeToggle(Long productId, Long memberId) {
        Product product = productRepository.findById(productId).orElseThrow(
                () -> new NotFoundException(ErrorStatus.PRODUCT_NOT_FOUND.getMessage())
        );
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new NotFoundException(ErrorStatus.MEMBER_NOT_FOUND.getMessage())
        );

        Favorite favorite = favoriteRepository.findByMemberAndProduct(member, product);

        if (favorite != null) {
            favoriteRepository.delete(favorite);
            product.setLikeCount(product.getLikeCount() - 1);
            productRepository.save(product);
        } else {
            Favorite newFavorite = Favorite.builder()
                    .member(member)
                    .product(product)
                    .build();
            favoriteRepository.save(newFavorite);
            product.setLikeCount(product.getLikeCount() + 1);
            productRepository.save(product);
        }
    }

    public void updateProductStatus(StatusReqDto dto, Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new NotFoundException(ErrorStatus.MEMBER_NOT_FOUND.getMessage())
        );
        Product product = productRepository.findById(dto.getProductId()).orElseThrow(
                () -> new NotFoundException(ErrorStatus.PRODUCT_NOT_FOUND.getMessage())
        );

        if (member != product.getSeller())
            throw new BadRequestException(ErrorStatus.NOT_SELLER_EXCEPTION.getMessage());

        product.setStatus(ProductStatus.valueOf(dto.getStatus()));
        productRepository.save(product);
    }
}
