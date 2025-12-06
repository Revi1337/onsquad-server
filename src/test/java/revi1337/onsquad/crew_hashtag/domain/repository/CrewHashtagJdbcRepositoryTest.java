package revi1337.onsquad.crew_hashtag.domain.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import revi1337.onsquad.common.PersistenceLayerTestSupport;
import revi1337.onsquad.common.fixture.CrewFixture;
import revi1337.onsquad.common.fixture.MemberFixture;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.repository.CrewJpaRepository;
import revi1337.onsquad.crew_hashtag.domain.entity.CrewHashtag;
import revi1337.onsquad.hashtag.domain.entity.Hashtag;
import revi1337.onsquad.hashtag.domain.entity.vo.HashtagType;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberJpaRepository;

@Import(CrewHashtagJdbcRepository.class)
@Sql({"/h2-hashtag.sql"})
class CrewHashtagJdbcRepositoryTest extends PersistenceLayerTestSupport {

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Autowired
    private CrewJpaRepository crewJpaRepository;

    @Autowired
    private CrewHashtagJpaRepository crewHashtagJpaRepository;

    @Autowired
    private CrewHashtagJdbcRepository crewHashtagJdbcRepository;

    @Test
    @DisplayName("크루 배치 삽입에 성공한다.")
    void success1() {
        Member member = memberJpaRepository.save(MemberFixture.REVI());
        Crew crew = crewJpaRepository.save(CrewFixture.CREW(member));
        Hashtag hashtag = Hashtag.fromHashtagType(HashtagType.ACTIVE);
        crewHashtagJdbcRepository.batchInsert(crew.getId(), List.of(hashtag));

        Optional<CrewHashtag> optional = crewHashtagJpaRepository.findByCrewIdAndHashtagId(crew.getId(), hashtag.getId());

        assertThat(optional).isPresent();
    }
}
