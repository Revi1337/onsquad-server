package revi1337.onsquad.crew.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.crew.dto.CrewWithMemberAndImageDto;
import revi1337.onsquad.crew.dto.OwnedCrewsDto;
import revi1337.onsquad.crew_member.domain.CrewMember;
import revi1337.onsquad.crew_member.domain.CrewMemberRepository;
import revi1337.onsquad.factory.CrewFactory;
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


class CrewJpaRepositoryTest extends PersistenceLayerTestSupport {

    @Autowired private CrewRepository crewRepository;
    @Autowired private MemberRepository memberRepository;
    @Autowired private CrewMemberRepository crewMemberRepository;

    @Test
    @DisplayName("Crew 이름으로 Crew 명이 중복되는지 확인한다. (1)")
    public void existsByName() {
        // given
        Member member = MemberFactory.defaultMember().build();
        Image image = ImageFactory.defaultImage();
        Crew crew = CrewFactory.defaultCrew().image(image).member(member).build();
        memberRepository.save(member);
        crewRepository.save(crew);

        // when
        boolean exists = crewRepository.existsByName(CrewFactory.NAME);

        // then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Crew 이름으로 Crew 명이 중복되는지 확인한다. (2)")
    public void existsByName2() {
        // given
        Member member = MemberFactory.defaultMember().build();
        memberRepository.save(member);

        // when
        boolean exists = crewRepository.existsByName(CrewFactory.NAME);

        // then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Crew 이름으로 Crew 단일 게시글을 조회한다.")
    public void findCrewByName() {
        // given
        Member member = MemberFactory.defaultMember().build();
        Image image = ImageFactory.defaultImage();
        Crew crew = CrewFactory.defaultCrew().image(image).member(member).build();
        memberRepository.save(member);
        crewRepository.save(crew);

        // when
        CrewWithMemberAndImageDto crewWithMemberAndImageDto = crewRepository.findCrewByName(CrewFactory.NAME).get();

        // then
        assertSoftly(softly -> {
            softly.assertThat(crewWithMemberAndImageDto.crewName()).isEqualTo(CrewFactory.NAME);
            softly.assertThat(crewWithMemberAndImageDto.crewDetail()).isEqualTo(CrewFactory.DETAIL);
        });
    }

    @Test
    @DisplayName("생성된 모든 Crew 들을 조회한다.")
    public void findCrewsByName() {
        // given
        Member member1 = MemberFactory.defaultMember().build();
        Member member2 = MemberFactory.defaultMember().build();
        Image image1 = ImageFactory.defaultImage();
        Image image2 = ImageFactory.defaultImage();
        Image image3 = ImageFactory.defaultImage();
        Image image4 = ImageFactory.defaultImage();
        Image image5 = ImageFactory.defaultImage();
        Crew crew1 = CrewFactory.defaultCrew().name(new Name("크루 이름 1")).image(image1).member(member1).build();
        Crew crew2 = CrewFactory.defaultCrew().name(new Name("크루 이름 2")).image(image2).member(member1).build();
        Crew crew3 = CrewFactory.defaultCrew().name(new Name("크루 이름 3")).image(image3).member(member2).build();
        Crew crew4 = CrewFactory.defaultCrew().name(new Name("크루 이름 4")).image(image4).member(member2).build();
        Crew crew5 = CrewFactory.defaultCrew().name(new Name("크루 이름 5")).image(image5).member(member2).build();
        memberRepository.saveAll(List.of(member1, member2));
        crewRepository.saveAll(List.of(crew1, crew2, crew3, crew4, crew5));

        // when
        List<CrewWithMemberAndImageDto> crewWithMemberAndImageDtoList = crewRepository.findCrewsByName();

        // then
        assertThat(crewWithMemberAndImageDtoList.size()).isEqualTo(5);
    }
    
    @Test
    @DisplayName("특정 사용자가 생성한 Crew 들을 조회한다.")
    public void findAllByMemberId() {
        // given
        Member member1 = MemberFactory.defaultMember().build();
        Member member2 = MemberFactory.defaultMember().build();
        Image image1 = ImageFactory.defaultImage();
        Image image2 = ImageFactory.defaultImage();
        Image image3 = ImageFactory.defaultImage();
        Image image4 = ImageFactory.defaultImage();
        Image image5 = ImageFactory.defaultImage();
        Crew crew1 = CrewFactory.defaultCrew().name(new Name("크루 이름 1")).image(image1).member(member1).build();
        Crew crew2 = CrewFactory.defaultCrew().name(new Name("크루 이름 2")).image(image2).member(member1).build();
        Crew crew3 = CrewFactory.defaultCrew().name(new Name("크루 이름 3")).image(image3).member(member2).build();
        Crew crew4 = CrewFactory.defaultCrew().name(new Name("크루 이름 4")).image(image4).member(member2).build();
        Crew crew5 = CrewFactory.defaultCrew().name(new Name("크루 이름 5")).image(image5).member(member2).build();
        memberRepository.saveAll(List.of(member1, member2));
        crewRepository.saveAll(List.of(crew1, crew2, crew3, crew4, crew5));

        // when
        List<OwnedCrewsDto> ownedCrews = crewRepository.findOwnedCrews(member2.getId());

        // then
        assertThat(ownedCrews.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("Crew 정보 업데이트를 성공적으로 진행한다. with image (1)")
    public void updateCrewTest() {
        // given
        Member member = MemberFactory.defaultMember().build();
        Image image = ImageFactory.defaultImage();
        Crew crew = CrewFactory.defaultCrew().image(image).member(member).build();
        String imageRemoteAddress = "[Remote Address]";
        memberRepository.save(member);
        crewRepository.save(crew);

        // when
        crew.updateCrew("변경 크루 이름", "변경 크루 소개", "변경 크루 디테일", List.of("해시태그1", "해시태그2"), "변경 카카오 링크", imageRemoteAddress);
        crewRepository.saveAndFlush(crew);

        // then
        assertSoftly(softly -> {
            softly.assertThat(crew.getName().getValue()).isEqualTo("변경 크루 이름");
            softly.assertThat(crew.getIntroduce().getValue()).isEqualTo("변경 크루 소개");
            softly.assertThat(crew.getDetail().getValue()).isEqualTo("변경 크루 디테일");
            softly.assertThat(crew.getHashTags().getValue()).isEqualTo("해시태그1,해시태그2");
            softly.assertThat(crew.getKakaoLink()).isEqualTo("변경 카카오 링크");
            softly.assertThat(crew.getImage().getImageUrl()).isEqualTo(imageRemoteAddress);
        });
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Test
    @DisplayName("Crew 와 Crew 에 속한 CrewMember 가 잘 조회되는지 확인한다.")
    public void findCrewWithMembersByName() {
        // given
        Member member = MemberFactory.defaultMember().build();
        Image image = ImageFactory.defaultImage();
        Crew crew = CrewFactory.defaultCrew().image(image).member(member).build();
        CrewMember crewMember = CrewMember.forGeneral(crew, member);
        Member member2 = MemberFactory.defaultMember().build();
        CrewMember crewMember2 = CrewMember.forGeneral(crew, member2);
        memberRepository.saveAll(List.of(member, member2));
        crewRepository.save(crew);
        crewMemberRepository.saveAll(List.of(crewMember, crewMember2));

        // when
        Optional<Crew> findCrewOptional = crewRepository.findCrewWithMembersByName(crew.getName());

        // then
        assertSoftly(softly -> {
            softly.assertThat(findCrewOptional).isPresent();
            softly.assertThat(findCrewOptional.get().getName()).isEqualTo(crew.getName());
            softly.assertThat(findCrewOptional.get().getCrewMembers()).hasSize(2);
            softly.assertThat(findCrewOptional.get().getCrewMembers().get(0).getMember().getId()).isEqualTo(1L);
            softly.assertThat(findCrewOptional.get().getCrewMembers().get(1).getMember().getId()).isEqualTo(2L);
        });
    }
}