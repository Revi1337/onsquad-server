package revi1337.onsquad.crew_member.dto;

import lombok.Builder;
import lombok.Getter;
import revi1337.onsquad.crew.dto.CrewDto;
import revi1337.onsquad.crew_member.domain.CrewMember;
import revi1337.onsquad.crew_member.domain.vo.JoinStatus;
import revi1337.onsquad.member.dto.MemberDto;

@Getter
public class CrewMemberDto {

    private Long id;
    private CrewDto crewDto;
    private MemberDto memberDto;
    private JoinStatus status;

    @Builder
    private CrewMemberDto(Long id, CrewDto crewDto, MemberDto memberDto, JoinStatus status) {
        this.id = id;
        this.crewDto = crewDto;
        this.memberDto = memberDto;
        this.status = status;
    }

    public static CrewMemberDto from(CrewMember crewMember) {
        return CrewMemberDto.builder()
                .id(crewMember.getId())
                .crewDto(CrewDto.from(crewMember.getCrew()))
                .memberDto(MemberDto.from(crewMember.getMember()))
                .status(crewMember.getStatus())
                .build();
    }
}
