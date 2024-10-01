package revi1337.onsquad.common.domain;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class RequestEntity {

    @Column(name = "request_at", nullable = false)
    private LocalDateTime requestAt;

    protected RequestEntity(LocalDateTime time) {
        this.requestAt = time;
    }
}
