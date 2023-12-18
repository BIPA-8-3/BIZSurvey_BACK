package com.bipa.bizsurvey.domain.community.repository;

import com.bipa.bizsurvey.domain.community.domain.VoteAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VoteAnswerRepository extends JpaRepository<VoteAnswer, Long> {
    List<VoteAnswer> findAllByVoteId(Long voteId);
}
