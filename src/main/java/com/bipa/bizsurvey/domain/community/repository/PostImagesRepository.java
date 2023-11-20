package com.bipa.bizsurvey.domain.community.repository;

import com.bipa.bizsurvey.domain.community.domain.PostImages;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostImagesRepository extends JpaRepository<PostImages, Long> {


    boolean existsByImgName(String imageName);
}
