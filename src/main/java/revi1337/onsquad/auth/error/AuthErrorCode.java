package revi1337.onsquad.auth.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import revi1337.onsquad.common.error.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {

    USERNAME_NOT_FOUND(401, "A001", "아이디 및 비밀번호가 일치하지 않음"),
    BAD_CREDENTIAL(401, "A002", "비밀번호가 일치하지 않음"),

    DUPLICATE_NICKNAME(401, "A003", "%s 닉네임은 이미 사용중입니다."),
    NON_AUTHENTICATE_EMAIL(401, "A004", "메일 인증이 되어있지 않습니다."),
    DUPLICATE_MEMBER(401, "A005", "이미 회원가입이 되어있는 사용자입니다.");

    private final int status;
    private final String code;
    private final String description;

}











//package revi1337.onsquad.auth.error;
//
//import lombok.Getter;
//import lombok.RequiredArgsConstructor;
//import revi1337.onsquad.common.error.ErrorCode;
//
//@Getter
//@RequiredArgsConstructor
//public enum AuthErrorCode implements ErrorCode {
//
//    USERNAME_NOT_FOUND(401, "A001", "아이디 및 비밀번호가 일치하지 않음"),
//    BAD_CREDENTIAL(401, "A002", "비밀번호가 일치하지 않음"),
//
//    INVALID_TOKEN_FORMAT(401, "A003", "토큰 포맷이 올바르지 않습니다."),
//    INVALID_TOKEN_SIGNATURE(401, "A004", "토큰 서명이 일치하지 않습니다."),
//    TOKEN_EXPIRED(401, "A005", "토큰이 만료되었습니다."),
//    EMPTY_TOKEN(401, "A006", "토큰이 필요한 API 입니다."),
//
//    DUPLICATE_NICKNAME(401, "A007", "%s 닉네임은 이미 사용중입니다."),
//    NON_AUTHENTICATE_EMAIL(401, "A008", "메일 인증이 되어있지 않습니다."),
//    DUPLICATE_MEMBER(401, "A009", "이미 회원가입이 되어있는 사용자입니다.");
//
//    private final int status;
//    private final String code;
//    private final String description;
//
//}
