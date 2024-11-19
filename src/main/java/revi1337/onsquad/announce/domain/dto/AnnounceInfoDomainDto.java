package revi1337.onsquad.announce.domain.dto;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import revi1337.onsquad.announce.domain.vo.Title;
import revi1337.onsquad.crew_member.domain.dto.SimpleCrewMemberDomainDto;

public record AnnounceInfoDomainDto(
        Boolean canModify,
        Long id,
        Title title,
        String content,
        LocalDateTime createdAt,
        boolean fixed,
        LocalDateTime fixedAt,
        SimpleCrewMemberDomainDto memberInfo
) {
    @QueryProjection
    public AnnounceInfoDomainDto(Boolean canModify, Long id, Title title, String content, LocalDateTime createdAt,
                                 boolean fixed, LocalDateTime fixedAt, SimpleCrewMemberDomainDto memberInfo) {
        this.canModify = canModify;
        this.id = id;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.fixed = fixed;
        this.fixedAt = fixedAt;
        this.memberInfo = memberInfo;
    }

    @QueryProjection
    public AnnounceInfoDomainDto(Long id, Title title, String content, LocalDateTime createdAt, boolean fixed,
                                 LocalDateTime fixedAt, SimpleCrewMemberDomainDto memberInfo) {
        this(null, id, title, content, createdAt, fixed, fixedAt, memberInfo);
    }

    @QueryProjection
    public AnnounceInfoDomainDto(Long id, Title title, LocalDateTime createdAt, boolean fixed, LocalDateTime fixedAt,
                                 SimpleCrewMemberDomainDto memberInfo) {
        this(id, title, null, createdAt, fixed, fixedAt, memberInfo);
    }
}
