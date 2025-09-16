package com.campick.server.common.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public enum SuccessStatus {

    /**
     * 200
     */
    SEND_REGISTER_SUCCESS(HttpStatus.OK,"회원가입 성공"),
    SEND_LOGIN_SUCCESS(HttpStatus.OK, "로그인 성공"),
    SEND_HEALTH_SUCCESS(HttpStatus.OK,"서버 상태 OK"),


    WITHDRAW_SUCCESS(HttpStatus.OK, "회원 탈퇴 성공"),
    SEND_CAR_LIST_SUCCESS(HttpStatus.OK, "매물 리스트 조회 성공"),
    SEND_EMAIL_VERIFICATION_CODE_SUCCESS(HttpStatus.OK, "이메일 전송 성공"),
    SEND_EMAIL_VERIFICATION_SUCCESS(HttpStatus.OK, "이메일 검증 성공"),

    /**
     * 201
     */

    SEND_CAR_CREATE_SUCCESS(HttpStatus.CREATED, "매물 생성 성공");


    private final HttpStatus httpStatus;
    private final String message;

    public int getStatusCode() {
        return this.httpStatus.value();
    }
}
