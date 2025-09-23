package com.campick.server.api.category.controller;

import com.campick.server.api.category.dto.ModelListResponseDto;
import com.campick.server.api.category.dto.TypeListResponseDto;
import com.campick.server.api.category.service.CategoryService;
import com.campick.server.common.response.ApiResponse;
import com.campick.server.common.response.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/category")
@Tag(name = "category", description = "종류와 모델을 관리합니다.")
@RequiredArgsConstructor
@RestController
public class CategoryController {
    private final CategoryService categoryService;

    @Operation(summary = "타입 목록 API", description = "존재하는 모든 타입 목록을 전부 불러옵니다.")
    @ApiResponses(
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "타입 목록 불러오기 성공")
    )
    @GetMapping("/type")
    public ResponseEntity<ApiResponse<TypeListResponseDto>> getTypeList(){
        TypeListResponseDto responseDto = categoryService.getTypeList();
        return ApiResponse.success(SuccessStatus.SEND_TYPE_LIST_SUCCESS, responseDto);
    }


    @Operation(summary = "모델 목록 API", description = "존재하는 모든 타입 목록을 전부 불러옵니다.")
    @ApiResponses(
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "모델 목록 불러오기 성공")
    )
    @GetMapping("/type/{typeName}")
    public ResponseEntity<ApiResponse<ModelListResponseDto>> getModelList(@PathVariable String typeName){
        ModelListResponseDto responseDto = categoryService.getModelList(typeName);
        return ApiResponse.success(SuccessStatus.SEND_MODEL_LIST_SUCCESS, responseDto);
    }
}
