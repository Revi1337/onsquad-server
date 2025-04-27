package revi1337.onsquad.crew.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW_WITH_IMAGE;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW_WITH_NAME_AND_IMAGE;
import static revi1337.onsquad.common.fixture.CrewValueFixture.CREW_DETAIL_VALUE;
import static revi1337.onsquad.common.fixture.CrewValueFixture.CREW_IMAGE_LINK_VALUE;
import static revi1337.onsquad.common.fixture.CrewValueFixture.CREW_INTRODUCE_VALUE;
import static revi1337.onsquad.common.fixture.CrewValueFixture.CREW_NAME_VALUE;
import static revi1337.onsquad.common.fixture.MemberFixtures.REVI;
import static revi1337.onsquad.common.fixture.ValueFixture.REVI_NICKNAME_VALUE;
import static revi1337.onsquad.hashtag.domain.vo.HashtagType.ACTIVE;
import static revi1337.onsquad.hashtag.domain.vo.HashtagType.ESCAPE;
import static revi1337.onsquad.hashtag.domain.vo.HashtagType.FOODIE;
import static revi1337.onsquad.hashtag.domain.vo.HashtagType.IMPULSIVE;
import static revi1337.onsquad.hashtag.domain.vo.HashtagType.MOVIE;
import static revi1337.onsquad.member.domain.vo.Mbti.ISTP;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;
import revi1337.onsquad.common.ApplicationLayerTestSupport;
import revi1337.onsquad.crew.application.dto.CrewInfoDto;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.crew.domain.CrewJpaRepository;
import revi1337.onsquad.crew.domain.CrewRepository;
import revi1337.onsquad.crew_hashtag.domain.CrewHashtagRepository;
import revi1337.onsquad.hashtag.domain.Hashtag;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.domain.MemberRepository;

@Sql({"/h2-hashtag.sql"})
class CrewQueryServiceTest extends ApplicationLayerTestSupport {

    @Autowired
    private CrewRepository crewRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CrewJpaRepository crewJpaRepository;

    @Autowired
    private CrewHashtagRepository crewHashtagRepository;

    @Autowired
    private CrewQueryService crewQueryService;

    @Nested
    @DisplayName("Crew Name 중복 검사를 테스트한다.")
    class IsDuplicateCrewName {

        @Test
        @DisplayName("Crew Name 이 중복되면 true 를 반환한다.")
        void success1() {
            Member REVI = memberRepository.save(REVI());
            crewRepository.save(CREW(REVI));

            boolean duplicate = crewQueryService.isDuplicateCrewName(CREW_NAME_VALUE);

            assertThat(duplicate).isTrue();
        }

        @Test
        @DisplayName("Crew Name 이 중복되지 않으면 false 를 반환한다.")
        void success2() {
            boolean duplicate = crewQueryService.isDuplicateCrewName(CREW_NAME_VALUE);

            assertThat(duplicate).isFalse();
        }
    }

    @Nested
    @DisplayName("Crew 조회를 테스트한다.")
    class FindCrewById {

        @Test
        @DisplayName("Crew Id 로 조회하면, DTO 를 반환한다.")
        void success() {
            Member REVI = memberRepository.save(REVI());
            Crew CREW = crewRepository.save(CREW_WITH_IMAGE(REVI, CREW_IMAGE_LINK_VALUE));
            crewHashtagRepository.batchInsert(CREW.getId(), Hashtag.fromHashtagTypes(List.of(ACTIVE, ESCAPE)));

            CrewInfoDto CREW_INFO = crewQueryService.findCrewById(CREW.getId());

            assertAll(() -> {
                assertThat(CREW_INFO.id()).isEqualTo(CREW.getId());
                assertThat(CREW_INFO.name()).isEqualTo(CREW_NAME_VALUE);
                assertThat(CREW_INFO.introduce()).isEqualTo(CREW_INTRODUCE_VALUE);
                assertThat(CREW_INFO.detail()).isEqualTo(CREW_DETAIL_VALUE);
                assertThat(CREW_INFO.imageUrl()).isEqualTo(CREW_IMAGE_LINK_VALUE);
                assertThat(CREW_INFO.kakaoLink()).isNull();
                assertThat(CREW_INFO.memberCnt()).isEqualTo(1);
                assertThat(CREW_INFO.hashtagTypes()).contains(ACTIVE.getText(), ESCAPE.getText());
                assertThat(CREW_INFO.crewOwner().id()).isEqualTo(REVI.getId());
                assertThat(CREW_INFO.crewOwner().nickname()).isEqualTo(REVI_NICKNAME_VALUE);
                assertThat(CREW_INFO.crewOwner().mbti()).isEqualTo(ISTP.name());
            });
        }
    }

    @Nested
    @DisplayName("Crew 검색을 테스트한다.")
    class findCrewsByName {

        @Test
        @DisplayName("Crew Name 로 조회하면, List<DTO> 를 반환한다.")
        void success() {
            Member REVI = memberRepository.save(REVI());
            Crew CREW_1 = CREW_WITH_NAME_AND_IMAGE(REVI, CREW_NAME_VALUE + 1, CREW_IMAGE_LINK_VALUE + 1);
            Crew CREW_2 = CREW_WITH_NAME_AND_IMAGE(REVI, CREW_NAME_VALUE + 2, CREW_IMAGE_LINK_VALUE + 2);
            Crew CREW_3 = CREW_WITH_NAME_AND_IMAGE(REVI, CREW_NAME_VALUE + 3, CREW_IMAGE_LINK_VALUE + 3);
            crewJpaRepository.saveAll(List.of(CREW_1, CREW_2, CREW_3));
            crewHashtagRepository.batchInsert(CREW_1.getId(), Hashtag.fromHashtagTypes(List.of(ACTIVE, ESCAPE)));
            crewHashtagRepository.batchInsert(CREW_2.getId(), Hashtag.fromHashtagTypes(List.of(FOODIE, MOVIE)));
            crewHashtagRepository.batchInsert(CREW_3.getId(), Hashtag.fromHashtagTypes(List.of(IMPULSIVE)));

            List<CrewInfoDto> FIND_CREWS = crewQueryService.fetchCrewsByName(CREW_NAME_VALUE, PageRequest.of(0, 10));

            assertAll(() -> {
                assertThat(FIND_CREWS.get(0).hashtagTypes()).contains(IMPULSIVE.getText());
                assertThat(FIND_CREWS.get(1).hashtagTypes()).contains(FOODIE.getText(), MOVIE.getText());
                assertThat(FIND_CREWS.get(2).hashtagTypes()).contains(ACTIVE.getText(), ESCAPE.getText());
            });
        }
    }
}