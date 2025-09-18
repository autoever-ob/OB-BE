package com.campick.server.api.product.controller;

import com.campick.server.api.product.dto.AllProductResponseDto;
import com.campick.server.api.product.dto.ProductCreateRequestDto;
import com.campick.server.api.product.dto.ProductCreateWithImageRequestDto;
import com.campick.server.api.product.service.ProductService;
import com.campick.server.common.response.ApiResponse;
import com.campick.server.common.response.SuccessStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final ObjectMapper objectMapper;

    @PostMapping
    public ResponseEntity<ApiResponse<Long>> createProduct(@RequestBody ProductCreateRequestDto dto) {
        Long productId = productService.createProduct(dto);
        return ApiResponse.success(SuccessStatus.SEND_PRODUCT_CREATE_SUCCESS, productId);

    }



    @GetMapping
    public ResponseEntity<ApiResponse<Page<AllProductResponseDto>>> getProducts() {
        //MOCK
        List<AllProductResponseDto> cars = new ArrayList<>();

        cars.add(new AllProductResponseDto("현대 아반떼 2021 1.6 가솔린", "1,150만 원", "42,000 km", "서울 강남구", LocalDateTime.now(), "https://example.com/images/car1.jpg", 1L, "AVAILABLE", true, 12));
        cars.add(new AllProductResponseDto("기아 K5 2020 2.0 가솔린", "1,500만 원", "68,000 km", "부산 해운대구", LocalDateTime.now().minusDays(1), "https://example.com/images/car2.jpg", 2L, "RESERVED", false, 5));
        cars.add(new AllProductResponseDto("BMW 320d 2019 디젤", "2,800만 원", "35,000 km", "서울 서초구", LocalDateTime.now().minusDays(2), "https://example.com/images/car3.jpg", 3L, "SOLD", false, 20));
        cars.add(new AllProductResponseDto("벤츠 C200 2020 가솔린", "3,200만 원", "27,000 km", "경기 성남시", LocalDateTime.now().minusHours(5), "https://example.com/images/car4.jpg", 4L, "AVAILABLE", true, 8));
        cars.add(new AllProductResponseDto("아우디 A4 2018 디젤", "2,500만 원", "50,000 km", "서울 송파구", LocalDateTime.now().minusDays(3), "https://example.com/images/car5.jpg", 5L, "AVAILABLE", false, 15));
        cars.add(new AllProductResponseDto("현대 쏘나타 2019 2.0 가솔린", "1,900만 원", "60,000 km", "부산 남구", LocalDateTime.now().minusDays(2), "https://example.com/images/car6.jpg", 6L, "AVAILABLE", true, 7));
        cars.add(new AllProductResponseDto("기아 스포티지 2020 1.6 디젤", "2,100만 원", "45,000 km", "대구 수성구", LocalDateTime.now().minusDays(4), "https://example.com/images/car7.jpg", 7L, "RESERVED", false, 9));
        cars.add(new AllProductResponseDto("쌍용 티볼리 2018 1.6 가솔린", "1,200만 원", "55,000 km", "경기 고양시", LocalDateTime.now().minusDays(5), "https://example.com/images/car8.jpg", 8L, "SOLD", false, 4));
        cars.add(new AllProductResponseDto("쉐보레 말리부 2019 2.0 가솔린", "1,800만 원", "48,000 km", "서울 강동구", LocalDateTime.now().minusHours(10), "https://example.com/images/car9.jpg", 9L, "AVAILABLE", true, 11));
        cars.add(new AllProductResponseDto("르노삼성 QM6 2020 2.0 가솔린", "2,300만 원", "38,000 km", "인천 남동구", LocalDateTime.now().minusDays(1), "https://example.com/images/car10.jpg", 10L, "AVAILABLE", false, 6));

        cars.add(new AllProductResponseDto("현대 투싼 2021 1.6 가솔린", "2,000만 원", "25,000 km", "서울 마포구", LocalDateTime.now().minusHours(2), "https://example.com/images/car11.jpg", 11L, "AVAILABLE", true, 13));
        cars.add(new AllProductResponseDto("기아 셀토스 2020 1.6 디젤", "1,700만 원", "40,000 km", "부산 해운대구", LocalDateTime.now().minusDays(3), "https://example.com/images/car12.jpg", 12L, "RESERVED", false, 8));
        cars.add(new AllProductResponseDto("BMW X3 2019 디젤", "4,200만 원", "30,000 km", "서울 송파구", LocalDateTime.now().minusDays(7), "https://example.com/images/car13.jpg", 13L, "SOLD", false, 22));
        cars.add(new AllProductResponseDto("벤츠 E200 2018 가솔린", "3,500만 원", "45,000 km", "경기 수원시", LocalDateTime.now().minusDays(6), "https://example.com/images/car14.jpg", 14L, "AVAILABLE", true, 10));
        cars.add(new AllProductResponseDto("아우디 A6 2020 디젤", "5,000만 원", "20,000 km", "서울 강남구", LocalDateTime.now().minusDays(2), "https://example.com/images/car15.jpg", 15L, "AVAILABLE", true, 18));
        cars.add(new AllProductResponseDto("현대 산타페 2019 2.2 디젤", "3,000만 원", "35,000 km", "부산 북구", LocalDateTime.now().minusDays(3), "https://example.com/images/car16.jpg", 16L, "RESERVED", false, 7));
        cars.add(new AllProductResponseDto("기아 모하비 2020 3.0 디젤", "4,500만 원", "28,000 km", "경기 용인시", LocalDateTime.now().minusDays(1), "https://example.com/images/car17.jpg", 17L, "AVAILABLE", true, 12));
        cars.add(new AllProductResponseDto("쉐보레 트랙스 2018 1.4 가솔린", "1,050만 원", "60,000 km", "대전 유성구", LocalDateTime.now().minusDays(4), "https://example.com/images/car18.jpg", 18L, "SOLD", false, 5));
        cars.add(new AllProductResponseDto("쌍용 렉스턴 2019 2.2 디젤", "3,800만 원", "32,000 km", "서울 마포구", LocalDateTime.now().minusDays(5), "https://example.com/images/car19.jpg", 19L, "AVAILABLE", true, 9));
        cars.add(new AllProductResponseDto("르노삼성 SM6 2020 1.6 가솔린", "2,200만 원", "42,000 km", "인천 연수구", LocalDateTime.now().minusDays(2), "https://example.com/images/car20.jpg", 20L, "RESERVED", false, 6));

        cars.add(new AllProductResponseDto("현대 코나 2021 1.6 가솔린", "1,800만 원", "22,000 km", "서울 동작구", LocalDateTime.now().minusHours(3), "https://example.com/images/car21.jpg", 21L, "AVAILABLE", true, 14));
        cars.add(new AllProductResponseDto("기아 니로 2020 1.6 하이브리드", "2,100만 원", "33,000 km", "부산 사하구", LocalDateTime.now().minusDays(2), "https://example.com/images/car22.jpg", 22L, "AVAILABLE", true, 11));
        cars.add(new AllProductResponseDto("BMW 520d 2019 디젤", "5,200만 원", "25,000 km", "서울 서초구", LocalDateTime.now().minusDays(5), "https://example.com/images/car23.jpg", 23L, "SOLD", false, 25));
        cars.add(new AllProductResponseDto("벤츠 GLC 2020 가솔린", "6,500만 원", "15,000 km", "경기 고양시", LocalDateTime.now().minusDays(1), "https://example.com/images/car24.jpg", 24L, "AVAILABLE", true, 20));
        cars.add(new AllProductResponseDto("아우디 Q5 2018 디젤", "4,000만 원", "40,000 km", "서울 강북구", LocalDateTime.now().minusDays(3), "https://example.com/images/car25.jpg", 25L, "RESERVED", false, 8));
        cars.add(new AllProductResponseDto("현대 팰리세이드 2021 2.2 디젤", "4,300만 원", "18,000 km", "서울 송파구", LocalDateTime.now().minusDays(2), "https://example.com/images/car26.jpg", 26L, "AVAILABLE", true, 15));

        return ApiResponse.success(SuccessStatus.SEND_PRODUCT_LIST_SUCCESS, new PageImpl<>(cars, PageRequest.of(0, 30), cars.size()));
//        List<AllProductResponseDto> productsList = productService.findAll();
//        return ApiResponse.success(SuccessStatus.SEND_PRODUCT_LIST_SUCCESS, productsList);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<CarDetailMock.CarDetailResponseDto>> getCarDetail() {
        return ApiResponse.success(SuccessStatus.SEND_PRODUCT_DETAIL_SUCCESS, CarDetailMock.getCarDetail());
    }
}
