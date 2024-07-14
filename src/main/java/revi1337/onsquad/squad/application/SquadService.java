package revi1337.onsquad.squad.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.crew.domain.CrewRepository;
import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.crew.error.CrewErrorCode;
import revi1337.onsquad.crew.error.exception.CrewBusinessException;
import revi1337.onsquad.crew_member.domain.CrewMember;
import revi1337.onsquad.crew_member.domain.CrewMemberRepository;
import revi1337.onsquad.crew_member.domain.vo.JoinStatus;
import revi1337.onsquad.crew_member.error.exception.CrewMemberBusinessException;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.domain.MemberRepository;
import revi1337.onsquad.member.error.MemberErrorCode;
import revi1337.onsquad.member.error.exception.MemberBusinessException;
import revi1337.onsquad.squad.domain.Squad;
import revi1337.onsquad.squad.domain.SquadRepository;
import revi1337.onsquad.squad.domain.vo.Title;
import revi1337.onsquad.squad.dto.SquadCreateDto;
import revi1337.onsquad.squad.dto.SquadJoinDto;
import revi1337.onsquad.squad.error.exception.SquadBusinessException;
import revi1337.onsquad.squad_member.domain.SquadMember;

import java.util.List;

import static revi1337.onsquad.crew_member.error.CrewMemberErrorCode.*;
import static revi1337.onsquad.squad.error.SquadErrorCode.*;
import static revi1337.onsquad.squad.error.SquadErrorCode.ALREADY_REQUEST;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class SquadService {

    private final SquadRepository squadRepository;
    private final MemberRepository memberRepository;
    private final CrewRepository crewRepository;
    private final CrewMemberRepository crewMemberRepository;

    @Transactional
    public void createNewSquad(SquadCreateDto dto, Long memberId) {
        memberRepository.findById(memberId)
                .map(member -> persistSquadIfMemberAndCrewIsValid(dto, member))
                .orElseThrow(() -> new MemberBusinessException.NotFound(MemberErrorCode.NOTFOUND, memberId));
    }

    private Crew persistSquadIfMemberAndCrewIsValid(SquadCreateDto dto, Member member) {
        return crewRepository.findCrewWithMembersByName(new Name(dto.crewName()))
                .map(crew -> persistSquadIfCrewMemberIsValid(dto, member, crew))
                .orElseThrow(() -> new CrewBusinessException.NotFoundByName(CrewErrorCode.NOTFOUND_CREW, dto.crewName()));
    }

    private Crew persistSquadIfCrewMemberIsValid(SquadCreateDto dto, Member member, Crew crew) {
        for (CrewMember crewMember : crew.getCrewMembers()) {
            if (crewMember.getMember().getId().equals(member.getId())) {
                if (crewMember.getStatus() == JoinStatus.PENDING) {
                    throw new CrewBusinessException.AlreadyRequest(CrewErrorCode.ALREADY_REQUEST, dto.crewName());
                }

                persistSquadAndRegisterAdmin(dto, member, crew);
                return crew;
            }
        }

        throw new CrewMemberBusinessException.NotParticipant(NOT_PARTICIPANT, member.getId(), dto.crewName());
    }

    private void persistSquadAndRegisterAdmin(SquadCreateDto dto, Member member, Crew crew) {
        Squad squad = dto.toEntity(member, crew);
        squad.addSquadMember(SquadMember.forLeader(member));
        squadRepository.save(squad);
    }

    @Transactional
    public void joinSquad(SquadJoinDto dto, Long memberId) {
        memberRepository.findById(memberId)
                .map(member -> checkMemberInCrew(dto, memberId, member))
                .map(member -> persistSquadMember(dto, member))
                .orElseThrow(() -> new MemberBusinessException.NotFound(MemberErrorCode.NOTFOUND, memberId));
    }

    private Member checkMemberInCrew(SquadJoinDto dto, Long memberId, Member member) {
        if (crewMemberRepository.existsCrewMember(memberId)) {
            return member;
        }

        throw new CrewMemberBusinessException.NotParticipant(NOT_PARTICIPANT, member.getId(), dto.crewName());
    }

    private Squad persistSquadMember(SquadJoinDto dto, Member member) {
        return squadRepository.findSquadWithMembersById(dto.squadId(), new Title(dto.squadTitle()))
                .map(squad -> persistSquadMemberIfMemberInSquad(dto, member, squad))
                .orElseThrow(() -> new SquadBusinessException.NotFound(NOTFOUND_SQUAD, dto.squadTitle()));
    }

    private Squad persistSquadMemberIfMemberInSquad(SquadJoinDto dto, Member member, Squad squad) {
        validateMemberInSquad(dto, member, squad);
        return persistSquadMemberInSquad(member, squad);
    }

    private void validateMemberInSquad(SquadJoinDto dto, Member member, Squad squad) {
        List<SquadMember> squadMembers = squad.getSquadMembers();
        for (SquadMember squadMember : squadMembers) {
            if (squadMember.getMember().getId().equals(member.getId())) {
                if (squadMember.getStatus() == JoinStatus.PENDING) {
                    throw new SquadBusinessException.AlreadyRequest(ALREADY_REQUEST, dto.squadTitle());
                }
                if (squadMember.getStatus() == JoinStatus.ACCEPT) {
                    throw new SquadBusinessException.AlreadyParticipant(ALREADY_JOIN, dto.squadTitle());
                }
            }
        }
    }

    private Squad persistSquadMemberInSquad(Member member, Squad squad) {
        squad.addSquadMember(SquadMember.forGeneral(member));
        squadRepository.saveAndFlush(squad);
        return squad;
    }
}

//    /**
//     * Member 가 있나 체크 (O)
//     * Member 가 Crew 에 속해있나 체크 (0)
//     * Squad 가 존재하나 체크 (O)
//     *      Squad 에 참여요청을 한적있나 체크 (O)
//     *      이미 Squad 에 가입된 사용자인지 체크. (O)
//     */
//    public void joinSquad(SquadJoinDto dto, Long memberId) {
//        memberRepository.findById(memberId)
//                .map(member -> {
//                    if (crewMemberRepository.existsCrewMember(memberId)) {
//                        return member;
//                    }
//
//                    throw new IllegalArgumentException("멤버가 Crew 에 속해있지 않음.");
//                })
//                .map(member -> squadRepository.findSquadWithMembersById(dto.squadId(), new Title(dto.squadTitle()))
//                        .map(squad -> {
//                            List<SquadMember> squadMembers = squad.getSquadMembers();
//                            for (SquadMember squadMember : squadMembers) {
//                                if (squadMember.getMember().getId().equals(member.getId())) {
//                                    if (squadMember.getStatus() == JoinStatus.PENDING) {
//                                        throw new SquadBusinessException.AlreadyRequest(ALREADY_REQUEST, dto.squadTitle());
//                                    }
//                                    if (squadMember.getStatus() == JoinStatus.ACCEPT) {
//                                        throw new SquadBusinessException.AlreadyParticipant(ALREADY_PARTICIPANT, dto.squadTitle());
//                                    }
//                                }
//                            }
//
//                            squad.addSquadMember(SquadMember.forGeneral(member));
//                            squadRepository.saveAndFlush(squad);
//                            return squad;
//                        })
//                        .orElseThrow(() -> new SquadBusinessException.NotFound(NOTFOUND_SQUAD, dto.squadTitle())))
//                .orElseThrow(() -> new MemberBusinessException.NotFound(MemberErrorCode.NOTFOUND, memberId));
//    }
