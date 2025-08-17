package revi1337.onsquad.crew.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW_1;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW_2;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW_3;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW_4;
import static revi1337.onsquad.common.fixture.CrewValueFixture.CREW_DETAIL;
import static revi1337.onsquad.common.fixture.CrewValueFixture.CREW_INTRODUCE;
import static revi1337.onsquad.common.fixture.CrewValueFixture.CREW_NAME;
import static revi1337.onsquad.common.fixture.MemberFixture.ANDONG;
import static revi1337.onsquad.common.fixture.MemberFixture.KWANGWON;
import static revi1337.onsquad.common.fixture.MemberFixture.REVI;
import static revi1337.onsquad.common.fixture.MemberValueFixture.ANDONG_MBTI;
import static revi1337.onsquad.common.fixture.MemberValueFixture.ANDONG_NICKNAME;
import static revi1337.onsquad.common.fixture.MemberValueFixture.KWANGWON_MBTI;
import static revi1337.onsquad.common.fixture.MemberValueFixture.KWANGWON_NICKNAME;
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_MBTI;
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_NICKNAME;
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_NICKNAME_VALUE;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import revi1337.onsquad.common.PersistenceLayerTestSupport;
import revi1337.onsquad.crew.domain.dto.CrewDomainDto;
import revi1337.onsquad.crew.domain.dto.EnrolledCrewDomainDto;
import revi1337.onsquad.crew.error.exception.CrewBusinessException;
import revi1337.onsquad.crew_member.domain.CrewMember;
import revi1337.onsquad.crew_member.domain.CrewMemberQueryDslRepository;
import revi1337.onsquad.crew_member.domain.CrewMemberRepository;
import revi1337.onsquad.crew_member.domain.CrewMemberRepositoryImpl;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.domain.MemberJpaRepository;
import revi1337.onsquad.member.domain.MemberRepositoryImpl;
import revi1337.onsquad.member.domain.dto.SimpleMemberDomainDto;
import revi1337.onsquad.member.domain.vo.Mbti;
import revi1337.onsquad.member.domain.vo.Nickname;

// TODO 테스트 분리 필요.
@Import({
        CrewRepositoryImpl.class,
        MemberRepositoryImpl.class,
        CrewQueryDslRepository.class,
        CrewMemberQueryDslRepository.class,
        CrewMemberRepositoryImpl.class
})
class CrewRepositoryTest extends PersistenceLayerTestSupport {

    @Autowired
    private CrewRepository crewRepository;

    @Autowired
    private CrewQueryDslRepository crewQueryDslRepository;

    @Autowired
    private CrewMemberRepository crewMemberRepository;

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Nested
    @DisplayName("Crew 조회를 테스트한다.")
    class Find {

        @Test
        @DisplayName("Crew id 로 조회했을 때, Crew 가 존재하는지 확인한다.")
        void findById() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewRepository.save(CREW(REVI));

            Optional<Crew> optionalCrew = crewRepository.findById(CREW.getId());

            assertThat(optionalCrew).isPresent();
        }

        @Test
        @DisplayName("Crew Id 로 조회하면, Crew 정보를 담은 DTO 를 반환한다.")
        void findCrewById() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewRepository.save(CREW(REVI));

            Optional<CrewDomainDto> OPTIONAL_CREW = crewRepository.findCrewById(CREW.getId());

            assertAll(() -> {
                assertThat(OPTIONAL_CREW).isPresent();
                assertThat(OPTIONAL_CREW.get().getId()).isEqualTo(CREW.getId());
                assertThat(OPTIONAL_CREW.get().getName()).isEqualTo(CREW_NAME);
                assertThat(OPTIONAL_CREW.get().getIntroduce()).isEqualTo(CREW_INTRODUCE);
                assertThat(OPTIONAL_CREW.get().getDetail()).isEqualTo(CREW_DETAIL);
                assertThat(OPTIONAL_CREW.get().getImageUrl()).isNull();
                assertThat(OPTIONAL_CREW.get().getKakaoLink()).isNull();
                assertThat(OPTIONAL_CREW.get().getMemberCnt()).isEqualTo(1);
                assertThat(OPTIONAL_CREW.get().getCrewOwner()).isEqualTo(new SimpleMemberDomainDto(
                        REVI.getId(),
                        new Nickname(REVI_NICKNAME_VALUE),
                        Mbti.ISTP
                ));
            });
        }

        @Test
        @DisplayName("Crew Id 로 조회했을 때, 없으면 오류를 반환한다.")
        void findCrewById2() {
            Long DUMMY_CREW_ID = 1L;

            assertThatThrownBy(() -> crewRepository.getCrewById(DUMMY_CREW_ID))
                    .isExactlyInstanceOf(CrewBusinessException.NotFoundById.class);
        }
    }

    @Nested
    @DisplayName("Exists 를 테스트한다.")
    class Exists {

        @Test
        @DisplayName("Crew Name 으로 조회했을 때, Crew 가 존재하면 true 를 반환한다.")
        void existsByName1() {
            Member REVI = memberJpaRepository.save(REVI());
            crewRepository.save(CREW(REVI));

            boolean exists = crewRepository.existsByName(CREW_NAME);

            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("Crew Name 으로 조회했을 때, Crew 가 존재하지 않으면 false 를 반환한다.")
        void existsByName2() {
            boolean exists = crewRepository.existsByName(CREW_NAME);

            assertThat(exists).isFalse();
        }
    }

    @Nested
    @DisplayName("내가 참여하고 있는 Crew 에 대한 CrewMember 들 조회를 테스트한다.")
    class FetchAllJoinedCrewsByMemberId {

        @Test
        @DisplayName("내가 참여하고 있는 Crew 에 대한 CrewMember 들 조회에 성공한다.")
        void success2() {
            Member ANDONG = memberJpaRepository.save(ANDONG());
            Member KWANGWON = memberJpaRepository.save(KWANGWON());
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW1 = crewRepository.save(CREW_1(ANDONG));
            Crew CREW2 = crewRepository.save(CREW_2(KWANGWON));
            Crew CREW3 = crewRepository.save(CREW_3(REVI));
            LocalDateTime NOW = LocalDateTime.now();
            crewMemberRepository.save(CrewMember.forGeneral(CREW1, REVI, NOW));
            crewMemberRepository.save(CrewMember.forGeneral(CREW2, REVI, NOW.plusMinutes(1)));
            crewMemberRepository.save(CrewMember.forGeneral(CREW2, ANDONG, NOW.plusMinutes(1)));

            List<EnrolledCrewDomainDto> DTOS = crewQueryDslRepository.fetchEnrolledCrewsByMemberId(REVI.getId());

            assertAll(() -> {
                assertThat(DTOS).hasSize(3);

                assertThat(DTOS.get(0).id()).isEqualTo(CREW2.getId());
                assertThat(DTOS.get(0).name()).isEqualTo(CREW2.getName());
                assertThat(DTOS.get(0).imageUrl()).isNull();
                assertThat(DTOS.get(0).isOwner()).isFalse();
                assertThat(DTOS.get(0).owner().id()).isEqualTo(KWANGWON.getId());
                assertThat(DTOS.get(0).owner().nickname()).isEqualTo(KWANGWON_NICKNAME);
                assertThat(DTOS.get(0).owner().mbti()).isSameAs(KWANGWON_MBTI);

                assertThat(DTOS.get(1).id()).isEqualTo(CREW1.getId());
                assertThat(DTOS.get(1).name()).isEqualTo(CREW1.getName());
                assertThat(DTOS.get(1).imageUrl()).isNull();
                assertThat(DTOS.get(1).isOwner()).isFalse();
                assertThat(DTOS.get(1).owner().id()).isEqualTo(ANDONG.getId());
                assertThat(DTOS.get(1).owner().nickname()).isEqualTo(ANDONG_NICKNAME);
                assertThat(DTOS.get(1).owner().mbti()).isSameAs(ANDONG_MBTI);

                assertThat(DTOS.get(2).id()).isEqualTo(CREW3.getId());
                assertThat(DTOS.get(2).name()).isEqualTo(CREW3.getName());
                assertThat(DTOS.get(2).imageUrl()).isNull();
                assertThat(DTOS.get(2).isOwner()).isTrue();
                assertThat(DTOS.get(2).owner().id()).isEqualTo(REVI.getId());
                assertThat(DTOS.get(2).owner().nickname()).isEqualTo(REVI_NICKNAME);
                assertThat(DTOS.get(2).owner().mbti()).isSameAs(REVI_MBTI);
            });
        }
    }

    @Nested
    @DisplayName("내가 개설한 크루 DTO 조회를 테스트한다.")
    class FetchCrewsByMemberId {

        @Test
        @DisplayName("내가 개설한 크루 DTO 조회에 성공한다.")
        void success() {
            Member ANDONG = memberJpaRepository.save(ANDONG());
            Member KWANGWON = memberJpaRepository.save(KWANGWON());
            crewRepository.save(CREW_1(ANDONG));
            crewRepository.save(CREW_2(ANDONG));
            Crew CREW3 = crewRepository.save(CREW_3(KWANGWON));
            Crew CREW4 = crewRepository.save(CREW_4(KWANGWON));
            PageRequest PAGE_REQUEST = PageRequest.of(0, 2);

            Page<CrewDomainDto> DTOS = crewQueryDslRepository.fetchCrewsByMemberId(KWANGWON.getId(), PAGE_REQUEST);

            assertThat(DTOS).hasSize(2);
            assertThat(DTOS.getContent().get(0).getId()).isEqualTo(CREW4.getId());
            assertThat(DTOS.getContent().get(0).getCrewOwner().id()).isEqualTo(KWANGWON.getId());
            assertThat(DTOS.getContent().get(1).getId()).isEqualTo(CREW3.getId());
            assertThat(DTOS.getContent().get(1).getCrewOwner().id()).isEqualTo(KWANGWON.getId());
        }
    }
}
