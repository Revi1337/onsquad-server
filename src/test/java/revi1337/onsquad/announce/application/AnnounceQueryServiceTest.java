package revi1337.onsquad.announce.application;

import static org.assertj.core.api.Assertions.assertThat;
import static revi1337.onsquad.common.constant.CacheConst.CREW_ANNOUNCE;
import static revi1337.onsquad.common.constant.CacheConst.CREW_ANNOUNCES;
import static revi1337.onsquad.common.fixture.AnnounceFixture.ANNOUNCE;
import static revi1337.onsquad.common.fixture.AnnounceFixture.FIXED_ANNOUNCE;
import static revi1337.onsquad.common.fixture.AnnounceValueFixture.ANNOUNCE_CONTENT_VALUE;
import static revi1337.onsquad.common.fixture.AnnounceValueFixture.ANNOUNCE_TITLE_VALUE;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW;
import static revi1337.onsquad.common.fixture.MemberFixture.REVI;
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_NICKNAME_VALUE;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.PageRequest;
import revi1337.onsquad.announce.application.dto.AnnounceWithFixAndModifyStateDto;
import revi1337.onsquad.announce.application.dto.AnnouncesWithWriteStateDto;
import revi1337.onsquad.announce.domain.entity.Announce;
import revi1337.onsquad.announce.domain.repository.AnnounceJpaRepository;
import revi1337.onsquad.common.ApplicationLayerTestSupport;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.repository.CrewRepository;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.crew_member.domain.entity.vo.CrewRole;
import revi1337.onsquad.crew_member.domain.repository.CrewMemberRepository;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberRepository;

class AnnounceQueryServiceTest extends ApplicationLayerTestSupport {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CrewRepository crewRepository;

    @Autowired
    private CrewMemberRepository crewMemberRepository;

    @Autowired
    private AnnounceJpaRepository announceJpaRepository;

    @Autowired
    private CacheManager concurrentMapCacheManager;

    @Autowired
    private AnnounceQueryService announceQueryService;

    @BeforeEach
    void setUp() {
        concurrentMapCacheManager.getCache(CREW_ANNOUNCE).clear();
        concurrentMapCacheManager.getCache(CREW_ANNOUNCES).clear();
    }

    @Nested
    @DisplayName("Announce 조회를 테스트한다. (Projection)")
    class Find {

        @Test
        @DisplayName("Announce 조회에 성공한다.")
        void success() {
            Member REVI = memberRepository.save(REVI());
            Crew CREW = crewRepository.save(CREW(REVI));
            CrewMember CREW_OWNER = crewMemberRepository.getByCrewIdAndMemberId(CREW.getId(), REVI.getId());
            Announce ANNOUNCE = announceJpaRepository.save(ANNOUNCE(CREW, CREW_OWNER));

            AnnounceWithFixAndModifyStateDto DTO = announceQueryService.findAnnounce(REVI.getId(), CREW.getId(),
                    ANNOUNCE.getId());

            assertThat(DTO.canModify()).isTrue();
            assertThat(DTO.canFix()).isTrue();
            assertThat(DTO.announce().id()).isEqualTo(ANNOUNCE.getId());
            assertThat(DTO.announce().title()).isEqualTo(ANNOUNCE_TITLE_VALUE);
            assertThat(DTO.announce().content()).isEqualTo(ANNOUNCE_CONTENT_VALUE);
            assertThat(DTO.announce().createdAt()).isNotNull();
            assertThat(DTO.announce().fixed()).isFalse();
            assertThat(DTO.announce().fixedAt()).isNull();
            assertThat(DTO.announce().writer().id()).isEqualTo(REVI.getId());
            assertThat(DTO.announce().writer().nickname()).isEqualTo(REVI_NICKNAME_VALUE);
            assertThat(DTO.announce().writer().role()).isEqualTo(CrewRole.OWNER.getText());
        }
    }

    @Nested
    @DisplayName("Announces 조회를 테스트한다. (Projection)")
    class Finds {

        @Test
        @DisplayName("Announces 조회에 성공한다.")
        void success() {
            Member REVI = memberRepository.save(REVI());
            Crew CREW = crewRepository.save(CREW(REVI));
            CrewMember CREW_OWNER = crewMemberRepository.getByCrewIdAndMemberId(CREW.getId(), REVI.getId());
            Announce ANNOUNCE = announceJpaRepository.save(FIXED_ANNOUNCE(CREW, CREW_OWNER));
            PageRequest PAGE_REQUEST = PageRequest.of(0, 10);

            AnnouncesWithWriteStateDto DTO = announceQueryService
                    .findAnnounces(REVI.getId(), CREW.getId(), PAGE_REQUEST);

            assertThat(DTO.canWrite()).isTrue();
            assertThat(DTO.announces().get(0).id()).isEqualTo(ANNOUNCE.getId());
            assertThat(DTO.announces().get(0).title()).isEqualTo(ANNOUNCE_TITLE_VALUE);
            assertThat(DTO.announces().get(0).content()).isEqualTo(ANNOUNCE_CONTENT_VALUE);
            assertThat(DTO.announces().get(0).createdAt()).isNotNull();
            assertThat(DTO.announces().get(0).fixed()).isTrue();
            assertThat(DTO.announces().get(0).fixedAt()).isNotNull();
            assertThat(DTO.announces().get(0).writer().id()).isEqualTo(REVI.getId());
            assertThat(DTO.announces().get(0).writer().nickname()).isEqualTo(REVI_NICKNAME_VALUE);
            assertThat(DTO.announces().get(0).writer().role()).isEqualTo(CrewRole.OWNER.getText());
        }
    }
}
