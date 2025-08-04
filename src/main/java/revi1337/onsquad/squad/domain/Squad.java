package revi1337.onsquad.squad.domain;

import static jakarta.persistence.CascadeType.MERGE;
import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static org.hibernate.annotations.OnDeleteAction.CASCADE;
import static revi1337.onsquad.squad.error.SquadErrorCode.INVALID_CAPACITY_SIZE;
import static revi1337.onsquad.squad.error.SquadErrorCode.NOT_ENOUGH_LEFT;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.OnDelete;
import revi1337.onsquad.common.domain.BaseEntity;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.crew_member.domain.CrewMember;
import revi1337.onsquad.member.domain.vo.Address;
import revi1337.onsquad.squad.domain.vo.Content;
import revi1337.onsquad.squad.domain.vo.Title;
import revi1337.onsquad.squad.error.exception.SquadDomainException;
import revi1337.onsquad.squad_category.domain.SquadCategory;
import revi1337.onsquad.squad_member.domain.SquadMember;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Squad extends BaseEntity {

    private static final int MIN_CAPACITY = 2;
    private static final int MAX_CAPACITY = 1000;
    private static final int CATEGORY_BATCH_SIZE = 20;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Embedded
    private Title title;

    @Embedded
    private Content content;

    @Embedded
    private Address address;

    @Column(name = "capacity", nullable = false)
    private int capacity;

    @Column(name = "remain", nullable = false)
    private int remain;

    private String kakaoLink;

    private String discordLink;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "crew_member_id", nullable = false)
    private CrewMember crewMember;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "crew_id", nullable = false)
    private Crew crew;

    @BatchSize(size = CATEGORY_BATCH_SIZE)
    @OnDelete(action = CASCADE)
    @OneToMany(mappedBy = "squad")
    private final List<SquadCategory> categories = new ArrayList<>();

    @OnDelete(action = CASCADE)
    @OneToMany(mappedBy = "squad", cascade = {PERSIST, MERGE})
    private final List<SquadMember> members = new ArrayList<>();

    public static Squad create(SquadMetadata metadata, CrewMember crewMember, Crew crew) {
        Squad squad = metadata.toEntity();
        squad.registerOwner(crewMember);
        squad.registerCrew(crew);
        squad.addMembers(SquadMember.forLeader(crewMember, LocalDateTime.now()));
        return squad;
    }

    public static Squad create(SquadMetadata metadata) {
        return metadata.toEntity();
    }

    private Squad(String title, String content, int capacity, String address, String addressDetail, String kakaoLink, String discordLink) {
        validateCapacity(capacity);
        this.title = new Title(title);
        this.content = new Content(content);
        this.address = new Address(address, addressDetail);
        this.capacity = capacity;
        this.remain = capacity;
        this.kakaoLink = kakaoLink;
        this.discordLink = discordLink;
    }

    public void addMembers(SquadMember... squadMembers) {
        for (SquadMember squadMember : squadMembers) {
            decreaseRemain();
            squadMember.addSquad(this);
            this.members.add(squadMember);
        }
    }

    public void registerOwner(CrewMember crewMember) {
        this.crewMember = crewMember;
    }

    public void registerCrew(Crew crew) {
        this.crew = crew;
    }

    public void increaseRemain() {
        if (this.remain + 1 > MAX_CAPACITY) {
            throw new SquadDomainException.NotEnoughLeft(NOT_ENOUGH_LEFT);
        }
        this.remain += 1;
    }

    public void decreaseRemain() {
        if (this.remain - 1 < 0) {
            throw new SquadDomainException.NotEnoughLeft(NOT_ENOUGH_LEFT);
        }
        this.remain -= 1;
    }

    public boolean misMatchCrewId(Long crewId) {
        return !matchCrewId(crewId);
    }

    public boolean matchCrewId(Long crewId) {
        return getCrewId().equals(crewId);
    }

    public boolean hasSameId(Long squadId) {
        return id.equals(squadId);
    }

    public boolean doesNotMatchOwner(Long crewMemberId) {
        return !matchOwner(crewMemberId);
    }

    public boolean matchOwner(Long crewMemberId) {
        return crewMember.hasSameId(crewMemberId);
    }

    /**
     * Squad 에 속한 SquadMember 의 실제 memberId 를 비교하여, Squad 의 특정 Member 가 있는지 확인한다.
     * <p>
     * 매우 주의해야 할 것은 Squad 조회 시, SquadMember 들을 같이 fetch 해오지 않으면, 반복문을 도는 과정에서 프록시 객체 초기화로 인해 size(this.members) 수 만큼 조회 쿼리가 나간다.
     */
    public boolean existsMember(Long crewMemberId) {
        for (SquadMember squadMember : this.members) {
            if (squadMember.isSameCrewMemberId(crewMemberId)) {
                return true;
            }
        }
        return false;
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

    public Long getCrewId() {
        return crew.getId();
    }

    public CrewMember getOwner() {
        return crewMember;
    }

    private void validateCapacity(int value) {
        if (value < MIN_CAPACITY || value > MAX_CAPACITY) {
            throw new SquadDomainException.InvalidCapacitySize(INVALID_CAPACITY_SIZE, MIN_CAPACITY, MAX_CAPACITY);
        }
    }

    public record SquadMetadata(
            String title,
            String content,
            int capacity,
            String address,
            String addressDetail,
            String kakaoLink,
            String discordLink
    ) {
        public Squad toEntity() {
            return new Squad(title, content, capacity, address, addressDetail, kakaoLink, discordLink);
        }
    }
}
