package revi1337.onsquad.crew_comment.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CrewCommentRepository extends JpaRepository<CrewComment, Long>, CrewCommentQueryRepository {
}

