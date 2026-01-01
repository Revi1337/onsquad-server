package revi1337.onsquad.announce.application.dto;

import revi1337.onsquad.announce.domain.entity.Announce;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.member.domain.entity.Member;

public record AnnounceUpdateDto(
        String title,
        String content
) {

    public Announce toEntity(Crew crew, Member member) {
        return new Announce(title, content, crew, member);
    }
}
