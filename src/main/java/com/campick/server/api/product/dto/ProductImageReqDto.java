package com.campick.server.api.product.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter @Setter
@NoArgsConstructor
public class ProductImageReqDto {
    private Long productId;
    private MultipartFile file;
}
