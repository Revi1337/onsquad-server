package revi1337.onsquad.squad.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.crew.domain.CrewRepository;
import revi1337.onsquad.factory.CrewFactory;
import revi1337.onsquad.factory.ImageFactory;
import revi1337.onsquad.factory.MemberFactory;
import revi1337.onsquad.factory.SquadFactory;
import revi1337.onsquad.image.domain.Image;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.domain.MemberRepository;
import revi1337.onsquad.support.PersistenceLayerTestSupport;

import java.util.Optional;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

@DisplayName("SquadRepository 테스트")
class SquadRepositoryTest extends PersistenceLayerTestSupport {

    @Autowired private SquadRepository squadRepository;
    @Autowired private MemberRepository memberRepository;
    @Autowired private CrewRepository crewRepository;

    @Test
    @DisplayName("id 와 title 로 Squad 와 Member 를 함께 조회한다.")
    public void findSquadWithMemberByIdAndTitle() {
        // given
        Member member = MemberFactory.defaultMember().build();
        Image image = ImageFactory.defaultImage();
        Crew crew = CrewFactory.defaultCrew().image(image).member(member).build();
        Squad squad = SquadFactory.defaultSquad().member(member).crew(crew).build();
        memberRepository.save(member);
        crewRepository.save(crew);
        squadRepository.save(squad);

        // when
        Optional<Squad> optionalSquad = squadRepository.findSquadWithMemberByIdAndTitle(1L, SquadFactory.TITLE);

        // then
        assertSoftly(softly -> {
            softly.assertThat(optionalSquad).isPresent();
            softly.assertThat(optionalSquad.get()).isEqualTo(squad);
            softly.assertThat(optionalSquad.get().getMember()).isEqualTo(member);
        });
    }
}