package revi1337.onsquad.member.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@ToString
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Address {

    @Column(name = "address")
    private String value;

    @Column(name = "address_detail")
    private String detail;

    public Address(String value, String detail) {
        this(value);
        this.detail = detail;
    }

    public Address(String value) {
        validate(value);
        this.value = value;
    }

    private void validate(String value) {
        if (value == null) {
            throw new NullPointerException("주소는 null 일 수 없습니다.");
        }
    }
}
