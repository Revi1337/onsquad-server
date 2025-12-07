package revi1337.onsquad.crew.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW_1;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW_2;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW_3;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW_4;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW_WITH_IMAGE;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW_WITH_NAME_AND_IMAGE;
import static revi1337.onsquad.common.fixture.CrewValueFixture.CREW_DETAIL_VALUE;
import static revi1337.onsquad.common.fixture.CrewValueFixture.CREW_IMAGE_LINK_VALUE;
import static revi1337.onsquad.common.fixture.CrewValueFixture.CREW_INTRODUCE_VALUE;
import static revi1337.onsquad.common.fixture.CrewValueFixture.CREW_NAME_VALUE;
import static revi1337.onsquad.common.fixture.MemberFixture.ANDONG;
import static revi1337.onsquad.common.fixture.MemberFixture.KWANGWON;
import static revi1337.onsquad.common.fixture.MemberFixture.REVI;
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_NICKNAME_VALUE;
import static revi1337.onsquad.hashtag.domain.entity.vo.HashtagType.ACTIVE;
import static revi1337.onsquad.hashtag.domain.entity.vo.HashtagType.ESCAPE;
import static revi1337.onsquad.hashtag.domain.entity.vo.HashtagType.FOODIE;
import static revi1337.onsquad.hashtag.domain.entity.vo.HashtagType.IMPULSIVE;
import static revi1337.onsquad.hashtag.domain.entity.vo.HashtagType.MOVIE;
import static revi1337.onsquad.member.domain.entity.vo.Mbti.ISTP;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;
import revi1337.onsquad.common.ApplicationLayerTestSupport;
import revi1337.onsquad.crew.application.dto.CrewDto;
import revi1337.onsquad.crew.application.dto.EnrolledCrewDto;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.repository.CrewJpaRepository;
import revi1337.onsquad.crew.domain.repository.CrewRepository;
import revi1337.onsquad.crew_hashtag.domain.repository.CrewHashtagRepository;
import revi1337.onsquad.crew_member.domain.entity.CrewMemberFactory;
import revi1337.onsquad.crew_member.domain.repository.CrewMemberJpaRepository;
import revi1337.onsquad.hashtag.domain.entity.Hashtag;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberJpaRepository;

@Sql({"/h2-hashtag.sql"})
class CrewQueryServiceTest extends ApplicationLayerTestSupport {

    @Autowired
    private CrewRepository crewRepository;

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Autowired
    private CrewMemberJpaRepository crewMemberJpaRepository;

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
            Member REVI = memberJpaRepository.save(REVI());
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
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewRepository.save(CREW_WITH_IMAGE(REVI, CREW_IMAGE_LINK_VALUE));
            crewHashtagRepository.batchInsert(CREW.getId(), Hashtag.fromHashtagTypes(List.of(ACTIVE, ESCAPE)));

            CrewDto CREW_INFO = crewQueryService.findCrewById(CREW.getId());

            assertAll(() -> {
                assertThat(CREW_INFO.id()).isEqualTo(CREW.getId());
                assertThat(CREW_INFO.name()).isEqualTo(CREW_NAME_VALUE);
                assertThat(CREW_INFO.introduce()).isEqualTo(CREW_INTRODUCE_VALUE);
                assertThat(CREW_INFO.detail()).isEqualTo(CREW_DETAIL_VALUE);
                assertThat(CREW_INFO.imageUrl()).isEqualTo(CREW_IMAGE_LINK_VALUE);
                assertThat(CREW_INFO.kakaoLink()).isNull();
                assertThat(CREW_INFO.memberCnt()).isEqualTo(1);
                assertThat(CREW_INFO.hashtagTypes()).contains(ACTIVE.getText(), ESCAPE.getText());
                assertThat(CREW_INFO.owner().id()).isEqualTo(REVI.getId());
                assertThat(CREW_INFO.owner().nickname()).isEqualTo(REVI_NICKNAME_VALUE);
                assertThat(CREW_INFO.owner().mbti()).isEqualTo(ISTP.name());
            });
        }
    }

    @Nested
    @DisplayName("Crew 검색을 테스트한다.")
    class findCrewsByName {

        @Test
        @DisplayName("Crew Name 로 조회하면, List<DTO> 를 반환한다.")
        void success() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW_1 = CREW_WITH_NAME_AND_IMAGE(REVI, CREW_NAME_VALUE + 1, CREW_IMAGE_LINK_VALUE + 1);
            Crew CREW_2 = CREW_WITH_NAME_AND_IMAGE(REVI, CREW_NAME_VALUE + 2, CREW_IMAGE_LINK_VALUE + 2);
            Crew CREW_3 = CREW_WITH_NAME_AND_IMAGE(REVI, CREW_NAME_VALUE + 3, CREW_IMAGE_LINK_VALUE + 3);
            crewJpaRepository.saveAll(List.of(CREW_1, CREW_2, CREW_3));
            crewHashtagRepository.batchInsert(CREW_1.getId(), Hashtag.fromHashtagTypes(List.of(ACTIVE, ESCAPE)));
            crewHashtagRepository.batchInsert(CREW_2.getId(), Hashtag.fromHashtagTypes(List.of(FOODIE, MOVIE)));
            crewHashtagRepository.batchInsert(CREW_3.getId(), Hashtag.fromHashtagTypes(List.of(IMPULSIVE)));

            List<CrewDto> FIND_CREWS = crewQueryService.fetchCrewsByName(CREW_NAME_VALUE, PageRequest.of(0, 10));

            assertAll(() -> {
                assertThat(FIND_CREWS.get(0).hashtagTypes()).contains(IMPULSIVE.getText());
                assertThat(FIND_CREWS.get(1).hashtagTypes()).contains(FOODIE.getText(), MOVIE.getText());
                assertThat(FIND_CREWS.get(2).hashtagTypes()).contains(ACTIVE.getText(), ESCAPE.getText());
            });
        }
    }

    @Nested
    @DisplayName("내가 참여하고 있는 Crew 에 대한 CrewMember 들 조회를 테스트한다.")
    class FetchAllJoinedCrews {

        @Test
        @DisplayName("내가 참여하고 있는 Crew 에 대한 CrewMember 들 조회에 성공한다.")
        void success() {
            Member ANDONG = memberJpaRepository.save(ANDONG());
            Member KWANGWON = memberJpaRepository.save(KWANGWON());
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW1 = crewJpaRepository.save(CREW_1(ANDONG));
            Crew CREW2 = crewJpaRepository.save(CREW_2(KWANGWON));
            Crew CREW3 = crewJpaRepository.save(CREW_3(REVI));
            LocalDateTime NOW = LocalDateTime.now();
            crewMemberJpaRepository.save(CrewMemberFactory.general(CREW1, REVI, NOW));
            crewMemberJpaRepository.save(CrewMemberFactory.general(CREW2, REVI, NOW.plusMinutes(1)));
            crewMemberJpaRepository.save(CrewMemberFactory.general(CREW2, ANDONG, NOW.plusMinutes(1)));

            List<EnrolledCrewDto> DTOS = crewQueryService.fetchParticipantCrews(REVI.getId());

            assertAll(() -> {
                assertThat(DTOS).hasSize(3);

                assertThat(DTOS.get(0).id()).isEqualTo(CREW2.getId());
                assertThat(DTOS.get(0).name()).isEqualTo(CREW2.getName().getValue());
                assertThat(DTOS.get(0).imageUrl()).isNull();
                assertThat(DTOS.get(0).isOwner()).isFalse();
                assertThat(DTOS.get(0).owner().id()).isEqualTo(KWANGWON.getId());
                assertThat(DTOS.get(0).owner().nickname()).isEqualTo(KWANGWON.getNickname().getValue());
                assertThat(DTOS.get(0).owner().mbti()).isEqualTo(KWANGWON.getMbti().name());

                assertThat(DTOS.get(1).id()).isEqualTo(CREW1.getId());
                assertThat(DTOS.get(1).name()).isEqualTo(CREW1.getName().getValue());
                assertThat(DTOS.get(1).imageUrl()).isNull();
                assertThat(DTOS.get(1).isOwner()).isFalse();
                assertThat(DTOS.get(1).owner().id()).isEqualTo(ANDONG.getId());
                assertThat(DTOS.get(1).owner().nickname()).isEqualTo(ANDONG.getNickname().getValue());
                assertThat(DTOS.get(1).owner().mbti()).isEqualTo(ANDONG.getMbti().name());

                assertThat(DTOS.get(2).id()).isEqualTo(CREW3.getId());
                assertThat(DTOS.get(2).name()).isEqualTo(CREW3.getName().getValue());
                assertThat(DTOS.get(2).imageUrl()).isNull();
                assertThat(DTOS.get(2).isOwner()).isTrue();
                assertThat(DTOS.get(2).owner().id()).isEqualTo(REVI.getId());
                assertThat(DTOS.get(2).owner().nickname()).isEqualTo(REVI.getNickname().getValue());
                assertThat(DTOS.get(2).owner().mbti()).isEqualTo(REVI.getMbti().name());
            });
        }
    }

    @Nested
    @DisplayName("내가 개설한 크루 조회를 테스트한다.")
    class FetchOwnedCrews {

        @Test
        @DisplayName("내가 개설한 크루 조회에 성공한다.")
        void success() {
            Member ANDONG = memberJpaRepository.save(ANDONG());
            Member KWANGWON = memberJpaRepository.save(KWANGWON());
            crewJpaRepository.save(CREW_1(ANDONG));
            crewJpaRepository.save(CREW_2(ANDONG));
            Crew CREW3 = crewJpaRepository.save(CREW_3(KWANGWON));
            Crew CREW4 = crewJpaRepository.save(CREW_4(KWANGWON));
            PageRequest PAGE_REQUEST = PageRequest.of(0, 2);

            List<CrewDto> DTOS = crewQueryService.fetchOwnedCrews(KWANGWON.getId(), PAGE_REQUEST);

            assertThat(DTOS).hasSize(2);
            assertThat(DTOS.get(0).id()).isEqualTo(CREW4.getId());
            assertThat(DTOS.get(0).owner().id()).isEqualTo(KWANGWON.getId());
            assertThat(DTOS.get(1).id()).isEqualTo(CREW3.getId());
            assertThat(DTOS.get(1).owner().id()).isEqualTo(KWANGWON.getId());
        }
    }
}
