package com.campick.server.api.car.controller;


import com.campick.server.api.car.dto.CarCreateRequestDTO;
import com.campick.server.api.car.dto.CarResponseDTO;
import com.campick.server.api.car.service.CarService;
import com.campick.server.common.response.ApiResponse;
import com.campick.server.common.response.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/car")
@Tag(name="Car", description = "자동차 관련 API 입니다.")
public class CarController {

    private final CarService carService;

    @Operation(summary = "자동차 리스트 API", description = "자동차 리스트를 반환합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<CarResponseDTO>>> getCars() {
        List<CarResponseDTO> list = carService.listCars();
        return ApiResponse.success(SuccessStatus.SEND_CAR_LIST_SUCCESS, list);
    }

    @Operation(summary = "자동차 생성 API", description = "자동차를 생성합니다. (Mock 데이터)")
    @PostMapping
    public ResponseEntity<ApiResponse<CarResponseDTO>> createCar(@RequestBody CarCreateRequestDTO request) {
        CarResponseDTO created = carService.createCar(request);
        return ApiResponse.success(SuccessStatus.SEND_CAR_CREATE_SUCCESS, created);
    }
}
