package revi1337.onsquad.common.fixture;

import static revi1337.onsquad.common.config.FixedTime.CLOCK;
import static revi1337.onsquad.common.fixture.MemberValueFixture.ANDONG_MBTI_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.ANDONG_NICKNAME_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.KWANGWON_MBTI_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.KWANGWON_NICKNAME_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_MBTI_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_NICKNAME_VALUE;

import java.time.LocalDateTime;
import revi1337.onsquad.backup.crew.domain.CrewTopMember;

public class CrewTopMemberFixture {

    public static final CrewTopMember CREW1_REVI_RANK1 = new CrewTopMember(
            1L,
            1,
            10,
            1L,
            REVI_NICKNAME_VALUE,
            REVI_MBTI_VALUE,
            LocalDateTime.now(CLOCK)
    );

    public static final CrewTopMember CREW1_ANDONG_RANK2 = new CrewTopMember(
            1L,
            2,
            9,
            1L,
            ANDONG_NICKNAME_VALUE,
            ANDONG_MBTI_VALUE,
            LocalDateTime.now(CLOCK)
    );

    public static final CrewTopMember CREW1_KWANGWON_RANK3 = new CrewTopMember(
            1L,
            3,
            8,
            1L,
            KWANGWON_NICKNAME_VALUE,
            KWANGWON_MBTI_VALUE,
            LocalDateTime.now(CLOCK)
    );
}
