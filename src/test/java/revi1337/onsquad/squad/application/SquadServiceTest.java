package revi1337.onsquad.squad.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.domain.MemberRepository;
import revi1337.onsquad.member.domain.vo.Email;
import revi1337.onsquad.member.domain.vo.Nickname;
import revi1337.onsquad.member.domain.vo.Password;
import revi1337.onsquad.member.dto.MemberDto;
import revi1337.onsquad.squad.domain.Squad;
import revi1337.onsquad.squad.domain.SquadRepository;
import revi1337.onsquad.squad.domain.vo.Content;
import revi1337.onsquad.squad.domain.vo.Title;
import revi1337.onsquad.squad.dto.SquadDto;

import java.util.Optional;

import static org.mockito.BDDMockito.*;

@DisplayName("Squad 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class SquadServiceTest {

    private static final String TEST_EMAIL = "test@email.com";
    private static final String TEST_PASSWORD = "12345!@asa";
    private static final String TEST_NICKNAME = "nickname";
    private static final String TEST_TITLE = "title";
    private static final String TEST_CONTENT = "content";


    @Mock private MemberRepository memberRepository;
    @Mock private SquadRepository squadRepository;
    @InjectMocks private SquadService squadService;

    @Test
    @DisplayName("Squad 생성 게시글이 생성되는지 확인한다. (모든 객체를 Mock)")
    public void createNewSquad() {
        // given
        Long memberId = 1L;
        MemberDto memberDto = mock(MemberDto.class);
        SquadDto squadDto = mock(SquadDto.class);
        Member member = mock(Member.class);
        Squad squad = mock(Squad.class);
        when(memberDto.getId()).thenReturn(memberId);
        when(squadDto.getMemberDto()).thenReturn(memberDto);
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(squadDto.toEntity(member)).thenReturn(squad);

        // when
        squadService.createNewSquad(squadDto);

        // then
        then(memberRepository).should(times(1)).findById(memberId);
        then(squadRepository).should(times(1)).save(squad);
        then(squadDto).should(times(1)).toEntity(member);
    }

    @Test
    @DisplayName("Squad 생성 게시글이 생성되는지 확인한다. (일부 객체만 Mock)")
    public void createNewSquad2() {
        // given
        Long memberId = 1L;
        MemberDto memberDto = MemberDto.builder().id(memberId).email(new Email(TEST_EMAIL)).nickname(new Nickname(TEST_NICKNAME)).password(new Password(TEST_PASSWORD)).build();
        Member member = memberDto.toEntity();
        SquadDto squadDto = SquadDto.builder().title(new Title(TEST_TITLE)).content(new Content(TEST_CONTENT)).memberDto(memberDto).build();
        Squad squad = squadDto.toEntity(member);
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        // when
        squadService.createNewSquad(squadDto);

        // then
        then(memberRepository).should(times(1)).findById(memberId);
        then(squadRepository).should(times(1)).save(any(Squad.class));
    }
}
