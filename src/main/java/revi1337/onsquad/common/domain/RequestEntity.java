package revi1337.onsquad.common.domain;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@MappedSuperclass
@NoArgsConstructor(access = PROTECTED)
public abstract class RequestEntity {

    @Column(name = "request_at", nullable = false)
    private LocalDateTime requestAt;

    protected RequestEntity(LocalDateTime time) {
        this.requestAt = time;
    }

    public void updateRequestAt(LocalDateTime now) {
        this.requestAt = now;
    }
}
