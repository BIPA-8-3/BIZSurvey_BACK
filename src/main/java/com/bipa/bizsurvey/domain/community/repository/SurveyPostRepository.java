package com.bipa.bizsurvey.domain.community.repository;


import com.bipa.bizsurvey.domain.community.domain.SurveyPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SurveyPostRepository extends JpaRepository<SurveyPost, Long> {
}
