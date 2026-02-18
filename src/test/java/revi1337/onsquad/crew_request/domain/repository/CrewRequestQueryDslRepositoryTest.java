package revi1337.onsquad.crew_request.domain.repository;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static revi1337.onsquad.common.fixture.CrewFixture.createCrew;
import static revi1337.onsquad.common.fixture.MemberFixture.createAndong;
import static revi1337.onsquad.common.fixture.MemberFixture.createKwangwon;
import static revi1337.onsquad.common.fixture.MemberFixture.createMember;
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
import revi1337.onsquad.crew_request.domain.entity.CrewRequest;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberJpaRepository;

@Import(CrewRequestQueryDslRepository.class)
class CrewRequestQueryDslRepositoryTest extends PersistenceLayerTestSupport {

    @Autowired
    private MemberJpaRepository memberRepository;

    @Autowired
    private CrewJpaRepository crewRepository;

    @Autowired
    private CrewRequestJpaRepository crewRequestRepository;

    @Autowired
    private CrewRequestQueryDslRepository crewRequestQueryDslRepository;

    @Test
    @DisplayName("내가 보낸 크루 참가신청 목록을 조회한다.")
    void fetchAllWithSimpleCrewByMemberId() {
        Member revi = memberRepository.save(createRevi());
        Member andong = memberRepository.save(createAndong());
        Member kwangwon = memberRepository.save(createKwangwon());
        Crew crew1 = crewRepository.save(createCrew(revi));
        Crew crew2 = crewRepository.save(createCrew(revi));
        Crew crew3 = crewRepository.save(createCrew(andong));
        LocalDateTime dateTime = LocalDate.of(2026, 1, 4).atStartOfDay();
        crewRequestRepository.saveAll(List.of(
                createCrewRequest(crew1, kwangwon, dateTime.plusHours(1)),
                createCrewRequest(crew2, kwangwon, dateTime.plusHours(2)),
                createCrewRequest(crew3, kwangwon, dateTime.plusHours(3))
        ));
        clearPersistenceContext();
        PageRequest pageRequest = PageRequest.of(0, 2, Sort.by("requestAt").descending());

        Page<CrewRequest> requests = crewRequestQueryDslRepository.fetchAllWithSimpleCrewByMemberId(kwangwon.getId(), pageRequest);

        assertSoftly(softly -> {
            softly.assertThat(requests).hasSize(2);
            List<CrewRequest> contents = requests.getContent();
            softly.assertThat(contents.get(0).getCrew().getId()).isEqualTo(crew3.getId());
            softly.assertThat(contents.get(1).getCrew().getId()).isEqualTo(crew2.getId());
        });
    }

    @Test
    @DisplayName("특정 크루의 참가신청 목록을 조회한다.")
    void fetchCrewRequests() {
        Member revi = memberRepository.save(createRevi());
        Member andong = memberRepository.save(createAndong());
        Member kwangwon = memberRepository.save(createKwangwon());
        Member dummy = memberRepository.save(createMember(1));
        Crew crew = crewRepository.save(createCrew(revi));
        LocalDateTime dateTime = LocalDate.of(2026, 1, 4).atStartOfDay();
        crewRequestRepository.saveAll(List.of(
                createCrewRequest(crew, andong, dateTime.plusHours(1)),
                createCrewRequest(crew, kwangwon, dateTime.plusHours(2)),
                createCrewRequest(crew, dummy, dateTime.plusHours(3))
        ));
        clearPersistenceContext();
        PageRequest pageRequest = PageRequest.of(0, 2, Sort.by("requestAt").descending());

        Page<CrewRequest> requests = crewRequestQueryDslRepository.fetchCrewRequests(crew.getId(), pageRequest);

        assertSoftly(softly -> {
            softly.assertThat(requests).hasSize(2);
            List<CrewRequest> contents = requests.getContent();
            softly.assertThat(contents.get(0).getMember().getId()).isEqualTo(dummy.getId());
            softly.assertThat(contents.get(1).getMember().getId()).isEqualTo(kwangwon.getId());
        });
    }

    private CrewRequest createCrewRequest(Crew crew, Member andong, LocalDateTime dateTime) {
        return CrewRequest.of(crew, andong, dateTime);
    }
}
