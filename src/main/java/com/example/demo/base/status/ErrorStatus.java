package com.example.demo.base.status;

import com.example.demo.base.code.BaseErrorCode;
import com.example.demo.base.code.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

    // 공통 에러
    COMMON_INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_500", "서버 에러가 발생했습니다. 관리자에게 문의하세요."),
    COMMON_BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON_4001", "잘못된 요청입니다."),
    COMMON_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON_4002", "인증이 필요합니다."),

    // 유저 관련 에러
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "USER_4001", "해당 유저를 찾을 수 없습니다."),
    USER_ALREADY_WITHDRAWN(HttpStatus.BAD_REQUEST, "USER_4002", "이미 탈퇴한 유저입니다."),
    USER_ALREADY_LOGOUT(HttpStatus.BAD_REQUEST, "USER_4003", "이미 로그아웃한 유저입니다."),
    USER_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "USER_4004", "이미 존재하는 사용자입니다."),
    USER_INVALID_PROVIDER(HttpStatus.BAD_REQUEST, "USER_4005", "로그인 경로가 규칙에 맞지 않습니다."),
    USER_INVALID_PASSWORD_FORMAT(HttpStatus.BAD_REQUEST, "USER_4006", "비밀번호 설정 규칙에 맞지 않습니다."),
    USER_INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "USER_4007", "비밀번호가 잘못되었습니다."),

    // 토큰 관련 에러
    TOKEN_MISSING(HttpStatus.UNAUTHORIZED, "TOKEN_4001", "토큰이 누락되었습니다."),
    TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "TOKEN_4002", "해당 토큰을 찾을 수 없습니다."),
    TOKEN_INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "TOKEN_4003", "만료되거나 잘못된 엑세스 토큰입니다."),
    TOKEN_INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "TOKEN_4004", "만료되거나 잘못된 리프레시 토큰입니다."),

    // 소셜 로그인 관련 에러
    OAUTH_LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "OAUTH_4001", "OAuth 로그인에 실패했습니다."),
    OAUTH_PROCESSING_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "OAUTH_4002", "OAuth 로그인 처리에 실패했습니다."),

    // ChatGPT 관련 에러
    CHAT_GPT_API_CALL_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "CHAT_GPT_5001", "ChatGPT API 호출 실패. 관리자에게 문의해주세요."),
    FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "CHAT_GPT_4041", "파일을 찾을 수 없습니다."),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "CHAT_GPT_4001", "잘못된 요청입니다."),
    JSON_PARSE_ERROR(HttpStatus.BAD_REQUEST, "JSON_1001", "JSON 파싱 오류"),

    // 결제 관련 에러
    PAYMENT_REQUEST_FAILED(HttpStatus.BAD_REQUEST, "PAY_4001", "PAY API 요청 실패"),
    PAYMENT_REQUEST_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "PAY_4002", "PAY API 요청 중 오류 발생"),
    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "PAYMENT_4041", "결제 정보가 없습니다"),
    PAYMENT_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "PAYMENT_4003", "결제 정보 저장 실패")


    ;


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDTO getReason() {
        return ErrorReasonDTO.builder()
                .httpStatus(httpStatus)
                .code(code)
                .message(message)
                .isSuccess(false)
                .build();
    }

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return getReason();
    }

}