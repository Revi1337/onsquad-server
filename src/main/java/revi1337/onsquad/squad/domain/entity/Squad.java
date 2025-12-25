package revi1337.onsquad.squad.domain.entity;

import static jakarta.persistence.CascadeType.MERGE;
import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;
import static org.hibernate.annotations.OnDeleteAction.CASCADE;
import static revi1337.onsquad.squad.error.SquadErrorCode.INVALID_CAPACITY_SIZE;
import static revi1337.onsquad.squad.error.SquadErrorCode.NOT_ENOUGH_LEFT;
import static revi1337.onsquad.squad.error.SquadErrorCode.SQUAD_MEMBER_UNDERFLOW;

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
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.OnDelete;
import revi1337.onsquad.common.domain.BaseEntity;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.entity.vo.Address;
import revi1337.onsquad.squad.domain.entity.vo.Content;
import revi1337.onsquad.squad.domain.entity.vo.Title;
import revi1337.onsquad.squad.error.SquadDomainException;
import revi1337.onsquad.squad_category.domain.entity.SquadCategory;
import revi1337.onsquad.squad_comment.domain.entity.SquadComment;
import revi1337.onsquad.squad_member.domain.entity.SquadMember;
import revi1337.onsquad.squad_member.domain.entity.SquadMemberFactory;
import revi1337.onsquad.squad_request.domain.entity.SquadRequest;

@Getter
@NoArgsConstructor(access = PROTECTED)
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

    @Column(name = "current_size", nullable = false)
    private int currentSize;

    @Column(name = "remain", nullable = false)
    private int remain;

    private String kakaoLink;

    private String discordLink;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

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

    @OnDelete(action = CASCADE)
    @OneToMany(mappedBy = "squad")
    private final List<SquadComment> comments = new ArrayList<>();

    @OnDelete(action = CASCADE)
    @OneToMany(mappedBy = "squad")
    private final List<SquadRequest> participants = new ArrayList<>();

    public static Squad create(SquadMetadata metadata, Member member, Crew crew) {
        Squad squad = metadata.toEntity();
        squad.registerMember(member);
        squad.registerCrew(crew);
        squad.addMembers(SquadMemberFactory.leader(member, LocalDateTime.now()));
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
            increaseCurrentSize();
            squadMember.addSquad(this);
            this.members.add(squadMember);
        }
    }

    public void increaseCurrentSize() {
        if (this.currentSize + 1 > this.capacity) {
            throw new SquadDomainException.NotEnoughLeft(NOT_ENOUGH_LEFT);
        }
        this.currentSize++;
        this.remain--;
    }

    public void decreaseCurrentSize() {
        if (this.currentSize - 1 < 0) {
            throw new SquadDomainException.UnderflowSize(SQUAD_MEMBER_UNDERFLOW);
        }
        this.currentSize--;
        this.remain++;
    }

    public void delegateLeader(SquadMember currentLeader, SquadMember nextLeader) {
        if (currentLeader.isLeader()) {
            nextLeader.promoteToLeader();
            currentLeader.demoteToGeneral();
            updateLeader(nextLeader.getMember());
        }
    }

    public void registerMember(Member member) {
        this.member = member;
    }

    public void registerCrew(Crew crew) {
        this.crew = crew;
    }

    public boolean matchId(Long squadId) {
        return id.equals(squadId);
    }

    public boolean matchCrewId(Long crewId) {
        return getCrewId().equals(crewId);
    }

    public boolean mismatchCrewId(Long crewId) {
        return !matchCrewId(crewId);
    }

    public boolean mismatchMemberId(Long memberId) {
        return !member.getId().equals(memberId);
    }

    public Long getCrewId() {
        return crew.getId();
    }

    public Member getOwner() {
        return member;
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

    private void validateCapacity(int value) {
        if (value < MIN_CAPACITY || value > MAX_CAPACITY) {
            throw new SquadDomainException.InvalidCapacitySize(INVALID_CAPACITY_SIZE, MIN_CAPACITY, MAX_CAPACITY);
        }
    }

    private void updateLeader(Member member) {
        this.member = member;
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

        Squad toEntity() {
            return new Squad(title, content, capacity, address, addressDetail, kakaoLink, discordLink);
        }
    }
}
