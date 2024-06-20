package revi1337.onsquad.crew.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.crew.domain.CrewRepository;
import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.crew.dto.CrewCreateDto;
import revi1337.onsquad.crew.dto.CrewJoinDto;
import revi1337.onsquad.crew.dto.CrewWithMemberAndImageDto;
import revi1337.onsquad.crew_member.domain.CrewMember;
import revi1337.onsquad.crew_member.domain.CrewMemberRepository;
import revi1337.onsquad.crew_member.domain.vo.JoinStatus;
import revi1337.onsquad.image.domain.Image;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.domain.MemberRepository;

import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CrewService {

    private final CrewRepository crewRepository;
    private final CrewMemberRepository crewMemberRepository;
    private final MemberRepository memberRepository;

    public boolean checkDuplicateNickname(String crewName) {
        return crewRepository.existsByName(new Name(crewName));
    }

    @Transactional
    public void createNewCrew(CrewCreateDto crewCreateDto, Long memberId, byte[] image) {
        memberRepository.findById(memberId)
                .ifPresent(member -> crewRepository.findByName(new Name(crewCreateDto.name()))
                        .ifPresentOrElse(
                                crew -> crewRepository.save(crewCreateDto.toEntity(new Image(image), member)),
                                () -> { throw new IllegalArgumentException("크루명이 이미 존재합니다."); } // TODO 커스텀 익셉션 필요.
                        )
                );
    }

    public CrewWithMemberAndImageDto findCrewByName(String crewName) {
        return crewRepository.findCrewByName(new Name(crewName))
                .orElseThrow(() -> new IllegalArgumentException("해당 이름의 크루가 존재하지 않습니다.")); // TODO 커스텀 익셉션 필요
    }

    public List<CrewWithMemberAndImageDto> findCrewsByName() {
        return crewRepository.findCrewsByName();
    }

    @Transactional
    public void joinCrew(CrewJoinDto crewJoinDto, Long memberId) {
        memberRepository.findById(memberId)
                .ifPresent(member -> crewRepository.findByName(new Name(crewJoinDto.crewName()))
                        .ifPresentOrElse(
                                crew -> retrieveAndJoinCrewMember(member, crew),
                                () -> { throw new IllegalArgumentException("크루가 존재하지 않아 크루에 가입신청을 할 수 없습니다."); } // TODO 커스텀 익셉션 필요
                        )
                );
    }

    private void retrieveAndJoinCrewMember(Member member, Crew crew) {
        crewMemberRepository.findCrewMemberByMemberId(member.getId())
                .ifPresentOrElse(
                        this::judgementCurrentJoinStatus,
                        () -> crewMemberRepository.save(CrewMember.of(crew, member))
                );
    }

    private void judgementCurrentJoinStatus(CrewMember crewMember) {
        if (crewMember.getStatus() == JoinStatus.ACCEPT) {
            throw new IllegalArgumentException("이미 해당 크루에 가입된 사용자입니다."); // TODO 커스텀 익셉션 필요.
        }

        if (crewMember.getStatus() == JoinStatus.PENDING) {
            throw new IllegalArgumentException("이미 해당 크루에 가입신청을 하였습니다."); // TODO 커스텀 익셉션 필요.
        }
    }
}
