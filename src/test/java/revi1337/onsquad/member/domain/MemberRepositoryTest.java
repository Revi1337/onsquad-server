package revi1337.onsquad.member.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.crew.domain.CrewJpaRepository;
import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.crew_member.domain.CrewMember;
import revi1337.onsquad.crew_member.domain.CrewMemberJpaRepository;
import revi1337.onsquad.factory.CrewFactory;
import revi1337.onsquad.factory.ImageFactory;
import revi1337.onsquad.member.domain.vo.Email;
import revi1337.onsquad.member.domain.vo.Nickname;
import revi1337.onsquad.factory.MemberFactory;
import revi1337.onsquad.support.PersistenceLayerTestSupport;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("멤버 리포지토리 테스트")
class MemberRepositoryTest extends PersistenceLayerTestSupport {

    private static final String TEST_EMAIL = "test@email.com";
    private static final String TEST_NICKNAME = "nickname";
    private static final String TEST_AUTH_CODE = "1111";

    @Autowired private MemberJpaRepository memberRepository;
    @Autowired private CrewJpaRepository crewJpaRepository;
    @Autowired private CrewMemberJpaRepository crewMemberRepository;

    @DisplayName("이미 닉네임 중복이 확인되면 true 를 반환한다.")
    @Test
    public void existsByNickname() {
        // given
        Nickname nickname = new Nickname(TEST_NICKNAME);
        Member member = MemberFactory.withNickname(nickname);
        memberRepository.save(member);

        // when
        boolean exists = memberRepository.existsByNickname(nickname);

        // then
        assertThat(exists).isTrue();
    }

    @DisplayName("이미 닉네임 중복이 확인되지 않으면 false 를 반환한다.")
    @Test
    public void existsByNickname2() {
        // given
        Nickname nickname = new Nickname(TEST_NICKNAME);

        // when
        boolean exists = memberRepository.existsByNickname(nickname);

        // then
        assertThat(exists).isFalse();
    }

    @DisplayName("이메일 중복이 확인되면 true 를 반환한다.")
    @Test
    public void existsByEmail() {
        // given
        Email email = new Email(TEST_EMAIL);
        Member member = MemberFactory.withEmail(email);
        memberRepository.save(member);

        // when
        boolean exists = memberRepository.existsByEmail(email);

        // then
        assertThat(exists).isTrue();
    }

    @DisplayName("이메일 중복이 확인되지 않으면 false 를 반환한다.")
    @Test
    public void existsByEmail2() {
        // given
        Email email = new Email(TEST_EMAIL);

        // when
        boolean exists = memberRepository.existsByEmail(email);

        // then
        assertThat(exists).isFalse();
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Test
    public void findMemberWithCrewMembers() {
        // given
        Member member1 = MemberFactory.defaultMember().build();
        Member member2 = MemberFactory.defaultMember().build();
        Crew crew1 = CrewFactory.defaultCrew().image(ImageFactory.defaultImage()).name(new Name("크루 1")) .member(member1).build();
        Crew crew2 = CrewFactory.defaultCrew().image(ImageFactory.defaultImage()).name(new Name("크루 2")) .member(member2).build();
        Crew crew3 = CrewFactory.defaultCrew().image(ImageFactory.defaultImage()).name(new Name("크루 3")) .member(member1).build();

        Member member3 = MemberFactory.defaultMember().build();
        Member member4 = MemberFactory.defaultMember().build();
        Member member5 = MemberFactory.defaultMember().build();
        Member member6 = MemberFactory.defaultMember().build();
        Member member7 = MemberFactory.defaultMember().build();
        Member member8 = MemberFactory.defaultMember().build();
        Member member9 = MemberFactory.defaultMember().build();
        Member member10 = MemberFactory.defaultMember().build();
        CrewMember crewMember1 = CrewMember.of(crew1, member3);
        CrewMember crewMember2 = CrewMember.of(crew1, member4);
        CrewMember crewMember3 = CrewMember.of(crew1, member5);
        CrewMember crewMember4 = CrewMember.of(crew2, member6);
        CrewMember crewMember5 = CrewMember.of(crew2, member7);
        CrewMember crewMember6 = CrewMember.of(crew2, member8);
        CrewMember crewMember7 = CrewMember.of(crew2, member9);
        CrewMember crewMember8 = CrewMember.of(crew3, member10);
        memberRepository.saveAll(List.of(member1, member2, member3, member4, member5, member6, member7, member8, member9, member10));
        crewJpaRepository.saveAll(List.of(crew1, crew2, crew3));
        crewMemberRepository.saveAll(List.of(crewMember1, crewMember2, crewMember3, crewMember4, crewMember5, crewMember6, crewMember7, crewMember8));

        Member member = memberRepository.findMemberWithRefCrewAndRefCrewMembersById(1L).get();
        Set<Crew> crews = member.getCrews();
//        for (Crew crew : crews) {
//            System.out.println("crew = " + crew.getName().getValue());
//            List<CrewMember> crewMembers = crew.getCrewMembers();
//            for (CrewMember crewMember : crewMembers) {
//                System.out.println("--> " + crewMember.getStatus());
//            }
//        }

        for (Crew crew : crews) {
            List<CrewMember> crewMembers = crew.getCrewMembers();
            for (CrewMember crewMember : crewMembers) {
                System.out.println(crewMember.getCrew().getName().getValue());
            }
        }
    }
}
