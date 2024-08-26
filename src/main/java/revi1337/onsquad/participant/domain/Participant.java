package revi1337.onsquad.participant.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Participant {

    @Column(name = "request_at", nullable = false)
    private LocalDateTime requestAt;

    protected Participant(LocalDateTime time) {
        this.requestAt = time;
    }

    public void updateRequestTimestamp(LocalDateTime time) {
        this.requestAt = time;
    }
}
