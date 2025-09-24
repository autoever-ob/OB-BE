package com.campick.server.api.chat.dto;

import com.campick.server.api.product.dto.ProductImageResDto;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatImageResDto {
    private String chatImageUrl;
}