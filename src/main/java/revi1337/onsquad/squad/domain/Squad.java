package revi1337.onsquad.squad.domain;

import static jakarta.persistence.CascadeType.DETACH;
import static jakarta.persistence.CascadeType.MERGE;
import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.CascadeType.REFRESH;
import static jakarta.persistence.FetchType.LAZY;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;
import revi1337.onsquad.common.domain.BaseEntity;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.crew_member.domain.CrewMember;
import revi1337.onsquad.member.domain.vo.Address;
import revi1337.onsquad.squad.domain.vo.Capacity;
import revi1337.onsquad.squad.domain.vo.Content;
import revi1337.onsquad.squad.domain.vo.Title;
import revi1337.onsquad.squad_category.domain.SquadCategory;
import revi1337.onsquad.squad_member.domain.SquadMember;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Squad extends BaseEntity {

    private static final int CATEGORY_BATCH_SIZE = 20;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Title title;

    @Embedded
    private Content content;

    @Embedded
    private Capacity capacity;

    @Embedded
    private Address address;

    private String kakaoLink;

    private String discordLink;

    @BatchSize(size = CATEGORY_BATCH_SIZE)
    @OneToMany(mappedBy = "squad")
    private final List<SquadCategory> categories = new ArrayList<>();

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "crew_member_id", nullable = false)
    private CrewMember crewMember;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "crew_id", nullable = false)
    private Crew crew;

    @OneToMany(mappedBy = "squad", cascade = {PERSIST, MERGE, DETACH, REFRESH})
    private final List<SquadMember> squadMembers = new ArrayList<>();

    @Builder
    private Squad(Long id, Title title, Content content, Capacity capacity, Address address, String kakaoLink,
                  String discordLink, CrewMember crewMember, Crew crew) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.capacity = capacity;
        this.address = address;
        this.kakaoLink = kakaoLink;
        this.discordLink = discordLink;
        this.crewMember = crewMember;
        this.crew = crew;
    }

    public void addSquadMember(SquadMember... squadMembers) {
        for (SquadMember squadMember : squadMembers) {
            capacity.decreaseRemain();
            squadMember.addSquad(this);
            this.squadMembers.add(squadMember);
        }
    }

    public boolean hasNotSameCrewId(Long crewId) {
        return !hasSameCrewId(crewId);
    }

    public boolean hasSameCrewId(Long crewId) {
        return crewId.equals(crew.getId());
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Squad squad)) {
            return false;
        }
        return id != null && Objects.equals(id, squad.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public boolean isSquadMemberAlreadyParticipant(Long crewMemberId) {
        for (SquadMember squadMember : this.squadMembers) {
            if (squadMember.isSameCrewMemberId(crewMemberId)) {
                return true;
            }
        }
        return false;
    }

    public boolean isNotSameCrewId(Long crewId) {
        return !isSameCrewId(crewId);
    }

    public boolean isSameCrewId(Long crewId) {
        return getCrewId().equals(crewId);
    }

    public Long getCrewId() {
        return crew.getId();
    }

    public Long getOwnerId() {
        return crewMember.getId();
    }
}
