package com.campick.server.common.response;

import ch.qos.logback.classic.spi.ILoggingEvent;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)

public enum ErrorStatus {

    /**
     * 400 BAD_REQUEST
     */
    VALIDATION_REQUEST_MISSING_EXCEPTION(HttpStatus.BAD_REQUEST, "요청 값이 입력되지 않았습니다."),
    ALREADY_REGISTERED_ACCOUNT_EXCEPTION(HttpStatus.BAD_REQUEST, "이미 회원가입된 이메일입니다."),
    NOT_MATCHED_LOGIN_USER_EXCEPTION(HttpStatus.BAD_REQUEST, "아이디 또는 비밀번호가 일치하지 않습니다."),
    PASSWORD_MISMATCH_EXCEPTION(HttpStatus.BAD_REQUEST, "비밀번호와 비밀번호 확인 값이 일치하지 않습니다."),
    INVALID_PASSWORD_EXCEPTION(HttpStatus.BAD_REQUEST, "비밀번호가 올바르지 않습니다."),
    VALIDATION_EMAIL_FORMAT_EXCEPTION(HttpStatus.BAD_REQUEST, "이메일 형식이 올바르지 않습니다."),
    WRONG_EMAIL_VERIFICATION_CODE_EXCEPTION(HttpStatus.BAD_REQUEST, "이메일 인증 코드가 일치하지 않습니다."),
    TOO_MANY_FILES(HttpStatus.BAD_REQUEST, "이미지는 최대 5 장 업로드 가능합니다"),
    INVALID_VEHICLE_TYPE(HttpStatus.BAD_REQUEST, "존재하지 않는 캠핑카 타입입니다."),
    PASSWORD_RESET_INVALID_CODE(HttpStatus.BAD_REQUEST, "비밀번호 재설정 코드가 유효하지 않습니다."),
    PASSWORD_RESET_EXPIRED_CODE(HttpStatus.BAD_REQUEST, "비밀번호 재설정 코드가 만료되었습니다."),
    PASSWORD_RESET_CODE_ALREADY_USED(HttpStatus.BAD_REQUEST, "이미 사용된 비밀번호 재설정 코드입니다."),
    PASSWORD_RESET_ALREADY_USED(HttpStatus.BAD_REQUEST, "이전 비밀번호와 다른 비밀번호로 설정해주세요."),
    NOT_SELLER_EXCEPTION(HttpStatus.BAD_REQUEST, "이 매물의 판매자가 아닙니다"),


    /**
     * 401 UNAUTHORIZED
     */
    USER_UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"인증되지 않은 사용자입니다."),
    EXPIRED_TOKEN_EXCEPTION(HttpStatus.UNAUTHORIZED,"만료된 토큰입니다."),
    UNAUTHORIZED_EMAIL_VERIFICATION_CODE_EXCEPTION(HttpStatus.UNAUTHORIZED, "만료된 이메일 인증코드 입니다"),
    MALFORMED_ACCESS_TOKEN_EXCEPTION(HttpStatus.UNAUTHORIZED, "유효하지 않은 액세스 토큰입니다."),
    MALFORMED_REFRESH_TOKEN_EXCEPTION(HttpStatus.UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED,  "리프래시 토큰이 존재하지 않습니다."),
    REFRESH_TOKEN_NOT_EQUAL(HttpStatus.UNAUTHORIZED,  "리프래시 토큰이 일치하지 않습니다."),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "리프래시 토큰이 만료되었습니다."),

    /**
     * 403 FORBIDDEN
     */
    MEMBER_BLOCKED(HttpStatus.FORBIDDEN, "차단된 회원입니다."),

    /**
     * 404 NOT_FOUND
     */
    NOT_LOGIN_EXCEPTION(HttpStatus.NOT_FOUND,"로그인이 필요합니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    CHAT_NOT_FOUND(HttpStatus.NOT_FOUND, "채팅방을 찾을 수 없습니다"),
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "매물을 찾을 수 없습니다."),
    NOT_REGISTER_USER_EXCEPTION(HttpStatus.NOT_FOUND, "존재하지 않는 사용자 입니다."),
    EMPTY_FILE_EXCEPTION(HttpStatus.NOT_FOUND, "업로드할 파일이 없습니다.");


    /**
     * 500 SERVER_ERROR
     */

    private final HttpStatus httpStatus;
    private final String message;

    public int getStatusCode() {
        return this.httpStatus.value();
    }
}