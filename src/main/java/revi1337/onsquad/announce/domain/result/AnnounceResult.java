package revi1337.onsquad.announce.domain.result;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import revi1337.onsquad.announce.domain.entity.vo.Title;
import revi1337.onsquad.crew_member.domain.result.SimpleCrewMemberResult;

public record AnnounceResult(
        Long id,
        Title title,
        String content,
        LocalDateTime createdAt,
        boolean fixed,
        LocalDateTime fixedAt,
        SimpleCrewMemberResult writer
) {

    @QueryProjection
    public AnnounceResult(Long id, Title title, String content, LocalDateTime createdAt,
                          boolean fixed, LocalDateTime fixedAt, SimpleCrewMemberResult writer) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.fixed = fixed;
        this.fixedAt = fixedAt;
        this.writer = writer;
    }
}
