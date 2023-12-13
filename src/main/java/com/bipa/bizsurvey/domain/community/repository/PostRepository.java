package com.bipa.bizsurvey.domain.community.repository;

import com.bipa.bizsurvey.domain.community.domain.Post;
import com.bipa.bizsurvey.domain.community.enums.PostType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByDelFlagIsFalseAndReportedIsFalse(Pageable pageable);
}
