package revi1337.onsquad.crew.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW_UPDATED_NAME_VALUE;
import static revi1337.onsquad.common.fixture.CrewFixture.createCrew;
import static revi1337.onsquad.common.fixture.MemberFixture.createAndong;
import static revi1337.onsquad.common.fixture.MemberFixture.createRevi;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;
import revi1337.onsquad.common.ApplicationLayerTestSupport;
import revi1337.onsquad.common.dto.DuplicateResponse;
import revi1337.onsquad.common.dto.PageResponse;
import revi1337.onsquad.crew.application.dto.response.CrewResponse;
import revi1337.onsquad.crew.application.dto.response.CrewWithParticipantStateResponse;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.repository.CrewRepository;
import revi1337.onsquad.crew_hashtag.domain.repository.CrewHashtagRepository;
import revi1337.onsquad.hashtag.domain.entity.Hashtag;
import revi1337.onsquad.hashtag.domain.entity.vo.HashtagType;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberRepository;

@Sql({"/h2-hashtag.sql"})
class CrewQueryServiceTest extends ApplicationLayerTestSupport {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CrewRepository crewRepository;

    @Autowired
    private CrewHashtagRepository crewHashtagRepository;

    @Autowired
    private CrewQueryService crewQueryService;

    @Test
    void checkNameDuplicate1() {
        Member member = memberRepository.save(createRevi());
        Crew crew = crewRepository.save(createCrew(member));

        DuplicateResponse response = crewQueryService.checkNameDuplicate(crew.getName().getValue());

        assertThat(response.duplicate()).isTrue();
    }

    @Test
    void checkNameDuplicate2() {
        Member member = memberRepository.save(createRevi());
        crewRepository.save(createCrew(member));

        DuplicateResponse response = crewQueryService.checkNameDuplicate(CREW_UPDATED_NAME_VALUE);

        assertThat(response.duplicate()).isFalse();
    }

    @Test
    void findCrewById1() {
        Member member = memberRepository.save(createRevi());
        Crew crew = crewRepository.save(createCrew(member));
        crewHashtagRepository.insertBatch(crew.getId(), Hashtag.fromHashtagTypes(List.of(HashtagType.ACTIVE, HashtagType.PASSIONATE)));

        CrewWithParticipantStateResponse response = crewQueryService.findCrewById(member.getId(), crew.getId());

        assertSoftly(softly -> {
            softly.assertThat(response.states().alreadyParticipant()).isTrue();
            softly.assertThat(response.id()).isEqualTo(crew.getId());
            softly.assertThat(response.hashtags()).hasSize(2);
            softly.assertThat(response.name()).isEqualTo(crew.getName().getValue());
            softly.assertThat(response.introduce()).isEqualTo(crew.getIntroduce().getValue());
            softly.assertThat(response.detail()).isEqualTo(crew.getDetail().getValue());
            softly.assertThat(response.imageUrl()).isEqualTo(crew.getImageUrl());
            softly.assertThat(response.kakaoLink()).isEqualTo(crew.getKakaoLink());
            softly.assertThat(response.detail()).isEqualTo(crew.getDetail().getValue());
            softly.assertThat(response.memberCount()).isEqualTo(crew.getCurrentSize());
            softly.assertThat(response.owner().id()).isEqualTo(member.getId());
            softly.assertThat(response.owner().email()).isNull();
            softly.assertThat(response.owner().nickname()).isEqualTo(member.getNickname().getValue());
            softly.assertThat(response.owner().introduce()).isEqualTo(member.getIntroduce().getValue());
            softly.assertThat(response.owner().mbti()).isEqualTo(member.getMbti().name());
        });
    }

    @Test
    void findCrewById2() {
        Member member = memberRepository.save(createRevi());
        Crew crew = crewRepository.save(createCrew(member));
        crewHashtagRepository.insertBatch(crew.getId(), Hashtag.fromHashtagTypes(List.of(HashtagType.ACTIVE, HashtagType.PASSIONATE)));

        CrewWithParticipantStateResponse response = crewQueryService.findCrewById(null, crew.getId());

        assertSoftly(softly -> {
            softly.assertThat(response.states().alreadyParticipant()).isNull();
            softly.assertThat(response.id()).isEqualTo(crew.getId());
            softly.assertThat(response.hashtags()).hasSize(2);
            softly.assertThat(response.name()).isEqualTo(crew.getName().getValue());
            softly.assertThat(response.introduce()).isEqualTo(crew.getIntroduce().getValue());
            softly.assertThat(response.detail()).isEqualTo(crew.getDetail().getValue());
            softly.assertThat(response.imageUrl()).isEqualTo(crew.getImageUrl());
            softly.assertThat(response.kakaoLink()).isEqualTo(crew.getKakaoLink());
            softly.assertThat(response.detail()).isEqualTo(crew.getDetail().getValue());
            softly.assertThat(response.memberCount()).isEqualTo(crew.getCurrentSize());
            softly.assertThat(response.owner().id()).isEqualTo(member.getId());
            softly.assertThat(response.owner().email()).isNull();
            softly.assertThat(response.owner().nickname()).isEqualTo(member.getNickname().getValue());
            softly.assertThat(response.owner().introduce()).isEqualTo(member.getIntroduce().getValue());
            softly.assertThat(response.owner().mbti()).isEqualTo(member.getMbti().name());
        });
    }

    @Test
    void findCrewById3() {
        Member revi = memberRepository.save(createRevi());
        Member andong = memberRepository.save(createAndong());
        Crew crew = crewRepository.save(createCrew(revi));
        crewHashtagRepository.insertBatch(crew.getId(), Hashtag.fromHashtagTypes(List.of(HashtagType.ACTIVE, HashtagType.PASSIONATE)));

        CrewWithParticipantStateResponse response = crewQueryService.findCrewById(andong.getId(), crew.getId());

        assertSoftly(softly -> {
            softly.assertThat(response.states().alreadyParticipant()).isFalse();
            softly.assertThat(response.id()).isEqualTo(crew.getId());
            softly.assertThat(response.hashtags()).hasSize(2);
            softly.assertThat(response.name()).isEqualTo(crew.getName().getValue());
            softly.assertThat(response.introduce()).isEqualTo(crew.getIntroduce().getValue());
            softly.assertThat(response.detail()).isEqualTo(crew.getDetail().getValue());
            softly.assertThat(response.imageUrl()).isEqualTo(crew.getImageUrl());
            softly.assertThat(response.kakaoLink()).isEqualTo(crew.getKakaoLink());
            softly.assertThat(response.detail()).isEqualTo(crew.getDetail().getValue());
            softly.assertThat(response.memberCount()).isEqualTo(crew.getCurrentSize());
            softly.assertThat(response.owner().id()).isEqualTo(revi.getId());
            softly.assertThat(response.owner().email()).isNull();
            softly.assertThat(response.owner().nickname()).isEqualTo(revi.getNickname().getValue());
            softly.assertThat(response.owner().introduce()).isEqualTo(revi.getIntroduce().getValue());
            softly.assertThat(response.owner().mbti()).isEqualTo(revi.getMbti().name());
        });
    }

    @Test
    void fetchCrewsByName() {
        Member revi = memberRepository.save(createRevi());
        Crew crew1 = crewRepository.save(createCrew("crew-1", revi));
        Crew crew2 = crewRepository.save(createCrew("crew-2", revi));
        Crew crew3 = crewRepository.save(createCrew("crew-3", revi));
        Member andong = memberRepository.save(createAndong());
        Crew crew4 = crewRepository.save(createCrew("crew-4", andong));
        Crew crew5 = crewRepository.save(createCrew("crew-5", andong));
        Crew crew6 = crewRepository.save(createCrew("crew-6", andong));
        crewHashtagRepository.insertBatch(crew1.getId(), Hashtag.fromHashtagTypes(List.of(HashtagType.ACTIVE, HashtagType.PASSIONATE)));
        crewHashtagRepository.insertBatch(crew2.getId(), Hashtag.fromHashtagTypes(List.of(HashtagType.ESCAPE, HashtagType.MOVIE)));
        crewHashtagRepository.insertBatch(crew3.getId(), Hashtag.fromHashtagTypes(List.of(HashtagType.IMPULSIVE, HashtagType.GAME_LOVER_FEMALE)));
        crewHashtagRepository.insertBatch(crew4.getId(), Hashtag.fromHashtagTypes(List.of(HashtagType.HOME_BODY_MALE, HashtagType.POSITIVE)));
        crewHashtagRepository.insertBatch(crew5.getId(), Hashtag.fromHashtagTypes(List.of(HashtagType.IMPULSIVE, HashtagType.WINE)));
        crewHashtagRepository.insertBatch(crew6.getId(), Hashtag.fromHashtagTypes(List.of(HashtagType.WITTY, HashtagType.WALKING)));
        String crewName = "crew-";
        PageRequest pageRequest = PageRequest.of(1, 2);

        PageResponse<CrewResponse> response = crewQueryService.fetchCrewsByName(crewName, pageRequest);

        assertSoftly(softly -> {
            softly.assertThat(response.size()).isEqualTo(2);
            softly.assertThat(response.page()).isEqualTo(2);
            softly.assertThat(response.totalPages()).isEqualTo(3);
            softly.assertThat(response.totalCount()).isEqualTo(6);
            softly.assertThat(response.resultsSize()).isEqualTo(2);

            List<CrewResponse> results = response.results();
            softly.assertThat(results.get(0).id()).isEqualTo(crew4.getId());
            softly.assertThat(results.get(0).owner().id()).isEqualTo(andong.getId());
            softly.assertThat(results.get(1).id()).isEqualTo(crew3.getId());
            softly.assertThat(results.get(1).owner().id()).isEqualTo(revi.getId());
        });
    }
}
