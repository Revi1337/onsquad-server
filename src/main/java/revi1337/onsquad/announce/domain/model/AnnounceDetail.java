package revi1337.onsquad.announce.domain.model;

import java.time.LocalDateTime;
import revi1337.onsquad.announce.domain.entity.vo.Title;
import revi1337.onsquad.member.domain.model.SimpleMember;

public record AnnounceDetail(
        Long id,
        Title title,
        String content,
        LocalDateTime createdAt,
        boolean pinned,
        LocalDateTime pinnedAt,
        SimpleMember writer
) {

    public AnnounceDetail(Long id, Title title, String content, LocalDateTime createdAt, boolean pinned, LocalDateTime pinnedAt, SimpleMember writer) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.pinned = pinned;
        this.pinnedAt = pinnedAt;
        this.writer = writer;
    }
}
