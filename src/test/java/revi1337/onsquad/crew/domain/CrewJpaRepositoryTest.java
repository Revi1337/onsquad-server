package revi1337.onsquad.crew.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.crew_member.domain.CrewMember;
import revi1337.onsquad.crew_member.domain.CrewMemberJpaRepository;
import revi1337.onsquad.factory.CrewFactory;
import revi1337.onsquad.factory.ImageFactory;
import revi1337.onsquad.factory.MemberFactory;
import revi1337.onsquad.image.domain.Image;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.domain.MemberJpaRepository;
import revi1337.onsquad.support.PersistenceLayerTestSupport;


class CrewJpaRepositoryTest extends PersistenceLayerTestSupport {

    @Autowired
    private CrewJpaRepository crewJpaRepository;
    @Autowired
    private MemberJpaRepository memberRepository;
    @Autowired
    private CrewMemberJpaRepository crewMemberRepository;

    @Test
    @DisplayName("Crew 이름으로 Crew 명이 중복되는지 확인한다. (1)")
    public void existsByName() {
        // given
        Member member = MemberFactory.defaultMember().build();
        Image image = ImageFactory.defaultImage();
        Crew crew = CrewFactory.defaultCrew().image(image).member(member).build();
        memberRepository.save(member);
        crewJpaRepository.save(crew);

        // when
        boolean exists = crewJpaRepository.existsByName(CrewFactory.NAME);

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
        boolean exists = crewJpaRepository.existsByName(CrewFactory.NAME);

        // then
        assertThat(exists).isFalse();
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
        crewJpaRepository.save(crew);

        // when
        crew.updateCrew("변경 크루 이름", "변경 크루 소개", "변경 크루 디테일", List.of("해시태그1", "해시태그2"), "변경 카카오 링크",
                imageRemoteAddress);
        crewJpaRepository.saveAndFlush(crew);

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
        crewJpaRepository.save(crew);
        crewMemberRepository.saveAll(List.of(crewMember, crewMember2));

        // when
        Optional<Crew> findCrewOptional = crewJpaRepository.findByNameWithCrewMembers(crew.getName());

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