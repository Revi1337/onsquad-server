package revi1337.onsquad.squad.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static revi1337.onsquad.category.domain.entity.vo.CategoryType.ALL;
import static revi1337.onsquad.category.domain.entity.vo.CategoryType.GAME;
import static revi1337.onsquad.category.domain.entity.vo.CategoryType.MANGACAFE;
import static revi1337.onsquad.category.domain.entity.vo.CategoryType.MOVIE;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW;
import static revi1337.onsquad.common.fixture.CrewMemberFixture.GENERAL_CREW_MEMBER;
import static revi1337.onsquad.common.fixture.MemberFixture.ANDONG;
import static revi1337.onsquad.common.fixture.MemberFixture.KWANGWON;
import static revi1337.onsquad.common.fixture.MemberFixture.REVI;
import static revi1337.onsquad.common.fixture.SquadFixture.SQUAD;
import static revi1337.onsquad.common.fixture.SquadMemberFixture.GENERAL_SQUAD_MEMBER;
import static revi1337.onsquad.common.fixture.SquadValueFixture.SQUAD_ADDRESS_DETAIL_VALUE;
import static revi1337.onsquad.common.fixture.SquadValueFixture.SQUAD_ADDRESS_VALUE;
import static revi1337.onsquad.common.fixture.SquadValueFixture.SQUAD_CAPACITY_VALUE;
import static revi1337.onsquad.common.fixture.SquadValueFixture.SQUAD_CONTENT_VALUE;
import static revi1337.onsquad.common.fixture.SquadValueFixture.SQUAD_DISCORD_LINK_VALUE;
import static revi1337.onsquad.common.fixture.SquadValueFixture.SQUAD_KAKAO_LINK_VALUE;
import static revi1337.onsquad.common.fixture.SquadValueFixture.SQUAD_TITLE_VALUE;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import revi1337.onsquad.common.ApplicationLayerTestSupport;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.repository.CrewJpaRepository;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.crew_member.domain.repository.CrewMemberJpaRepository;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberJpaRepository;
import revi1337.onsquad.squad.application.dto.SquadCreateDto;
import revi1337.onsquad.squad.domain.entity.Squad;
import revi1337.onsquad.squad.domain.repository.SquadJpaRepository;
import revi1337.onsquad.squad.domain.repository.SquadRepository;
import revi1337.onsquad.squad.error.SquadBusinessException.InsufficientAuthority;
import revi1337.onsquad.squad_category.domain.repository.SquadCategoryJpaRepository;
import revi1337.onsquad.squad_comment.domain.entity.SquadComment;
import revi1337.onsquad.squad_comment.domain.repository.SquadCommentJpaRepository;
import revi1337.onsquad.squad_member.domain.entity.SquadMember;
import revi1337.onsquad.squad_member.domain.repository.SquadMemberJpaRepository;

class SquadCommandServiceTest extends ApplicationLayerTestSupport {

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Autowired
    private CrewJpaRepository crewJpaRepository;

    @Autowired
    private CrewMemberJpaRepository crewMemberJpaRepository;

    @Autowired
    private SquadMemberJpaRepository squadMemberJpaRepository;

    @Autowired
    private SquadJpaRepository squadJpaRepository;

    @Autowired
    private SquadRepository squadRepository;

    @Autowired
    private SquadCommandService squadCommandService;

    @Autowired
    private SquadCategoryJpaRepository squadCategoryJpaRepository;

    @Autowired
    private SquadCommentJpaRepository squadCommentJpaRepository;

    @Nested
    @DisplayName("스쿼드 생성을 테스트한다.")
    class Create {

        @Test
        @DisplayName("스쿼드 생성에 성공한다.")
        void success() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            Member ANDONG = memberJpaRepository.save(ANDONG());
            CrewMember GENERAL_CREW_MEMBER = crewMemberJpaRepository.save(GENERAL_CREW_MEMBER(CREW, ANDONG));
            SquadCreateDto CREATE_DTO = new SquadCreateDto(
                    SQUAD_TITLE_VALUE,
                    SQUAD_CONTENT_VALUE,
                    SQUAD_CAPACITY_VALUE,
                    SQUAD_ADDRESS_VALUE,
                    SQUAD_ADDRESS_DETAIL_VALUE,
                    List.of(GAME, MOVIE),
                    SQUAD_KAKAO_LINK_VALUE,
                    SQUAD_DISCORD_LINK_VALUE
            );
            clearPersistenceContext();

            Long SQUAD_ID = squadCommandService.newSquad(ANDONG.getId(), CREW.getId(), CREATE_DTO);
            clearPersistenceContext();

            assertThat(squadRepository.findById(SQUAD_ID)).isPresent();
            assertThat(squadCategoryJpaRepository.findAllBySquadId(SQUAD_ID)).hasSize(2);
            assertThat(squadMemberJpaRepository
                    .findBySquadIdAndMemberId(SQUAD_ID, GENERAL_CREW_MEMBER.getId()))
                    .isPresent();
        }

        @Test
        @DisplayName("ALL 카테고리를 제외하고 스쿼드 생성에 성공한다.")
        void success2() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            Member ANDONG = memberJpaRepository.save(ANDONG());
            CrewMember GENERAL_CREW_MEMBER = crewMemberJpaRepository.save(GENERAL_CREW_MEMBER(CREW, ANDONG));
            SquadCreateDto CREATE_DTO = new SquadCreateDto(
                    SQUAD_TITLE_VALUE,
                    SQUAD_CONTENT_VALUE,
                    SQUAD_CAPACITY_VALUE,
                    SQUAD_ADDRESS_VALUE,
                    SQUAD_ADDRESS_DETAIL_VALUE,
                    List.of(GAME, MOVIE, MANGACAFE, ALL),
                    SQUAD_KAKAO_LINK_VALUE,
                    SQUAD_DISCORD_LINK_VALUE
            );
            clearPersistenceContext();

            Long SQUAD_ID = squadCommandService.newSquad(ANDONG.getId(), CREW.getId(), CREATE_DTO);
            clearPersistenceContext();

            assertThat(squadRepository.findById(SQUAD_ID)).isPresent();
            assertThat(squadCategoryJpaRepository.findAllBySquadId(SQUAD_ID)).hasSize(3);
            assertThat(squadMemberJpaRepository
                    .findBySquadIdAndMemberId(SQUAD_ID, GENERAL_CREW_MEMBER.getId()))
                    .isPresent();
        }

        @Test
        @DisplayName("ALL 카테고리 하나면, 카테고리 없이 스쿼드가 생성된다.")
        void success3() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            Member ANDONG = memberJpaRepository.save(ANDONG());
            CrewMember GENERAL_CREW_MEMBER = crewMemberJpaRepository.save(GENERAL_CREW_MEMBER(CREW, ANDONG));
            SquadCreateDto CREATE_DTO = new SquadCreateDto(
                    SQUAD_TITLE_VALUE,
                    SQUAD_CONTENT_VALUE,
                    SQUAD_CAPACITY_VALUE,
                    SQUAD_ADDRESS_VALUE,
                    SQUAD_ADDRESS_DETAIL_VALUE,
                    List.of(ALL),
                    SQUAD_KAKAO_LINK_VALUE,
                    SQUAD_DISCORD_LINK_VALUE
            );
            clearPersistenceContext();

            Long SQUAD_ID = squadCommandService.newSquad(ANDONG.getId(), CREW.getId(), CREATE_DTO);
            clearPersistenceContext();

            assertThat(squadRepository.findById(SQUAD_ID)).isPresent();
            assertThat(squadCategoryJpaRepository.findAllBySquadId(SQUAD_ID)).hasSize(0);
            assertThat(squadMemberJpaRepository
                    .findBySquadIdAndMemberId(SQUAD_ID, GENERAL_CREW_MEMBER.getId()))
                    .isPresent();
        }

        @Test
        @DisplayName("카테고리가 없으면, 카테고리 없이 스쿼드가 생성된다.")
        void success4() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            Member ANDONG = memberJpaRepository.save(ANDONG());
            CrewMember GENERAL_CREW_MEMBER = crewMemberJpaRepository.save(GENERAL_CREW_MEMBER(CREW, ANDONG));
            SquadCreateDto CREATE_DTO = new SquadCreateDto(
                    SQUAD_TITLE_VALUE,
                    SQUAD_CONTENT_VALUE,
                    SQUAD_CAPACITY_VALUE,
                    SQUAD_ADDRESS_VALUE,
                    SQUAD_ADDRESS_DETAIL_VALUE,
                    List.of(),
                    SQUAD_KAKAO_LINK_VALUE,
                    SQUAD_DISCORD_LINK_VALUE
            );
            clearPersistenceContext();

            Long SQUAD_ID = squadCommandService.newSquad(ANDONG.getId(), CREW.getId(), CREATE_DTO);
            clearPersistenceContext();

            assertThat(squadRepository.findById(SQUAD_ID)).isPresent();
            assertThat(squadCategoryJpaRepository.findAllBySquadId(SQUAD_ID)).hasSize(0);
            assertThat(squadMemberJpaRepository
                    .findBySquadIdAndMemberId(SQUAD_ID, GENERAL_CREW_MEMBER.getId()))
                    .isPresent();
        }
    }

    @Nested
    @DisplayName("스쿼드 삭제를 테스트한다.")
    class Delete {

        @Test
        @DisplayName("스쿼드 리더면 스쿼드 삭제에 성공한다.")
        void success() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            CrewMember OWNER = crewMemberJpaRepository.findByCrewIdAndMemberId(CREW.getId(), REVI.getId()).get();
            Squad SQUAD = squadJpaRepository.save(SQUAD(OWNER, CREW));
            Member ANDONG = memberJpaRepository.save(ANDONG());
            CrewMember GENERAL_CREW_MEMBER1 = crewMemberJpaRepository.save(GENERAL_CREW_MEMBER(CREW, ANDONG));
            SquadMember GENERAL_SQUAD_MEMBER1 = squadMemberJpaRepository.save(GENERAL_SQUAD_MEMBER(SQUAD, GENERAL_CREW_MEMBER1));
            Member KWANGWON = memberJpaRepository.save(KWANGWON());
            CrewMember GENERAL_CREW_MEMBER2 = crewMemberJpaRepository.save(GENERAL_CREW_MEMBER(CREW, KWANGWON));
            SquadMember GENERAL_SQUAD_MEMBER2 = squadMemberJpaRepository.save(GENERAL_SQUAD_MEMBER(SQUAD, GENERAL_CREW_MEMBER2));
            SquadComment parent = squadCommentJpaRepository.save(SquadComment.create("content", SQUAD, GENERAL_CREW_MEMBER1));
            SquadComment reply = squadCommentJpaRepository.save(SquadComment.createReply(parent, "reply", SQUAD, GENERAL_CREW_MEMBER2));
            clearPersistenceContext();

            squadCommandService.deleteSquad(REVI.getId(), CREW.getId(), SQUAD.getId());
            clearPersistenceContext();

            assertThat(squadMemberJpaRepository.findById(GENERAL_SQUAD_MEMBER1.getId())).isEmpty();
            assertThat(squadMemberJpaRepository.findById(GENERAL_SQUAD_MEMBER2.getId())).isEmpty();
            assertThat(squadCommentJpaRepository.findById(parent.getId())).isEmpty();
            assertThat(squadCommentJpaRepository.findById(reply.getId())).isEmpty();
            assertThat(squadMemberJpaRepository.findById(SQUAD.getId())).isEmpty();
        }

        @Test
        @DisplayName("스쿼드 리더가 아니면 스쿼드 삭제에 실패한다.")
        void fail() {
            Member REVI = memberJpaRepository.save(REVI());
            Crew CREW = crewJpaRepository.save(CREW(REVI));
            CrewMember OWNER = crewMemberJpaRepository.findByCrewIdAndMemberId(CREW.getId(), REVI.getId()).get();
            Squad SQUAD = squadJpaRepository.save(SQUAD(OWNER, CREW));
            Member ANDONG = memberJpaRepository.save(ANDONG());
            CrewMember GENERAL_CREW_MEMBER1 = crewMemberJpaRepository.save(GENERAL_CREW_MEMBER(CREW, ANDONG));
            SquadMember GENERAL_SQUAD_MEMBER1 = squadMemberJpaRepository.save(GENERAL_SQUAD_MEMBER(SQUAD, GENERAL_CREW_MEMBER1));
            Member KWANGWON = memberJpaRepository.save(KWANGWON());
            CrewMember GENERAL_CREW_MEMBER2 = crewMemberJpaRepository.save(GENERAL_CREW_MEMBER(CREW, KWANGWON));
            SquadMember GENERAL_SQUAD_MEMBER2 = squadMemberJpaRepository.save(GENERAL_SQUAD_MEMBER(SQUAD, GENERAL_CREW_MEMBER2));
            clearPersistenceContext();

            assertThatThrownBy(() -> squadCommandService.deleteSquad(KWANGWON.getId(), CREW.getId(), SQUAD.getId()))
                    .isExactlyInstanceOf(InsufficientAuthority.class);
        }
    }
}
