package revi1337.onsquad.squad_request.domain.entity;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;
import revi1337.onsquad.common.domain.RequestEntity;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.squad.domain.entity.Squad;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
@Table(uniqueConstraints = {@UniqueConstraint(name = "uk_squad_request_squad_id_crew_member_id", columnNames = {"squad_id", "crew_member_id"})})
public class SquadRequest extends RequestEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "squad_id", nullable = false)
    private Squad squad;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "crew_member_id", nullable = false)
    private CrewMember crewMember;

    private SquadRequest(Squad squad, CrewMember crewMember, LocalDateTime requestAt) {
        super(requestAt);
        this.squad = squad;
        this.crewMember = crewMember;
    }

    public static SquadRequest of(Squad squad, CrewMember crewMember, LocalDateTime requestAt) {
        return new SquadRequest(squad, crewMember, requestAt);
    }

    public boolean matchSquadId(Long squadId) {
        return squad.getId().equals(squadId);
    }

    public boolean isNotMatchSquadId(Long squadId) {
        return !matchSquadId(squadId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SquadRequest that)) {
            return false;
        }
        return id != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}

//package revi1337.onsquad.squad_participant.domain.entity;
//
//import jakarta.persistence.Entity;
//import jakarta.persistence.FetchType;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.GenerationType;
//import jakarta.persistence.Id;
//import jakarta.persistence.JoinColumn;
//import jakarta.persistence.ManyToOne;
//import jakarta.persistence.Table;
//import jakarta.persistence.UniqueConstraint;
//import java.time.LocalDateTime;
//import java.util.Objects;
//import lombok.AccessLevel;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import revi1337.onsquad.common.domain.RequestEntity;
//import revi1337.onsquad.crew_member.domain.entity.CrewMember;
//import revi1337.onsquad.squad.domain.entity.Squad;
//
//@Getter
//@Entity
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
//@Table(uniqueConstraints = {
//        @UniqueConstraint(
//                name = "squadparticipant_uidx_squad_id_crewmember_id",
//                columnNames = {"squad_id", "crew_member_id"}
//        )
//})
//public class SquadParticipant extends RequestEntity { // TODO 신청정보인데.. Table 이름이 왜 squad_participant.. 고쳐야함;
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "squad_id")
//    private Squad squad;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "crew_member_id")
//    private CrewMember crewMember;
//
//    private SquadParticipant(Squad squad, CrewMember crewMember, LocalDateTime requestAt) {
//        super(requestAt);
//        this.squad = squad;
//        this.crewMember = crewMember;
//    }
//
//    public static SquadParticipant of(Squad squad, CrewMember crewMember, LocalDateTime requestAt) {
//        return new SquadParticipant(squad, crewMember, requestAt);
//    }
//
//    public boolean matchSquadId(Long squadId) {
//        return squad.getId().equals(squadId);
//    }
//
//    public boolean isNotMatchSquadId(Long squadId) {
//        return !matchSquadId(squadId);
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) {
//            return true;
//        }
//        if (!(o instanceof SquadParticipant that)) {
//            return false;
//        }
//        return id != null && Objects.equals(getId(), that.getId());
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hashCode(getId());
//    }
//}
