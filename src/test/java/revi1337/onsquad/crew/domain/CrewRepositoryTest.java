package revi1337.onsquad.crew.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import revi1337.onsquad.config.TestJpaAuditingConfig;
import revi1337.onsquad.config.TestQueryDslConfig;
import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.crew.dto.CrewWithMemberAndImageDto;
import revi1337.onsquad.factory.CrewFactory;
import revi1337.onsquad.factory.ImageFactory;
import revi1337.onsquad.factory.MemberFactory;
import revi1337.onsquad.image.domain.Image;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.domain.MemberRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;


@Import({TestJpaAuditingConfig.class, TestQueryDslConfig.class})
@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CrewRepositoryTest {

    @Autowired private CrewRepository crewRepository;
    @Autowired private MemberRepository memberRepository;

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
            assertThat(crewWithMemberAndImageDto.crewName()).isEqualTo(CrewFactory.NAME);
            assertThat(crewWithMemberAndImageDto.crewDetail()).isEqualTo(CrewFactory.DETAIL);
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
    @DisplayName("현재 사용자가 생성한 Crew 들을 조회한다.")
    public void findAllByMemberId() {
        List<Crew> findAllByMemberId = crewRepository.findAllByMemberId(1L);
    }
}