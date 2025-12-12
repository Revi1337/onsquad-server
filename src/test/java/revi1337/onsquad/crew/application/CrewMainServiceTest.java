package revi1337.onsquad.crew.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static revi1337.onsquad.common.fixture.AnnounceFixture.ANNOUNCE;
import static revi1337.onsquad.common.fixture.AnnounceValueFixture.ANNOUNCE_CONTENT_VALUE;
import static revi1337.onsquad.common.fixture.AnnounceValueFixture.ANNOUNCE_TITLE_VALUE;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW_WITH_IMAGE;
import static revi1337.onsquad.common.fixture.CrewValueFixture.CREW_DETAIL_VALUE;
import static revi1337.onsquad.common.fixture.CrewValueFixture.CREW_IMAGE_LINK_VALUE;
import static revi1337.onsquad.common.fixture.CrewValueFixture.CREW_INTRODUCE_VALUE;
import static revi1337.onsquad.common.fixture.CrewValueFixture.CREW_NAME_VALUE;
import static revi1337.onsquad.common.fixture.MemberFixture.ANDONG;
import static revi1337.onsquad.common.fixture.MemberFixture.KWANGWON;
import static revi1337.onsquad.common.fixture.MemberFixture.REVI;
import static revi1337.onsquad.hashtag.domain.entity.vo.HashtagType.ACTIVE;
import static revi1337.onsquad.hashtag.domain.entity.vo.HashtagType.ESCAPE;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;
import revi1337.onsquad.announce.domain.entity.Announce;
import revi1337.onsquad.announce.domain.repository.AnnounceRepository;
import revi1337.onsquad.common.ApplicationLayerTestSupport;
import revi1337.onsquad.crew.application.dto.CrewMainDto;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.repository.CrewRepository;
import revi1337.onsquad.crew_hashtag.domain.repository.CrewHashtagRepository;
import revi1337.onsquad.crew_member.domain.entity.CrewMemberFactory;
import revi1337.onsquad.crew_member.domain.repository.CrewMemberRepository;
import revi1337.onsquad.hashtag.domain.entity.Hashtag;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberRepository;

// TODO 크루 메인 페이지는 추후 테스트 해야함. (미완성. Crew Top Member & CrewMember 가 필요.)
@Sql({"/h2-hashtag.sql"})
class CrewMainServiceTest extends ApplicationLayerTestSupport {

    @Autowired
    private CrewRepository crewRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CrewMemberRepository crewMemberRepository;

    @Autowired
    private AnnounceRepository announceRepository;

    @Autowired
    private CrewHashtagRepository crewHashtagRepository;

    @Autowired
    private CrewMainService crewMainService;

    @Test
    @DisplayName("Crew 메인 페이지를 테스트한다.")
    void fetchMain() {
        Member REVI = memberRepository.save(REVI());
        Member ANDONG = memberRepository.save(ANDONG());
        Member KWANGWON = memberRepository.save(KWANGWON());
        Crew CREW = CREW_WITH_IMAGE(REVI, CREW_IMAGE_LINK_VALUE);
        CREW.addCrewMember(CrewMemberFactory.general(CREW, ANDONG, LocalDateTime.now()));
        CREW.addCrewMember(CrewMemberFactory.general(CREW, KWANGWON, LocalDateTime.now()));
        crewRepository.save(CREW);
        crewHashtagRepository.insertBatch(CREW.getId(), Hashtag.fromHashtagTypes(List.of(ACTIVE, ESCAPE)));
        Announce ANNOUNCE = ANNOUNCE(CREW, crewMemberRepository.getByCrewIdAndMemberId(CREW.getId(), REVI.getId()));
        announceRepository.save(ANNOUNCE);

        CrewMainDto CREW_MAIN_DTO = crewMainService.fetchMain(
                REVI.getId(), CREW.getId(), PageRequest.of(0, 10)
        );

        assertAll(() -> {
            assertThat(CREW_MAIN_DTO.crew().id()).isNotNull();
            assertThat(CREW_MAIN_DTO.crew().name()).isEqualTo(CREW_NAME_VALUE);
            assertThat(CREW_MAIN_DTO.crew().introduce()).isEqualTo(CREW_INTRODUCE_VALUE);
            assertThat(CREW_MAIN_DTO.crew().detail()).isEqualTo(CREW_DETAIL_VALUE);
            assertThat(CREW_MAIN_DTO.crew().imageUrl()).isEqualTo(CREW_IMAGE_LINK_VALUE);
            assertThat(CREW_MAIN_DTO.crew().kakaoLink()).isNull();

            assertThat(CREW_MAIN_DTO.announces().get(0).id()).isEqualTo(ANNOUNCE.getId());
            assertThat(CREW_MAIN_DTO.announces().get(0).title()).isEqualTo(ANNOUNCE_TITLE_VALUE);
            assertThat(CREW_MAIN_DTO.announces().get(0).content()).isEqualTo(ANNOUNCE_CONTENT_VALUE);
            assertThat(CREW_MAIN_DTO.announces().get(0).createdAt()).isNotNull();
            assertThat(CREW_MAIN_DTO.announces().get(0).fixed()).isEqualTo(false);
            assertThat(CREW_MAIN_DTO.announces().get(0).fixedAt()).isNull();
        });
    }
}
