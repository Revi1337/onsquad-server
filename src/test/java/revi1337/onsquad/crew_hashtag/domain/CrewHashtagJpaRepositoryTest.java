package revi1337.onsquad.crew_hashtag.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW;
import static revi1337.onsquad.common.fixture.MemberFixture.REVI;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import revi1337.onsquad.common.PersistenceLayerTestSupport;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.crew.domain.CrewJpaRepository;
import revi1337.onsquad.hashtag.domain.Hashtag;
import revi1337.onsquad.hashtag.domain.vo.HashtagType;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.domain.MemberJpaRepository;

@Import(CrewHashtagJdbcRepository.class)
@Sql({"/h2-hashtag.sql"})
class CrewHashtagJpaRepositoryTest extends PersistenceLayerTestSupport {

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Autowired
    private CrewJpaRepository crewJpaRepository;

    @Autowired
    private CrewHashtagJdbcRepository crewHashtagJdbcRepository;

    @Autowired
    private CrewHashtagJpaRepository crewHashtagJpaRepository;

    @Test
    @DisplayName("크루 해시태그 조회에 성공한다.")
    void success1() {
        Member member = memberJpaRepository.save(REVI());
        Crew crew = crewJpaRepository.save(CREW(member));
        Hashtag hashtag = Hashtag.fromHashtagType(HashtagType.ACTIVE);
        crewHashtagJdbcRepository.batchInsert(crew.getId(), List.of(hashtag));

        Optional<CrewHashtag> optional = crewHashtagJpaRepository.findByCrewIdAndHashtagId(crew.getId(), hashtag.getId());

        assertThat(optional).isPresent();
    }

    @Test
    @DisplayName("크루 해시태그 삭제에 성공한다.")
    void success2() {
        Member member = memberJpaRepository.save(REVI());
        Crew crew = crewJpaRepository.save(CREW(member));
        Hashtag hashtag = Hashtag.fromHashtagType(HashtagType.ACTIVE);
        crewHashtagJdbcRepository.batchInsert(crew.getId(), List.of(hashtag));
        clearPersistenceContext();

        crewHashtagJpaRepository.deleteById(crew.getId());

        assertThat(crewHashtagJpaRepository.findByCrewIdAndHashtagId(crew.getId(), hashtag.getId())).isEmpty();
    }
}