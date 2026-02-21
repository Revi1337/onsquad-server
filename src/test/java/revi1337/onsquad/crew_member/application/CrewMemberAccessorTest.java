package revi1337.onsquad.crew_member.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.crew_member.domain.error.CrewMemberBusinessException;
import revi1337.onsquad.crew_member.domain.repository.CrewMemberRepository;

@ExtendWith(MockitoExtension.class)
class CrewMemberAccessorTest {

    @Mock
    private CrewMemberRepository crewMemberRepository;

    @InjectMocks
    private CrewMemberAccessor crewMemberAccessor;

    @Test
    @DisplayName("멤버 ID와 크루 ID로 크루 멤버를 조회하면 해당 객체를 반환한다.")
    void getByMemberIdAndCrewId1() {
        Long memberId = 1L;
        Long crewId = 2L;
        CrewMember crewMember = mock(CrewMember.class);

        given(crewMemberRepository.findByCrewIdAndMemberId(crewId, memberId))
                .willReturn(Optional.of(crewMember));

        CrewMember result = crewMemberAccessor.getByMemberIdAndCrewId(memberId, crewId);

        assertThat(result).isEqualTo(crewMember);
        verify(crewMemberRepository).findByCrewIdAndMemberId(crewId, memberId);
    }

    @Test
    @DisplayName("조회된 크루 멤버가 없으면 NotParticipant 예외를 던진다.")
    void getByMemberIdAndCrewId2() {
        Long memberId = 1L;
        Long crewId = 2L;

        given(crewMemberRepository.findByCrewIdAndMemberId(crewId, memberId))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> crewMemberAccessor.getByMemberIdAndCrewId(memberId, crewId))
                .isExactlyInstanceOf(CrewMemberBusinessException.NotParticipant.class);
    }

    @Test
    @DisplayName("멤버가 크루에 속해있지 않으면 NotParticipant 예외를 던진다")
    void validateMemberInCrew1() {
        Long memberId = 1L;
        Long crewId = 2L;
        given(crewMemberRepository.findByCrewIdAndMemberId(crewId, memberId))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> crewMemberAccessor.validateMemberInCrew(memberId, crewId))
                .isExactlyInstanceOf(CrewMemberBusinessException.NotParticipant.class);
    }

    @Test
    @DisplayName("멤버가 크루에 속해있으면 아무런 예외를 던지지 않는다")
    void validateMemberInCrew2() {
        Long memberId = 1L;
        Long crewId = 2L;
        given(crewMemberRepository.findByCrewIdAndMemberId(crewId, memberId))
                .willReturn(Optional.of(mock(CrewMember.class)));

        assertThatCode(() -> crewMemberAccessor.validateMemberInCrew(memberId, crewId))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("멤버가 이미 크루에 속해있으면 AlreadyParticipant 예외를 던진다")
    void validateMemberNotInCrew1() {
        Long memberId = 1L;
        Long crewId = 2L;
        given(crewMemberRepository.findByCrewIdAndMemberId(crewId, memberId))
                .willReturn(Optional.of(mock(CrewMember.class)));

        assertThatThrownBy(() -> crewMemberAccessor.validateMemberNotInCrew(memberId, crewId))
                .isExactlyInstanceOf(CrewMemberBusinessException.AlreadyParticipant.class);
    }

    @Test
    @DisplayName("멤버가 크루에 속해있지 않으면 검증을 통과한다")
    void validateMemberNotInCrew2() {
        Long memberId = 1L;
        Long crewId = 2L;
        given(crewMemberRepository.findByCrewIdAndMemberId(crewId, memberId))
                .willReturn(Optional.empty());

        assertThatCode(() -> crewMemberAccessor.validateMemberNotInCrew(memberId, crewId))
                .doesNotThrowAnyException();
    }
}
