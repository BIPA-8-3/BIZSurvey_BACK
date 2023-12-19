package com.bipa.bizsurvey.domain.community.repository;


import com.bipa.bizsurvey.domain.community.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    //
    List<Comment> findByPostIdOrderByRegDateDesc(Long postId);
}
