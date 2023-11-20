package com.bipa.bizsurvey.domain.community.repository;

import com.bipa.bizsurvey.domain.community.domain.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteRepository extends JpaRepository<Vote, Long> {
}
