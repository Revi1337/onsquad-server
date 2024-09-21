package revi1337.onsquad.member.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Address {

    @Column(name = "address", nullable = false)
    private String value;

    @Column(name = "address_detail", nullable = false)
    private String detail;

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
