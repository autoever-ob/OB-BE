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
    DELETE_MEMBER_SUCCESS(HttpStatus.OK, "회원 탈퇴 성공" ),

    SEND_MY_PROFILE_SUCCESS(HttpStatus.OK, "내 정보 조회 성공"),
    SEND_MY_SOLD_SUCCESS(HttpStatus.OK, "내가 판 매물 조회 성공"),
    SEND_MY_BOUGHT_SUCCESS(HttpStatus.OK, "내가 산 매물 조회 성공"),
    SEND_MY_SELL_SUCCESS(HttpStatus.OK, "내가 팔고 있는 매물 조회 성공"),
    SEND_REVIEW_SUCCESS(HttpStatus.OK, "리뷰 조회 성공"),

    LOGOUT_SUCCESS(HttpStatus.OK,"로그아웃 성공"),
    CHECK_EMAIL_DUPLICATE(HttpStatus.OK, "이메일 중복 검사 결과입니다."),
    CHECK_NICKNAME_DUPLICATE(HttpStatus.OK, "닉네임 중복 검사 결과입니다."),
    CHECK_PASSWORD_VALIDATION(HttpStatus.OK, "비밀번호 확인 검사 결과입니다."),
    UPDATE_PASSWORD_SUCCESS(HttpStatus.OK, "비밀번호 변경 성공"),
    UPDATE_PROFILE_IMAGE_SUCCESS(HttpStatus.OK, "프로필 이미지 변경 성공" ),
    REISSUE_SUCCESS(HttpStatus.OK, "토큰 재발행 성공" ),
    SEND_FOLLOWING_LIST_SUCCESS(HttpStatus.OK, "회원 정보 조회 성공"),
    PASSWORD_RESET_LINK_SENT(HttpStatus.OK, "이메일 초기화 코드 전송 성공"),
    PASSWORD_RESET_CODE_VERIFIED(HttpStatus.OK, "비밀번호 초기화 코드 검증 성공"),
    PASSWORD_RESET_SUCCESS(HttpStatus.OK, "비밀번호 초기화 성공"),
    UPDATE_MEMBER_INFO_SUCCESS(HttpStatus.OK, "멤버 정보 수정 성공"),


    WITHDRAW_SUCCESS(HttpStatus.OK, "회원 탈퇴 성공"),
    SEND_CAR_LIST_SUCCESS(HttpStatus.OK, "자동차 리스트 조회 성공"),
    SEND_RECOMMEND_SUCCESS(HttpStatus.OK, "추천 매물 조회 성공"),
    SEND_INFO_LIST_SUCCESS(HttpStatus.OK, "타입, 모델, 옵션 조회 성공"),
    SEND_PRODUCT_LIST_SUCCESS(HttpStatus.OK, "매물 조회 성공"),
    SEND_PRODUCT_DETAIL_SUCCESS(HttpStatus.OK, "매물 상세 조회 성공"),
    SEND_PRODUCT_LIKE_SUCCESS(HttpStatus.OK, "하트 누르기 성공(좋아요, 취소)"),
    SEND_PRODUCT_STATUS_UPDATED(HttpStatus.OK, "매물 상태 변경 성공"),
    SEND_PRODUCT_UPDATE_SUCCESS(HttpStatus.OK, "매물 수정 성공"),
    SEND_PRODUCT_DELETE_SUCCESS(HttpStatus.OK, "매물 삭제 성공"),
    SEND_CHAT_CREATED(HttpStatus.OK, "채팅방 생성 성공"),
    SEND_EMAIL_VERIFICATION_CODE_SUCCESS(HttpStatus.OK, "이메일 전송 성공"),
    SEND_EMAIL_VERIFICATION_SUCCESS(HttpStatus.OK, "이메일 검증 성공"),
    SEND_MEMBER_PRODUCTS_AVAILABLE_SUCCESS(HttpStatus.OK, "내가 팔거나 예약 중인 매물 목록 전송 성공."),
    SEND_MEMBER_PRODUCTS_ALL_SUCCESS(HttpStatus.OK, "{memberId} 별 모든 매물 조회 성공"),
    SEND_MEMBER_SOLD_PRODUCTS_SUCCESS(HttpStatus.OK, "{memberId} 별 판 매물 조회 성공"),
    SEND_MEMBER_BOUGHT_PRODUCTS_SUCCESS(HttpStatus.OK, "{memberId} 별 산 매물 조회 성공"),
    SEND_MEMBER_ALL_PRODUCTS_COUNT_SUCCESS(HttpStatus.OK, "{memberId} 별 모든 매물 개수 전송 성공"),
    SEND_MEMBER_AVAILABLE_PRODUCTS_COUNT_SUCCESS(HttpStatus.OK, "{memberId} 별 판매중 또는 예약중인 매물 개수 전송 성공"),
    SEND_MEMBER_SOLD_PRODUCTS_COUNT_SUCCESS(HttpStatus.OK, "{memberId} 별 판매된 매물 개수 전송 성공"),
     SEND_MEMBER_FAVORITE_PRODUCTS_SUCCESS(HttpStatus.OK, "{memberId} 별 좋아요 누른 매물 목록 전송 성공"),
    SEND_MEMBER_FAVORITE_PRODUCTS_COUNT_SUCCESS(HttpStatus.OK, "{memberId} 별 좋아요 누른 매물 개수 전송 성공"),

    UPLOAD_PRODUCT_IMAGE_SUCCESS(HttpStatus.OK, "매물 사진 등록 성공"),

    SEND_LOAD_CHATROOM(HttpStatus.OK, "채팅 상세 조회 성공"),
    SEND_MY_CHATROOMS(HttpStatus.OK, "내 채팅방 조회 성공"),
    SEND_TOTAL_UNREAD_MSG(HttpStatus.OK, "총 안 읽은 메시지 수 조회 성공"),
    COMPLETE_CHAT(HttpStatus.OK, "채팅방 종료 완료"),


    /**
     * 201
     */

    SEND_CAR_CREATE_SUCCESS(HttpStatus.CREATED, "자동차 생성 성공"),
    SEND_PRODUCT_CREATE_SUCCESS(HttpStatus.CREATED, "매물 생성 성공");

    private final HttpStatus httpStatus;
    private final String message;

    public int getStatusCode() {
        return this.httpStatus.value();
    }
}
