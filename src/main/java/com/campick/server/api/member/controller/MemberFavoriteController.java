package com.campick.server.api.member.controller;


import com.campick.server.common.response.ApiResponse;
import com.campick.server.common.response.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/favorite")
@Tag(name="member-favorite", description = "멤버의 좋아요 관련 API 입니다.")
@RequiredArgsConstructor
@RestController
public class MemberFavoriteController {

}
