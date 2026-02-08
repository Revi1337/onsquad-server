package revi1337.onsquad.crew_member.application.leaderboard;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import revi1337.onsquad.crew_member.domain.entity.CrewRankedMember;
import revi1337.onsquad.crew_member.domain.model.CrewRankedMemberDetail;
import revi1337.onsquad.crew_member.domain.repository.rank.CrewRankedMemberRepository;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class CrewLeaderboardRebuildService {

    private final MemberRepository memberRepository;
    private final CrewRankedMemberRepository crewRankedMemberRepository;
    private final CrewRankerBackupManager crewRankerBackupManager;

    @Deprecated
    public void renewTopRankers(LocalDateTime from, LocalDateTime to, Integer rankLimit) {
        try {
            List<CrewRankedMember> rankedMembers = crewRankedMemberRepository.fetchAggregatedRankedMembers(from, to, rankLimit).stream()
                    .map(CrewRankedMemberDetail::toEntity)
                    .toList();

            crewRankedMemberRepository.truncate();
            crewRankedMemberRepository.insertBatch(rankedMembers);
            log.info("[Successfully Renew CrewRankedMember In DataBase : {} ~ {}]", from, to);
        } catch (Exception exception) {
            log.error("[Fail to Renew CrewRankedMember In DataBase : {} ~ {}]", from, to, exception);
            throw exception;
        }
    }

    public void renewTopRankers(List<CrewRankedMemberDetail> rankedMembers) {
        try {
            List<Long> memberIds = collectRankedMemberIds(rankedMembers);
            Map<Long, Member> memberMapping = getMemberLookupTable(memberIds);
            List<CrewRankedMember> newRankedMembers = getNewCrewRankedMembers(rankedMembers, memberMapping);

            crewRankedMemberRepository.truncate();
            crewRankedMemberRepository.insertBatch(newRankedMembers);
            log.info("[Rebuild] Database sync complete. ({} members)", newRankedMembers.size());
        } catch (Exception exception) {
            log.error("[Rebuild] Failed to sync database. Attempting emergency recovery...", exception);
            attemptRecoveryFromBackup();
            throw exception;
        }
    }

    private void attemptRecoveryFromBackup() {
        try {
            List<CrewRankedMemberDetail> backup = crewRankerBackupManager.getBackup();
            if (backup.isEmpty()) {
                log.warn("[Recovery] Fallback failed: No backup found in Redis.");
                return;
            }
            List<Long> memberIds = backup.stream()
                    .map(CrewRankedMemberDetail::memberId)
                    .toList();

            Map<Long, Member> memberMapping = getMemberLookupTable(memberIds);
            List<CrewRankedMember> recoveredMembers = getNewCrewRankedMembers(backup, memberMapping);
            crewRankedMemberRepository.insertBatch(recoveredMembers);
            log.info("[Recovery] Successfully restored {} members from Redis backup.", recoveredMembers.size());
        } catch (Exception recoveryException) {
            log.error("[Recovery] Fatal error during backup restoration!", recoveryException);
        }
    }

    private List<Long> collectRankedMemberIds(List<CrewRankedMemberDetail> rankedMembers) {
        return rankedMembers.stream()
                .map(CrewRankedMemberDetail::memberId)
                .toList();
    }

    private Map<Long, Member> getMemberLookupTable(List<Long> memberIds) {
        return memberRepository.findByIdIn(memberIds).stream()
                .collect(Collectors.toMap(Member::getId, member -> member));
    }

    private List<CrewRankedMember> getNewCrewRankedMembers(List<CrewRankedMemberDetail> rankedMembers, Map<Long, Member> memberLookupTable) {
        List<CrewRankedMember> newRankedMembers = new ArrayList<>();
        for (CrewRankedMemberDetail rankedResult : rankedMembers) {
            Member member = memberLookupTable.get(rankedResult.memberId());
            if (member == null) {
                continue;
            }
            newRankedMembers.add(rankedResult.toEntityWithMember(member));
        }
        return newRankedMembers;
    }
}
