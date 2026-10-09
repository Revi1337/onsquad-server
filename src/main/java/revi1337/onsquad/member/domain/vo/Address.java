package revi1337.onsquad.member.domain.vo;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = PROTECTED)
@Embeddable
public class Address {

    private static final String DEFAULT_VALUE = "";
    private static final String DEFAULT_VALUE_DETAIL = "";

    @Column(name = "address", nullable = false)
    private String value;

    @Column(name = "address_detail", nullable = false)
    private String detail;

    public static Address defaultValue() {
        return new Address(DEFAULT_VALUE, DEFAULT_VALUE_DETAIL);
    }

    public Address(String value, String detail) {
        validate(value, detail);
        this.value = value;
        this.detail = detail;
    }

    private void validate(String value, String detail) {
        if (value == null) {
            throw new IllegalArgumentException("주소는 null 일 수 없습니다.");
        }

        if (detail == null) {
            throw new IllegalArgumentException("상세 주소는 null 일 수 없습니다.");
        }
    }
}
