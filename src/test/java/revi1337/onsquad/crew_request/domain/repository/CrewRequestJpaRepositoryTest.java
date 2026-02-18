package revi1337.onsquad.crew_request.domain.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static revi1337.onsquad.common.fixture.CrewFixture.createCrew;
import static revi1337.onsquad.common.fixture.MemberFixture.createAndong;
import static revi1337.onsquad.common.fixture.MemberFixture.createKwangwon;
import static revi1337.onsquad.common.fixture.MemberFixture.createRevi;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import revi1337.onsquad.common.PersistenceLayerTestSupport;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.repository.CrewJpaRepository;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.crew_member.domain.entity.CrewMemberFactory;
import revi1337.onsquad.crew_request.domain.entity.CrewRequest;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberJpaRepository;

class CrewRequestJpaRepositoryTest extends PersistenceLayerTestSupport {

    @Autowired
    private MemberJpaRepository memberRepository;

    @Autowired
    private CrewJpaRepository crewRepository;

    @Autowired
    private CrewRequestJpaRepository crewRequestRepository;

    @Test
    void findByCrewIdAndMemberId() {
        Member revi = memberRepository.save(createRevi());
        Crew crew = crewRepository.save(createCrew(revi));
        Member andong = memberRepository.save(createAndong());
        crewRequestRepository.save(createCrewRequest(crew, andong));
        clearPersistenceContext();

        Optional<CrewRequest> requestOpt = crewRequestRepository.findByCrewIdAndMemberId(crew.getId(), andong.getId());

        assertThat(requestOpt).isPresent();
    }

    @Test
    void deleteByMemberId() {
        Member revi = memberRepository.save(createRevi());
        Member andong = memberRepository.save(createAndong());
        Member kwangwon = memberRepository.save(createKwangwon());
        Crew crew1 = crewRepository.save(createCrew(revi));
        Crew crew2 = crewRepository.save(createCrew(andong));
        crewRequestRepository.saveAll(List.of(createCrewRequest(crew1, kwangwon), createCrewRequest(crew2, kwangwon)));
        clearPersistenceContext();

        int deleted = crewRequestRepository.deleteByMemberId(kwangwon.getId());

        assertThat(deleted).isEqualTo(2);
    }

    @Test
    void deleteByCrewIdAndMemberIdIn() {
        Member revi = memberRepository.save(createRevi());
        Crew crew = crewRepository.save(createCrew(revi));
        Member andong = memberRepository.save(createAndong());
        crewRequestRepository.save(createCrewRequest(crew, andong));
        clearPersistenceContext();

        int deleted = crewRequestRepository.deleteByCrewIdAndMemberId(crew.getId(), andong.getId());

        assertThat(deleted).isEqualTo(1);
    }

    @Test
    void deleteByCrewIdIn() {
        Member revi = memberRepository.save(createRevi());
        Member andong = memberRepository.save(createAndong());
        Member kwangwon = memberRepository.save(createKwangwon());
        Crew crew1 = crewRepository.save(createCrew(revi));
        Crew crew2 = crewRepository.save(createCrew(andong));
        crewRequestRepository.saveAll(List.of(createCrewRequest(crew1, andong), createCrewRequest(crew1, kwangwon)));
        crewRequestRepository.saveAll(List.of(createCrewRequest(crew2, revi), createCrewRequest(crew2, kwangwon)));
        clearPersistenceContext();

        int deleted = crewRequestRepository.deleteByCrewIdIn(List.of(crew1.getId(), crew2.getId()));

        assertThat(deleted).isEqualTo(4);
    }

    private CrewRequest createCrewRequest(Crew crew, Member andong) {
        return CrewRequest.of(crew, andong, LocalDateTime.now());
    }

    private CrewMember createManagerCrewMember(Crew crew, Member member) {
        return CrewMemberFactory.manager(crew, member, LocalDateTime.now());
    }
}
