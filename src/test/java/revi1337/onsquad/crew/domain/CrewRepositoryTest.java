package revi1337.onsquad.crew.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import revi1337.onsquad.config.TestJpaAuditingConfig;
import revi1337.onsquad.config.TestQueryDslConfig;
import revi1337.onsquad.crew.dto.CrewWithMemberAndImage;
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
    public void findByCrewName() {
        // given
        Member member = MemberFactory.defaultMember().build();
        Image image = ImageFactory.defaultImage();
        Crew crew = CrewFactory.defaultCrew().image(image).member(member).build();
        memberRepository.save(member);
        crewRepository.save(crew);

        // when
        CrewWithMemberAndImage crewWithMemberAndImage = crewRepository.findCrewByName(CrewFactory.NAME).get();

        // then
        assertSoftly(softly -> {
            assertThat(crewWithMemberAndImage.crewName()).isEqualTo(CrewFactory.NAME);
            assertThat(crewWithMemberAndImage.crewDetail()).isEqualTo(CrewFactory.DETAIL);
        });
    }
    
    @Test
    @DisplayName("현재 사용자가 생성한 Crew 들을 조회한다.")
    public void findAllByMemberId() {
        List<Crew> findAllByMemberId = crewRepository.findAllByMemberId(1L);
    }
}