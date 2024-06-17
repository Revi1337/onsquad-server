package revi1337.onsquad.crew_member.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import revi1337.onsquad.config.TestJpaAuditingConfig;
import revi1337.onsquad.config.TestQueryDslConfig;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.crew.domain.CrewRepository;
import revi1337.onsquad.factory.CrewFactory;
import revi1337.onsquad.factory.CrewMemberFactory;
import revi1337.onsquad.factory.MemberFactory;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.domain.MemberRepository;

import static org.assertj.core.api.Assertions.assertThat;


@Import({TestJpaAuditingConfig.class, TestQueryDslConfig.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest(showSql = false)
@DisplayName("CrewMemberRepository 테스트")
class CrewMemberRepositoryTest {

    @Autowired private CrewMemberRepository crewMemberRepository;
    @Autowired private MemberRepository memberRepository;
    @Autowired private CrewRepository crewRepository;

    @BeforeEach
    void tearDown() {
        Member member = MemberFactory.defaultMember().build();
        Crew crew = CrewFactory.defaultCrew().member(member).build();
        CrewMember crewMember = CrewMemberFactory.defaultCrewMember().member(member).crew(crew).build();
        memberRepository.save(member);
        crewRepository.save(crew);
        crewMemberRepository.save(crewMember);
    }

    @Test
    @DisplayName("Member 가 이미 Crew 에 가입신청을 하였으면 실패한다.")
    public void existsCrewMember() {
        // when
        boolean memberExists = crewMemberRepository.existsCrewMember(1L);

        // then
        assertThat(memberExists).isTrue();
    }

    @Test
    @DisplayName("Member 가 이미 Crew 에 가입신청을 하지 않았으면 성공한다.")
    public void existsCrewMember2() {
        // when
        boolean memberExists = crewMemberRepository.existsCrewMember(2L);

        // then
        assertThat(memberExists).isFalse();
    }
}