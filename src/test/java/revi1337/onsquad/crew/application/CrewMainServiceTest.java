package revi1337.onsquad.crew.application;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static revi1337.onsquad.common.fixture.CrewFixture.createCrew;
import static revi1337.onsquad.common.fixture.MemberFixture.createAndong;
import static revi1337.onsquad.common.fixture.MemberFixture.createKwangwon;
import static revi1337.onsquad.common.fixture.MemberFixture.createMember;
import static revi1337.onsquad.common.fixture.MemberFixture.createRevi;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import revi1337.onsquad.common.ApplicationLayerTestSupport;
import revi1337.onsquad.crew.application.dto.response.CrewManageResponse;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.repository.CrewJpaRepository;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.crew_member.domain.entity.CrewMemberFactory;
import revi1337.onsquad.crew_request.domain.entity.CrewRequest;
import revi1337.onsquad.crew_request.domain.repository.CrewRequestJpaRepository;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberJpaRepository;
import revi1337.onsquad.squad.domain.entity.Squad;
import revi1337.onsquad.squad.domain.model.SquadCreateSpec;
import revi1337.onsquad.squad.domain.repository.SquadJpaRepository;

class CrewMainServiceTest extends ApplicationLayerTestSupport {

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Autowired
    private CrewJpaRepository crewJpaRepository;

    @Autowired
    private CrewRequestJpaRepository crewRequestJpaRepository;

    @Autowired
    private SquadJpaRepository squadJpaRepository;

    @Autowired
    private CrewMainService crewMainService;

    @Test
    @DisplayName("매니저 권한의 유저가 크루 관리 정보를 조회하면 수정/삭제 권한은 없지만 통계 정보는 정상 조회된다.")
    void fetchManageInfo() {
        Member revi = memberJpaRepository.save(createRevi());
        Member andong = memberJpaRepository.save(createAndong());
        Member kwangwon = memberJpaRepository.save(createKwangwon());
        Member member = memberJpaRepository.save(createMember(1));
        Crew crew = createCrew(revi);
        crew.addCrewMember(createManagerCrewMember(crew, andong));
        crew.addCrewMember(createManagerCrewMember(crew, kwangwon));
        crewJpaRepository.save(crew);
        crewRequestJpaRepository.save(createCrewRequest(crew, member));
        squadJpaRepository.saveAll(List.of(createSquad(crew, andong), createSquad(crew, kwangwon)));
        clearPersistenceContext();

        CrewManageResponse response = crewMainService.fetchManageInfo(andong.getId(), crew.getId());

        assertSoftly(softly -> {
            softly.assertThat(response.states().canModify()).isFalse();
            softly.assertThat(response.states().canDelete()).isFalse();
            softly.assertThat(response.requestCnt()).isEqualTo(1);
            softly.assertThat(response.squadCnt()).isEqualTo(2);
            softly.assertThat(response.memberCnt()).isEqualTo(3);
        });
    }

    @Test
    @DisplayName("Owner 권한의 유저가 크루 관리 정보를 조회하면 수정/삭제 권한을 포함한 모든 통계 정보가 조회된다.")
    void fetchManageInfo2() {
        Member revi = memberJpaRepository.save(createRevi());
        Member andong = memberJpaRepository.save(createAndong());
        Member kwangwon = memberJpaRepository.save(createKwangwon());
        Member member = memberJpaRepository.save(createMember(1));
        Crew crew = createCrew(revi);
        crew.addCrewMember(createManagerCrewMember(crew, andong));
        crew.addCrewMember(createManagerCrewMember(crew, kwangwon));
        crewJpaRepository.save(crew);
        crewRequestJpaRepository.save(createCrewRequest(crew, member));
        squadJpaRepository.saveAll(List.of(createSquad(crew, andong), createSquad(crew, kwangwon)));
        clearPersistenceContext();

        CrewManageResponse response = crewMainService.fetchManageInfo(revi.getId(), crew.getId());

        assertSoftly(softly -> {
            softly.assertThat(response.states().canModify()).isTrue();
            softly.assertThat(response.states().canDelete()).isTrue();
            softly.assertThat(response.requestCnt()).isEqualTo(1);
            softly.assertThat(response.squadCnt()).isEqualTo(2);
            softly.assertThat(response.memberCnt()).isEqualTo(3);
        });
    }

    private CrewRequest createCrewRequest(Crew crew, Member andong) {
        return CrewRequest.of(crew, andong, LocalDateTime.now());
    }

    private CrewMember createManagerCrewMember(Crew crew, Member member) {
        return CrewMemberFactory.manager(crew, member, LocalDateTime.now());
    }

    private Squad createSquad(Crew crew, Member member) {
        return Squad.create(
                new SquadCreateSpec(
                        "title",
                        "content",
                        20,
                        "어딘가",
                        "상세-어딘가",
                        List.of(),
                        "kakao-link",
                        "discord-link"
                ),
                crew,
                member,
                LocalDateTime.now()
        );
    }
}
