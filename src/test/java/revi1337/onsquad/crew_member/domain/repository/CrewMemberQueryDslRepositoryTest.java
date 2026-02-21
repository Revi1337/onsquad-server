package revi1337.onsquad.crew_member.domain.repository;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static revi1337.onsquad.common.fixture.CrewFixture.createCrew;
import static revi1337.onsquad.common.fixture.MemberFixture.createAndong;
import static revi1337.onsquad.common.fixture.MemberFixture.createKwangwon;
import static revi1337.onsquad.common.fixture.MemberFixture.createRevi;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import revi1337.onsquad.common.PersistenceLayerTestSupport;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.repository.CrewJpaRepository;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.crew_member.domain.entity.CrewMemberFactory;
import revi1337.onsquad.crew_member.domain.model.MyParticipantCrew;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberJpaRepository;

@Import(CrewMemberQueryDslRepository.class)
class CrewMemberQueryDslRepositoryTest extends PersistenceLayerTestSupport {

    @Autowired
    private MemberJpaRepository memberRepository;

    @Autowired
    private CrewJpaRepository crewRepository;

    @Autowired
    private CrewMemberQueryDslRepository crewMemberQueryDslRepository;

    @Test
    @DisplayName("특정 크루의 참가자 목록을 최신 가입 순으로 페이징하여 조회한다")
    void fetchParticipantsByCrewId() {
        Member revi = memberRepository.save(createRevi());
        Member kwangwon = memberRepository.save(createKwangwon());
        Member andong = memberRepository.save(createAndong());
        LocalDateTime ownerParticipateAt = LocalDate.of(2026, 1, 4).atStartOfDay();
        Crew crew = createCrew(revi, ownerParticipateAt);
        crew.addCrewMember(createManagerCrewMember(crew, kwangwon, ownerParticipateAt.plusDays(1)));
        crew.addCrewMember(createGeneralCrewMember(crew, andong, ownerParticipateAt.plusDays(2)));
        crewRepository.save(crew);
        clearPersistenceContext();
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by("participateAt").descending());

        Page<CrewMember> participants = crewMemberQueryDslRepository.fetchParticipantsByCrewId(crew.getId(), pageRequest);

        assertSoftly(softly -> {
            softly.assertThat(participants.getSize()).isEqualTo(3);
            softly.assertThat(participants.getNumber()).isEqualTo(0);
            softly.assertThat(participants.getTotalPages()).isEqualTo(1);
            softly.assertThat(participants.getTotalElements()).isEqualTo(3);
            softly.assertThat(participants.getNumberOfElements()).isEqualTo(3);

            List<CrewMember> contents = participants.getContent();
            softly.assertThat(contents.get(0).getMember().getId()).isEqualTo(andong.getId());
            softly.assertThat(contents.get(1).getMember().getId()).isEqualTo(kwangwon.getId());
            softly.assertThat(contents.get(2).getMember().getId()).isEqualTo(revi.getId());
        });
    }

    @Test
    @DisplayName("참여 중인 크루 목록 조회 시, 내가 방장인 크루가 먼저 나오고 나머지는 최신 가입 순으로 정렬된다")
    void fetchParticipantCrews() {
        Member revi = memberRepository.save(createRevi());
        Member kwangwon = memberRepository.save(createKwangwon());
        Member andong = memberRepository.save(createAndong());
        LocalDateTime ownerParticipateAt = LocalDate.of(2026, 1, 4).atStartOfDay();
        Crew crew1 = createCrew(revi, ownerParticipateAt);
        Crew crew2 = createCrew(kwangwon, ownerParticipateAt.plusDays(1));
        Crew crew3 = createCrew(andong, ownerParticipateAt.plusDays(2));
        Crew crew4 = createCrew(revi, ownerParticipateAt.plusDays(3));
        crew2.addCrewMember(createManagerCrewMember(crew2, revi, ownerParticipateAt.plusDays(1)));
        crew3.addCrewMember(createGeneralCrewMember(crew3, revi, ownerParticipateAt.plusDays(2)));
        crewRepository.saveAll(List.of(crew1, crew2, crew3, crew4));
        clearPersistenceContext();
        PageRequest pageRequest = PageRequest.of(0, 4);

        Page<MyParticipantCrew> participants = crewMemberQueryDslRepository.fetchParticipantCrews(revi.getId(), pageRequest);

        assertSoftly(softly -> {
            softly.assertThat(participants.getSize()).isEqualTo(4);
            softly.assertThat(participants.getNumber()).isEqualTo(0);
            softly.assertThat(participants.getTotalPages()).isEqualTo(1);
            softly.assertThat(participants.getTotalElements()).isEqualTo(4);
            softly.assertThat(participants.getNumberOfElements()).isEqualTo(4);

            List<MyParticipantCrew> results = participants.getContent();
            softly.assertThat(results).hasSize(4);

            softly.assertThat(results.get(0).isOwner()).isTrue();
            softly.assertThat(results.get(0).crew().id()).isEqualTo(crew4.getId());
            softly.assertThat(results.get(0).crew().owner().id()).isEqualTo(revi.getId());

            softly.assertThat(results.get(1).isOwner()).isTrue();
            softly.assertThat(results.get(1).crew().id()).isEqualTo(crew1.getId());
            softly.assertThat(results.get(1).crew().owner().id()).isEqualTo(revi.getId());

            softly.assertThat(results.get(2).isOwner()).isFalse();
            softly.assertThat(results.get(2).crew().id()).isEqualTo(crew3.getId());
            softly.assertThat(results.get(2).crew().owner().id()).isEqualTo(andong.getId());

            softly.assertThat(results.get(3).isOwner()).isFalse();
            softly.assertThat(results.get(3).crew().id()).isEqualTo(crew2.getId());
            softly.assertThat(results.get(3).crew().owner().id()).isEqualTo(kwangwon.getId());
        });
    }

    private CrewMember createManagerCrewMember(Crew crew, Member member, LocalDateTime participantAt) {
        return CrewMemberFactory.manager(crew, member, participantAt);
    }

    private CrewMember createGeneralCrewMember(Crew crew, Member member, LocalDateTime participantAt) {
        return CrewMemberFactory.general(crew, member, participantAt);
    }
}
