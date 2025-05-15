package revi1337.onsquad.announce.domain.dto;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import revi1337.onsquad.announce.domain.vo.Title;
import revi1337.onsquad.crew_member.domain.dto.SimpleCrewMemberDomainDto;

public record AnnounceDomainDto(
        Long id,
        Title title,
        String content,
        LocalDateTime createdAt,
        boolean fixed,
        LocalDateTime fixedAt,
        SimpleCrewMemberDomainDto writer
) {
    @QueryProjection
    public AnnounceDomainDto(Long id, Title title, String content, LocalDateTime createdAt,
                             boolean fixed, LocalDateTime fixedAt, SimpleCrewMemberDomainDto writer) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.fixed = fixed;
        this.fixedAt = fixedAt;
        this.writer = writer;
    }
}
