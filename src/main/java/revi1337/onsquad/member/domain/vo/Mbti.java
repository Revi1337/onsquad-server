package revi1337.onsquad.member.domain.vo;

import static revi1337.onsquad.member.error.MemberErrorCode.INVALID_MBTI;

import revi1337.onsquad.member.error.exception.MemberDomainException;

public enum Mbti {

    ISTJ,
    ISFJ,
    INFJ,
    INTJ,
    ISTP,
    ISFP,
    INFP,
    INTP,
    ESTP,
    ESFP,
    ENFP,
    ENTP,
    ESTJ,
    ESFJ,
    ENFJ,
    ENTJ;

    public static Mbti parse(String value) {
        try {
            return Mbti.valueOf(value);
        } catch (IllegalArgumentException e) {
            throw new MemberDomainException.InvalidMbti(INVALID_MBTI);
        }
    }
}
