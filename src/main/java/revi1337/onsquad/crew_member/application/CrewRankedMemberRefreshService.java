package revi1337.onsquad.crew_member.application;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.crew_member.application.scheduler.CrewRankedMemberRefreshScheduler;
import revi1337.onsquad.crew_member.domain.entity.CrewRankedMember;
import revi1337.onsquad.crew_member.domain.repository.rank.CrewRankedMemberRepository;
import revi1337.onsquad.crew_member.domain.result.CrewRankedMemberResult;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberRepository;

/**
 * Service responsible for aggregating and refreshing top-ranked members in each crew.
 *
 * <p>This service recalculates crew member rankings for a given period and
 * updates the persisted ranking data in batch.</p>
 *
 * <p>The refresh operation is triggered by {@link CrewRankedMemberRefreshScheduler} and executed synchronously.</p>
 */
@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class CrewRankedMemberRefreshService {

    private final CrewRankedMemberRepository crewRankedMemberRepository;
    private final MemberRepository memberRepository;

    public void refresh(LocalDateTime from, LocalDateTime to, Integer rankLimit) {
        try {
            List<CrewRankedMember> rankedMembers = crewRankedMemberRepository.fetchAggregatedRankedMembers(from, to, rankLimit).stream()
                    .map(CrewRankedMemberResult::toEntity)
                    .toList();

            crewRankedMemberRepository.deleteAllInBatch();
            crewRankedMemberRepository.insertBatch(rankedMembers);
            log.info("[Successfully Renew CrewRankedMember In DataBase : {} ~ {}]", from, to);
        } catch (Exception exception) {
            log.error("[Fail to Renew CrewRankedMember In DataBase : {} ~ {}]", from, to, exception);
            throw exception;
        }
    }

    public void refresh(List<CrewRankedMemberResult> rankedMembers) {
        try {
            List<Long> memberIds = collectRankedMemberIds(rankedMembers);
            Map<Long, Member> memberMapping = getMemberLookupTable(memberIds);
            List<CrewRankedMember> newRankedMembers = getNewCrewRankedMembers(rankedMembers, memberMapping);
            crewRankedMemberRepository.deleteAllInBatch();
            crewRankedMemberRepository.insertBatch(newRankedMembers);
            log.info("[Successfully Renew CrewRankedMember : {} members]", newRankedMembers.size());
        } catch (Exception exception) {
            log.error("[Fail to Renew CrewRankedMember In DataBase]", exception);
            throw exception;
        }
    }

    private List<Long> collectRankedMemberIds(List<CrewRankedMemberResult> rankedMembers) {
        return rankedMembers.stream()
                .map(CrewRankedMemberResult::memberId)
                .toList();
    }

    private Map<Long, Member> getMemberLookupTable(List<Long> memberIds) {
        return memberRepository.findByIdIn(memberIds).stream()
                .collect(Collectors.toMap(Member::getId, member -> member));
    }

    private List<CrewRankedMember> getNewCrewRankedMembers(List<CrewRankedMemberResult> rankedMembers, Map<Long, Member> memberLookupTable) {
        List<CrewRankedMember> newRankedMembers = new ArrayList<>();
        for (CrewRankedMemberResult rankedResult : rankedMembers) {
            Member member = memberLookupTable.get(rankedResult.memberId());
            if (isMemberDeleted(member)) {
                continue;
            }
            newRankedMembers.add(rankedResult.toEntityWithMember(member));
        }
        return newRankedMembers;
    }

    private boolean isMemberDeleted(Member member) {
        return member == null;
    }
}
