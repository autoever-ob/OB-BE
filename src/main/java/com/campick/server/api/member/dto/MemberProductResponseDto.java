package com.campick.server.api.member.dto;

import com.google.type.DateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberProductResponseDto {
    private Long id;
    private Integer cost;
    private Integer mileage;
    private String location;
    private DateTime createAt;


}
