package revi1337.onsquad.member.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Address {

    private static final String DEFAULT_VALUE = "공백";
    private static final String DEFAULT_VALUE_DETAIL = "공백";

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

    public Address update(String value, String detail) {
        return new Address(value, detail);
    }
}
