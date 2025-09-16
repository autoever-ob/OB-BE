package com.campick.server.api.car.service;

import com.campick.server.api.car.dto.CarCreateRequestDTO;
import com.campick.server.api.car.dto.CarResponseDTO;
import com.campick.server.api.car.entity.Car;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
@Slf4j
public class CarService {

    private static final List<Car> MOCK_STORE = new ArrayList<>();
    private static final AtomicLong ID_SEQ = new AtomicLong(1);

    static {
        MOCK_STORE.add(Car.builder().id(ID_SEQ.getAndIncrement()).build());
        MOCK_STORE.add(Car.builder().id(ID_SEQ.getAndIncrement()).build());
        MOCK_STORE.add(Car.builder().id(ID_SEQ.getAndIncrement()).build());
    }

    public List<CarResponseDTO> listCars() {
        return MOCK_STORE.stream()
                .map(c -> new CarResponseDTO(c.getId(), "CAR"))
                .toList();
    }

    public CarResponseDTO createCar(CarCreateRequestDTO request) {
        String name = request.getCarName();
        if (name == null || name.isBlank()) {
            name = "MockCar-" + ID_SEQ.get();
        }
        Car car = Car.builder()
                .id(ID_SEQ.getAndIncrement())
                .build();
        MOCK_STORE.add(car);
        return new CarResponseDTO(car.getId(), name);
    }
}
