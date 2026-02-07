package revi1337.onsquad.common.fixture;

import java.time.LocalDateTime;
import org.springframework.test.util.ReflectionTestUtils;
import revi1337.onsquad.history.domain.HistoryType;
import revi1337.onsquad.history.domain.entity.HistoryEntity;

public class HistoryFixture {

    public static HistoryEntity createCrewCreateHistory(Long id, Long memberId, Long crewId, String crewName, LocalDateTime recordedAt) {
        HistoryEntity historyEntity = createCrewCreateHistory(memberId, crewId, crewName, recordedAt);
        ReflectionTestUtils.setField(historyEntity, "id", id);
        return historyEntity;
    }

    public static HistoryEntity createCrewRequestHistory(Long id, Long memberId, Long crewId, String crewName, LocalDateTime recordedAt) {
        HistoryEntity historyEntity = createCrewRequestHistory(memberId, crewId, crewName, recordedAt);
        ReflectionTestUtils.setField(historyEntity, "id", id);
        return historyEntity;
    }

    public static HistoryEntity createCrewAcceptHistory(Long id, Long memberId, String requesterNickname, Long crewId, String crewName,
                                                        LocalDateTime recordedAt) {
        HistoryEntity historyEntity = createCrewAcceptHistory(memberId, requesterNickname, crewId, crewName, recordedAt);
        ReflectionTestUtils.setField(historyEntity, "id", id);
        return historyEntity;
    }

    public static HistoryEntity createCrewRejectHistory(Long id, Long memberId, String requesterNickname, Long crewId, String crewName,
                                                        LocalDateTime recordedAt) {
        HistoryEntity historyEntity = createCrewRejectHistory(memberId, requesterNickname, crewId, crewName, recordedAt);
        ReflectionTestUtils.setField(historyEntity, "id", id);
        return historyEntity;
    }

    public static HistoryEntity createCrewCancelHistory(Long id, Long memberId, Long crewId, String crewName, LocalDateTime recordedAt) {
        HistoryEntity historyEntity = createCrewCancelHistory(memberId, crewId, crewName, recordedAt);
        ReflectionTestUtils.setField(historyEntity, "id", id);
        return historyEntity;
    }

    public static HistoryEntity createSquadCreateHistory(Long id, Long memberId, Long crewId, String crewName, Long squadId, String squadName,
                                                         LocalDateTime recordedAt) {
        HistoryEntity historyEntity = createSquadCreateHistory(memberId, crewId, crewName, squadId, squadName, recordedAt);
        ReflectionTestUtils.setField(historyEntity, "id", id);
        return historyEntity;
    }

    public static HistoryEntity createSquadRequestHistory(Long id, Long memberId, Long crewId, String crewName, Long squadId, String squadName,
                                                          LocalDateTime recordedAt) {
        HistoryEntity historyEntity = createSquadRequestHistory(memberId, crewId, crewName, squadId, squadName, recordedAt);
        ReflectionTestUtils.setField(historyEntity, "id", id);
        return historyEntity;
    }

    public static HistoryEntity createSquadAcceptHistory(Long id, Long memberId, String requesterNickname, Long crewId, String crewName, Long squadId,
                                                         String squadName,
                                                         LocalDateTime recordedAt) {
        HistoryEntity historyEntity = createSquadAcceptHistory(memberId, requesterNickname, crewId, crewName, squadId, squadName, recordedAt);
        ReflectionTestUtils.setField(historyEntity, "id", id);
        return historyEntity;
    }

    public static HistoryEntity createSquadRejectHistory(Long id, Long memberId, String requesterNickname, Long crewId, String crewName, Long squadId,
                                                         String squadName, LocalDateTime recordedAt) {
        HistoryEntity historyEntity = createSquadRejectHistory(memberId, requesterNickname, crewId, crewName, squadId, squadName, recordedAt);
        ReflectionTestUtils.setField(historyEntity, "id", id);
        return historyEntity;
    }

    public static HistoryEntity createSquadCancelHistory(Long id, Long memberId, Long crewId, String crewName, Long squadId, String squadName,
                                                         LocalDateTime recordedAt) {
        HistoryEntity historyEntity = createSquadCancelHistory(memberId, crewId, crewName, squadId, squadName, recordedAt);
        ReflectionTestUtils.setField(historyEntity, "id", id);
        return historyEntity;
    }

    public static HistoryEntity createSquadCommentHistory(Long id, Long memberId, Long crewId, String crewName, Long squadId, String squadName,
                                                          LocalDateTime recordedAt) {
        HistoryEntity historyEntity = createSquadCommentHistory(memberId, crewId, crewName, squadId, squadName, recordedAt);
        ReflectionTestUtils.setField(historyEntity, "id", id);
        return historyEntity;
    }

    public static HistoryEntity createSquadCommentReplyHistory(Long id, Long memberId, Long crewId, String crewName, Long squadId, String squadName,
                                                               LocalDateTime recordedAt) {
        HistoryEntity historyEntity = createSquadCommentHistory(memberId, crewId, crewName, squadId, squadName, recordedAt);
        ReflectionTestUtils.setField(historyEntity, "id", id);
        return historyEntity;
    }

    public static HistoryEntity createCrewCreateHistory(Long memberId, Long crewId, String crewName, LocalDateTime recordedAt) {
        return HistoryEntity.builder()
                .memberId(memberId)
                .crewId(crewId)
                .type(HistoryType.CREW_CREATE)
                .message(HistoryType.CREW_CREATE.formatMessage(crewName))
                .recordedAt(recordedAt)
                .build();
    }

    public static HistoryEntity createCrewRequestHistory(Long memberId, Long crewId, String crewName, LocalDateTime recordedAt) {
        return HistoryEntity.builder()
                .memberId(memberId)
                .crewId(crewId)
                .type(HistoryType.CREW_REQUEST)
                .message(HistoryType.CREW_REQUEST.formatMessage(crewName))
                .recordedAt(recordedAt)
                .build();
    }

    public static HistoryEntity createCrewAcceptHistory(Long memberId, String requesterNickname, Long crewId, String crewName, LocalDateTime recordedAt) {
        return HistoryEntity.builder()
                .memberId(memberId)
                .crewId(crewId)
                .type(HistoryType.CREW_ACCEPT)
                .message(HistoryType.CREW_ACCEPT.formatMessage(crewName, requesterNickname))
                .recordedAt(recordedAt)
                .build();
    }

    public static HistoryEntity createCrewRejectHistory(Long memberId, String requesterNickname, Long crewId, String crewName, LocalDateTime recordedAt) {
        return HistoryEntity.builder()
                .memberId(memberId)
                .crewId(crewId)
                .type(HistoryType.CREW_REJECT)
                .message(HistoryType.CREW_REJECT.formatMessage(crewName, requesterNickname))
                .recordedAt(recordedAt)
                .build();
    }

    public static HistoryEntity createCrewCancelHistory(Long memberId, Long crewId, String crewName, LocalDateTime recordedAt) {
        return HistoryEntity.builder()
                .memberId(memberId)
                .crewId(crewId)
                .type(HistoryType.CREW_CANCEL)
                .message(HistoryType.CREW_CANCEL.formatMessage(crewName))
                .recordedAt(recordedAt)
                .build();
    }

    public static HistoryEntity createSquadCreateHistory(Long memberId, Long crewId, String crewName, Long squadId, String squadName,
                                                         LocalDateTime recordedAt) {
        return HistoryEntity.builder()
                .memberId(memberId)
                .crewId(crewId)
                .squadId(squadId)
                .type(HistoryType.SQUAD_CREATE)
                .message(HistoryType.SQUAD_CREATE.formatMessage(crewName, squadName))
                .recordedAt(recordedAt)
                .build();
    }

    public static HistoryEntity createSquadRequestHistory(Long memberId, Long crewId, String crewName, Long squadId, String squadName,
                                                          LocalDateTime recordedAt) {
        return HistoryEntity.builder()
                .memberId(memberId)
                .crewId(crewId)
                .squadId(squadId)
                .type(HistoryType.SQUAD_REQUEST)
                .message(HistoryType.SQUAD_REQUEST.formatMessage(crewName, squadName))
                .recordedAt(recordedAt)
                .build();
    }

    public static HistoryEntity createSquadAcceptHistory(Long memberId, String requesterNickname, Long crewId, String crewName, Long squadId, String squadName,
                                                         LocalDateTime recordedAt) {
        return HistoryEntity.builder()
                .memberId(memberId)
                .crewId(crewId)
                .squadId(squadId)
                .type(HistoryType.SQUAD_ACCEPT)
                .message(HistoryType.SQUAD_ACCEPT.formatMessage(crewName, squadName, requesterNickname))
                .recordedAt(recordedAt)
                .build();
    }

    public static HistoryEntity createSquadRejectHistory(Long memberId, String requesterNickname, Long crewId, String crewName, Long squadId, String squadName,
                                                         LocalDateTime recordedAt) {
        return HistoryEntity.builder()
                .memberId(memberId)
                .crewId(crewId)
                .squadId(squadId)
                .type(HistoryType.SQUAD_REJECT)
                .message(HistoryType.SQUAD_REJECT.formatMessage(crewName, squadName, requesterNickname))
                .recordedAt(recordedAt)
                .build();
    }

    public static HistoryEntity createSquadCancelHistory(Long memberId, Long crewId, String crewName, Long squadId, String squadName,
                                                         LocalDateTime recordedAt) {
        return HistoryEntity.builder()
                .memberId(memberId)
                .crewId(crewId)
                .squadId(squadId)
                .type(HistoryType.SQUAD_CANCEL)
                .message(HistoryType.SQUAD_CANCEL.formatMessage(crewName, squadName))
                .recordedAt(recordedAt)
                .build();
    }

    public static HistoryEntity createSquadCommentHistory(Long memberId, Long crewId, String crewName, Long squadId, String squadName,
                                                          LocalDateTime recordedAt) {
        return HistoryEntity.builder()
                .memberId(memberId)
                .crewId(crewId)
                .squadId(squadId)
                .type(HistoryType.SQUAD_COMMENT)
                .message(HistoryType.SQUAD_COMMENT.formatMessage(crewName, squadName))
                .recordedAt(recordedAt)
                .build();
    }

    public static HistoryEntity createSquadCommentReplyHistory(Long memberId, Long crewId, String crewName, Long squadId, String squadName,
                                                               LocalDateTime recordedAt) {
        return HistoryEntity.builder()
                .memberId(memberId)
                .crewId(crewId)
                .squadId(squadId)
                .type(HistoryType.SQUAD_COMMENT_REPLY)
                .message(HistoryType.SQUAD_COMMENT_REPLY.formatMessage(crewName, squadName))
                .recordedAt(recordedAt)
                .build();
    }
}
