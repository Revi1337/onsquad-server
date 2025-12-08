package revi1337.onsquad.crew_request.domain.entity;

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
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.member.domain.entity.Member;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
@Table(uniqueConstraints = {@UniqueConstraint(name = "uk_crew_request_crew_id_member_id", columnNames = {"crew_id", "member_id"})})
public class CrewRequest extends RequestEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "crew_id", nullable = false)
    private Crew crew;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    public CrewRequest(Crew crew, Member member, LocalDateTime requestAt) {
        super(requestAt);
        this.crew = crew;
        this.member = member;
    }

    public CrewRequest(Long id, Crew crew, Member member, LocalDateTime requestAt) {
        super(requestAt);
        this.id = id;
        this.crew = crew;
        this.member = member;
    }

    public boolean isNotFrom(Long crewId) {
        return !isFrom(crewId);
    }

    public boolean isFrom(Long crewId) {
        return crew.getId().equals(crewId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CrewRequest that)) {
            return false;
        }
        return id != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    public Long getRequestMemberId() {
        return member.getId();
    }
}

//package revi1337.onsquad.crew_participant.domain.entity;
//
//import static jakarta.persistence.FetchType.LAZY;
//import static jakarta.persistence.GenerationType.IDENTITY;
//import static lombok.AccessLevel.PROTECTED;
//
//import jakarta.persistence.Entity;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.Id;
//import jakarta.persistence.Index;
//import jakarta.persistence.JoinColumn;
//import jakarta.persistence.ManyToOne;
//import jakarta.persistence.Table;
//import jakarta.persistence.UniqueConstraint;
//import java.time.LocalDateTime;
//import java.util.Objects;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import revi1337.onsquad.common.domain.RequestEntity;
//import revi1337.onsquad.crew.domain.entity.Crew;
//import revi1337.onsquad.member.domain.entity.Member;
//
//@Getter
//@Entity
//@NoArgsConstructor(access = PROTECTED)
//@Table(
//        indexes = {@Index(name = "idx_crew_request", columnList = "crew_id, request_at")},
//        uniqueConstraints = {@UniqueConstraint(name = "uniq_idx_crew_member", columnNames = {"crew_id", "member_id"})}
//)
//public class CrewParticipant extends RequestEntity { // TODO 신청정보인데.. Table 이름이 왜 crew_participant.. 고쳐야함;
//
//    @Id
//    @GeneratedValue(strategy = IDENTITY)
//    private Long id;
//
//    @ManyToOne(fetch = LAZY)
//    @JoinColumn(name = "crew_id", nullable = false)
//    private Crew crew;
//
//    @ManyToOne(fetch = LAZY)
//    @JoinColumn(name = "member_id", nullable = false)
//    private Member member;
//
//    public CrewParticipant(Crew crew, Member member, LocalDateTime requestAt) {
//        super(requestAt);
//        this.crew = crew;
//        this.member = member;
//    }
//
//    public CrewParticipant(Long id, Crew crew, Member member, LocalDateTime requestAt) {
//        super(requestAt);
//        this.id = id;
//        this.crew = crew;
//        this.member = member;
//    }
//
//    public boolean isNotFrom(Long crewId) {
//        return !isFrom(crewId);
//    }
//
//    public boolean isFrom(Long crewId) {
//        return crew.getId().equals(crewId);
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) {
//            return true;
//        }
//        if (!(o instanceof CrewParticipant that)) {
//            return false;
//        }
//        return id != null && Objects.equals(getId(), that.getId());
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hashCode(getId());
//    }
//
//    public Long getRequestMemberId() {
//        return member.getId();
//    }
//}
