package com.bipa.bizsurvey.domain.community.repository;

import com.bipa.bizsurvey.domain.community.domain.VoteUserAnswer;
import com.bipa.bizsurvey.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteUserAnswerRepository extends JpaRepository<VoteUserAnswer, Long> {
    boolean existsByUserIdAndVoteId(Long userId, Long voteId);
}
