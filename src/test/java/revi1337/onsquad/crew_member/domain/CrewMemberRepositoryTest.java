package revi1337.onsquad.crew_member.domain;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.crew.domain.CrewRepository;
import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.crew_member.dto.EnrolledCrewMemberDto;
import revi1337.onsquad.factory.CrewFactory;
import revi1337.onsquad.factory.CrewMemberFactory;
import revi1337.onsquad.factory.ImageFactory;
import revi1337.onsquad.factory.MemberFactory;
import revi1337.onsquad.image.domain.Image;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.domain.MemberRepository;
import revi1337.onsquad.support.PersistenceLayerTestSupport;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;


@DisplayName("CrewMemberRepository 테스트")
class CrewMemberRepositoryTest extends PersistenceLayerTestSupport {

    @Autowired private CrewMemberRepository crewMemberRepository;
    @Autowired private MemberRepository memberRepository;
    @Autowired private CrewRepository crewRepository;

    @Test
    @DisplayName("Member Id 를 통해 CrewMember 를 찾는다. (1)")
    public void findCrewMemberByMemberId() {
        Member member = MemberFactory.defaultMember().build();
        Image image = ImageFactory.defaultImage();
        Crew crew = CrewFactory.defaultCrew().member(member).image(image).build();
        CrewMember crewMember = CrewMemberFactory.defaultCrewMember().member(member).crew(crew).build();
        memberRepository.save(member);
        crewRepository.save(crew);
        crewMemberRepository.save(crewMember);

        // when
        Optional<CrewMember> findCrewMember = crewMemberRepository.findCrewMemberByMemberId(1L);

        assertThat(findCrewMember).isPresent();
    }

    @Test
    @DisplayName("Member Id 를 통해 CrewMember 를 찾는다. (2)")
    public void findCrewMemberByMemberId2() {
        Member member = MemberFactory.defaultMember().build();
        Image image = ImageFactory.defaultImage();
        Crew crew = CrewFactory.defaultCrew().member(member).image(image).build();
        CrewMember crewMember = CrewMemberFactory.defaultCrewMember().member(member).crew(crew).build();
        memberRepository.save(member);
        crewRepository.save(crew);
        crewMemberRepository.save(crewMember);

        // when
        Optional<CrewMember> findCrewMember = crewMemberRepository.findCrewMemberByMemberId(2L);

        assertThat(findCrewMember).isNotPresent();
    }

    @Test
    @DisplayName("Member 가 Crew 에 가입신청을 한 이력이 있으면 true 를 반환한다.")
    public void existsCrewMember() {
        // given
        Member member = MemberFactory.defaultMember().build();
        Image image = ImageFactory.defaultImage();
        Crew crew = CrewFactory.defaultCrew().member(member).image(image).build();
        CrewMember crewMember = CrewMemberFactory.defaultCrewMember().member(member).crew(crew).build();
        memberRepository.save(member);
        crewRepository.save(crew);
        crewMemberRepository.save(crewMember);

        // when
        boolean memberExists = crewMemberRepository.existsCrewMember(1L);

        // then
        assertThat(memberExists).isTrue();
    }

    @Test
    @DisplayName("Member 가 Crew 에 가입신청을 한 이력이 없으면 false 를 반환한다.")
    public void existsCrewMember2() {
        // given
        Member member = MemberFactory.defaultMember().build();
        Image image = ImageFactory.defaultImage();
        Crew crew = CrewFactory.defaultCrew().member(member).image(image).build();
        CrewMember crewMember = CrewMemberFactory.defaultCrewMember().member(member).crew(crew).build();
        memberRepository.save(member);
        crewRepository.save(crew);
        crewMemberRepository.save(crewMember);

        // when
        boolean memberExists = crewMemberRepository.existsCrewMember(2L);

        // then
        assertThat(memberExists).isFalse();
    }

    @Test
    @DisplayName("특정 Member 가 생성한 Crew 에 속한 CrewMember 들을 조회한다.")
    public void findMembersForSpecifiedCrew() {
        // given
        Member member1 = MemberFactory.defaultMember().build();
        Member member2 = MemberFactory.defaultMember().build();
        Image image1 = ImageFactory.defaultImage();
        Image image2 = ImageFactory.defaultImage();
        Image image3 = ImageFactory.defaultImage();
        Crew crew1 = CrewFactory.defaultCrew().name(new Name("크루 이름 1")).image(image1).member(member1).build();
        Crew crew2 = CrewFactory.defaultCrew().name(new Name("크루 이름 2")).image(image2).member(member1).build();
        Crew crew3 = CrewFactory.defaultCrew().name(new Name("크루 이름 3")).image(image3).member(member2).build();
        CrewMember crewMember1 = CrewMemberFactory.defaultCrewMember().member(member1).crew(crew1).build();
        CrewMember crewMember2 = CrewMemberFactory.defaultCrewMember().member(member1).crew(crew2).build();
        CrewMember crewMember3 = CrewMemberFactory.defaultCrewMember().member(member2).crew(crew3).build();
        memberRepository.saveAll(List.of(member1, member2));
        crewRepository.saveAll(List.of(crew1, crew2, crew3));
        crewMemberRepository.saveAll(List.of(crewMember1, crewMember2, crewMember3));

        // when
        List<EnrolledCrewMemberDto> enrolledCrewMemberDtos = crewMemberRepository.findMembersForSpecifiedCrew(new Name("크루 이름 1"), 1L);

        // then
        assertThat(enrolledCrewMemberDtos.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("특정 Member 가 특정 Crew 에 참여요청을 한 이력이 있으면 실패한다.")
    public void findCrewMemberByCrewIdAndMemberId1() {
        // given
        Member member1 = MemberFactory.defaultMember().build();
        Member member2 = MemberFactory.defaultMember().build();
        Image image1 = ImageFactory.defaultImage();
        Image image2 = ImageFactory.defaultImage();
        Image image3 = ImageFactory.defaultImage();
        Crew crew1 = CrewFactory.defaultCrew().name(new Name("크루 이름 1")).image(image1).member(member1).build();
        Crew crew2 = CrewFactory.defaultCrew().name(new Name("크루 이름 2")).image(image2).member(member1).build();
        Crew crew3 = CrewFactory.defaultCrew().name(new Name("크루 이름 3")).image(image3).member(member2).build();
        CrewMember crewMember1 = CrewMemberFactory.defaultCrewMember().member(member1).crew(crew1).build();
        CrewMember crewMember2 = CrewMemberFactory.defaultCrewMember().member(member1).crew(crew2).build();
        CrewMember crewMember3 = CrewMemberFactory.defaultCrewMember().member(member2).crew(crew3).build();
        memberRepository.saveAll(List.of(member1, member2));
        crewRepository.saveAll(List.of(crew1, crew2, crew3));
        crewMemberRepository.saveAll(List.of(crewMember1, crewMember2, crewMember3));

        // when
        Optional<CrewMember> findCrewMember = crewMemberRepository.findCrewMemberByCrewIdAndMemberId(1L, 3L);

        // then
        assertThat(findCrewMember).isNotPresent();
    }

    @Test
    @DisplayName("특정 Member 가 특정 Crew 에 참여요청을 한 이력이 없으면 성공한다.")
    public void findCrewMemberByCrewIdAndMemberId2() {
        // given
        Member member1 = MemberFactory.defaultMember().build();
        Member member2 = MemberFactory.defaultMember().build();
        Image image1 = ImageFactory.defaultImage();
        Image image2 = ImageFactory.defaultImage();
        Image image3 = ImageFactory.defaultImage();
        Crew crew1 = CrewFactory.defaultCrew().name(new Name("크루 이름 1")).image(image1).member(member1).build();
        Crew crew2 = CrewFactory.defaultCrew().name(new Name("크루 이름 2")).image(image2).member(member1).build();
        Crew crew3 = CrewFactory.defaultCrew().name(new Name("크루 이름 3")).image(image3).member(member2).build();
        CrewMember crewMember1 = CrewMemberFactory.defaultCrewMember().member(member1).crew(crew1).build();
        CrewMember crewMember2 = CrewMemberFactory.defaultCrewMember().member(member1).crew(crew2).build();
        CrewMember crewMember3 = CrewMemberFactory.defaultCrewMember().member(member2).crew(crew3).build();
        memberRepository.saveAll(List.of(member1, member2));
        crewRepository.saveAll(List.of(crew1, crew2, crew3));
        crewMemberRepository.saveAll(List.of(crewMember1, crewMember2, crewMember3));

        // when
        Optional<CrewMember> findCrewMember = crewMemberRepository.findCrewMemberByCrewIdAndMemberId(1L, 2L);

        // then
        assertSoftly(softly -> {
            assertThat(findCrewMember).isPresent();
            assertThat(findCrewMember.get().getMember().getId()).isEqualTo(1L);
            assertThat(findCrewMember.get().getCrew().getId()).isEqualTo(2L);
        });
    }
}
