package revi1337.onsquad.crew_member.domain.repository;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.crew_member.domain.model.CrewMembership;

@Repository
@RequiredArgsConstructor
public class CrewMemberJdbcRepository {

    private final NamedParameterJdbcTemplate namedJdbcTemplate;

    public Set<CrewMembership> findActiveMemberships(Collection<CrewMembership> candidates) {
        List<CrewMembership> distinctMemberships = candidates.stream()
                .distinct()
                .toList();

        if (distinctMemberships.isEmpty()) {
            return Set.of();
        }

        String placeholders = distinctMemberships.stream()
                .map(membership -> "(?, ?)")
                .collect(Collectors.joining(", "));
        String sql = "SELECT crew_id, member_id FROM crew_member WHERE (crew_id, member_id) IN (" + placeholders + ")";

        List<CrewMembership> activeMemberships = namedJdbcTemplate.getJdbcOperations().query(
                sql,
                ps -> {
                    int index = 1;
                    for (CrewMembership membership : distinctMemberships) {
                        ps.setLong(index++, membership.crewId());
                        ps.setLong(index++, membership.memberId());
                    }
                },
                (rs, rowNum) -> new CrewMembership(rs.getLong("crew_id"), rs.getLong("member_id"))
        );

        return new HashSet<>(activeMemberships);
    }
}
