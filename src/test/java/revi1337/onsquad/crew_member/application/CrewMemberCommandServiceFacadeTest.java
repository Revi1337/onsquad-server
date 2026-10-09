package revi1337.onsquad.crew_member.application;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static revi1337.onsquad.common.fixture.CrewFixture.createCrew;
import static revi1337.onsquad.common.fixture.MemberFixture.createMember;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.jdbc.Sql;
import revi1337.onsquad.common.ApplicationLayerTestSupport;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.repository.CrewJpaRepository;
import revi1337.onsquad.crew_member.domain.entity.CrewMemberFactory;
import revi1337.onsquad.crew_member.domain.error.CrewMemberBusinessException;
import revi1337.onsquad.crew_member.domain.error.CrewMemberErrorCode;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberJpaRepository;

@Sql({"/h2-truncate.sql", "/h2-category.sql"})
class CrewMemberCommandServiceFacadeTest extends ApplicationLayerTestSupport {

    @Autowired
    private MemberJpaRepository memberRepository;

    @Autowired
    private CrewJpaRepository crewRepository;

    @Autowired
    private CrewMemberCommandServiceFacade facade;

    @Test
    @DisplayName("크루장이 아닌 사용자의 위임 요청은 재시도 없이 권한 예외가 그대로 전파된다.")
    void propagatesInsufficientAuthority_whenNonOwnerDelegates() {
        Member owner = memberRepository.save(createMember(1));
        Member general = memberRepository.save(createMember(2));
        Crew crew = createCrew(owner);
        crew.addCrewMember(CrewMemberFactory.general(crew, general, LocalDateTime.now()));
        crewRepository.save(crew);
        clearPersistenceContext();

        assertThatThrownBy(() -> facade.delegateOwner(general.getId(), crew.getId(), owner.getId()))
                .isExactlyInstanceOf(CrewMemberBusinessException.InsufficientAuthority.class);
    }

    @Test
    @DisplayName("추방 권한이 없는 사용자의 요청은 재시도 없이 권한 예외가 그대로 전파된다.")
    void propagatesInsufficientAuthority_whenGeneralKicksOut() {
        Member owner = memberRepository.save(createMember(1));
        Member general = memberRepository.save(createMember(2));
        Crew crew = createCrew(owner);
        crew.addCrewMember(CrewMemberFactory.general(crew, general, LocalDateTime.now()));
        crewRepository.save(crew);
        clearPersistenceContext();

        assertThatThrownBy(() -> facade.kickOutMember(general.getId(), crew.getId(), owner.getId()))
                .isExactlyInstanceOf(CrewMemberBusinessException.InsufficientAuthority.class);
    }

    @Test
    @DisplayName("위임 재시도가 낙관적 락 충돌로 소진되면 원인을 담은 409 충돌 예외를 던진다.")
    void throwsConcurrentModification_whenDelegateRetriesExhausted() {
        ObjectOptimisticLockingFailureException conflict = new ObjectOptimisticLockingFailureException(Crew.class, 1L);

        assertThatThrownBy(() -> facade.recoverDelegateOwner(conflict, 1L, 2L, 3L))
                .isExactlyInstanceOf(CrewMemberBusinessException.ConcurrentModification.class)
                .hasCause(conflict)
                .extracting(exception -> ((CrewMemberBusinessException) exception).getErrorCode().getStatus())
                .isEqualTo(409);
    }

    @Test
    @DisplayName("추방 재시도가 낙관적 락 충돌로 소진되면 원인을 담은 409 충돌 예외를 던진다.")
    void throwsConcurrentModification_whenKickOutRetriesExhausted() {
        ObjectOptimisticLockingFailureException conflict = new ObjectOptimisticLockingFailureException(Crew.class, 1L);

        assertThatThrownBy(() -> facade.recoverKickOutMember(conflict, 1L, 2L, 3L))
                .isExactlyInstanceOf(CrewMemberBusinessException.ConcurrentModification.class)
                .hasCause(conflict)
                .extracting(exception -> ((CrewMemberBusinessException) exception).getErrorCode().getStatus())
                .isEqualTo(409);
    }

    @Test
    @DisplayName("낙관적 락 충돌이 아닌 예외는 감싸지 않고 같은 예외를 던진다.")
    void rethrowsSameException_whenNotOptimisticLockFailure() {
        CrewMemberBusinessException.InsufficientAuthority exception =
                new CrewMemberBusinessException.InsufficientAuthority(CrewMemberErrorCode.INSUFFICIENT_DELEGATE_OWNER_AUTHORITY);

        assertThatThrownBy(() -> facade.recoverDelegateOwner(exception, 1L, 2L, 3L)).isSameAs(exception);
        assertThatThrownBy(() -> facade.recoverKickOutMember(exception, 1L, 2L, 3L)).isSameAs(exception);
    }
}
