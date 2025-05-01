package revi1337.onsquad.backup.crew.domain;

import java.time.LocalDate;
import java.util.List;

public interface CrewTopMemberRepository {

    List<CrewTopMember> findAllTopNCrewMembers(LocalDate from, LocalDate to, Integer integer);

    boolean exists();

    void deleteAllInBatch();

    void batchInsert(List<CrewTopMember> crewTopCaches);

}
