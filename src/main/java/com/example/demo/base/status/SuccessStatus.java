package com.example.demo.base.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import com.example.demo.base.code.BaseCode;
import com.example.demo.base.code.ReasonDTO;

@Getter
@AllArgsConstructor
public enum SuccessStatus implements BaseCode {

    // 공통 성공
    _OK(HttpStatus.OK, "COMMON_200", "성공입니다."),

    // 회원 관련 성공
    USER_SIGNUP_SUCCESS(HttpStatus.OK, "USER_2001", "회원가입이 성공적으로 완료되었습니다."),
    USER_WITHDRAWN_SUCCESS(HttpStatus.OK, "USER_2002", "성공적으로 탈퇴하였습니다. 일정 시간 내로 재로그인 하지 않을시, 모든 정보가 삭제됩니다."),
    USER_LOGIN_SUCCESS(HttpStatus.OK, "USER_2003", "로그인에 성공하였습니다."),
    USER_LOGOUT_SUCCESS(HttpStatus.OK, "USER_2004", "성공적으로 로그아웃하였습니다."),
    USER_INFO_RETRIEVED(HttpStatus.OK, "USER_2005", "유저 정보가 성공적으로 조회되었습니다."),

    // 토큰 관련 성공
    TOKEN_REFRESH_SUCCESS(HttpStatus.OK, "TOKEN_2001", "리프레시 토큰을 통한 토큰 갱신에 성공하였습니다."),

    // ChatGPT 관련 성공
    CHAT_SUCCESS(HttpStatus.OK, "CHAT_2001", "gpt 호출이 성공하였습니다."),

    // 결제 관련 성공
    PAYMENT_READY_SUCCESS(HttpStatus.OK, "PAYMENT_2001", "결제 준비가 성공적으로 완료되었습니다."),
    PAYMENT_APPROVE_SUCCESS(HttpStatus.OK, "PAYMENT_2002", "결제 승인이 성공적으로 완료되었습니다."),

    // 요정 관련 성공 응답
    FAIRY_CREATED(HttpStatus.CREATED, "FAIRY_2010", "요정이 성공적으로 생성되었습니다."),
    FAIRY_RETRIEVED(HttpStatus.OK, "FAIRY_2000", "요정이 성공적으로 조회되었습니다."),
    FAIRY_LIST_RETRIEVED(HttpStatus.OK, "FAIRY_2001", "요정 목록이 성공적으로 조회되었습니다."),

    // 동화 관련 성공 응답
    FAIRY_TALE_CREATED(HttpStatus.CREATED, "FAIRY_TALE_2010", "동화가 성공적으로 생성되었습니다."),
    FAIRY_TALE_LIST_RETRIEVED(HttpStatus.OK, "FAIRY_TALE_2000", "동화 목록이 성공적으로 조회되었습니다."),
    FAIRY_TALE_UPDATED(HttpStatus.OK, "FAIRY_TALE_2001", "동화가 성공적으로 수정되었습니다."),
    FAIRY_TALE_CONTENT_RETRIEVED(HttpStatus.OK, "FAIRY_TALE_2002", "동화 내용이 성공적으로 조회되었습니다.")


    ;


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ReasonDTO getReason() {
        return ReasonDTO.builder()
                .httpStatus(httpStatus)
                .code(code)
                .message(message)
                .isSuccess(true)
                .build();
    }

    @Override
    public ReasonDTO getReasonHttpStatus() {
        return getReason();
    }
}