// TODO 여기 테스트 코드 짜야함.
//package revi1337.onsquad.squad.domain;
//
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import revi1337.onsquad.crew.domain.Crew;
//import revi1337.onsquad.crew.domain.CrewRepository;
//import revi1337.onsquad.crew.domain.vo.Name;
//import revi1337.onsquad.factory.CrewFactory;
//import revi1337.onsquad.factory.ImageFactory;
//import revi1337.onsquad.factory.MemberFactory;
//import revi1337.onsquad.factory.SquadFactory;
//import revi1337.onsquad.image.domain.Image;
//import revi1337.onsquad.member.domain.Member;
//import revi1337.onsquad.member.domain.MemberRepository;
//import revi1337.onsquad.support.PersistenceLayerTestSupport;
//
//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;
//
//import static org.assertj.core.api.SoftAssertions.assertSoftly;
//
//@DisplayName("SquadRepository 테스트")
//class SquadRepositoryTest extends PersistenceLayerTestSupport {
//
//    @Autowired private SquadRepository squadRepository;
//    @Autowired private MemberRepository memberRepository;
//    @Autowired private CrewRepository crewRepository;
//
//    @Test
//    @DisplayName("id 와 title 로 Squad 와 Member 를 함께 조회한다.")
//    public void findSquadWithMemberByIdAndTitle() {
//        // given
//        Member member = MemberFactory.defaultMember().build();
//        Image image = ImageFactory.defaultImage();
//        Crew crew = CrewFactory.defaultCrew().image(image).member(member).build();
//        Squad squad = SquadFactory.defaultSquad().member(member).crew(crew).build();
//        memberRepository.save(member);
//        crewRepository.save(crew);
//        squadRepository.save(squad);
//
//        // when
//        Optional<Squad> optionalSquad = squadRepository.findSquadWithMemberByIdAndTitle(1L, SquadFactory.TITLE);
//
//        // then
//        assertSoftly(softly -> {
//            softly.assertThat(optionalSquad).isPresent();
//            softly.assertThat(optionalSquad.get()).isEqualTo(squad);
//            softly.assertThat(optionalSquad.get().getMember()).isEqualTo(member);
//        });
//    }
//
//    @Test
//    @DisplayName("Crew 에 속한 Squad 들을 조회한다.")
//    public void findSquadsByCrewName() {
//        // given
//        Member member = MemberFactory.defaultMember().build();
//        Image image1 = ImageFactory.defaultImage();
//        Image image2 = ImageFactory.defaultImage();
//        Crew crew1 = CrewFactory.defaultCrew().name(new Name("크루 이름 1")).image(image1).member(member).build();
//        Crew crew2 = CrewFactory.defaultCrew().name(new Name("크루 이름 2")).image(image2).member(member).build();
//        Squad squad1 = SquadFactory.defaultSquad().member(member).crew(crew1).build();
//        Squad squad2 = SquadFactory.defaultSquad().member(member).crew(crew2).build();
//        Squad squad3 = SquadFactory.defaultSquad().member(member).crew(crew1).build();
//        memberRepository.save(member);
//        crewRepository.saveAll(List.of(crew1, crew2));
//        squadRepository.saveAll(List.of(squad1, squad2, squad3));
//
//        // when
//        Page<Squad> pageableSquads = squadRepository.findSquadsByCrewName(new Name("크루 이름 1"), PageRequest.of(0, 10));
//
//        // then
//        assertSoftly(softly -> softly.assertThat(pageableSquads).hasSize(2));
//    }
//
//    @Test
//    @DisplayName("Crew 에 속한 Squad 이 없으면 [] 를 반환한다.")
//    public void findSquadsByCrewName2() {
//        // given
//        Member member = MemberFactory.defaultMember().build();
//        Image image1 = ImageFactory.defaultImage();
//        Image image2 = ImageFactory.defaultImage();
//        Crew crew1 = CrewFactory.defaultCrew().name(new Name("크루 이름 1")).image(image1).member(member).build();
//        Crew crew2 = CrewFactory.defaultCrew().name(new Name("크루 이름 2")).image(image2).member(member).build();
//        Squad squad1 = SquadFactory.defaultSquad().member(member).crew(crew1).build();
//        Squad squad2 = SquadFactory.defaultSquad().member(member).crew(crew2).build();
//        Squad squad3 = SquadFactory.defaultSquad().member(member).crew(crew1).build();
//        memberRepository.save(member);
//        crewRepository.saveAll(List.of(crew1, crew2));
//        squadRepository.saveAll(List.of(squad1, squad2, squad3));
//
//        // when
//        Page<Squad> pageableSquads = squadRepository.findSquadsByCrewName(new Name(UUID.randomUUID().toString()), PageRequest.of(0, 10));
//
//        // then
//        assertSoftly(softly -> softly.assertThat(pageableSquads).hasSize(0));
//    }
//}