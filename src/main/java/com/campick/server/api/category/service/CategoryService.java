package com.campick.server.api.category.service;

import com.campick.server.api.category.dto.ModelListResponseDto;
import com.campick.server.api.category.dto.TypeListResponseDto;
import com.campick.server.api.model.entity.Model;
import com.campick.server.api.model.repository.ModelRepository;
import com.campick.server.api.type.entity.Type;
import com.campick.server.api.type.entity.VehicleTypeName;
import com.campick.server.api.type.repository.TypeRepository;
import com.campick.server.common.exception.NotFoundException;
import com.campick.server.common.response.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final TypeRepository typeRepository;
    private final ModelRepository modelRepository;

    public TypeListResponseDto getTypeList() {
        List<String> types = typeRepository.findAll().stream()
                .map(type -> type.getTypeName().getKorean())
                .toList();

        return new TypeListResponseDto(types);
    }

    public ModelListResponseDto getModelList(String typeName) {
        Type typeEntity = typeRepository.getTypeByTypeName(VehicleTypeName.fromKorean(typeName))
                .orElseThrow(() -> new NotFoundException(ErrorStatus.TYPE_NOT_FOUND.getMessage()));

        List<Model> models = modelRepository.getModelsByType(typeEntity);

        return new ModelListResponseDto(models.stream().map(Model::getModelName).toList());
    }
}
